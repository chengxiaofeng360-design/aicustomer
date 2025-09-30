package com.aicustomer.service.impl;

import com.aicustomer.entity.AiChat;
import com.aicustomer.service.AiChatService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI聊天服务实现类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Service
public class AiChatServiceImpl implements AiChatService {
    
    @Override
    public AiChat sendMessage(String sessionId, String userMessage, Long customerId) {
        AiChat chat = new AiChat();
        chat.setId(System.currentTimeMillis());
        chat.setSessionId(sessionId);
        chat.setCustomerId(customerId);
        chat.setMessageType(1); // 1:用户消息
        chat.setContent(userMessage);
        chat.setReplyContent(generateAiResponse(userMessage));
        chat.setReplyTime(LocalDateTime.now());
        chat.setSatisfactionScore(0);
        chat.setCreateTime(LocalDateTime.now());
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
    
    private String generateAiResponse(String userMessage) {
        // 简单的AI回复生成逻辑
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