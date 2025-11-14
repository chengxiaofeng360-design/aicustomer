package com.aicustomer.service;

import com.aicustomer.entity.AiChat;

import java.util.List;
import java.util.Map;

/**
 * AI聊天服务接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
public interface AiChatService {
    
    /**
     * 发送消息
     */
    AiChat sendMessage(String sessionId, String userMessage, Long customerId);
    
    /**
     * 发送消息（支持多轮对话历史）
     */
    AiChat sendMessage(String sessionId, String userMessage, Long customerId, List<Map<String, String>> history);
    
    /**
     * 获取聊天统计
     */
    Map<String, Object> getChatStatistics();
    
    /**
     * 获取聊天历史
     */
    Map<String, Object> getChatHistory(int pageNum, int pageSize);
}