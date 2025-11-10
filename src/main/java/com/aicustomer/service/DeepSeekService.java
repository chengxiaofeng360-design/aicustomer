package com.aicustomer.service;

import com.aicustomer.config.DeepSeekConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * DeepSeek AI服务类
 * 
 * 封装DeepSeek API调用，提供聊天、分析、推荐等功能
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Slf4j
@Service
public class DeepSeekService {
    
    private final DeepSeekConfig deepSeekConfig;
    private final RestTemplate restTemplate;
    private final Gson gson;
    
    @Autowired
    public DeepSeekService(DeepSeekConfig deepSeekConfig) {
        this.deepSeekConfig = deepSeekConfig;
        this.restTemplate = new RestTemplate();
        this.gson = new Gson();
    }
    
    /**
     * 调用DeepSeek API生成回复
     * 
     * @param messages 对话消息列表
     * @return AI生成的回复内容
     */
    public String generateResponse(List<Map<String, String>> messages) {
        String apiKey = deepSeekConfig.getApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("DeepSeek API Key未配置，返回默认回复");
            return "抱歉，AI服务暂未配置，请联系管理员。";
        }
        
        try {
            String url = deepSeekConfig.getBaseUrl() + "/chat/completions";
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", deepSeekConfig.getModel());
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", deepSeekConfig.getMaxTokens());
            requestBody.put("temperature", deepSeekConfig.getTemperature());
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            // 发送请求
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // 解析响应
                JsonObject jsonResponse = gson.fromJson(response.getBody(), JsonObject.class);
                if (jsonResponse.has("choices") && jsonResponse.getAsJsonArray("choices").size() > 0) {
                    JsonObject choice = jsonResponse.getAsJsonArray("choices").get(0).getAsJsonObject();
                    JsonObject message = choice.getAsJsonObject("message");
                    String content = message.get("content").getAsString();
                    log.debug("DeepSeek API调用成功，返回内容长度: {}", content.length());
                    return content;
                }
            }
            
            log.error("DeepSeek API调用失败，响应: {}", response.getBody());
            return "抱歉，AI服务暂时无法响应，请稍后重试。";
            
        } catch (Exception e) {
            log.error("调用DeepSeek API异常: {}", e.getMessage(), e);
            return "抱歉，AI服务调用异常，请稍后重试。";
        }
    }
    
    /**
     * 单轮对话
     * 
     * @param userMessage 用户消息
     * @param systemPrompt 系统提示词（可选）
     * @return AI回复
     */
    public String chat(String userMessage, String systemPrompt) {
        List<Map<String, String>> messages = new ArrayList<>();
        
        if (systemPrompt != null && !systemPrompt.trim().isEmpty()) {
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);
            messages.add(systemMsg);
        }
        
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);
        
        return generateResponse(messages);
    }
    
    /**
     * 多轮对话（带历史上下文）
     * 
     * @param messages 完整的对话历史（包括系统提示、用户消息、AI回复）
     * @return AI回复
     */
    public String chatWithHistory(List<Map<String, String>> messages) {
        return generateResponse(messages);
    }
    
    /**
     * 获取API Key（用于检查配置）
     */
    public String getApiKey() {
        return deepSeekConfig.getApiKey();
    }
    
    /**
     * 检查服务是否可用
     */
    public boolean isAvailable() {
        String apiKey = deepSeekConfig.getApiKey();
        return apiKey != null && !apiKey.trim().isEmpty();
    }
}

