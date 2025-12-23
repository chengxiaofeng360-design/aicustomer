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
    
    /**
     * 获取会话列表
     * 
     * @param userId 用户ID（可选，如果为null则获取所有会话）
     * @param limit 限制数量
     * @return 会话列表
     */
    List<Map<String, Object>> getSessionList(Long userId, Integer limit);
    
    /**
     * 根据会话ID获取消息列表
     * 
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<AiChat> getMessagesBySessionId(String sessionId);
    
    /**
     * 创建新会话
     * 
     * @param userId 用户ID
     * @param customerId 客户ID（可选）
     * @return 新会话ID
     */
    String createNewSession(Long userId, Long customerId);
}