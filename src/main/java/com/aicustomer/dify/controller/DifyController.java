package com.aicustomer.dify.controller;

import com.aicustomer.dify.service.DifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Dify 独立控制器
 * 提供全套 Dify Client 能力
 */
@Slf4j
@RestController
@RequestMapping("/api/dify")
@RequiredArgsConstructor
public class DifyController {

    private final DifyService difyService;

    /**
     * 发送消息
     */
    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody Map<String, Object> payload) {
        return difyService.chat(payload);
    }

    /**
     * 上传文件
     */
    @PostMapping("/files/upload")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file,
            @RequestParam("user") String user) {
        return difyService.uploadFile(file, user);
    }

    /**
     * 消息反馈
     */
    @PostMapping("/messages/{messageId}/feedbacks")
    public Map<String, Object> feedback(@PathVariable String messageId,
            @RequestBody Map<String, String> body) {
        return difyService.feedback(messageId, body.get("rating"), body.get("user"));
    }

    /**
     * 获取会话列表
     */
    @GetMapping("/conversations")
    public Map<String, Object> getConversations(@RequestParam String user,
            @RequestParam(required = false) String last_id,
            @RequestParam(defaultValue = "20") int limit) {
        return difyService.getConversations(user, last_id, limit);
    }

    /**
     * 获取会话历史
     */
    @GetMapping("/messages")
    public Map<String, Object> getMessages(@RequestParam String user,
            @RequestParam String conversation_id,
            @RequestParam(required = false) String first_id,
            @RequestParam(defaultValue = "20") int limit) {
        return difyService.getConversationMessages(conversation_id, user, first_id, limit);
    }

    /**
     * 重命名会话
     */
    @PostMapping("/conversations/{conversationId}/name")
    public Map<String, Object> renameConversation(@PathVariable String conversationId,
            @RequestBody Map<String, String> body) {
        return difyService.renameConversation(conversationId, body.get("name"), body.get("user"));
    }

    /**
     * 删除会话
     */
    @DeleteMapping("/conversations/{conversationId}")
    public Map<String, Object> deleteConversation(@PathVariable String conversationId,
            @RequestBody Map<String, String> body) {
        return difyService.deleteConversation(conversationId, body.get("user"));
    }
}
