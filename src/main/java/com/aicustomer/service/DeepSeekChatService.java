package com.aicustomer.service;

import com.aicustomer.config.DeepSeekConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeepSeekChatService {

    private final DeepSeekConfig deepSeekConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean isAvailable() {
        return deepSeekConfig.getApiKey() != null && !deepSeekConfig.getApiKey().isEmpty();
    }

    public String chat(String userMessage, String systemPrompt) {
        try {
            String url = deepSeekConfig.getBaseUrl() + "/chat/completions";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", deepSeekConfig.getModel());

            List<Map<String, String>> messages = new ArrayList<>();
            if (systemPrompt != null && !systemPrompt.isEmpty()) {
                Map<String, String> systemMsg = new HashMap<>();
                systemMsg.put("role", "system");
                systemMsg.put("content", systemPrompt);
                messages.add(systemMsg);
            }

            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.add(userMsg);

            requestBody.put("messages", messages);
            requestBody.put("max_tokens", deepSeekConfig.getMaxTokens());
            requestBody.put("temperature", deepSeekConfig.getTemperature());
            requestBody.put("stream", false);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + deepSeekConfig.getApiKey());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.info("调用DeepSeek API: {}, Model: {}", url, deepSeekConfig.getModel());
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode choices = rootNode.path("choices");
                if (choices.isArray() && choices.size() > 0) {
                    return choices.get(0).path("message").path("content").asText();
                }
            }
            throw new RuntimeException("Empty response from DeepSeek");
        } catch (Exception e) {
            log.error("DeepSeek调用失败", e);
            throw new RuntimeException("DeepSeek调用失败: " + e.getMessage());
        }
    }
}
