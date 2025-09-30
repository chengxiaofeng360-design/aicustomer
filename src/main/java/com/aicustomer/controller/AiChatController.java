package com.aicustomer.controller;

import com.aicustomer.common.Result;
import com.aicustomer.entity.AiChat;
import com.aicustomer.service.AiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AI聊天控制器
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/ai-chat")
@RequiredArgsConstructor
public class AiChatController {
    
    private final AiChatService aiChatService;
    
    /**
     * 发送消息
     */
    @PostMapping("/send")
    public Result<Map<String, Object>> sendMessage(@RequestBody Map<String, Object> request) {
        try {
            String sessionId = request.get("sessionId").toString();
            String userMessage = request.get("message").toString();
            Long customerId = request.get("customerId") != null ? 
                Long.valueOf(request.get("customerId").toString()) : null;
            
            AiChat response = aiChatService.sendMessage(sessionId, userMessage, customerId);
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("id", response.getId());
            result.put("sessionId", sessionId);
            result.put("userMessage", userMessage);
            result.put("replyContent", response.getReplyContent());
            result.put("content", response.getContent());
            result.put("createTime", response.getCreateTime());
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("发送消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取聊天历史
     */
    @GetMapping("/history")
    public Result<Map<String, Object>> getChatHistory(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "50") int pageSize) {
        try {
            Map<String, Object> history = aiChatService.getChatHistory(pageNum, pageSize);
            return Result.success(history);
        } catch (Exception e) {
            return Result.error("获取聊天历史失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取聊天统计
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        try {
            Map<String, Object> stats = aiChatService.getChatStatistics();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取聊天统计失败: " + e.getMessage());
        }
    }
}