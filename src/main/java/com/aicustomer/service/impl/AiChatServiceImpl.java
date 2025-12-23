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
import com.aicustomer.service.VectorSearchService;
import com.aicustomer.service.CustomerQueryService;
import com.aicustomer.service.ZhipuChatService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final FaqQaService faqQaService;
    private final KnowledgeDocumentService knowledgeDocumentService;
    private final VectorSearchService vectorSearchService;
    private final CustomerQueryService customerQueryService;
    private final KnowledgeQueryService knowledgeQueryService;
    private final ZhipuChatService zhipuChatService;

    @Qualifier("doubaoChatModel")
    private final ChatModel doubaoChatModel;

    // 系统提示词，定义AI助手的角色和行为
    private static final String SYSTEM_PROMPT = "你是一个专业的AI客户管理助手。" +
            "你的职责是帮助用户解答关于客户管理、业务分析等问题。" +
            "\n\n### 极其重要的回复原则 (面向非技术用户) ###\n" +
            "1. **禁止使用技术术语**：在回复用户时，严禁使用“数据库”、“表”、“字段”、“列”、“SQL”、“代码”、“JSON”、“null”、“表结构”等词汇。用户不知道这些是什么。\n" +
            "2. **身份定位**：你是一个贴心的业务助手，不是一个编程接口。你的回复应该像真人一样自然。\n" +
            "3. **强制工具调用**：只要用户询问涉及客户的数量、名单、具体信息，你**必须**生成查询指令，**严禁凭历史对话或记忆猜测数据**。哪怕你刚查过，也请再次生成指令以确保准确。\n" +
            "4. **自然解释缺失信息**：如果用户询问的信息在系统中没有记录（如性别、年龄等），请礼貌地自然解释，例如：“抱歉，我们的系统中目前没有这些信息。” 而不是说“表中没有这个字段”。\n" +
            "5. **数据展示**：拿到数据后，请直接以自然语言或清晰的表格告知用户结果，不要提及是如何查询到的。\n" +
            "\n\n### 数据库查询工具说明 (仅供你内部使用) ###\n" +
            "当用户需要查询客户数据（如统计、筛选、详情）时，你必须根据以下结构生成指令，但**不要在回复中提到这些结构**。\n" +
            "**可用信息：**\n" +
            "- `customer_name` (客户名称/企业名)\n" +
            "- `contact_person` (联系人)\n" +
            "- `region` (地区，如北京、上海)\n" +
            "- `customer_level` (等级: 1-普通, 2-VIP, 3-钻石)\n" +
            "- `business_type` (业务: 1-品种权申请, 2-品种权转化, 3-知识产权协作, 4-科普教育, 5-景观设计, 6-图书出版)\n" +
            "- `status` (状态: 1-正常, 2-冻结, 3-注销)\n" +
            "- `create_time` (创建时间)\n" +
            "- `remark` (备注)\n" +
            "\n**可用资料信息 (知识库/上传资料)：**\n" +
            "- `title` (文档标题)\n" +
            "- `file_name` (原始文件名)\n" +
            "- `file_type` (文件类型: pdf, word, excel, txt)\n" +
            "- `category` (分类)\n" +
            "- `summary` (摘要)\n" +
            "\n**工具调用格式 (内部指令)：**\n" +
            "1. 客户查询: `{\"tool\": \"dynamic_sql_query\", \"sql\": \"SELECT ... FROM customer WHERE ...\"}`\n" +
            "2. 资料统计: `{\"tool\": \"query_knowledge_count\"}`\n" +
            "3. 资料清单: `{\"tool\": \"query_knowledge_list\", \"parameters\": {\"limit\": 20}}`\n" +
            "\n**操作原则：**\n" +
            "1. 只要涉及客户数量、具体客户信息，必须使用 `dynamic_sql_query`。\n" +
            "2. 只要涉及**上传资料的数量、有哪些文件、文件清单**，必须调用 `query_knowledge_count` 或 `query_knowledge_list`。\n" +
            "3. 拿到数据后，整合为自然、友好的回复。不要提到工具名。";

    @Override
    public AiChat sendMessage(String sessionId, String userMessage, Long customerId) {
        return sendMessage(sessionId, userMessage, customerId, null);
    }

    @Override
    public AiChat sendMessage(String sessionId, String userMessage, Long customerId,
            List<Map<String, String>> history) {
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
    private String generateAiResponse(String userMessage, List<Map<String, String>> history) {
        System.out.println("【AI聊天服务】开始生成AI回复");
        System.out.println("【AI聊天服务】用户消息: " + userMessage);

        // 1. 第一层：FAQ匹配
        try {
            List<FaqQa> faqs = faqQaService.searchFaq(userMessage, 1);
            if (faqs != null && !faqs.isEmpty()) {
                FaqQa faq = faqs.get(0);
                // 简单判断：如果包含关键词或问题长度相似，认为匹配成功
                // 实际生产中应该有更复杂的相似度计算
                log.info("【AI聊天服务】命中FAQ: {}", faq.getQuestion());
                System.out.println("【AI聊天服务】命中FAQ: " + faq.getQuestion());
                faqQaService.incrementHitCount(faq.getId());
                return faq.getAnswer() + "\n\n(来源: 常见问题库)";
            }
        } catch (Exception e) {
            log.error("【AI聊天服务】FAQ匹配失败: {}", e.getMessage());
        }

        // 2. 第二层A：向量搜索（语义匹配，更精准）
        try {
            if (vectorSearchService.isAvailable()) {
                log.info("【AI聊天服务】开始向量搜索，查询: {}", userMessage);
                List<Map<String, Object>> vectorResults = vectorSearchService.searchByVector(userMessage, 3);

                if (vectorResults != null && !vectorResults.isEmpty()) {
                    // 获取第一个高相似度结果
                    Map<String, Object> topResult = vectorResults.get(0);
                    double score = (Double) topResult.getOrDefault("score", 0.0);
                    String title = (String) topResult.get("title");
                    String content = (String) topResult.get("content");

                    // 相似度阈值设为0.65，同时检查内容是否真的包含查询关键词
                    boolean contentContainsQuery = (content != null
                            && content.toLowerCase().contains(userMessage.toLowerCase())) ||
                            (title != null && title.toLowerCase().contains(userMessage.toLowerCase()));

                    // 高相似度(>0.65)直接返回，或者中等相似度(>0.55)且内容包含关键词也返回
                    if (score > 0.65 || (score > 0.55 && contentContainsQuery)) {
                        log.info("【AI聊天服务】向量搜索命中文档，score: {}, title: {}, 包含关键词: {}",
                                score, title, contentContainsQuery);

                        StringBuilder answer = new StringBuilder();
                        answer.append("**").append(title).append("**\n\n");
                        if (content != null && content.length() > 2000) {
                            answer.append(content.substring(0, 2000)).append("...\n\n*（内容较长，已截取）*");
                        } else if (content != null) {
                            answer.append(content);
                        }
                        answer.append("\n\n(来源: 知识库向量搜索，相似度: ").append(String.format("%.2f", score)).append(")");
                        return answer.toString();
                    }

                    log.info("【AI聊天服务】向量搜索结果相似度较低({})或不包含关键词, 继续使用全文搜索", score);
                }
            } else {
                log.info("【AI聊天服务】向量搜索服务不可用，使用MySQL全文搜索");
            }
        } catch (Exception e) {
            log.warn("【AI聊天服务】向量搜索失败，回退到MySQL: {}", e.getMessage());
        }

        // 2. 第二层B：知识库MySQL全文搜索（作为向量搜索的回退）
        StringBuilder contextBuilder = new StringBuilder();
        try {
            log.info("【AI聊天服务】开始全文搜索知识库，关键词: {}", userMessage);
            List<KnowledgeDocument> documents = knowledgeDocumentService.searchDocuments(userMessage, 3);
            log.info("【AI聊天服务】知识库搜索结果数量: {}", documents != null ? documents.size() : 0);

            if (documents != null && !documents.isEmpty()) {
                System.out.println("【AI聊天服务】搜索到知识库文档: " + documents.size() + "篇");

                // 检查是否有高度匹配的文档（标题包含关键词或内容开头包含关键词）
                for (KnowledgeDocument doc : documents) {
                    log.info("【AI聊天服务】找到文档: id={}, title={}, contentLength={}",
                            doc.getId(), doc.getTitle(), doc.getContent() != null ? doc.getContent().length() : 0);

                    String title = doc.getTitle() != null ? doc.getTitle().toLowerCase() : "";
                    String content = doc.getContent() != null ? doc.getContent() : "";
                    String searchTermLower = userMessage.toLowerCase();

                    // 如果标题高度匹配，直接返回知识库内容
                    if (title.contains(searchTermLower)
                            || searchTermLower.contains(title.replace("是什么", "").replace("什么是", ""))) {
                        log.info("【AI聊天服务】找到高度匹配的知识库文档，直接返回: {}", doc.getTitle());
                        knowledgeDocumentService.incrementViewCount(doc.getId());

                        // 格式化返回内容
                        StringBuilder answer = new StringBuilder();
                        answer.append("**").append(doc.getTitle()).append("**\n\n");

                        // 返回完整内容（如果内容过长则截取）
                        if (content.length() > 2000) {
                            answer.append(content.substring(0, 2000)).append("...\n\n*（内容较长，已截取）*");
                        } else {
                            answer.append(content);
                        }
                        answer.append("\n\n(来源: 知识库文档)");
                        return answer.toString();
                    }
                }

                // 如果没有高度匹配，构建上下文供AI参考
                contextBuilder.append("以下是参考资料：\n");
                for (int i = 0; i < documents.size(); i++) {
                    KnowledgeDocument doc = documents.get(i);
                    contextBuilder.append(i + 1).append(". ").append(doc.getTitle()).append("：\n");
                    // 截取部分内容作为上下文，避免Token过长
                    String content = doc.getContent();
                    if (content.length() > 500) {
                        content = content.substring(0, 500) + "...";
                    }
                    contextBuilder.append(content).append("\n\n");

                    // 增加查看次数
                    knowledgeDocumentService.incrementViewCount(doc.getId());
                }
            } else {
                log.info("【AI聊天服务】知识库中未找到相关文档");
            }
        } catch (Exception e) {
            log.error("【AI聊天服务】知识库搜索失败: {}", e.getMessage(), e);
        }

        String context = contextBuilder.toString();
        String finalSystemPrompt = SYSTEM_PROMPT;

        if (!context.isEmpty()) {
            finalSystemPrompt += "\n\n" + context + "\n请根据上述参考资料回答用户问题。如果参考资料中没有相关信息，请利用你的通用知识回答。";
        }

        // 3. 第三层：AI生成
        // 优先级顺序：DeepSeek -> 豆包 (Spring AI) -> 智谱 (Zhipu)

        // A. 优先尝试 DeepSeek
        if (deepSeekService.isAvailable()) {
            System.out.println("【AI聊天服务】优先使用 DeepSeek 服务");
            try {
                String aiReply;
                if (history != null && !history.isEmpty()) {
                    List<Map<String, String>> messages = new ArrayList<>();
                    Map<String, String> systemMsg = new HashMap<>();
                    systemMsg.put("role", "system");
                    systemMsg.put("content", finalSystemPrompt);
                    messages.add(systemMsg);

                    int historySize = Math.min(history.size(), 20);
                    for (int i = Math.max(0, history.size() - historySize); i < history.size(); i++) {
                        messages.add(history.get(i));
                    }

                    Map<String, String> userMsg = new HashMap<>();
                    userMsg.put("role", "user");
                    userMsg.put("content", userMessage);
                    messages.add(userMsg);
                    aiReply = deepSeekService.chatWithHistory(messages);
                } else {
                    aiReply = deepSeekService.chat(userMessage, finalSystemPrompt);
                }

                if (aiReply != null && !aiReply.trim().isEmpty()) {
                    aiReply = processPotentialToolCall(aiReply, userMessage, finalSystemPrompt, "deepseek");
                    if (!context.isEmpty() && !aiReply.contains("(来源:")) {
                        aiReply += "\n\n(来源: 知识库智能生成 - DeepSeek)";
                    }
                    return aiReply;
                }
            } catch (Exception e) {
                log.warn("【AI聊天服务】DeepSeek 调用失败，准备回退到豆包: {}", e.getMessage());
            }
        }

        // B. 其次尝试豆包 (Spring AI)
        try {
            System.out.println("【AI聊天服务】回退使用 Spring AI 豆包模型");
            List<org.springframework.ai.chat.messages.Message> messages = new ArrayList<>();
            messages.add(new org.springframework.ai.chat.messages.SystemMessage(finalSystemPrompt));

            if (history != null && !history.isEmpty()) {
                for (Map<String, String> historyItem : history) {
                    String role = historyItem.get("role");
                    String content = historyItem.get("content");
                    if (content != null && !content.trim().isEmpty()) {
                        if ("user".equals(role))
                            messages.add(new org.springframework.ai.chat.messages.UserMessage(content));
                        else if ("assistant".equals(role))
                            messages.add(new org.springframework.ai.chat.messages.AssistantMessage(content));
                    }
                }
            }
            messages.add(new org.springframework.ai.chat.messages.UserMessage(userMessage));

            ChatResponse chatResponse = doubaoChatModel.call(new Prompt(messages));
            String aiReply = chatResponse.getResult().getOutput().getContent();

            if (aiReply != null && !aiReply.trim().isEmpty()) {
                aiReply = processPotentialToolCall(aiReply, userMessage, finalSystemPrompt, "doubao");
                if (!context.isEmpty() && !aiReply.contains("(来源:")) {
                    aiReply += "\n\n(来源: 知识库智能生成 - 豆包)";
                }
                return aiReply;
            }
        } catch (Exception e) {
            log.warn("【AI聊天服务】豆包模型调用失败，准备回退到智谱: {}", e.getMessage());
        }

        // C. 最后尝试智谱 (Zhipu AI)
        if (zhipuChatService.isAvailable()) {
            try {
                System.out.println("【AI聊天服务】回退使用智谱 AI 服务");
                String aiReply = zhipuChatService.chat(userMessage, finalSystemPrompt);
                if (aiReply != null && !aiReply.trim().isEmpty()) {
                    aiReply = processPotentialToolCall(aiReply, userMessage, finalSystemPrompt, "zhipu");
                    if (!context.isEmpty() && !aiReply.contains("(来源:")) {
                        aiReply += "\n\n(来源: 知识库智能生成 - 智谱)";
                    }
                    return aiReply;
                }
            } catch (Exception e) {
                log.error("【AI聊天服务】所有 AI 服务调用均失败: {}", e.getMessage());
            }
        }

        // 4. 最终回退：规则匹配
        return generateFallbackResponse(userMessage);
    }

    /**
     * 检查并处理 AI 回复中可能存在的工具调用
     */
    private String processPotentialToolCall(String aiReply, String userMessage, String systemPrompt, String modelType) {
        if (aiReply.contains("{") && aiReply.contains("}")) {
            String toolResult = executeTool(aiReply);
            if (toolResult != null) {
                log.info("【AI聊天服务】工具结果获取成功 ({})，进行二次生成...", modelType);
                String followUpMessage = userMessage + "\n\n(工具查询结果: " + toolResult + ", 请整合并回复)";

                return switch (modelType) {
                    case "deepseek" -> deepSeekService.chat(followUpMessage, systemPrompt);
                    case "doubao" -> {
                        List<org.springframework.ai.chat.messages.Message> messages = new ArrayList<>();
                        messages.add(new org.springframework.ai.chat.messages.SystemMessage(systemPrompt));
                        messages.add(new org.springframework.ai.chat.messages.UserMessage(followUpMessage));
                        ChatResponse resp = doubaoChatModel.call(new Prompt(messages));
                        yield resp.getResult().getOutput().getContent();
                    }
                    case "zhipu" -> zhipuChatService.chat(followUpMessage, systemPrompt);
                    default -> aiReply;
                };
            }
        }
        return aiReply;
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
        String newSessionId = "session_" + System.currentTimeMillis() + "_"
                + (userId != null ? userId : DEFAULT_USER_ID);
        log.info("创建新会话: sessionId={}, userId={}, customerId={}", newSessionId, userId, customerId);
        return newSessionId;
    }

    /**
     * 执行 AI 调用的工具
     */
    private String executeTool(String aiReply) {
        try {
            // 提取 JSON 部分
            int startIndex = aiReply.indexOf("{");
            int endIndex = aiReply.lastIndexOf("}");
            if (startIndex == -1 || endIndex == -1 || startIndex >= endIndex) {
                return null;
            }

            String jsonStr = aiReply.substring(startIndex, endIndex + 1);
            log.info("【AI聊天服务】解析到工具调用 JSON: {}", jsonStr);
            JSONObject json = JSON.parseObject(jsonStr);

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

            return switch (tool) {
                case "query_total_count" -> customerQueryService.getTotalCustomerCount();
                case "query_by_region" -> customerQueryService.getCustomersByRegion(params.getString("region"));
                case "query_customer_detail" -> customerQueryService.getCustomerDetail(params.getString("name"));
                case "dynamic_sql_query" -> customerQueryService.executeDynamicQuery(params.getString("sql"));
                case "query_knowledge_count" -> knowledgeQueryService.getKnowledgeCount();
                case "query_knowledge_list" -> knowledgeQueryService
                        .getKnowledgeList(params.getInteger("limit") != null ? params.getInteger("limit") : 20);
                default -> null; // 未知工具返回 null，表示不进行二次生成
            };
        } catch (Exception e) {
            log.error("【AI聊天服务】工具执行异常", e);
            return "服务暂时无法处理该查询：" + e.getMessage();
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