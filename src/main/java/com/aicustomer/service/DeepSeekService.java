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
            log.warn("【DeepSeek】API Key未配置，返回默认回复");
            System.out.println("【DeepSeek】警告: API Key未配置");
            return "抱歉，AI服务暂未配置，请联系管理员。";
        }
        
        try {
            String url = deepSeekConfig.getBaseUrl() + "/chat/completions";
            log.info("【DeepSeek】开始调用API，URL: {}", url);
            System.out.println("【DeepSeek】开始调用API，URL: " + url);
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", deepSeekConfig.getModel());
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", deepSeekConfig.getMaxTokens());
            requestBody.put("temperature", deepSeekConfig.getTemperature());
            
            log.info("【DeepSeek】请求参数 - 模型: {}, 消息数: {}, max_tokens: {}, temperature: {}", 
                deepSeekConfig.getModel(), messages.size(), deepSeekConfig.getMaxTokens(), deepSeekConfig.getTemperature());
            System.out.println("【DeepSeek】请求参数 - 模型: " + deepSeekConfig.getModel() + 
                ", 消息数: " + messages.size() + ", max_tokens: " + deepSeekConfig.getMaxTokens());
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            // 发送请求
            log.info("【DeepSeek】发送HTTP请求...");
            System.out.println("【DeepSeek】发送HTTP请求...");
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
            );
            
            log.info("【DeepSeek】收到响应，状态码: {}", response.getStatusCode());
            System.out.println("【DeepSeek】收到响应，状态码: " + response.getStatusCode());
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("【DeepSeek】响应体长度: {}", response.getBody().length());
                System.out.println("【DeepSeek】响应体长度: " + response.getBody().length());
                
                // 解析响应
                JsonObject jsonResponse = gson.fromJson(response.getBody(), JsonObject.class);
                if (jsonResponse.has("choices") && jsonResponse.getAsJsonArray("choices").size() > 0) {
                    JsonObject choice = jsonResponse.getAsJsonArray("choices").get(0).getAsJsonObject();
                    JsonObject message = choice.getAsJsonObject("message");
                    String content = message.get("content").getAsString();
                    log.info("【DeepSeek】API调用成功，返回内容长度: {}", content.length());
                    System.out.println("【DeepSeek】API调用成功，返回内容长度: " + content.length());
                    return content;
                } else {
                    log.error("【DeepSeek】响应中没有choices字段或choices为空");
                    System.out.println("【DeepSeek】错误: 响应中没有choices字段或choices为空");
                    System.out.println("【DeepSeek】完整响应: " + response.getBody());
                }
            } else {
                log.error("【DeepSeek】API调用失败，状态码: {}, 响应: {}", response.getStatusCode(), response.getBody());
                System.out.println("【DeepSeek】错误: API调用失败");
                System.out.println("【DeepSeek】状态码: " + response.getStatusCode());
                System.out.println("【DeepSeek】响应内容: " + response.getBody());
            }
            
            return "抱歉，AI服务暂时无法响应，请稍后重试。";
            
        } catch (Exception e) {
            log.error("【DeepSeek】调用API异常: {}", e.getMessage(), e);
            System.out.println("【DeepSeek】异常: " + e.getMessage());
            System.out.println("【DeepSeek】异常类型: " + e.getClass().getName());
            e.printStackTrace();
            return "抱歉，AI服务调用异常，请稍后重试。错误信息: " + e.getMessage();
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

