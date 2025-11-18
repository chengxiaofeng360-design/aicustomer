package com.aicustomer.service.impl;

import com.aicustomer.entity.AiChat;
import com.aicustomer.service.AiChatService;
import com.aicustomer.service.DeepSeekService;
import com.aicustomer.service.ArkChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    
    private final DeepSeekService deepSeekService;
    private final ArkChatService arkChatService;
    
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
        
        AiChat chat = new AiChat();
        chat.setId(System.currentTimeMillis());
        chat.setSessionId(sessionId);
        chat.setCustomerId(customerId);
        chat.setMessageType(1); // 1:用户消息
        chat.setContent(userMessage);
        
        // 使用DeepSeek生成AI回复（支持多轮对话）
        System.out.println("【AiChatService】开始生成AI回复...");
        String aiReply = generateAiResponse(userMessage, history);
        System.out.println("【AiChatService】AI回复生成完成，长度: " + (aiReply != null ? aiReply.length() : 0));
        
        if (aiReply == null || aiReply.trim().isEmpty()) {
            System.out.println("【AiChatService】警告: AI回复为空！");
            aiReply = "抱歉，AI服务返回了空回复，请检查后端日志。";
        }
        
        chat.setReplyContent(aiReply);
        chat.setReplyTime(LocalDateTime.now());
        chat.setSatisfactionScore(0);
        chat.setCreateTime(LocalDateTime.now());
        
        System.out.println("【AiChatService】返回AiChat对象，replyContent: " + (aiReply != null ? aiReply.substring(0, Math.min(100, aiReply.length())) + "..." : "null"));
        return chat;
    }
    
    public List<AiChat> getChatHistory(String sessionId, Long customerId) {
        // 模拟返回聊天历史
        List<AiChat> history = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            AiChat chat = new AiChat();
            chat.setId(System.currentTimeMillis() - i * 1000);
            chat.setSessionId(sessionId);
            chat.setCustomerId(customerId);
            chat.setMessageType(1); // 1:用户消息
            chat.setContent("用户消息 " + (i + 1));
            chat.setReplyContent("AI回复 " + (i + 1));
            chat.setReplyTime(LocalDateTime.now().minusMinutes(i * 10));
            chat.setSatisfactionScore(4 + i % 2);
            chat.setCreateTime(LocalDateTime.now().minusMinutes(i * 10));
            history.add(chat);
        }
        return history;
    }
    
    @Override
    public Map<String, Object> getChatStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMessages", 156);
        stats.put("satisfactionScore", 4.3);
        stats.put("avgResponseTime", 2.1);
        stats.put("activeSessions", 8);
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
        
        // 优先使用Ark（如果配置了Ark Key）
        if (arkChatService.isAvailable()) {
            System.out.println("【AI聊天服务】Ark服务可用，开始调用");
            try {
                String aiReply = arkChatService.chatWithHistory(userMessage, history);
                if (aiReply != null && !aiReply.trim().isEmpty()) {
                    log.info("【AI聊天服务】Ark生成回复成功，用户消息长度: {}, 回复长度: {}", userMessage.length(), aiReply.length());
                    System.out.println("【AI聊天服务】Ark生成回复成功，回复长度: " + aiReply.length());
                    return aiReply;
                }
            } catch (Exception e) {
                log.error("【AI聊天服务】Ark API调用失败，尝试DeepSeek: {}", e.getMessage(), e);
                System.out.println("【AI聊天服务】错误: Ark API调用失败");
                System.out.println("【AI聊天服务】错误信息: " + e.getMessage());
            }
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
        List<AiChat> history = new ArrayList<>();
        
        // 模拟聊天历史数据
        for (int i = 1; i <= pageSize; i++) {
            AiChat chat = new AiChat();
            chat.setId((long) i);
            chat.setSessionId("session-" + i);
            chat.setUserMessage("用户消息 #" + i);
            chat.setReplyContent("AI回复 #" + i);
            chat.setContent("AI回复 #" + i);
            chat.setCreateTime(LocalDateTime.now().minusHours(i));
            history.add(chat);
        }
        
        result.put("list", history);
        result.put("total", 200);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("pages", 20);
        
        return result;
    }
}