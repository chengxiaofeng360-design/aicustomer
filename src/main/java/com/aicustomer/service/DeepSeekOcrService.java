package com.aicustomer.service;

import com.aicustomer.entity.Customer;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class DeepSeekOcrService {

    @Value("${ai-customer.ai.deepseek.ocr.api-key:${ai-customer.ai.deepseek.api-key}}")
    private String apiKey;

    @Value("${ai-customer.ai.deepseek.ocr.base-url:${ai-customer.ai.deepseek.base-url}}")
    private String baseUrl;

    @Value("${ai-customer.ai.deepseek.ocr.model:deepseek-ai/DeepSeek-OCR}")
    private String ocrModel;

    @Value("${ai-customer.ai.deepseek.ocr.max-tokens:1000}")
    private int maxTokens;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Customer parseBusinessCard(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传的文件为空");
        }

        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
        String imageUrl = "data:" + file.getContentType() + ";base64," + base64Image;

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", ocrModel);
        requestBody.put("max_tokens", maxTokens);

        // 构造消息
        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");

        List<Map<String, Object>> contentList = new ArrayList<>();

        // 文本提示
        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text",
                "请识别这张名片的内容，并提取以下信息：姓名、职位、公司名称、手机号、邮箱、地址、备注（包含其他所有信息）。请以JSON格式返回，字段名为：name, position, company, phone, email, address, remark。只返回JSON，不要包含markdown格式标记。");
        contentList.add(textContent);

        // 图片内容
        Map<String, Object> imageContent = new HashMap<>();
        imageContent.put("type", "image_url");
        Map<String, String> imageUrlMap = new HashMap<>();
        imageUrlMap.put("url", imageUrl);
        imageContent.put("image_url", imageUrlMap);
        contentList.add(imageContent);

        userMessage.put("content", contentList);
        messages.add(userMessage);

        requestBody.put("messages", messages);

        // 发送请求
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            String apiUrl = baseUrl + "/chat/completions"; // 假设是标准的 Chat Completion 接口
            log.info("Sending OCR request to DeepSeek API: {}", apiUrl);

            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parseResponse(response.getBody());
            } else {
                log.error("DeepSeek API returned error: {}", response.getStatusCode());
                throw new RuntimeException("名片识别失败: API调用错误");
            }
        } catch (Exception e) {
            log.error("Error calling DeepSeek API", e);
            throw new RuntimeException("名片识别失败: " + e.getMessage());
        }
    }

    private Customer parseResponse(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode choices = rootNode.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                String content = choices.get(0).path("message").path("content").asText();
                log.debug("DeepSeek OCR Response Content: {}", content);

                // 清理可能存在的 Markdown 代码块标记
                content = content.replace("```json", "").replace("```", "").trim();

                JsonNode info = objectMapper.readTree(content);

                Customer customer = new Customer();
                // 优先使用 company，如果没有则使用 name
                String name = info.has("company") ? info.get("company").asText()
                        : (info.has("name") ? info.get("name").asText() : "");
                customer.setCustomerName(name);

                // 如果有联系人字段，设置联系人
                if (info.has("name") && info.has("company")) {
                    customer.setContactPerson(info.get("name").asText());
                } else if (info.has("contact_person")) {
                    customer.setContactPerson(info.get("contact_person").asText());
                }
                if (info.has("phone"))
                    customer.setPhone(info.get("phone").asText());
                if (info.has("email"))
                    customer.setEmail(info.get("email").asText());
                if (info.has("address"))
                    customer.setAddress(info.get("address").asText());
                if (info.has("remark"))
                    customer.setRemark(info.get("remark").asText());

                return customer;
            }
        } catch (Exception e) {
            log.error("Error parsing OCR response", e);
            throw new RuntimeException("解析识别结果失败");
        }
        return null;
    }
}
