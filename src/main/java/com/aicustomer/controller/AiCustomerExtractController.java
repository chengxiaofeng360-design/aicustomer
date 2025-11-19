package com.aicustomer.controller;

import com.aicustomer.common.Result;
import com.aicustomer.entity.Customer;
import com.aicustomer.service.ArkChatService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI客户信息识别控制器
 * 
 * 提供从非结构化文本中提取客户信息的API接口
 * 使用豆包（Doubao）AI模型进行信息提取
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/ai/customer-extract")
public class AiCustomerExtractController {
    
    @Autowired
    private ArkChatService arkChatService;
    
    @Autowired(required = false)
    @Qualifier("doubaoChatClient")
    private ChatClient chatClient; // Spring AI ChatClient，如果可用则优先使用
    
    private final Gson gson = new Gson();
    
    /**
     * 从文本中提取客户信息
     * 
     * @param request 包含文本内容的请求对象
     * @return 提取的客户信息
     */
    @PostMapping("/extract")
    public Result<Map<String, Object>> extractCustomerInfo(@RequestBody Map<String, String> request) {
        try {
            String text = request.get("text");
            if (text == null || text.trim().isEmpty()) {
                return Result.error("文本内容不能为空");
            }
            
            // 直接使用AI进行客户信息提取
            Map<String, Object> extractedInfo = extractWithAI(text);
            
            return Result.success(extractedInfo);
        } catch (Exception e) {
            log.error("提取客户信息失败: {}", e.getMessage(), e);
            return Result.error("提取客户信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 使用AI提取客户信息
     * 
     * @param text 输入文本
     * @return 提取的信息
     */
    private Map<String, Object> extractWithAI(String text) {
        Map<String, Object> info = new HashMap<>();
        
        try {
            String prompt = String.format(
                "请从以下文本中提取客户信息，并以JSON格式返回结果：\n\n" +
                "文本内容：%s\n\n" +
                "请提取以下信息：\n" +
                "1. 客户姓名 (customerName)\n" +
                "2. 客户类型 (customerType) - 个人客户或企业客户\n" +
                "3. 手机号码 (phoneNumber)\n" +
                "4. 电子邮箱 (email)\n" +
                "5. 公司名称 (companyName)\n" +
                "6. 职位 (position)\n" +
                "7. 地址 (address)\n\n" +
                "请严格按照以下JSON格式返回结果，不要包含其他内容：\n" +
                "{\n" +
                "  \"customerName\": \"姓名\",\n" +
                "  \"customerType\": \"个人客户或企业客户\",\n" +
                "  \"phoneNumber\": \"手机号码\",\n" +
                "  \"email\": \"电子邮箱\",\n" +
                "  \"companyName\": \"公司名称\",\n" +
                "  \"position\": \"职位\",\n" +
                "  \"address\": \"地址\"\n" +
                "}\n\n" +
                "如果某些信息不存在，请将对应值设为null。",
                text
            );
            
            String systemPrompt = "你是一个专业的信息提取专家，擅长从非结构化文本中提取结构化信息。请严格按照指定的JSON格式返回结果，只返回JSON对象，不要包含任何其他文字说明。";
            
            String aiResult;
            
            // 优先使用Spring AI ChatClient（如果已配置）
            if (chatClient != null) {
                try {
                    log.info("【客户信息提取】使用Spring AI ChatClient");
                    Prompt springAiPrompt = new Prompt(List.of(
                        new SystemMessage(systemPrompt),
                        new UserMessage(prompt)
                    ));
                    aiResult = chatClient.call(springAiPrompt).getResult().getOutput().getContent();
                } catch (Exception e) {
                    log.warn("【客户信息提取】Spring AI调用失败，回退到ArkChatService: {}", e.getMessage());
                    // 回退到直接调用ArkChatService
                    if (!arkChatService.isAvailable()) {
                        info.put("aiProcessed", false);
                        info.put("aiError", "AI服务不可用");
                        return info;
                    }
                    aiResult = arkChatService.chat(prompt, systemPrompt);
                }
            } else {
                // 使用豆包（Ark）AI模型进行信息提取
                if (!arkChatService.isAvailable()) {
                    log.warn("【客户信息提取】豆包服务不可用，请检查配置");
                    info.put("aiProcessed", false);
                    info.put("aiError", "豆包AI服务未配置或不可用，请检查API密钥配置");
                    return info;
                }
                aiResult = arkChatService.chat(prompt, systemPrompt);
            }
            log.info("【客户信息提取】豆包AI返回结果长度: {}", aiResult != null ? aiResult.length() : 0);
            
            if (aiResult == null || aiResult.trim().isEmpty()) {
                log.warn("【客户信息提取】豆包AI返回结果为空");
                info.put("aiProcessed", false);
                info.put("aiError", "豆包AI返回结果为空");
                return info;
            }
            
            // 清理AI返回的结果，提取JSON部分
            String cleanedResult = cleanJsonResponse(aiResult);
            log.info("【客户信息提取】清理后的结果: {}", cleanedResult.substring(0, Math.min(200, cleanedResult.length())));
            
            // 尝试解析AI返回的JSON结果
            try {
                // 验证AI返回的结果是否为有效的JSON格式
                @SuppressWarnings("unchecked")
                Map<String, Object> parsedJson = (Map<String, Object>) gson.fromJson(cleanedResult, Map.class);
                // 如果解析成功，将解析后的结果放入info中
                info.put("aiProcessed", true);
                info.put("aiResult", cleanedResult);
                info.put("parsedData", parsedJson);
                log.info("【客户信息提取】JSON解析成功");
            } catch (Exception e) {
                // 如果解析失败，将原始结果放入info中
                log.warn("【客户信息提取】AI返回的结果不是有效的JSON格式: {}", cleanedResult);
                log.warn("【客户信息提取】解析错误: {}", e.getMessage());
                info.put("aiProcessed", true);
                info.put("aiResult", cleanedResult);
                info.put("rawResult", true);
                info.put("parseError", e.getMessage());
            }
            
        } catch (Exception e) {
            log.error("AI提取客户信息失败: {}", e.getMessage(), e);
            info.put("aiProcessed", false);
            info.put("aiError", e.getMessage());
        }
        
        return info;
    }
    
    /**
     * 清理AI返回的JSON响应，提取纯JSON内容
     * 处理可能包含markdown代码块、说明文字等情况
     * 
     * @param rawResponse AI原始响应
     * @return 清理后的JSON字符串
     */
    private String cleanJsonResponse(String rawResponse) {
        if (rawResponse == null) {
            return "";
        }
        
        String cleaned = rawResponse.trim();
        
        // 移除markdown代码块标记
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        
        cleaned = cleaned.trim();
        
        // 查找第一个 { 和最后一个 }
        int firstBrace = cleaned.indexOf('{');
        int lastBrace = cleaned.lastIndexOf('}');
        
        if (firstBrace >= 0 && lastBrace > firstBrace) {
            cleaned = cleaned.substring(firstBrace, lastBrace + 1);
        }
        
        return cleaned.trim();
    }
    
    /**
     * 批量解析客户数据（用于批量导入）
     * 使用豆包AI自动识别每行数据的各个字段
     * 
     * @param request 包含多行文本数据的请求对象
     * @return 解析后的客户信息列表
     */
    @PostMapping("/batch-parse")
    public Result<Map<String, Object>> batchParseCustomerData(@RequestBody Map<String, String> request) {
        try {
            String data = request.get("data");
            if (data == null || data.trim().isEmpty()) {
                return Result.error("数据内容不能为空");
            }
            
            // 按行分割数据
            String[] lines = data.split("\n");
            java.util.List<Map<String, Object>> parsedList = new java.util.ArrayList<>();
            
            // 使用豆包AI逐行解析
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) {
                    continue;
                }
                
                Map<String, Object> parsedItem = parseLineWithAI(line, i + 1);
                parsedList.add(parsedItem);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("parsedList", parsedList);
            result.put("totalCount", parsedList.size());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量解析客户数据失败: {}", e.getMessage(), e);
            return Result.error("批量解析客户数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 使用AI解析单行客户数据
     * 
     * @param line 单行文本数据
     * @param lineNumber 行号（用于错误提示）
     * @return 解析后的客户信息
     */
    private Map<String, Object> parseLineWithAI(String line, int lineNumber) {
        Map<String, Object> result = new HashMap<>();
        result.put("lineNumber", lineNumber);
        result.put("originalLine", line);
        
        try {
            String prompt = String.format(
                "请从以下文本中提取客户信息，并严格按照JSON格式返回结果。\n\n" +
                "文本内容：%s\n\n" +
                "请识别并提取以下字段：\n" +
                "1. customerName (客户名称) - 公司名称或个人姓名\n" +
                "2. contactPerson (联系人) - 联系人姓名\n" +
                "3. phone (电话) - 手机号码或固定电话\n" +
                "4. customerType (客户类型) - 必须是：个人、企业、科研院所 中的一个\n" +
                "5. position (职务) - 职位或职务\n" +
                "6. qqWeixin (QQ/微信) - QQ号或微信号\n" +
                "7. cooperationContent (合作内容) - 业务类型或合作内容\n" +
                "8. region (地区) - 省份或城市\n" +
                "9. address (地址) - 详细地址（可选）\n" +
                "10. remark (备注) - 其他备注信息（可选）\n\n" +
                "请严格按照以下JSON格式返回，只返回JSON对象，不要包含任何其他文字：\n" +
                "{\n" +
                "  \"customerName\": \"客户名称\",\n" +
                "  \"contactPerson\": \"联系人\",\n" +
                "  \"phone\": \"电话\",\n" +
                "  \"customerType\": \"客户类型（个人/企业/科研院所）\",\n" +
                "  \"position\": \"职务\",\n" +
                "  \"qqWeixin\": \"QQ/微信\",\n" +
                "  \"cooperationContent\": \"合作内容\",\n" +
                "  \"region\": \"地区\",\n" +
                "  \"address\": \"地址\",\n" +
                "  \"remark\": \"备注\"\n" +
                "}\n\n" +
                "如果某个字段在文本中不存在，请将对应值设为空字符串\"\"。",
                line
            );
            
            String systemPrompt = "你是一个专业的数据解析专家，擅长从非结构化文本中提取结构化信息。请严格按照指定的JSON格式返回结果，只返回JSON对象，不要包含任何其他文字说明、markdown标记或解释。";
            
            String aiResult;
            
            // 优先使用Spring AI ChatClient（如果已配置）
            if (chatClient != null) {
                try {
                    log.info("【批量解析】第{}行使用Spring AI ChatClient", lineNumber);
                    Prompt springAiPrompt = new Prompt(List.of(
                        new SystemMessage(systemPrompt),
                        new UserMessage(prompt)
                    ));
                    aiResult = chatClient.call(springAiPrompt).getResult().getOutput().getContent();
                } catch (Exception e) {
                    log.warn("【批量解析】第{}行Spring AI调用失败，回退到ArkChatService: {}", lineNumber, e.getMessage());
                    // 回退到直接调用ArkChatService
                    if (!arkChatService.isAvailable()) {
                        result.put("status", "error");
                        result.put("error", "AI服务未配置");
                        return result;
                    }
                    aiResult = arkChatService.chat(prompt, systemPrompt);
                }
            } else {
                // 使用豆包AI解析
                if (!arkChatService.isAvailable()) {
                    result.put("status", "error");
                    result.put("error", "豆包AI服务未配置");
                    return result;
                }
                aiResult = arkChatService.chat(prompt, systemPrompt);
            }
            
            if (aiResult == null || aiResult.trim().isEmpty()) {
                result.put("status", "error");
                result.put("error", "AI返回结果为空");
                return result;
            }
            
            // 清理并解析JSON
            String cleanedResult = cleanJsonResponse(aiResult);
            
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> parsedData = (Map<String, Object>) gson.fromJson(cleanedResult, Map.class);
                
                // 将解析的数据合并到结果中
                result.putAll(parsedData);
                result.put("status", "valid");
                result.put("aiProcessed", true);
                
                log.info("【批量解析】第{}行解析成功", lineNumber);
            } catch (Exception e) {
                log.warn("【批量解析】第{}行JSON解析失败: {}", lineNumber, e.getMessage());
                result.put("status", "error");
                result.put("error", "JSON解析失败: " + e.getMessage());
                result.put("rawResult", cleanedResult);
            }
            
        } catch (Exception e) {
            log.error("【批量解析】第{}行解析异常: {}", lineNumber, e.getMessage(), e);
            result.put("status", "error");
            result.put("error", "解析异常: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 保存提取的客户信息
     * 
     * @param customer 客户信息
     * @return 保存结果
     */
    @PostMapping("/save")
    public Result<String> saveCustomerInfo(@RequestBody Customer customer) {
        try {
            // 这里应该调用客户服务保存客户信息
            // 为简化示例，我们只返回成功消息
            log.info("保存客户信息: {}", customer);
            return Result.success("客户信息保存成功");
        } catch (Exception e) {
            log.error("保存客户信息失败: {}", e.getMessage(), e);
            return Result.error("保存客户信息失败: " + e.getMessage());
        }
    }
}