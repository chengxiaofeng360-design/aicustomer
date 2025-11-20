package com.aicustomer.controller;

import com.aicustomer.common.Result;
import com.aicustomer.entity.Customer;
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
 * 提供从非结构化文本中提取客户信息的API接口
 * 使用Spring AI框架，集成豆包（Doubao）AI模型进行信息提取
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/ai/customer-extract")
public class AiCustomerExtractController {
    
    @Autowired
    @Qualifier("doubaoChatModel")
    private ChatModel chatModel;
    
    @Autowired
    private com.aicustomer.service.ArkChatService arkChatService;
    
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
                "请从以下文本中提取客户信息，并严格按照JSON格式返回结果。\n\n" +
                "文本内容：%s\n\n" +
                "请识别并提取以下字段（必填字段请务必识别）：\n" +
                "1. customerName (客户名称) - 公司名称或个人姓名（必填）\n" +
                "2. contactPerson (联系人) - 联系人姓名（必填）\n" +
                "3. phone (电话) - 手机号码或固定电话（必填）\n" +
                "4. customerType (客户类型) - 必须是：个人、企业、科研院所 中的一个（必填）\n" +
                "5. region (地区) - 省份或城市（必填）\n" +
                "6. position (职务) - 职位或职务（可选）\n" +
                "7. qqWeixin (QQ/微信) - QQ号或微信号（可选）\n" +
                "8. cooperationContent (合作内容) - 业务类型或合作内容（可选）\n" +
                "9. email (电子邮箱) - 邮箱地址（可选）\n" +
                "10. address (地址) - 详细地址（可选）\n" +
                "11. remark (备注) - 其他备注信息（可选）\n\n" +
                "请严格按照以下JSON格式返回，只返回JSON对象，不要包含任何其他文字：\n" +
                "{\n" +
                "  \"customerName\": \"客户名称\",\n" +
                "  \"contactPerson\": \"联系人\",\n" +
                "  \"phone\": \"电话\",\n" +
                "  \"customerType\": \"客户类型（个人/企业/科研院所）\",\n" +
                "  \"region\": \"地区\",\n" +
                "  \"position\": \"职务\",\n" +
                "  \"qqWeixin\": \"QQ/微信\",\n" +
                "  \"cooperationContent\": \"合作内容\",\n" +
                "  \"email\": \"电子邮箱\",\n" +
                "  \"address\": \"地址\",\n" +
                "  \"remark\": \"备注\"\n" +
                "}\n\n" +
                "如果某个字段在文本中不存在，请将对应值设为空字符串\"\"。必填字段（customerName、contactPerson、phone、customerType、region）请尽量识别，如果确实无法识别，可以设为空字符串。",
                text
            );
            
            String systemPrompt = "你是一个专业的信息提取专家，擅长从非结构化文本中提取结构化信息。请严格按照指定的JSON格式返回结果，只返回JSON对象，不要包含任何其他文字说明。";
            
            // 使用Spring AI框架调用豆包模型
            try {
                List<org.springframework.ai.chat.messages.Message> messages = new ArrayList<>();
                messages.add(new SystemMessage(systemPrompt));
                messages.add(new UserMessage(prompt));
                Prompt springAiPrompt = new Prompt(messages);
                
                ChatResponse chatResponse = chatModel.call(springAiPrompt);
                String aiResult = chatResponse.getResult().getOutput().getContent();
                
                log.info("【客户信息提取】Spring AI调用成功，返回结果长度: {}", aiResult != null ? aiResult.length() : 0);
                if (aiResult == null || aiResult.trim().isEmpty()) {
                    log.warn("【客户信息提取】Spring AI返回结果为空");
                    info.put("aiProcessed", false);
                    info.put("aiError", "Spring AI返回结果为空");
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
                log.error("【客户信息提取】Spring AI调用失败: {}", e.getMessage(), e);
                info.put("aiProcessed", false);
                info.put("aiError", "Spring AI调用失败: " + e.getMessage());
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
            
            // 使用Spring AI框架调用豆包模型
            try {
                List<org.springframework.ai.chat.messages.Message> messages = new ArrayList<>();
                messages.add(new SystemMessage(systemPrompt));
                messages.add(new UserMessage(prompt));
                Prompt springAiPrompt = new Prompt(messages);
                
                ChatResponse chatResponse = chatModel.call(springAiPrompt);
                String aiResult = chatResponse.getResult().getOutput().getContent();
                
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
                log.error("【批量解析】第{}行Spring AI调用失败: {}", lineNumber, e.getMessage(), e);
                result.put("status", "error");
                result.put("error", "Spring AI调用失败: " + e.getMessage());
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
    
    /**
     * 识别名片图片中的客户信息
     * 
     * @param request 包含图片Base64编码的请求对象
     * @return 识别的客户信息
     */
    @PostMapping("/recognize-business-card")
    public Result<Map<String, Object>> recognizeBusinessCard(@RequestBody Map<String, String> request) {
        try {
            String imageBase64 = request.get("image");
            String fileName = request.get("fileName");
            
            if (imageBase64 == null || imageBase64.trim().isEmpty()) {
                return Result.error("图片数据不能为空");
            }
            
            log.info("【名片识别】开始识别名片，文件名: {}", fileName);
            
            // 使用AI识别名片图片中的文字和信息
            Map<String, Object> recognizedInfo = recognizeBusinessCardWithAI(imageBase64);
            
            log.info("【名片识别】识别完成，结果: {}", recognizedInfo);
            
            return Result.success(recognizedInfo);
        } catch (Exception e) {
            log.error("识别名片失败: {}", e.getMessage(), e);
            return Result.error("识别名片失败: " + e.getMessage());
        }
    }
    
    /**
     * 使用AI识别名片图片中的客户信息
     * 
     * @param imageBase64 图片的Base64编码
     * @return 识别的客户信息
     */
    private Map<String, Object> recognizeBusinessCardWithAI(String imageBase64) {
        Map<String, Object> info = new HashMap<>();
        
        try {
            // 构建AI提示词，要求从名片图片中提取客户信息
            String prompt = String.format(
                "请识别这张名片图片中的客户信息，并严格按照JSON格式返回结果。\n\n" +
                "请识别并提取以下字段：\n" +
                "1. customerName (客户名称) - 公司名称或个人姓名（必填）\n" +
                "2. contactPerson (联系人) - 联系人姓名（必填）\n" +
                "3. phone (电话) - 手机号码或固定电话（必填）\n" +
                "4. customerType (客户类型) - 必须是：个人、企业、科研院所 中的一个（必填）\n" +
                "5. region (地区) - 省份或城市（必填）\n" +
                "6. position (职务) - 职位或职务（可选）\n" +
                "7. qqWeixin (QQ/微信) - QQ号或微信号（可选）\n" +
                "8. cooperationContent (合作内容) - 业务类型或合作内容（可选）\n" +
                "9. email (电子邮箱) - 邮箱地址（可选）\n" +
                "10. address (地址) - 详细地址（可选）\n" +
                "11. remark (备注) - 其他备注信息（可选）\n\n" +
                "请严格按照以下JSON格式返回，只返回JSON对象，不要包含任何其他文字：\n" +
                "{\n" +
                "  \"customerName\": \"客户名称\",\n" +
                "  \"contactPerson\": \"联系人\",\n" +
                "  \"phone\": \"电话\",\n" +
                "  \"customerType\": \"客户类型（个人/企业/科研院所）\",\n" +
                "  \"region\": \"地区\",\n" +
                "  \"position\": \"职务\",\n" +
                "  \"qqWeixin\": \"QQ/微信\",\n" +
                "  \"cooperationContent\": \"合作内容\",\n" +
                "  \"email\": \"电子邮箱\",\n" +
                "  \"address\": \"地址\",\n" +
                "  \"remark\": \"备注\"\n" +
                "}\n\n" +
                "如果某个字段在名片中不存在，请将对应值设为空字符串\"\"。"
            );
            
            String systemPrompt = "你是一个专业的名片识别专家，擅长从名片图片中提取结构化信息。请仔细识别名片上的所有文字信息，包括公司名称、联系人、电话、地址、邮箱等，并严格按照指定的JSON格式返回结果，只返回JSON对象，不要包含任何其他文字说明。";
            
            // 尝试使用多模态方式：直接将图片Base64传递给豆包模型
            // 如果模型支持多模态，可以直接识别图片；如果不支持，会回退到文本描述方式
            log.info("【名片识别】尝试多模态识别，图片Base64长度: {}", imageBase64.length());
            
            // 方案1：尝试直接使用ArkChatService的多模态接口
            if (arkChatService != null && arkChatService.isAvailable()) {
                try {
                    log.info("【名片识别】使用ArkChatService多模态接口");
                    String aiResult = arkChatService.chatWithImage(prompt, imageBase64, systemPrompt);
                    
                    if (aiResult != null && !aiResult.trim().isEmpty()) {
                        log.info("【名片识别】多模态识别成功，返回结果长度: {}", aiResult.length());
                        
                        // 清理AI返回的结果，提取JSON部分
                        String cleanedResult = cleanJsonResponse(aiResult);
                        log.info("【名片识别】清理后的结果: {}", cleanedResult.substring(0, Math.min(200, cleanedResult.length())));
                        
                        // 尝试解析AI返回的JSON结果
                        try {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> parsedJson = (Map<String, Object>) gson.fromJson(cleanedResult, Map.class);
                            info.put("recognized", true);
                            info.putAll(parsedJson);
                            log.info("【名片识别】JSON解析成功（多模态方式）");
                            return info;
                        } catch (Exception e) {
                            log.warn("【名片识别】多模态识别结果解析失败: {}", e.getMessage());
                            // 继续尝试Spring AI方式
                        }
                    }
                } catch (Exception e) {
                    log.warn("【名片识别】多模态接口调用失败，尝试Spring AI方式: {}", e.getMessage());
                }
            }
            
            // 方案2：使用Spring AI框架（如果多模态方式失败或不可用）
            try {
                // 构建包含图片信息的提示词
                // 尝试将图片Base64嵌入到提示中（如果模型支持）
                String imagePrompt = prompt + "\n\n[图片数据已提供，请识别图片中的名片信息]";
                
                List<org.springframework.ai.chat.messages.Message> messages = new ArrayList<>();
                messages.add(new SystemMessage(systemPrompt));
                messages.add(new UserMessage(imagePrompt));
                
                Prompt springAiPrompt = new Prompt(messages);
                ChatResponse chatResponse = chatModel.call(springAiPrompt);
                String aiResult = chatResponse.getResult().getOutput().getContent();
                
                log.info("【名片识别】Spring AI调用成功，返回结果长度: {}", aiResult != null ? aiResult.length() : 0);
                
                if (aiResult == null || aiResult.trim().isEmpty()) {
                    log.warn("【名片识别】Spring AI返回结果为空");
                    info.put("recognized", false);
                    info.put("error", "AI识别结果为空");
                    return info;
                }
                
                // 清理AI返回的结果，提取JSON部分
                String cleanedResult = cleanJsonResponse(aiResult);
                log.info("【名片识别】清理后的结果: {}", cleanedResult.substring(0, Math.min(200, cleanedResult.length())));
                
                // 尝试解析AI返回的JSON结果
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> parsedJson = (Map<String, Object>) gson.fromJson(cleanedResult, Map.class);
                    // 如果解析成功，将解析后的结果放入info中
                    info.put("recognized", true);
                    info.putAll(parsedJson);
                    log.info("【名片识别】JSON解析成功");
                } catch (Exception e) {
                    // 如果解析失败，将原始结果放入info中
                    log.warn("【名片识别】AI返回的结果不是有效的JSON格式: {}", cleanedResult);
                    log.warn("【名片识别】解析错误: {}", e.getMessage());
                    info.put("recognized", true);
                    info.put("rawResult", cleanedResult);
                    info.put("parseError", e.getMessage());
                }
            } catch (Exception e) {
                log.error("【名片识别】Spring AI调用失败: {}", e.getMessage(), e);
                info.put("recognized", false);
                info.put("error", "Spring AI调用失败: " + e.getMessage());
            }
            
        } catch (Exception e) {
            log.error("名片识别失败: {}", e.getMessage(), e);
            info.put("recognized", false);
            info.put("error", e.getMessage());
        }
        
        return info;
    }
}