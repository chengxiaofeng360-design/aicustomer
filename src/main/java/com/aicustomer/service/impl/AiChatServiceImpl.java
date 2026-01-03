package com.aicustomer.service.impl;

import com.aicustomer.entity.AiChat;
import com.aicustomer.entity.FaqQa;
import com.aicustomer.entity.KnowledgeDocument;
import com.aicustomer.mapper.AiChatMapper;
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

    @Qualifier("doubaoChatModel")
    private final ChatModel doubaoChatModel;

    // 系统提示词，定义AI助手的角色和行为
    private static final String SYSTEM_PROMPT = "你是一个专业的AI客户管理助手。" +
            "\n\n### 极其重要的回复原则 ###\n" +
            "1. **强制工具调用 (核心指令)**：你本身并不了解系统的任何实时数据。只要用户的问题涉及“有多少文件”、“有哪些资料”、“查看文件内容”、“查找客户”、“统计数据”等，你**必须且仅能**通过调用下方定义的 JSON 工具指令来获取。**严禁凭猜测回答。**\n"
            +
            "2. **严禁解释过程**：你的回复中不要提到“我在查表”、“调用接口”等。用户只需要答案。\n" +
            "3. **先查后说**：对于任何关于“资料/清单/详情”的问题，必须先输出 JSON 工具指令。\n" +
            "\n\n### 数据库查询工具说明 ###\n" +
            "**可用资料信息 (知识库)：**\n" +
            "- `query_knowledge_count`: 用于统计当前上传的文件总数。\n" +
            "- `query_knowledge_list`: 用于获取已上传文件的列表。\n" +
            "- `query_knowledge_detail`: 用于获取特定文件的详细全文内容。参数: {\"fileName\": \"文件名或标题\"}\n" +
            "- `query_knowledge_word_count`: 用于统计特定文件的字数/字符数。参数: {\"fileName\": \"文件名或标题\"}\n" +
            "\n**可用客户信息：**\n" +
            "- `dynamic_sql_query`: **仅用于查询客户数据** (数据源是 customer 表)。禁止用于查询知识库文档内容。\n" +
            "\n**客户表 (customer) 字段说明：**\n" +
            "- `customer_name`: 客户名称\n" +
            "- `region`: 地区（如\"北京\"、\"上海\"、\"广东\"等）\n" +
            "- `contact_person`: 联系人\n" +
            "- `phone`: 电话\n" +
            "- `customer_type`: 客户类型 (1=个人, 2=企业, 3=科研院所)\n" +
            "- `customer_level`: 客户等级 (1=普通, 2=VIP, 3=钻石)\n" +
            "- `business_type`: 业务类型 (1-6)\n" +
            "- `progress`: 进度 (0=未开始, 1=进行中, 2=暂停, 3=已成功, 4=放弃)\n" +
            "\n**SQL 查询示例：**\n" +
            "- 查询北京客户数量: `SELECT COUNT(*) FROM customer WHERE region = '北京'`\n" +
            "- 查询VIP客户: `SELECT customer_name, contact_person FROM customer WHERE customer_level = 2`\n" +
            "- 查询进行中的项目: `SELECT customer_name, region FROM customer WHERE progress = 1`\n" +
            "\n**工具调用格式：**\n" +
            "{\n" +
            "  \"tool\": \"工具名\",\n" +
            "  \"parameters\": { \"key\": \"value\" }\n" +
            "}\n" +
            "\n**操作原则：**\n" +
            "1. 用户问“有哪些资料”、“上传了什么”，用 `{\"tool\": \"query_knowledge_list\"}`。\n" +
            "2. 用户问“有多少个文件”，用 `{\"tool\": \"query_knowledge_count\"}`。\n" +
            "3. 用户问“某某文件的内容是什么”、“查看某某文件”，用 `{\"tool\": \"query_knowledge_detail\", \"parameters\": {\"fileName\": \"xxx\"}}`。\n"
            +
            "4. 用户问“某某文件有多少字”，用 `{\"tool\": \"query_knowledge_word_count\", \"parameters\": {\"fileName\": \"xxx\"}}`。\n"
            +
            "5. 用户问客户相关问题（如“北京客户有几个”、“VIP客户有哪些”），用 `{\"tool\": \"dynamic_sql_query\", \"parameters\": {\"sql\": \"SELECT COUNT(*) FROM customer WHERE region = '北京'\"}}`。\n"
            +
            "6. **Important Terminology**: `用户` (User) and `客户` (Customer) are often used interchangeably. If the user asks about '用户' (users), assume they mean '客户' (customers) and query the `customer` table, unless they explicitly ask for 'system accounts' or 'admins'.\n"
            +
            "7. **重要**：查询地区时必须使用 `region` 字段，不要使用 `location`。\n" +
            "8. 只有当工具返回数据后，才整合为自然语言回复。";

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

    // ...

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
}