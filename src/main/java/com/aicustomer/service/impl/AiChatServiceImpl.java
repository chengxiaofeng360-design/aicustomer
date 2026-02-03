package com.aicustomer.service.impl;

import com.aicustomer.entity.AiChat;
import com.aicustomer.entity.FaqQa;
import com.aicustomer.mapper.AiChatMapper;
import com.aicustomer.model.FunctionCallRequest;
import com.aicustomer.model.FunctionCallResponse;
import com.aicustomer.service.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * AI聊天服务实现类
 * 
 * 全面拥抱 Dify 引擎
 * 旧版 adapter 逻辑已废弃
 */
@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("deprecation")
public class AiChatServiceImpl implements AiChatService {

    private static final Long DEFAULT_USER_ID = 0L;
    private static final String DEFAULT_USER_NAME = "系统访客";

    // 新版核心服务
    private final com.aicustomer.dify.service.DifyService difyService;

    private final AiChatMapper aiChatMapper;
    private final UserService userService;

    // 旧版依赖（保留以维持编译通过，逻辑中不再使用）
    private final DeepSeekService deepSeekService;
    private final FaqQaService faqQaService;
    private final KnowledgeDocumentService knowledgeDocumentService;
    private final VectorSearchService vectorSearchService;
    private final CustomerQueryService customerQueryService;
    private final KnowledgeQueryService knowledgeQueryService;
    private final ZhipuChatService zhipuChatService;
    private final KbDocumentService kbDocumentService;
    private final FunctionCallingService functionCallingService;
    private final com.aicustomer.adapter.AiModelAdapterManager adapterManager;
    private final WebSearchService webSearchService;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.aicustomer.adapter.impl.DifyAdapter difyAdapter;

    @Qualifier("doubaoChatModel")
    private final ChatModel doubaoChatModel;

    @Value("${ai.engine:dify}")
    private String aiEngine;

    @Override
    public AiChat sendMessage(String sessionId, String userMessage, Long customerId) {
        return sendMessage(sessionId, userMessage, customerId, null, 0L);
    }

    @Override
    public AiChat sendMessage(String sessionId, String userMessage, Long customerId,
            List<Map<String, String>> history, Long userId) {

        log.info("【AiChatService】Dify 接管对话 - 会话ID: {}, 用户: {}", sessionId, userId);

        Long effectiveUserId = (userId != null) ? userId : DEFAULT_USER_ID;
        String normalizedSessionId = (sessionId == null || sessionId.trim().isEmpty())
                ? createNewSession(effectiveUserId, customerId)
                : sessionId;
        LocalDateTime now = LocalDateTime.now();

        // 1. 记录用户消息
        AiChat userRecord = buildMessageRecord(normalizedSessionId, customerId, 1, userMessage, null, now);
        userRecord.setUserId(effectiveUserId);
        aiChatMapper.insert(userRecord);

        // 2. 调用 Dify Service
        String aiReplyContent = "";
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("query", userMessage);
            payload.put("user", String.valueOf(effectiveUserId));
            payload.put("inputs", new HashMap<>());
            payload.put("response_mode", "blocking");

            // 可选：传递 conversation_id 如果需要在这个层面维持Dify会话上下文
            // payload.put("conversation_id", sessionId);

            log.info("🚀 调用 Dify API...");
            Map<String, Object> response = difyService.chat(payload);

            if (response != null && response.containsKey("answer")) {
                aiReplyContent = (String) response.get("answer");
                log.info("✅ Dify 响应成功: {}", aiReplyContent);
            } else {
                // 尝试提取错误信息
                String errorMsg = "AI 服务暂时无响应 (Dify API returned empty answer)";
                if (response != null && (response.containsKey("message") || response.containsKey("code"))) {
                    errorMsg = String.format("Dify 调用失败: %s (Code: %s)",
                            response.getOrDefault("message", "Unknown error"),
                            response.getOrDefault("code", "N/A"));
                }
                aiReplyContent = errorMsg;
                log.warn("❌ Dify 响应异常: {}", response);
            }

        } catch (Exception e) {
            log.error("❌ 调用 Dify 失败", e);
            aiReplyContent = "抱歉，系统暂时无法处理您的请求。(" + e.getMessage() + ")";
        }

        // 3. 记录AI回复
        AiChat aiRecord = buildMessageRecord(normalizedSessionId, customerId, 2, aiReplyContent, aiReplyContent, now);
        aiRecord.setUserId(effectiveUserId);
        aiChatMapper.insert(aiRecord);

        return aiRecord;
    }

    @Override
    public void streamMessage(String sessionId, String userMessage, Long customerId,
            List<Map<String, String>> history, Long userId, java.util.function.Consumer<String> chunkHandler) {

        log.info("【AiChatService】Dify 流式对话 - 会话ID: {}, 用户: {}", sessionId, userId);

        Long effectiveUserId = (userId != null) ? userId : DEFAULT_USER_ID;
        String normalizedSessionId = (sessionId == null || sessionId.trim().isEmpty())
                ? createNewSession(effectiveUserId, customerId)
                : sessionId;
        LocalDateTime now = LocalDateTime.now();

        // 1. 记录用户消息
        AiChat userRecord = buildMessageRecord(normalizedSessionId, customerId, 1, userMessage, null, now);
        userRecord.setUserId(effectiveUserId);
        aiChatMapper.insert(userRecord);

        // 2. 调用 Dify Service (Stream)
        StringBuilder fullResponseBuilder = new StringBuilder();

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("query", userMessage);
            payload.put("user", String.valueOf(effectiveUserId));

            // 注入上下文变量供 DSL 使用
            Map<String, Object> inputs = new HashMap<>();
            inputs.put("current_user", String.valueOf(effectiveUserId));
            payload.put("inputs", inputs);

            log.info("🚀 调用 Dify Streaming API...");

            // 核心修复：确保流式响应完成后立即返回
            difyService.streamChat(payload, chunk -> {
                // Accumulate full response
                fullResponseBuilder.append(chunk);
                // Forward chunk to frontend
                chunkHandler.accept(chunk);
            });

            log.info("✅ Dify 流式响应结束, 长度: {}", fullResponseBuilder.length());

        } catch (Exception e) {
            log.error("❌ 调用 Dify Stream 失败", e);
            String errorMsg = "抱歉，系统暂时无法处理您的请求。(" + e.getMessage() + ")";
            chunkHandler.accept(errorMsg);
            fullResponseBuilder.append(errorMsg);
        }

        // 3. 记录AI回复 (Full Text) - 在流结束后立即执行
        String aiReplyContent = fullResponseBuilder.toString();
        if (aiReplyContent.isEmpty()) {
            aiReplyContent = "AI 服务未返回任何内容";
        }

        log.info("💾 保存AI回复到数据库, 长度: {}", aiReplyContent.length());
        AiChat aiRecord = buildMessageRecord(normalizedSessionId, customerId, 2, aiReplyContent, aiReplyContent,
                LocalDateTime.now());
        aiRecord.setUserId(effectiveUserId);
        aiChatMapper.insert(aiRecord);
        log.info("✅ AI回复已保存");
    }

    @Override
    public Map<String, Object> getChatStatistics() {
        Map<String, Object> stats = aiChatMapper.selectStatistics(null, null, null, null);
        if (stats == null) {
            stats = new HashMap<>();
            stats.put("totalChats", 0);
            stats.put("userMessages", 0);
            stats.put("aiReplies", 0);
            stats.put("avgSatisfaction", 0);
            stats.put("satisfiedCount", 0);
        }
        return stats;
    }

    // --- 辅助方法 ---

    @Override
    public Map<String, Object> getChatHistory(int pageNum, int pageSize, Long userId) {
        Map<String, Object> result = new HashMap<>();
        int offset = Math.max(pageNum - 1, 0) * pageSize;

        AiChat criteria = new AiChat();
        if (userId != null) {
            criteria.setUserId(userId);
        }

        List<AiChat> records = aiChatMapper.selectPage(criteria, offset, pageSize);
        Long total = aiChatMapper.selectCount(criteria);
        long safeTotal = total == null ? 0 : total;
        long pages = pageSize == 0 ? 0 : (long) Math.ceil(safeTotal * 1.0 / pageSize);

        result.put("list", records);
        result.put("total", safeTotal);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("pages", pages);

        return result;
    }

    @Override
    public List<Map<String, Object>> getSessionList(Long userId, Integer limit) {
        return aiChatMapper.selectSessionList(userId, limit);
    }

    @Override
    public List<AiChat> getMessagesBySessionId(String sessionId) {
        return aiChatMapper.selectMessagesBySessionId(sessionId);
    }

    @Override
    public String createNewSession(Long userId, Long customerId) {
        String newSessionId = "session_" + System.currentTimeMillis() + "_"
                + (userId != null ? userId : DEFAULT_USER_ID);
        log.info("创建新会话: sessionId={}, userId={}, customerId={}", newSessionId, userId, customerId);
        return newSessionId;
    }

    @Override
    public boolean deleteSession(String sessionId) {
        log.info("删除会话: sessionId={}", sessionId);
        int rows = aiChatMapper.deleteBySessionId(sessionId);
        return rows >= 0;
    }

    private AiChat buildMessageRecord(String sessionId, Long customerId, Integer messageType, String content,
            String replyContent, LocalDateTime timestamp) {
        AiChat record = new AiChat();
        record.setSessionId(sessionId);
        record.setCustomerId(customerId);
        record.setMessageType(messageType);
        record.setContent(content);
        record.setReplyContent(replyContent);
        record.setReplyTime(timestamp);
        record.setUserId(DEFAULT_USER_ID);
        record.setUserName(DEFAULT_USER_NAME);
        record.setCreateTime(timestamp);
        record.setUpdateTime(timestamp);
        record.setCreateBy(DEFAULT_USER_NAME);
        record.setUpdateBy(DEFAULT_USER_NAME);
        record.setDeleted(0);
        record.setVersion(1);
        return record;
    }

    // --- 占位方法 (废弃逻辑) ---
    private String generateAiResponseLegacy(String userMessage, List<Map<String, String>> history) {
        return null;
    }

    private String handleToolCalls(FunctionCallResponse response, FunctionCallRequest originalRequest) {
        return null;
    }
}