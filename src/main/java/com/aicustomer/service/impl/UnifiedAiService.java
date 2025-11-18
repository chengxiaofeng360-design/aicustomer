package com.aicustomer.service.impl;

import com.aicustomer.service.AiService;
import com.aicustomer.service.ArkChatService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 统一的AI服务实现
 * 提供类似Spring AI的抽象层，当前使用豆包（Ark）作为默认实现
 * 便于后续迁移到Spring AI框架
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Slf4j
@Service
public class UnifiedAiService implements AiService {
    
    @Autowired
    private ArkChatService arkChatService;
    
    private final Gson gson = new Gson();
    
    @Override
    public boolean isAvailable() {
        return arkChatService.isAvailable();
    }
    
    @Override
    public String chat(String userMessage, String systemPrompt) {
        if (!isAvailable()) {
            log.warn("【统一AI服务】豆包服务不可用");
            return null;
        }
        return arkChatService.chat(userMessage, systemPrompt);
    }
    
    @Override
    public String chatWithHistory(String userMessage, List<Map<String, String>> history) {
        if (!isAvailable()) {
            log.warn("【统一AI服务】豆包服务不可用");
            return null;
        }
        return arkChatService.chatWithHistory(userMessage, history);
    }
    
    @Override
    public <T> T chatForStructuredOutput(String userMessage, String systemPrompt, Class<T> responseClass) {
        String response = chat(userMessage, systemPrompt);
        if (response == null || response.trim().isEmpty()) {
            return null;
        }
        
        try {
            // 清理响应，提取JSON部分
            String cleanedResponse = cleanJsonResponse(response);
            return gson.fromJson(cleanedResponse, responseClass);
        } catch (Exception e) {
            log.error("【统一AI服务】结构化输出解析失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 清理AI返回的JSON响应，提取纯JSON内容
     */
    private String cleanJsonResponse(String rawResponse) {
        if (rawResponse == null) {
            return "";
        }
        
        String cleaned = rawResponse.trim();
        
        // 移除markdown代码块标记
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        
        cleaned = cleaned.trim();
        
        // 查找第一个 { 和最后一个 }
        int firstBrace = cleaned.indexOf('{');
        int lastBrace = cleaned.lastIndexOf('}');
        
        if (firstBrace >= 0 && lastBrace > firstBrace) {
            cleaned = cleaned.substring(firstBrace, lastBrace + 1);
        }
        
        return cleaned.trim();
    }
}

