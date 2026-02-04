package com.aicustomer.controller;

import com.aicustomer.common.Result;
import com.aicustomer.entity.Customer;
import com.aicustomer.service.OcrService;
import com.aicustomer.service.ZhipuChatService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI客户信息识别控制器
 * 
 * 已升级：统一使用 OcrService (PaddleOCR + DeepSeek) 进行智能判断和识别
 */
@Slf4j
@RestController
@RequestMapping("/api/ai/customer-extract")
public class AiCustomerExtractController {

    @Autowired
    @Qualifier("doubaoChatModel")
    private ChatModel chatModel;

    @Autowired
    private ZhipuChatService zhipuChatService;

    @Autowired
    private OcrService ocrService;

    private final Gson gson = new Gson();

    /**
     * 从文本中提取客户信息
     */
    @PostMapping("/extract")
    public Result<Map<String, Object>> extractCustomerInfo(@RequestBody Map<String, String> request) {
        try {
            String text = request.get("text");
            if (text == null || text.trim().isEmpty()) {
                return Result.error("文本内容不能为空");
            }

            // 使用 OcrService 中的统一解析逻辑
            List<Customer> customerList = ocrService.parseText(text);

            List<Map<String, Object>> resultList = new ArrayList<>();
            for (Customer customer : customerList) {
                Map<String, Object> result = new HashMap<>();
                result.put("customerName", customer.getCustomerName());
                result.put("contactPerson", customer.getContactPerson());
                result.put("phone", customer.getPhone());
                result.put("position", customer.getPosition());
                result.put("email", customer.getEmail());
                result.put("address", customer.getAddress());
                result.put("remark", customer.getRemark());
                result.put("region", customer.getRegion());
                result.put("customerType",
                        customer.getCustomerType() == 1 ? "个人" : customer.getCustomerType() == 3 ? "科研院所" : "企业");
                resultList.add(result);
            }

            // 保持返回 Map 结构，但在其中放入 parsedList
            Map<String, Object> response = new HashMap<>();
            response.put("parsedList", resultList);
            return Result.success(response);

        } catch (Exception e) {
            log.error("提取客户信息失败: {}", e.getMessage());
            return Result.error("提取客户信息失败: " + e.getMessage());
        }
    }

    /**
     * 使用AI提取客户信息（核心逻辑）
     */
    private Map<String, Object> extractWithAI(String text) {
        Map<String, Object> info = new HashMap<>();
        try {
            String prompt = String.format(
                    "请从以下文本中提取客户信息：\n%s\n\n要求返回规范的JSON格式。", text);
            String systemPrompt = "你是一个信息提取专家。只输出JSON。";

            // 优先智谱
            if (zhipuChatService != null && zhipuChatService.isAvailable()) {
                String aiResult = zhipuChatService.chat(prompt, systemPrompt);
                String cleaned = cleanJsonResponse(aiResult);
                Map<String, Object> parsed = (Map<String, Object>) gson.fromJson(cleaned, Map.class);
                info.put("aiProcessed", true);
                info.put("parsedData", parsed);
                info.put("aiProvider", "zhipu");
                return info;
            }

            // 备选 Spring AI (Doubao)
            List<org.springframework.ai.chat.messages.Message> messages = new ArrayList<>();
            messages.add(new SystemMessage(systemPrompt));
            messages.add(new UserMessage(prompt));
            ChatResponse response = chatModel.call(new Prompt(messages));
            String result = response.getResult().getOutput().getContent();
            info.put("aiProcessed", true);
            info.put("parsedData", gson.fromJson(cleanJsonResponse(result), Map.class));
            info.put("aiProvider", "doubao-sdk");

        } catch (Exception e) {
            log.error("AI提取失败: {}", e.getMessage());
            info.put("aiProcessed", false);
            info.put("error", e.getMessage());
        }
        return info;
    }

    /**
     * 【核心入口】识别名片或表格图片
     * 统一调用 OcrService，具备智能判断输入内容（单人/多人表格）的能力
     */
    @PostMapping("/recognize-business-card")
    public Result<List<Map<String, Object>>> recognizeBusinessCard(@RequestBody Map<String, String> request) {
        try {
            String imageBase64 = request.get("image");
            if (imageBase64 == null || imageBase64.trim().isEmpty()) {
                return Result.error("图片数据不能为空");
            }

            log.info("【智能识别】执行统一判断链 (PaddleOCR + DeepSeek)");
            List<Customer> customerList = ocrService.parseBusinessCard(imageBase64);

            List<Map<String, Object>> resultList = new ArrayList<>();
            for (Customer customer : customerList) {
                Map<String, Object> result = new HashMap<>();
                result.put("customerName", customer.getCustomerName());
                result.put("contactPerson", customer.getContactPerson());
                result.put("phone", customer.getPhone());
                result.put("position", customer.getPosition());
                result.put("email", customer.getEmail());
                result.put("address", customer.getAddress());
                result.put("remark", customer.getRemark());
                result.put("region", customer.getRegion());
                result.put("customerType",
                        customer.getCustomerType() == 1 ? "个人" : customer.getCustomerType() == 3 ? "科研院所" : "企业");
                result.put("recognized", true);
                resultList.add(result);
            }

            return Result.success(resultList);
        } catch (Exception e) {
            log.error("识别图片失败: {}", e.getMessage());
            return Result.error("识别失败: " + e.getMessage());
        }
    }

    /**
     * AI批量解析客户数据
     */
    @PostMapping("/batch-parse")
    public Result<Map<String, Object>> batchParseCustomerInfo(@RequestBody Map<String, String> request) {
        String data = request.get("data");
        if (data == null || data.trim().isEmpty())
            return Result.error("输入不能为空");

        try {
            String prompt = "请将以下内容解析为JSON数组：\n" + data;
            // 简单演示，实际应用中建议在这里也走统一的 DeepSeek 解析逻辑
            List<org.springframework.ai.chat.messages.Message> msgs = new ArrayList<>();
            msgs.add(new SystemMessage("你是一个数据解析专家。返回JSON数组。"));
            msgs.add(new UserMessage(prompt));
            ChatResponse resp = chatModel.call(new Prompt(msgs));
            String jsonRaw = cleanJsonResponse(resp.getResult().getOutput().getContent());

            List<Map<String, Object>> list = (List<Map<String, Object>>) gson.fromJson(jsonRaw, List.class);
            Map<String, Object> res = new HashMap<>();
            res.put("parsedList", list);
            return Result.success(res);
        } catch (Exception e) {
            return Result.error("批量解析失败: " + e.getMessage());
        }
    }

    private String cleanJsonResponse(String raw) {
        if (raw == null)
            return "";
        String cleaned = raw.trim();
        if (cleaned.contains("```json")) {
            cleaned = cleaned.split("```json")[1].split("```")[0];
        } else if (cleaned.contains("```")) {
            cleaned = cleaned.split("```")[1].split("```")[0];
        }
        int start = cleaned.indexOf('{');
        int end = cleaned.lastIndexOf('}');
        if (start == -1)
            start = cleaned.indexOf('[');
        if (end == -1)
            end = cleaned.lastIndexOf(']');
        if (start >= 0 && end > start)
            return cleaned.substring(start, end + 1);
        return cleaned;
    }

    @PostMapping("/save")
    public Result<String> saveCustomerInfo(@RequestBody Customer customer) {
        log.info("保存客户: {}", customer);
        return Result.success("保存成功");
    }
}