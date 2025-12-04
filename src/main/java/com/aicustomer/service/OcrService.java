package com.aicustomer.service;

import com.aicustomer.entity.Customer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class OcrService {

    @Value("${ai-customer.ai.deepseek.ocr.api-key:helloworld}")
    private String apiKey;

    @Value("${ai-customer.ai.deepseek.ocr.base-url:https://api.ocr.space/parse/image}")
    private String baseUrl;

    @Value("${ai-customer.ai.deepseek.ocr.language:chs}")
    private String language;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Customer parseBusinessCard(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传的文件为空");
        }

        // 转换为Base64
        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
        String base64Data = "data:" + file.getContentType() + ";base64," + base64Image;

        // 构建请求
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("apikey", apiKey);
        body.add("base64Image", base64Data);
        body.add("language", language);
        body.add("isOverlayRequired", "false");
        body.add("detectOrientation", "true");
        body.add("scale", "true");
        body.add("OCREngine", "2"); // 使用引擎2，支持中文更好

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            log.info("Sending OCR request to OCR.space API");

            ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parseOcrSpaceResponse(response.getBody());
            } else {
                log.error("OCR.space API returned error: {}", response.getStatusCode());
                throw new RuntimeException("名片识别失败: API调用错误");
            }
        } catch (Exception e) {
            log.error("Error calling OCR.space API", e);
            throw new RuntimeException("名片识别失败: " + e.getMessage());
        }
    }

    private Customer parseOcrSpaceResponse(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);

            // 检查OCR是否成功
            if (!rootNode.path("IsErroredOnProcessing").asBoolean(false)) {
                JsonNode parsedResults = rootNode.path("ParsedResults");
                if (parsedResults.isArray() && parsedResults.size() > 0) {
                    String parsedText = parsedResults.get(0).path("ParsedText").asText();
                    log.debug("OCR.space Response Text: {}", parsedText);

                    // 解析文本提取信息
                    return extractCustomerInfo(parsedText);
                }
            } else {
                String errorMessage = rootNode.path("ErrorMessage").asText("未知错误");
                log.error("OCR.space processing error: {}", errorMessage);
                throw new RuntimeException("OCR处理失败: " + errorMessage);
            }
        } catch (Exception e) {
            log.error("Error parsing OCR.space response", e);
            throw new RuntimeException("解析识别结果失败: " + e.getMessage());
        }
        return new Customer();
    }

    private Customer extractCustomerInfo(String text) {
        Customer customer = new Customer();

        // 使用正则表达式提取信息
        // 手机号 (中国手机号格式)
        Pattern phonePattern = Pattern.compile("1[3-9]\\d{9}");
        Matcher phoneMatcher = phonePattern.matcher(text);
        if (phoneMatcher.find()) {
            customer.setPhone(phoneMatcher.group());
        }

        // 邮箱
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        Matcher emailMatcher = emailPattern.matcher(text);
        if (emailMatcher.find()) {
            customer.setEmail(emailMatcher.group());
        }

        // 提取所有行
        String[] lines = text.split("\\r?\\n");

        // 简单启发式规则：
        // - 第一行通常是姓名
        // - 包含"公司"、"有限"、"科技"等关键词的行可能是公司名
        // - 包含"经理"、"总监"等的可能是职位

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty())
                continue;

            // 第一个非空行可能是姓名
            if (customer.getCustomerName() == null && i < 2) {
                // 如果不是手机号或邮箱，可能是姓名
                if (!line.matches(".*1[3-9]\\d{9}.*") && !line.contains("@")) {
                    if (line.length() <= 10) { // 姓名通常不会太长
                        customer.setContactPerson(line);
                    }
                }
            }

            // 查找公司名
            if (customer.getCustomerName() == null) {
                if (line.contains("公司") || line.contains("有限") || line.contains("科技") ||
                        line.contains("集团") || line.contains("企业") || line.contains("Co") ||
                        line.contains("Ltd") || line.contains("Inc")) {
                    customer.setCustomerName(line);
                }
            }

            // 查找职位
            if (customer.getPosition() == null) {
                if (line.contains("经理") || line.contains("总监") || line.contains("主管") ||
                        line.contains("总裁") || line.contains("CEO") || line.contains("CTO") ||
                        line.contains("Manager") || line.contains("Director")) {
                    customer.setPosition(line);
                }
            }

            // 查找地址 (包含"路"、"街"、"区"等)
            if (customer.getAddress() == null) {
                if (line.contains("路") || line.contains("街") || line.contains("区") ||
                        line.contains("市") || line.contains("省") || line.contains("Road") ||
                        line.contains("Street") || line.contains("Avenue")) {
                    customer.setAddress(line);
                }
            }
        }

        // 如果没有提取到公司名，使用联系人姓名
        if (customer.getCustomerName() == null && customer.getContactPerson() != null) {
            customer.setCustomerName(customer.getContactPerson());
        }

        // 将完整文本作为备注
        customer.setRemark("OCR识别原文：\n" + text);

        return customer;
    }
}
