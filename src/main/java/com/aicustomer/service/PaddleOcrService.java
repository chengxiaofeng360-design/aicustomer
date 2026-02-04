package com.aicustomer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * PaddleOCR 服务
 * 调用本地 Docker 化的 PaddleOCR 服务进行文字识别
 */
@Service
@Slf4j
public class PaddleOcrService {

    @Value("${ai-customer.ai.paddle-ocr.base-url:http://localhost:5001}")
    private String baseUrl;

    @Value("${ai-customer.ai.paddle-ocr.enabled:true}")
    private boolean enabled;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 检查服务是否可用
     */
    public boolean isAvailable() {
        if (!enabled) {
            return false;
        }

        try {
            String healthUrl = baseUrl + "/health";
            ResponseEntity<String> response = restTemplate.getForEntity(healthUrl, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.warn("PaddleOCR 服务不可用: {}", e.getMessage());
            return false;
        }
    }

    /**
     * OCR 识别图片
     * 
     * @param base64Image Base64 编码的图片数据
     * @return 识别出的文本
     */
    public String recognizeText(String base64Image) {
        try {
            String ocrUrl = baseUrl + "/ocr";

            // 构建请求体
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("image_base64", base64Image);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            log.info("调用 PaddleOCR 服务: {}", ocrUrl);
            ResponseEntity<String> response = restTemplate.postForEntity(ocrUrl, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());

                if (rootNode.path("success").asBoolean(false)) {
                    String text = rootNode.path("text").asText();
                    int lineCount = rootNode.path("line_count").asInt(0);

                    log.info("PaddleOCR 识别成功，共识别 {} 行文本", lineCount);
                    return text;
                } else {
                    String error = rootNode.path("error").asText("Unknown error");
                    throw new RuntimeException("PaddleOCR 识别失败: " + error);
                }
            }

            throw new RuntimeException("PaddleOCR 服务返回异常");

        } catch (Exception e) {
            log.error("PaddleOCR 调用失败", e);
            throw new RuntimeException("PaddleOCR 调用失败: " + e.getMessage());
        }
    }

    /**
     * 获取识别详细信息（包含每行文本和置信度）
     */
    public Map<String, Object> recognizeWithDetails(String base64Image) {
        try {
            String ocrUrl = baseUrl + "/ocr";

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("image_base64", base64Image);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(ocrUrl, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return objectMapper.readValue(response.getBody(), Map.class);
            }

            throw new RuntimeException("PaddleOCR 服务返回异常");

        } catch (Exception e) {
            log.error("PaddleOCR 调用失败", e);
            throw new RuntimeException("PaddleOCR 调用失败: " + e.getMessage());
        }
    }
}
