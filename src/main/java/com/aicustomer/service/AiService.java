package com.aicustomer.service;

import java.util.List;
import java.util.Map;

/**
 * 统一的AI服务接口
 * 提供与Spring AI类似的抽象层，便于后续迁移到Spring AI
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
public interface AiService {
    
    /**
     * 检查服务是否可用
     * 
     * @return true if available
     */
    boolean isAvailable();
    
    /**
     * 单轮对话
     * 
     * @param userMessage 用户消息
     * @param systemPrompt 系统提示词（可选）
     * @return AI回复
     */
    String chat(String userMessage, String systemPrompt);
    
    /**
     * 多轮对话（带历史消息）
     * 
     * @param userMessage 用户消息
     * @param history 对话历史
     * @return AI回复
     */
    String chatWithHistory(String userMessage, List<Map<String, String>> history);
    
    /**
     * 结构化输出（将AI响应解析为指定类型）
     * 
     * @param userMessage 用户消息
     * @param systemPrompt 系统提示词
     * @param responseClass 响应类型
     * @param <T> 响应类型
     * @return 解析后的对象
     */
    <T> T chatForStructuredOutput(String userMessage, String systemPrompt, Class<T> responseClass);
}

