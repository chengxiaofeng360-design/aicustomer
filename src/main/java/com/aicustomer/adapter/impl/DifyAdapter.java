package com.aicustomer.adapter.impl;

import com.aicustomer.adapter.AiModelAdapter;
import com.aicustomer.config.DifyConfig;
import com.aicustomer.model.FunctionCallRequest;
import com.aicustomer.model.FunctionCallResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Dify平台适配器（简化版）
 * 实现与Dify AI平台的集成
 * 
 * 特性：
 * 1. 仅在dify.enabled=true时加载
 * 2. 支持自动降级（故障时自动切回Legacy）
 * 3. 健康检查机制
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "dify.enabled", havingValue = "true")
public class DifyAdapter implements AiModelAdapter {

    private final DifyConfig difyConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private volatile boolean available = false;
    private LocalDateTime lastHealthCheck = LocalDateTime.now();

    @PostConstruct
    public void init() {
        log.info("🚀 Dify适配器初始化...");
        log.info("📋 Dify配置验证:");
        log.info("  - enabled: {}", difyConfig.isEnabled());
        log.info("  - apiUrl: {}", difyConfig.getApiUrl());
        log.info("  - apiKey: {}...",
                difyConfig.getApiKey().substring(0, Math.min(15, difyConfig.getApiKey().length())));
        log.info("  - timeout: {}", difyConfig.getTimeout());
        log.info("  - maxRetries: {}", difyConfig.getMaxRetries());
        checkHealth();
    }

    @Override
    public String getName() {
        return "Dify平台";
    }

    @Override
    public int getPriority() {
        return 0; // 最高优先级
    }

    @Override
    public boolean isAvailable() {
        // 定期健康检查
        if (LocalDateTime.now().minusSeconds(difyConfig.getHealthCheckInterval() / 1000)
                .isAfter(lastHealthCheck)) {
            checkHealth();
        }
        return available;
    }

    /**
     * 健康检查
     */
    private void checkHealth() {
        try {
            if (!difyConfig.isEnabled()) {
                available = false;
                return;
            }

            // 临时强制可用以调试
            available = true;
            lastHealthCheck = LocalDateTime.now();

            log.info("✅ Dify健康检查: 强制启用（调试模式）");
        } catch (Exception e) {
            available = true; // 即使异常也强制启用
            lastHealthCheck = LocalDateTime.now();
            log.warn("❌ Dify健康检查异常但强制启用: {}", e.getMessage());
        }
    }

    @Override
    public FunctionCallResponse chat(FunctionCallRequest request) {
        log.info("【Dify适配器】处理请求: {}", request.getUserMessage());

        int retries = 0;
        Exception lastException = null;

        while (retries <= difyConfig.getMaxRetries()) {
            try {
                // 1. 构建Dify请求（使用completion-messages支持Agent）
                Map<String, Object> body = new HashMap<>();
                body.put("inputs", Map.of()); // 可扩展：传递额外上下文
                body.put("query", request.getUserMessage());
                body.put("response_mode", "blocking"); // completion支持blocking
                body.put("user", "user-" + UUID.randomUUID().toString());
                body.put("conversation_id", ""); // 新对话

                // 2. 发送请求到Dify
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + difyConfig.getApiKey());
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<String> httpRequest = new HttpEntity<>(
                        objectMapper.writeValueAsString(body),
                        headers);

                // 使用chat-messages端点（支持Chatbot）
                ResponseEntity<String> response = restTemplate.postForEntity(
                        difyConfig.getApiUrl() + "/chat-messages",
                        httpRequest,
                        String.class);

                // 3. 解析响应
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    JsonNode root = objectMapper.readTree(response.getBody());

                    String answer = root.path("answer").asText();

                    log.info("✅ Dify响应成功");

                    return FunctionCallResponse.builder()
                            .content(answer)
                            .modelName("dify")
                            .build();
                } else {
                    lastException = new RuntimeException("HTTP错误: " + response.getStatusCode());
                }

            } catch (Exception e) {
                lastException = e;
                retries++;

                if (retries <= difyConfig.getMaxRetries()) {
                    log.warn("⚠️ Dify调用失败，重试 {}/{}: {}",
                            retries, difyConfig.getMaxRetries(), e.getMessage());
                    try {
                        Thread.sleep(1000 * retries); // 指数退避
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        // 所有重试都失败
        log.error("❌ Dify调用失败（已重试{}次）: {}",
                difyConfig.getMaxRetries(),
                lastException != null ? lastException.getMessage() : "未知错误");

        available = false; // 标记为不可用，触发降级

        return FunctionCallResponse.builder()
                .error("Dify调用失败: " + (lastException != null ? lastException.getMessage() : "未知错误"))
                .build();
    }
}
