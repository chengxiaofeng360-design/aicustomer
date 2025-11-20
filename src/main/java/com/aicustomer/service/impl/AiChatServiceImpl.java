package com.aicustomer.service.impl;

import com.aicustomer.entity.AiChat;
import com.aicustomer.mapper.AiChatMapper;
import com.aicustomer.service.AiChatService;
import com.aicustomer.service.DeepSeekService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI聊天服务实现类
 * 
 * 使用DeepSeek大模型提供智能对话服务
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService {
    
    private static final Long DEFAULT_USER_ID = 0L;
    private static final String DEFAULT_USER_NAME = "系统访客";
    
    private final DeepSeekService deepSeekService;
    private final AiChatMapper aiChatMapper;
    
    @Qualifier("doubaoChatModel")
    private final ChatModel doubaoChatModel;
    
    // 系统提示词，定义AI助手的角色和行为
    private static final String SYSTEM_PROMPT = "你是一个专业的客户服务AI助手，专门为种业客户管理系统提供服务。" +
            "你的职责是帮助用户解答关于客户管理、业务分析、产品推荐、市场趋势等方面的问题。" +
            "请用友好、专业、简洁的语言回答用户问题。如果遇到无法回答的问题，建议用户联系人工客服。";
    
    @Override
    public AiChat sendMessage(String sessionId, String userMessage, Long customerId) {
        return sendMessage(sessionId, userMessage, customerId, null);
    }
    
    @Override
    public AiChat sendMessage(String sessionId, String userMessage, Long customerId, List<Map<String, String>> history) {
        System.out.println("【AiChatService】sendMessage被调用");
        System.out.println("【AiChatService】会话ID: " + sessionId);
        System.out.println("【AiChatService】用户消息: " + userMessage);
        
        String normalizedSessionId = (sessionId == null || sessionId.trim().isEmpty())
                ? createNewSession(DEFAULT_USER_ID, customerId)
                : sessionId;
        LocalDateTime now = LocalDateTime.now();
        
        // 首先保存用户消息
        AiChat userRecord = buildMessageRecord(normalizedSessionId, customerId, 1, userMessage, null, now);
        aiChatMapper.insert(userRecord);
        
        // 使用DeepSeek生成AI回复（支持多轮对话）
        System.out.println("【AiChatService】开始生成AI回复...");
        String aiReply = generateAiResponse(userMessage, history);
        System.out.println("【AiChatService】AI回复生成完成，长度: " + (aiReply != null ? aiReply.length() : 0));
        
        if (aiReply == null || aiReply.trim().isEmpty()) {
            System.out.println("【AiChatService】警告: AI回复为空！");
            aiReply = "抱歉，AI服务返回了空回复，请检查后端日志。";
        }
        
        AiChat aiRecord = buildMessageRecord(normalizedSessionId, customerId, 2, aiReply, aiReply, now);
        aiChatMapper.insert(aiRecord);
        
        System.out.println("【AiChatService】返回AiChat对象，replyContent: " + aiReply.substring(0, Math.min(100, aiReply.length())) + "...");
        return aiRecord;
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
    
    /**
     * 使用DeepSeek生成AI回复
     * 如果DeepSeek服务不可用，则回退到规则匹配
     * 
     * @param userMessage 用户消息
     * @param history 对话历史（可选，用于多轮对话）
     */
    private String generateAiResponse(String userMessage, List<Map<String, String>> history) {
        System.out.println("【AI聊天服务】开始生成AI回复");
        System.out.println("【AI聊天服务】用户消息: " + userMessage);
        System.out.println("【AI聊天服务】是否有历史: " + (history != null && !history.isEmpty()));
        
        // 优先使用Spring AI的豆包模型
        try {
            System.out.println("【AI聊天服务】使用Spring AI豆包模型");
            
            // 构建Prompt，包含系统提示、历史对话和当前消息
            List<org.springframework.ai.chat.messages.Message> messages = new ArrayList<>();
            
            // 添加系统提示
            messages.add(new SystemMessage(SYSTEM_PROMPT));
            
            // 添加历史对话（如果有）
            if (history != null && !history.isEmpty()) {
                System.out.println("【AI聊天服务】添加对话历史，条数: " + history.size());
                for (Map<String, String> historyItem : history) {
                    String role = historyItem.get("role");
                    String content = historyItem.get("content");
                    if (content != null && !content.trim().isEmpty()) {
                        if ("user".equals(role)) {
                            messages.add(new UserMessage(content));
                        } else if ("assistant".equals(role)) {
                            messages.add(new org.springframework.ai.chat.messages.AssistantMessage(content));
                        }
                    }
                }
            }
            
            // 添加当前用户消息
            messages.add(new UserMessage(userMessage));
            
            // 调用Spring AI
            Prompt prompt = new Prompt(messages);
            ChatResponse chatResponse = doubaoChatModel.call(prompt);
            String aiReply = chatResponse.getResult().getOutput().getContent();
            
            if (aiReply != null && !aiReply.trim().isEmpty()) {
                log.info("【AI聊天服务】Spring AI豆包模型生成回复成功，用户消息长度: {}, 回复长度: {}", userMessage.length(), aiReply.length());
                System.out.println("【AI聊天服务】Spring AI豆包模型生成回复成功，回复长度: " + aiReply.length());
                return aiReply;
            }
        } catch (Exception e) {
            log.error("【AI聊天服务】Spring AI豆包模型调用失败，尝试DeepSeek: {}", e.getMessage(), e);
            System.out.println("【AI聊天服务】错误: Spring AI豆包模型调用失败");
            System.out.println("【AI聊天服务】错误信息: " + e.getMessage());
        }
        
        // 其次尝试DeepSeek
        if (deepSeekService.isAvailable()) {
            System.out.println("【AI聊天服务】DeepSeek服务可用，开始调用");
            try {
                String aiReply;
                
                // 如果有对话历史，使用多轮对话
                if (history != null && !history.isEmpty()) {
                    System.out.println("【AI聊天服务】使用多轮对话，历史条数: " + history.size());
                    // 构建完整的消息列表（系统提示 + 历史对话 + 当前消息）
                    List<Map<String, String>> messages = new ArrayList<>();
                    
                    // 添加系统提示
                    Map<String, String> systemMsg = new HashMap<>();
                    systemMsg.put("role", "system");
                    systemMsg.put("content", SYSTEM_PROMPT);
                    messages.add(systemMsg);
                    
                    // 添加历史对话（最多保留最近10轮）
                    int historySize = Math.min(history.size(), 20); // 最多10轮（每轮2条消息）
                    for (int i = Math.max(0, history.size() - historySize); i < history.size(); i++) {
                        messages.add(history.get(i));
                    }
                    
                    // 添加当前用户消息
                    Map<String, String> userMsg = new HashMap<>();
                    userMsg.put("role", "user");
                    userMsg.put("content", userMessage);
                    messages.add(userMsg);
                    
                    System.out.println("【AI聊天服务】构建消息列表完成，总消息数: " + messages.size());
                    // 调用多轮对话API
                    aiReply = deepSeekService.chatWithHistory(messages);
                } else {
                    System.out.println("【AI聊天服务】使用单轮对话");
                    // 单轮对话
                    aiReply = deepSeekService.chat(userMessage, SYSTEM_PROMPT);
                }
                
                log.info("【AI聊天服务】DeepSeek生成回复成功，用户消息长度: {}, 回复长度: {}", userMessage.length(), aiReply.length());
                System.out.println("【AI聊天服务】DeepSeek生成回复成功，回复长度: " + aiReply.length());
                return aiReply;
            } catch (Exception e) {
                log.error("【AI聊天服务】DeepSeek API调用失败，回退到规则匹配: {}", e.getMessage(), e);
                System.out.println("【AI聊天服务】错误: DeepSeek API调用失败");
                System.out.println("【AI聊天服务】错误信息: " + e.getMessage());
                System.out.println("【AI聊天服务】回退到规则匹配");
                e.printStackTrace();
                // 回退到规则匹配
                return generateFallbackResponse(userMessage);
            }
        } else {
            log.warn("【AI聊天服务】DeepSeek服务不可用，使用规则匹配生成回复");
            System.out.println("【AI聊天服务】警告: DeepSeek服务不可用，使用规则匹配");
            return generateFallbackResponse(userMessage);
        }
    }
    
    /**
     * 规则匹配回退方案（当DeepSeek不可用时使用）
     */
    private String generateFallbackResponse(String userMessage) {
        String lowerMessage = userMessage.toLowerCase();
        
        if (lowerMessage.contains("产品") || lowerMessage.contains("服务")) {
            return "感谢您的咨询！我们提供多种产品和服务，包括智能管理系统、数据分析工具等。请问您对哪个方面比较感兴趣？";
        } else if (lowerMessage.contains("价格") || lowerMessage.contains("费用")) {
            return "我们的产品价格根据具体需求而定。建议您联系我们的销售团队，他们会为您提供详细的报价方案。";
        } else if (lowerMessage.contains("技术支持") || lowerMessage.contains("帮助")) {
            return "我们提供7x24小时技术支持服务。您可以通过电话、邮件或在线客服联系我们，我们会尽快为您解决问题。";
        } else if (lowerMessage.contains("合同") || lowerMessage.contains("协议")) {
            return "关于合同和协议的具体条款，建议您与我们的法务部门联系。我们会确保所有条款都符合相关法律法规。";
        } else {
            return "感谢您的咨询！我是AI助手，可以为您解答关于产品、服务、技术支持等方面的问题。请告诉我您需要了解什么？";
        }
    }
    
    @Override
    public Map<String, Object> getChatHistory(int pageNum, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        int offset = Math.max(pageNum - 1, 0) * pageSize;
        AiChat criteria = new AiChat();
        
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
        // 生成新的会话ID
        String newSessionId = "session_" + System.currentTimeMillis() + "_" + (userId != null ? userId : DEFAULT_USER_ID);
        log.info("创建新会话: sessionId={}, userId={}, customerId={}", newSessionId, userId, customerId);
        return newSessionId;
    }
    
    private AiChat buildMessageRecord(String sessionId, Long customerId, Integer messageType, String content, String replyContent, LocalDateTime timestamp) {
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
}