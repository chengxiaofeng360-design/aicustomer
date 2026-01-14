package com.aicustomer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * 智谱AI GLM-4-Plus 聊天服务
 * 使用glm-4-plus模型，专门优化聊天性能
 * 
 * @author AI Customer Management System
 * @version 2.0.0
 */
@Service
@Slf4j
public class ZhipuPlusChatService {

    @Value("${ai-customer.ai.zhipu-chat.api-key}")
    private String apiKey;

    @Value("${ai-customer.ai.zhipu-chat.base-url}")
    private String baseUrl;

    @Value("${ai-customer.ai.zhipu-chat.model:glm-4-plus}")
    private String model;

    @Value("${ai-customer.ai.zhipu-chat.max-tokens:4000}")
    private int maxTokens;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 调用智谱AI进行对话（GLM-4-Plus聊天优化版）
     *
     * @param userMessage   用户消息
     * @param systemMessage 系统提示词
     * @return AI响应内容
     */
    public String chat(String userMessage, String systemMessage) {
        try {
            String url = baseUrl + "/chat/completions";

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);

            List<Map<String, String>> messages = new ArrayList<>();
            if (systemMessage != null && !systemMessage.isEmpty()) {
                Map<String, String> systemMsg = new HashMap<>();
                systemMsg.put("role", "system");
                systemMsg.put("content", systemMessage);
                messages.add(systemMsg);
            }

            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.add(userMsg);

            requestBody.put("messages", messages);
            requestBody.put("max_tokens", maxTokens);
            requestBody.put("temperature", 0.7);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.info("调用智谱AI Plus API: {}, 模型: {}", url, model);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode choices = rootNode.path("choices");

                if (choices.isArray() && choices.size() > 0) {
                    String content = choices.get(0).path("message").path("content").asText();
                    log.info("智谱AI Plus响应成功，内容长度: {}", content.length());
                    return content;
                }
            }

            log.error("智谱AI Plus响应异常: {}", response.getBody());
            throw new RuntimeException("智谱AI Plus调用失败");

        } catch (Exception e) {
            log.error("调用智谱AI Plus失败", e);
            throw new RuntimeException("智谱AI Plus调用失败: " + e.getMessage());
        }
    }

    /**
     * 检查服务是否可用
     */
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isEmpty() &&
                baseUrl != null && !baseUrl.isEmpty();
    }
}
