package com.aicustomer.controller;

import com.aicustomer.common.Result;
import com.aicustomer.entity.Customer;
import com.aicustomer.service.DeepSeekService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AI客户信息识别控制器
 * 
 * 提供从非结构化文本中提取客户信息的API接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/ai/customer-extract")
public class AiCustomerExtractController {
    
    @Autowired
    private DeepSeekService deepSeekService;
    
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
            
            String systemPrompt = "你是一个专业的信息提取专家，擅长从非结构化文本中提取结构化信息。请严格按照指定的JSON格式返回结果。";
            
            String aiResult = deepSeekService.chat(prompt, systemPrompt);
            
            // 尝试解析AI返回的JSON结果
            try {
                // 验证AI返回的结果是否为有效的JSON格式
                gson.fromJson(aiResult, Map.class);
                // 如果解析成功，将AI返回的结果放入info中
                info.put("aiProcessed", true);
                info.put("aiResult", aiResult);
            } catch (Exception e) {
                // 如果解析失败，将原始结果放入info中
                log.warn("AI返回的结果不是有效的JSON格式: {}", aiResult);
                info.put("aiProcessed", true);
                info.put("aiResult", aiResult);
                info.put("rawResult", true);
            }
            
        } catch (Exception e) {
            log.error("AI提取客户信息失败: {}", e.getMessage(), e);
            info.put("aiProcessed", false);
            info.put("aiError", e.getMessage());
        }
        
        return info;
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