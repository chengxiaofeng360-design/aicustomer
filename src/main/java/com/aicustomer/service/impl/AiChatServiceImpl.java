package com.aicustomer.service.impl;

import com.aicustomer.entity.AiChat;
import com.aicustomer.entity.FaqQa;
import com.aicustomer.entity.KnowledgeDocument;
import com.aicustomer.mapper.AiChatMapper;
import com.aicustomer.model.FunctionCallRequest;
import com.aicustomer.model.FunctionCallResponse;
import com.aicustomer.service.AiChatService;
import com.aicustomer.service.DeepSeekService;
import com.aicustomer.service.FaqQaService;
import com.aicustomer.service.KnowledgeDocumentService;
import com.aicustomer.service.KnowledgeQueryService;
import com.aicustomer.service.KbDocumentService;
import com.aicustomer.service.VectorSearchService;
import com.aicustomer.service.CustomerQueryService;
import com.aicustomer.service.ZhipuChatService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
    private final FaqQaService faqQaService;
    private final KnowledgeDocumentService knowledgeDocumentService;
    private final VectorSearchService vectorSearchService;
    private final CustomerQueryService customerQueryService;
    private final KnowledgeQueryService knowledgeQueryService;
    private final ZhipuChatService zhipuChatService;
    private final KbDocumentService kbDocumentService;
    private final com.aicustomer.service.UserService userService;
    private final com.aicustomer.service.FunctionCallingService functionCallingService;
    private final com.aicustomer.adapter.AiModelAdapterManager adapterManager;
    private final com.aicustomer.service.WebSearchService webSearchService;

    @Qualifier("doubaoChatModel")
    private final ChatModel doubaoChatModel;

    // 系统提示词，定义AI助手的角色和行为
    private static final String SYSTEM_PROMPT = "你是AI客户管理助手。" +
            "\n\n### 工具调用规则（必须严格遵守！）###\n" +
            "**重要：你有Function Calling能力。当需要查询数据时，你必须调用工具函数，绝对不要在回复中返回JSON格式的字符串！**\n\n" +
            "**错误示例❌：**\n" +
            "- {\"tool\":\"get_file_detail\",\"parameters\":{\"fileName\":\"xxx\"}}\n" +
            "- get_file_list {\"limit\": 20}\n\n" +
            "**正确做法✅：**\n" +
            "- 直接使用系统提供的Function Calling机制调用工具\n" +
            "- 不要用文本形式告诉用户你要调用什么工具\n" +
            "- 让系统自动执行工具并获取结果\n\n" +
            "### 可用工具 ###\n" +
            "- get_file_count: 文件总数\n" +
            "- get_file_list: 文件列表\n" +
            "- get_file_detail: 获取文件内容\n" +
            "- get_customer_count: 客户总数\n" +
            "- get_customer_list: 客户列表\n" +
            "\n### 必须遵守的规则 ###\n" +
            "1. 用户问\"XXX是什么/是啥/内容\" → 立刻调用 get_file_detail，文件名=XXX\n" +
            "2. 用户说\"好/对/是/OK/查看\" → 根据上一轮对话执行相应工具\n" +
            "3. 获取到文件内容后，完整输出，禁止摘要\n" +
            "4. 禁止回复：\"请问您需要吗\"、\"我可以帮您获取\"、\"您是否要查看\"\n" +
            "5. 禁止返回初始问候语，必须基于上下文回复\n" +
            "6. **再次强调：禁止返回JSON字符串，必须使用原生Function Calling！**\n" +
            "\n**记住：用户提到文件名 → 立即调用工具 → 输出完整结果**";

    @Override
    public AiChat sendMessage(String sessionId, String userMessage, Long customerId) {
        return sendMessage(sessionId, userMessage, customerId, null, 0L);
    }

    @Override
    public AiChat sendMessage(String sessionId, String userMessage, Long customerId,
            List<Map<String, String>> history, Long userId) {
        System.out.println("【AiChatService】sendMessage被调用");
        System.out.println("【AiChatService】会话ID: " + sessionId);
        System.out.println("【AiChatService】用户消息: " + userMessage);

        Long effectiveUserId = (userId != null) ? userId : DEFAULT_USER_ID;
        String normalizedSessionId = (sessionId == null || sessionId.trim().isEmpty())
                ? createNewSession(effectiveUserId, customerId)
                : sessionId;
        LocalDateTime now = LocalDateTime.now();

        // 首先保存用户消息
        AiChat userRecord = buildMessageRecord(normalizedSessionId, customerId, 1, userMessage, null, now);
        userRecord.setUserId(effectiveUserId);
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
        aiRecord.setUserId(effectiveUserId);
        aiChatMapper.insert(aiRecord);

        System.out.println("【AiChatService】返回AiChat对象，replyContent: "
                + aiReply.substring(0, Math.min(100, aiReply.length())) + "...");
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
     * @param history     对话历史（可选，用于多轮对话）
     */
    /**
     * 使用统一的AI适配器生成回复
     * 
     * 新架构：使用适配器模式，任何AI模型都能平等使用Function Calling
     * 
     * @param userMessage 用户消息
     * @param history     对话历史（可选，用于多轮对话）
     * @return AI回复
     */
    private String generateAiResponse(String userMessage, List<Map<String, String>> history) {
        log.info("【AI聊天服务 - 统一架构】开始处理查询: {}", userMessage);

        // 1. FAQ快速响应层
        try {
            List<FaqQa> faqs = faqQaService.searchFaq(userMessage, 1);
            if (faqs != null && !faqs.isEmpty()) {
                FaqQa faq = faqs.get(0);
                log.info("【AI聊天服务】命中FAQ: {}", faq.getQuestion());
                faqQaService.incrementHitCount(faq.getId());
                return faq.getAnswer() + "\n\n(来源: 常见问题库)";
            }
        } catch (Exception e) {
            log.error("【AI聊天服务】FAQ匹配异常: {}", e.getMessage());
        }

        // 2. 构建统一的Function Calling请求
        FunctionCallRequest request = FunctionCallRequest.builder()
                .systemPrompt(SYSTEM_PROMPT)
                .userMessage(userMessage)
                .history(history)
                .functions(functionCallingService.getAvailableFunctions())
                .build();

        // 3. 调用适配器管理器（自动选择模型和回退）
        FunctionCallResponse response = adapterManager.chatWithFallback(request);

        // 4. 处理响应
        if (response.hasError()) {
            log.error("【AI聊天服务】所有AI模型都失败: {}", response.getError());
            return generateFallbackResponse(userMessage);
        }

        // 5. 处理函数调用
        if (response.hasToolCalls()) {
            log.info("【AI聊天服务】AI请求调用{}个函数", response.getToolCalls().size());
            return handleToolCalls(response, request);
        }

        // 6. 直接回复
        if (response.getContent() != null && !response.getContent().trim().isEmpty()) {
            log.info("【AI聊天服务】使用模型: {}", response.getModelName());

            // 6.1 检测AI是否表示无法回答（Web Search fallback机制）
            String aiReply = response.getContent();
            if (shouldTriggerWebSearch(aiReply, userMessage)) {
                log.info("【AI聊天服务】检测到AI无法回答，触发web search fallback");
                return tryWebSearchFallback(userMessage, history, request);
            }

            return aiReply;
        }

        // 7. 未知情况，使用fallback
        log.warn("【AI聊天服务】AI返回了空响应，使用fallback");
        return generateFallbackResponse(userMessage);
    }

    /**
     * 处理函数调用
     * 执行函数并让AI整合结果
     */
    private String handleToolCalls(FunctionCallResponse response, FunctionCallRequest originalRequest) {
        log.info("【AI聊天服务】开始执行函数调用");

        try {
            // 获取当前用户权限
            List<Integer> allowedLevels = getCurrentUserAllowedLevels();
            log.info("【AI聊天服务】当前用户权限等级: {}", allowedLevels);

            // 1. 执行所有函数
            List<String> results = new ArrayList<>();
            for (FunctionCallResponse.ToolCall toolCall : response.getToolCalls()) {
                log.info("【AI聊天服务】执行函数: {}", toolCall.getFunctionName());

                // 执行函数（传递用户权限）
                String result = functionCallingService.executeFunction(
                        toolCall.getFunctionName(),
                        toolCall.getArguments(),
                        allowedLevels);

                results.add(result);
                log.info("【AI聊天服务】函数执行完成，结果长度: {}", result.length());
            }

            // 2. 合并结果
            String combinedResult = String.join("\n\n", results);

            // 3. 让AI整合结果生成用户友好的回复
            String followUpMessage = originalRequest.getUserMessage() +
                    "\n\n【系统查询结果】\n" + combinedResult +
                    "\n\n请根据上述查询结果，用自然语言回答用户的问题。";

            FunctionCallRequest followUpRequest = FunctionCallRequest.builder()
                    .systemPrompt(originalRequest.getSystemPrompt())
                    .userMessage(followUpMessage)
                    .history(originalRequest.getHistory())
                    .functions(originalRequest.getFunctions()) // 修复：保持完整函数列表
                    .build();

            FunctionCallResponse finalResponse = adapterManager.chatWithFallback(followUpRequest);

            if (finalResponse.isSuccess() && finalResponse.getContent() != null) {
                return finalResponse.getContent();
            }

            // 降级：直接返回查询结果
            log.warn("【AI聊天服务】AI整合失败，直接返回查询结果");
            return combinedResult;

        } catch (Exception e) {
            log.error("【AI聊天服务】处理函数调用异常", e);
            return "抱歉，处理您的请求时出现错误: " + e.getMessage();
        }
    }

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
        // 生成新的会话ID
        String newSessionId = "session_" + System.currentTimeMillis() + "_"
                + (userId != null ? userId : DEFAULT_USER_ID);
        log.info("创建新会话: sessionId={}, userId={}, customerId={}", newSessionId, userId, customerId);
        return newSessionId;
    }

    @Override
    public boolean deleteSession(String sessionId) {
        log.info("删除会话: sessionId={}", sessionId);
        int rows = aiChatMapper.deleteBySessionId(sessionId);
        return rows >= 0; // rows might be 0 if session has no messages or already deleted, but we
                          // consider the operation successful
    }

    private String executeTool(String aiReply) {
        try {
            // 提取 JSON 部分 - 寻找最外层的 { } 或 [ ]
            int braceStart = aiReply.indexOf("{");
            int bracketStart = aiReply.indexOf("[");
            int startIndex = -1;

            if (braceStart != -1 && bracketStart != -1)
                startIndex = Math.min(braceStart, bracketStart);
            else if (braceStart != -1)
                startIndex = braceStart;
            else if (bracketStart != -1)
                startIndex = bracketStart;

            int braceEnd = aiReply.lastIndexOf("}");
            int bracketEnd = aiReply.lastIndexOf("]");
            int endIndex = -1;

            if (braceEnd != -1 && bracketEnd != -1)
                endIndex = Math.max(braceEnd, bracketEnd);
            else if (braceEnd != -1)
                endIndex = braceEnd;
            else if (bracketEnd != -1)
                endIndex = bracketEnd;

            if (startIndex == -1 || endIndex == -1 || startIndex >= endIndex) {
                return null;
            }

            String jsonStr = aiReply.substring(startIndex, endIndex + 1);
            log.info("【AI聊天服务】解析到工具调用字符串: {}", jsonStr);

            Object parsed = JSON.parse(jsonStr);
            JSONObject json = null;

            if (parsed instanceof JSONArray) {
                JSONArray array = (JSONArray) parsed;
                if (!array.isEmpty()) {
                    json = array.getJSONObject(0);
                }
            } else if (parsed instanceof JSONObject) {
                json = (JSONObject) parsed;
            }

            if (json == null)
                return null;

            // 支持两种格式: {"tool": "name", ...} 和 {"name": "name", "parameters": {...}}
            String tool = json.getString("tool");
            if (tool == null) {
                tool = json.getString("name");
            }

            if (tool == null)
                return null;

            log.info("【AI聊天服务】正在执行工具查询: {}", tool);

            // 获取参数
            JSONObject params = json.getJSONObject("parameters");
            if (params == null) {
                params = json; // 如果没有 parameters 对象，则从根节点获取
            }

            // --- 权限控制逻辑 ---
            List<Integer> allowedLevels = null;
            if (isCustomerTool(tool)) {
                if (!canAccessCustomerData()) {
                    log.warn("【AI聊天服务】拦截越权查询 - 工具: {}, 用户无客户查询权限", tool);
                    return "❌ 权限拒绝：您当前的账号没有权限查询客户详细信息。请联系管理员开通[客户管理]权限。";
                }

                // 获取当前用户的权限等级
                try {
                    org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                            .getContext().getAuthentication();
                    String currentUsername;
                    Object principal = auth.getPrincipal();
                    if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                        currentUsername = ((org.springframework.security.core.userdetails.UserDetails) principal)
                                .getUsername();
                    } else {
                        currentUsername = principal.toString();
                    }
                    allowedLevels = getCustomerLevelPermission(currentUsername);
                } catch (Exception e) {
                    log.warn("获取用户权限失败，默认拒绝所有: {}", e.getMessage());
                    return "❌ 权限验证失败";
                }
            }
            // -------------------

            return switch (tool) {
                case "query_total_count" -> customerQueryService.getTotalCustomerCount(allowedLevels);
                case "query_by_region" ->
                    customerQueryService.getCustomersByRegion(params.getString("region"), allowedLevels);
                case "query_customer_detail" ->
                    customerQueryService.getCustomerDetail(params.getString("name"), allowedLevels);
                case "dynamic_sql_query" ->
                    customerQueryService.executeDynamicQuery(params.getString("sql"), allowedLevels);
                case "query_knowledge_count" -> knowledgeQueryService.getKnowledgeCount();
                case "query_knowledge_list" -> knowledgeQueryService
                        .getKnowledgeList(params.getInteger("limit") != null ? params.getInteger("limit") : 20);
                case "query_knowledge_detail" -> knowledgeQueryService.getKnowledgeDetail(params.getString("fileName"));
                case "query_knowledge_word_count" ->
                    knowledgeQueryService.getKnowledgeWordCount(params.getString("fileName"));
                default -> null; // 未知工具返回 null，表示不进行二次生成
            };
        } catch (Exception e) {
            log.error("【AI聊天服务】工具执行异常", e);
            return "服务暂时无法处理该查询：" + e.getMessage();
        }
    }

    private List<Integer> getCustomerLevelPermission(String username) {
        if ("admin".equals(username)) {
            return null; // All
        }
        com.aicustomer.entity.User user = userService.findByUsername(username);
        if (user == null)
            return java.util.Collections.emptyList();

        // 管理员看所有
        if (user.getUserType() != null && user.getUserType() == 1) {
            return null;
        }
        // 普通用户/业务员看普通客户(等级1)
        // TODO: 如果有更复杂的配置，可以从 permissionSettings 解析
        return java.util.Collections.singletonList(1);
    }

    /**
     * 获取当前登录用户的客户等级权限
     * 用于AI聊天时的数据权限控制
     */
    private List<Integer> getCurrentUserAllowedLevels() {
        try {
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication();

            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                log.warn("【权限控制】未认证用户，返回最低权限");
                return java.util.Collections.singletonList(1); // 只能看普通客户
            }

            String username;
            Object principal = auth.getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }

            log.info("【权限控制】当前用户: {}", username);
            return getCustomerLevelPermission(username);

        } catch (Exception e) {
            log.error("【权限控制】获取用户权限异常", e);
            return java.util.Collections.singletonList(1); // 异常时返回最低权限
        }
    }

    private boolean isCustomerTool(String toolName) {
        return "query_total_count".equals(toolName) ||
                "query_by_region".equals(toolName) ||
                "query_customer_detail".equals(toolName) ||
                "dynamic_sql_query".equals(toolName);
    }

    private boolean canAccessCustomerData() {
        try {
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return false;
            }

            String username;
            Object principal = auth.getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }

            // 超级管理员直接放行
            if ("admin".equals(username)) {
                return true;
            }

            // 查询数据库获取详细权限配置
            com.aicustomer.entity.User user = userService.findByUsername(username);
            if (user == null) {
                return false;
            }

            // 1. 检查用户类型 (1: 管理员)
            if (user.getUserType() != null && user.getUserType() == 1) {
                return true;
            }

            // 2. 检查具体的菜单权限配置 (permissionSettings JSON中包含 "customer")
            String perms = user.getPermissionSettings();
            if (perms != null && perms.contains("\"customer\"")) {
                return true;
            }

            // 3. 检查数据权限 (可选: 如果有更细粒度的 "canViewSensitive" 配置)
            if (perms != null && perms.contains("\"canViewSensitive\":true")) {
                return true;
            }

            return false;
        } catch (Exception e) {
            log.error("权限检查过程发生异常", e);
            return false; // 安全起见，异常时拒绝
        }
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

    /**
     * 判断是否应该触发Web Search fallback
     * 检测AI回复中是否包含"无法回答"的关键词
     */
    private boolean shouldTriggerWebSearch(String aiReply, String userMessage) {
        if (aiReply == null || aiReply.trim().isEmpty()) {
            return false;
        }

        // 检测关键词
        String[] keywords = {
                "不知道", "无法", "没有相关信息", "抱歉", "无权访问",
                "内部没有", "找不到", "不清楚", "无法提供"
        };

        String lowerReply = aiReply.toLowerCase();
        for (String keyword : keywords) {
            if (lowerReply.contains(keyword)) {
                log.debug("【Web Search Fallback】检测到关键词: {}", keyword);
                return true;
            }
        }

        return false;
    }

    /**
     * 执行Web Search fallback
     * 搜索后将结果再次发给AI整合
     */
    private String tryWebSearchFallback(String userMessage,
            List<Map<String, String>> history,
            FunctionCallRequest originalRequest) {
        try {
            // 1. 执行联网搜索
            log.info("【Web Search Fallback】开始搜索: {}", userMessage);
            String searchResults = webSearchService.searchAndFormat(userMessage);

            if (searchResults == null || searchResults.trim().isEmpty()) {
                log.warn("【Web Search Fallback】搜索结果为空");
                return "抱歉，我无法找到相关信息。";
            }

            // 2. 将搜索结果再次发给AI整合
            String enhancedMessage = userMessage +
                    "\n\n【联网搜索结果】\n" + searchResults +
                    "\n\n请根据上述搜索结果用自然语言回答用户的问题。";

            FunctionCallRequest fallbackRequest = FunctionCallRequest.builder()
                    .systemPrompt(originalRequest.getSystemPrompt())
                    .userMessage(enhancedMessage)
                    .history(history)
                    .functions(List.of()) // 不再需要函数调用
                    .build();

            FunctionCallResponse fallbackResponse = adapterManager.chatWithFallback(fallbackRequest);

            if (fallbackResponse.isSuccess() && fallbackResponse.getContent() != null) {
                log.info("【Web Search Fallback】AI成功整合搜索结果");
                return fallbackResponse.getContent();
            }

            // 3. 如果AI整合失败，直接返回搜索结果
            log.warn("【Web Search Fallback】AI整合失败，直接返回搜索结果");
            return "根据联网搜索，我找到了以下信息：\n\n" + searchResults;

        } catch (Exception e) {
            log.error("【Web Search Fallback】异常", e);
            return "抱歉，搜索过程中出现错误。";
        }
    }
}