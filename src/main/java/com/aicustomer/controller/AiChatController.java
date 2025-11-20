package com.aicustomer.controller;

import com.aicustomer.common.Result;
import com.aicustomer.entity.AiChat;
import com.aicustomer.service.AiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
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
     * 发送消息（支持多轮对话）
     */
    @PostMapping("/send")
    public Result<Map<String, Object>> sendMessage(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("【AI聊天】收到发送消息请求");
            System.out.println("【AI聊天】请求参数: " + request);
            
            String sessionId = request.get("sessionId").toString();
            String userMessage = request.get("message").toString();
            Long customerId = request.get("customerId") != null ? 
                Long.valueOf(request.get("customerId").toString()) : null;
            
            System.out.println("【AI聊天】会话ID: " + sessionId);
            System.out.println("【AI聊天】用户消息: " + userMessage);
            System.out.println("【AI聊天】客户ID: " + customerId);
            
            // 支持传递对话历史（用于多轮对话）
            @SuppressWarnings("unchecked")
            List<Map<String, String>> history = (List<Map<String, String>>) request.get("history");
            System.out.println("【AI聊天】对话历史条数: " + (history != null ? history.size() : 0));
            
            AiChat response = aiChatService.sendMessage(sessionId, userMessage, customerId, history);
            
            System.out.println("【AI聊天】AI回复内容: " + (response.getReplyContent() != null ? response.getReplyContent().substring(0, Math.min(100, response.getReplyContent().length())) + "..." : "null"));
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("id", response.getId());
            result.put("sessionId", sessionId);
            result.put("userMessage", userMessage);
            result.put("replyContent", response.getReplyContent());
            result.put("content", response.getContent());
            result.put("createTime", response.getCreateTime());
            
            System.out.println("【AI聊天】返回成功");
            return Result.success(result);
        } catch (Exception e) {
            System.out.println("【AI聊天】异常: " + e.getMessage());
            System.out.println("【AI聊天】异常类型: " + e.getClass().getName());
            e.printStackTrace();
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
    
    /**
     * 获取会话列表
     */
    @GetMapping("/sessions")
    public Result<List<Map<String, Object>>> getSessionList(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "50") Integer limit) {
        try {
            List<Map<String, Object>> sessions = aiChatService.getSessionList(userId, limit);
            return Result.success(sessions);
        } catch (Exception e) {
            return Result.error("获取会话列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据会话ID获取消息列表
     */
    @GetMapping("/messages")
    public Result<List<AiChat>> getMessagesBySessionId(@RequestParam String sessionId) {
        try {
            List<AiChat> messages = aiChatService.getMessagesBySessionId(sessionId);
            return Result.success(messages);
        } catch (Exception e) {
            return Result.error("获取消息列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建新会话
     */
    @PostMapping("/sessions/new")
    public Result<Map<String, Object>> createNewSession(@RequestBody Map<String, Object> request) {
        try {
            Long userId = request.get("userId") != null ? 
                Long.valueOf(request.get("userId").toString()) : null;
            Long customerId = request.get("customerId") != null ? 
                Long.valueOf(request.get("customerId").toString()) : null;
            
            String sessionId = aiChatService.createNewSession(userId, customerId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("sessionId", sessionId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("创建新会话失败: " + e.getMessage());
        }
    }
}