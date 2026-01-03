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
    private final com.aicustomer.service.UserService userService;

    /**
     * 发送消息（支持多轮对话）
     */

    private String getCurrentUsername() {
        try {
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication();
            if (auth != null
                    && auth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
                return ((org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal()).getUsername();
            } else if (auth != null) {
                return auth.getPrincipal().toString();
            }
        } catch (Exception e) {
        }
        return "anonymous";
    }

    /**
     * 发送消息（支持多轮对话）
     */
    @PostMapping("/send")
    public Result<Map<String, Object>> sendMessage(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("【AI聊天】收到发送消息请求");
            String sessionId = request.get("sessionId").toString();
            String userMessage = request.get("message").toString();
            Long customerId = request.get("customerId") != null ? Long.valueOf(request.get("customerId").toString())
                    : null;

            // 获取当前登录用户
            String username = getCurrentUsername();
            Long userId = 0L;
            if (!"anonymous".equals(username)) {
                com.aicustomer.entity.User user = userService.findByUsername(username);
                if (user != null) {
                    userId = user.getId();
                }
            }

            // 支持传递对话历史（用于多轮对话）
            @SuppressWarnings("unchecked")
            List<Map<String, String>> history = (List<Map<String, String>>) request.get("history");

            // 传递 userId 到 Service (需要修改 Service 接口)
            // 这里为了不修改 Service 太多签名，我们暂时将 userId 放入 history 或者 threadLocal，或者重载方法
            // 最佳实践：重载 sendMessage 方法接受 userId
            AiChat response = aiChatService.sendMessage(sessionId, userMessage, customerId, history, userId);

            // ... (rest of method)
            Map<String, Object> result = new HashMap<>();
            result.put("id", response.getId());
            result.put("sessionId", sessionId);
            result.put("userMessage", userMessage);
            result.put("replyContent", response.getReplyContent());
            result.put("content", response.getContent());
            result.put("createTime", response.getCreateTime());

            return Result.success(result);
        } catch (Exception e) {
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
            String username = getCurrentUsername();
            Long userId = null;
            if (!"anonymous".equals(username)) {
                com.aicustomer.entity.User user = userService.findByUsername(username);
                if (user != null) {
                    userId = user.getId();
                }
            }
            // Pass userId to service
            Map<String, Object> history = aiChatService.getChatHistory(pageNum, pageSize, userId);
            return Result.success(history);
        } catch (Exception e) {
            return Result.error("获取聊天历史失败: " + e.getMessage());
        }
    }

    // ... (other methods)

    /**
     * 获取会话列表
     */
    @GetMapping("/sessions")
    public Result<List<Map<String, Object>>> getSessionList(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "50") Integer limit) {
        try {
            // 如果未传userId，则使用当前登录用户Id
            if (userId == null) {
                String username = getCurrentUsername();
                if (!"anonymous".equals(username)) {
                    com.aicustomer.entity.User user = userService.findByUsername(username);
                    if (user != null) {
                        userId = user.getId();
                    }
                }
            }
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
            Long userId = request.get("userId") != null ? Long.valueOf(request.get("userId").toString()) : null;
            Long customerId = request.get("customerId") != null ? Long.valueOf(request.get("customerId").toString())
                    : null;

            String sessionId = aiChatService.createNewSession(userId, customerId);

            Map<String, Object> result = new HashMap<>();
            result.put("sessionId", sessionId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("创建新会话失败: " + e.getMessage());
        }
    }

    /**
     * 删除会话
     */
    @DeleteMapping("/sessions/{sessionId}")
    public Result<Void> deleteSession(@PathVariable String sessionId) {
        try {
            aiChatService.deleteSession(sessionId);
            return Result.success();
        } catch (Exception e) {
            return Result.error("删除会话失败: " + e.getMessage());
        }
    }
}