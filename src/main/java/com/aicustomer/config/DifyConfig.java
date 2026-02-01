package com.aicustomer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Dify平台配置类
 * 通过application.yml进行配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "dify")
public class DifyConfig {

    /**
     * 是否启用Dify引擎
     * 默认：false（关闭）
     */
    private boolean enabled = false;

    /**
     * Dify API地址
     * 默认：http://localhost/v1
     */
    private String apiUrl = "http://localhost/v1";

    /**
     * Dify API密钥
     * 从Dify应用中获取，建议通过环境变量配置
     */
    private String apiKey;

    /**
     * API调用超时时间（毫秒）
     * 默认：30秒
     */
    private int timeout = 30000;

    /**
     * 最大重试次数
     * 默认：2次
     */
    private int maxRetries = 2;

    /**
     * 健康检查间隔（毫秒）
     * 默认：60秒
     */
    private long healthCheckInterval = 60000;

    /**
     * 同步的目标数据集ID
     */
    /**
     * 同步的目标数据集ID
     */
    private String datasetId;

    /**
     * 数据集API密钥 (用于管理知识库文档)
     */
    private String datasetApiKey;

    @Bean
    public org.springframework.web.client.RestTemplate restTemplate() {
        org.springframework.http.client.SimpleClientHttpRequestFactory factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);
        return new org.springframework.web.client.RestTemplate(factory);
    }

    /**
     * Dify工具调用密钥 (用于Dify回调本服务)
     */
    private String toolSecret = "aicustomer-internal-secret-key-2024";
}
