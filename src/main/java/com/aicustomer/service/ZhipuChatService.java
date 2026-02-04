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
 * 智谱AI GLM-4-Flash服务
 * 提供免费的AI文本理解和信息提取能力
 */
@Service
@Slf4j
public class ZhipuChatService {

    @Value("${ai-customer.ai.zhipu.api-key}")
    private String apiKey;

    @Value("${ai-customer.ai.zhipu.base-url}")
    private String baseUrl;

    @Value("${ai-customer.ai.zhipu.model:glm-4-flash}")
    private String model;

    @Value("${ai-customer.ai.zhipu.max-tokens:2000}")
    private int maxTokens;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 调用智谱AI进行对话
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

            log.info("调用智谱AI API: {}", url);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode choices = rootNode.path("choices");

                if (choices.isArray() && choices.size() > 0) {
                    String content = choices.get(0).path("message").path("content").asText();
                    log.info("智谱AI响应成功，内容长度: {}", content.length());
                    return content;
                }
            }

            log.error("智谱AI响应异常: {}", response.getBody());
            throw new RuntimeException("智谱AI调用失败");

        } catch (Exception e) {
            log.error("调用智谱AI失败", e);
            throw new RuntimeException("智谱AI调用失败: " + e.getMessage());
        }
    }

    /**
     * 调用智谱AI进行多模态对话（图文识别）
     *
     * @param userMessage   用户消息
     * @param imageBase64   图片Base64编码
     * @param systemMessage 系统提示词
     * @return AI响应内容
     */
    public String chatWithImage(String userMessage, String imageBase64, String systemMessage) {
        try {
            String url = baseUrl + "/chat/completions";

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);

            List<Map<String, Object>> messages = new ArrayList<>();

            // 系统消息
            if (systemMessage != null && !systemMessage.isEmpty()) {
                Map<String, Object> systemMsg = new HashMap<>();
                systemMsg.put("role", "system");
                systemMsg.put("content", systemMessage);
                messages.add(systemMsg);
            }

            // 用户消息（构建多模态内容）
            Map<String, Object> userMsg = new HashMap<>();
            userMsg.put("role", "user");

            List<Map<String, Object>> contentList = new ArrayList<>();

            // 文本部分
            Map<String, Object> textPart = new HashMap<>();
            textPart.put("type", "text");
            textPart.put("text", userMessage);
            contentList.add(textPart);

            // 图片部分
            Map<String, Object> imagePart = new HashMap<>();
            imagePart.put("type", "image_url");
            Map<String, String> imageUrl = new HashMap<>();
            // 智谱/OpenAI格式需要前缀，如果已经是完整的data:image...则不用加，这里假设传入的是纯base64
            String finalImage = imageBase64;
            if (!imageBase64.startsWith("data:image")) {
                finalImage = "data:image/jpeg;base64," + imageBase64;
            }
            imageUrl.put("url", finalImage);
            imagePart.put("image_url", imageUrl);
            contentList.add(imagePart);

            userMsg.put("content", contentList);
            messages.add(userMsg);

            requestBody.put("messages", messages);
            requestBody.put("max_tokens", maxTokens);
            requestBody.put("temperature", 0.5); // 图文识别通常需要更准确，降低温度

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.info("调用智谱AI多模态 API: {}", url);
            // 注意：图片Base64可能很长，日志不要打印完整body

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode choices = rootNode.path("choices");

                if (choices.isArray() && choices.size() > 0) {
                    String content = choices.get(0).path("message").path("content").asText();
                    log.info("智谱AI多模态响应成功，内容长度: {}", content.length());
                    return content;
                }
            }

            log.error("智谱AI多模态响应异常: {}", response.getBody());
            throw new RuntimeException("智谱AI调用失败");

        } catch (Exception e) {
            log.error("调用智谱AI多模态失败", e);
            throw new RuntimeException("智谱AI调用失败: " + e.getMessage());
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
