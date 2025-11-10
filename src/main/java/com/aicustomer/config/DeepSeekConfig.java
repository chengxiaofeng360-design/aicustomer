package com.aicustomer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * DeepSeek AI配置类
 * 
 * DeepSeek API兼容OpenAI API，通过HTTP直接调用
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Slf4j
@Configuration
public class DeepSeekConfig {
    
    @Value("${ai-customer.ai.deepseek.api-key:}")
    private String apiKey;
    
    @Value("${ai-customer.ai.deepseek.base-url:https://api.deepseek.com/v1}")
    private String baseUrl;
    
    @Value("${ai-customer.ai.deepseek.model:deepseek-chat}")
    private String model;
    
    @Value("${ai-customer.ai.deepseek.timeout:30000}")
    private long timeout;
    
    @Value("${ai-customer.ai.deepseek.max-tokens:2000}")
    private int maxTokens;
    
    @Value("${ai-customer.ai.deepseek.temperature:0.7}")
    private double temperature;
    
    @PostConstruct
    public void init() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("DeepSeek API Key未配置，AI功能将无法使用。请设置环境变量DEEPSEEK_API_KEY或在配置文件中配置ai-customer.ai.deepseek.api-key");
        } else {
            log.info("DeepSeek配置加载成功，模型: {}, 端点: {}", model, baseUrl);
        }
    }
    
    /**
     * 获取配置的模型名称
     */
    public String getModel() {
        return model;
    }
    
    /**
     * 获取最大token数
     */
    public int getMaxTokens() {
        return maxTokens;
    }
    
    /**
     * 获取温度参数
     */
    public double getTemperature() {
        return temperature;
    }
    
    /**
     * 获取API端点
     */
    public String getBaseUrl() {
        return baseUrl;
    }
    
    /**
     * 获取API Key
     */
    public String getApiKey() {
        return apiKey;
    }
}

