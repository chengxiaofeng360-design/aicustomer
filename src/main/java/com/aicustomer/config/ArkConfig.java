package com.aicustomer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
public class ArkConfig {

    @Value("${ai-customer.ai.ark.api-key:}")
    private String apiKey;

    @Value("${ai-customer.ai.ark.base-url:https://ark.cn-beijing.volces.com/api/v3}")
    private String baseUrl;

    @Value("${ai-customer.ai.ark.model:doubao-seed-1-6-251015}")
    private String model;

    @Value("${ai-customer.ai.ark.timeout:30000}")
    private long timeout;

    @PostConstruct
    public void init() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("Ark API Key未配置，Ark聊天功能将不可用。");
        } else {
            log.info("Ark配置加载成功，模型: {}, 端点: {}", model, baseUrl);
        }
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getModel() {
        return model;
    }

    public long getTimeout() {
        return timeout;
    }
}


