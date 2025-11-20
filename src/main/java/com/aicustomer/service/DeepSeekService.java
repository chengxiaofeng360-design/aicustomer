package com.aicustomer.service;

import com.aicustomer.config.DeepSeekConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
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
            
        } catch (HttpClientErrorException e) {
            // 处理HTTP错误响应（如402余额不足、401认证失败等）
            org.springframework.http.HttpStatusCode statusCode = e.getStatusCode();
            String responseBody = e.getResponseBodyAsString();
            int statusValue = statusCode.value();
            
            log.error("【DeepSeek】API调用HTTP错误，状态码: {}, 响应: {}", statusValue, responseBody);
            System.out.println("【DeepSeek】HTTP错误，状态码: " + statusValue);
            System.out.println("【DeepSeek】响应内容: " + responseBody);
            
            // 特别处理402余额不足错误
            if (statusValue == 402) {
                try {
                    // 尝试解析错误响应，提取详细信息
                    JsonObject errorResponse = gson.fromJson(responseBody, JsonObject.class);
                    if (errorResponse.has("error")) {
                        JsonObject error = errorResponse.getAsJsonObject("error");
                        String errorMessage = error.has("message") ? error.get("message").getAsString() : "余额不足";
                        
                        if (errorMessage.contains("Insufficient Balance") || errorMessage.contains("余额不足")) {
                            return "⚠️ **DeepSeek账户余额不足**\n\n" +
                                   "您的DeepSeek API账户余额已用完，无法继续使用AI服务。\n\n" +
                                   "**解决方案：**\n" +
                                   "1. 登录DeepSeek平台（https://platform.deepseek.com）\n" +
                                   "2. 为账户充值\n" +
                                   "3. 充值后即可恢复正常使用\n\n" +
                                   "如有疑问，请联系系统管理员。";
                        }
                    }
                } catch (Exception parseException) {
                    // 解析失败，使用默认提示
                }
                return "⚠️ **DeepSeek账户余额不足**\n\n" +
                       "您的DeepSeek API账户余额已用完，请登录DeepSeek平台充值后继续使用。\n\n" +
                       "如需帮助，请联系系统管理员。";
            }
            
            // 处理其他HTTP错误
            if (statusValue == 401) {
                return "⚠️ **API认证失败**\n\n" +
                       "DeepSeek API密钥无效或已过期，请联系系统管理员更新API密钥。";
            }
            
            // 通用的HTTP错误提示
            String errorMsg = "DeepSeek API调用失败（状态码: " + statusValue + "）";
            try {
                JsonObject errorResponse = gson.fromJson(responseBody, JsonObject.class);
                if (errorResponse.has("error")) {
                    JsonObject error = errorResponse.getAsJsonObject("error");
                    if (error.has("message")) {
                        errorMsg = error.get("message").getAsString();
                    }
                }
            } catch (Exception parseException) {
                // 解析失败，使用默认提示
            }
            return "⚠️ **AI服务调用失败**\n\n" + errorMsg + "\n\n请稍后重试或联系系统管理员。";
            
        } catch (RestClientException e) {
            // 处理网络连接错误等
            log.error("【DeepSeek】网络连接错误: {}", e.getMessage(), e);
            System.out.println("【DeepSeek】网络连接错误: " + e.getMessage());
            return "⚠️ **网络连接失败**\n\n" +
                   "无法连接到DeepSeek API服务，请检查网络连接后重试。\n\n" +
                   "错误信息: " + e.getMessage();
                   
        } catch (Exception e) {
            log.error("【DeepSeek】调用API异常: {}", e.getMessage(), e);
            System.out.println("【DeepSeek】异常: " + e.getMessage());
            System.out.println("【DeepSeek】异常类型: " + e.getClass().getName());
            e.printStackTrace();
            return "⚠️ **AI服务调用异常**\n\n" +
                   "抱歉，AI服务调用时发生未知错误，请稍后重试。\n\n" +
                   "错误信息: " + e.getMessage();
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

