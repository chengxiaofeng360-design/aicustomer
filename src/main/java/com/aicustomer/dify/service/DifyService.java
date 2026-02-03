package com.aicustomer.dify.service;

import com.aicustomer.dify.client.DifyClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;
import java.util.UUID;

/**
 * Dify业务服务层
 * 处理业务逻辑、用户映射、参数校验
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DifyService {

    private final DifyClient difyClient;

    /**
     * 发送对话消息
     */
    public Map<String, Object> chat(Map<String, Object> payload) {
        // 这里可以添加业务逻辑，例如记录日志、转换UserId等
        return difyClient.sendChatMessage(payload);
    }

    /**
     * 发送对话消息 (流式)
     */
    public void streamChat(Map<String, Object> payload, java.util.function.Consumer<String> chunkHandler) {
        difyClient.streamChatMessage(payload, chunkHandler);
    }

    /**
     * 上传文件
     */
    public Map<String, Object> uploadFile(MultipartFile multipartFile, String userId) {
        File tempFile = null;
        try {
            // 转换MultipartFile为临时File
            String originalFilename = multipartFile.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            tempFile = File.createTempFile("upload_" + UUID.randomUUID(), extension);
            multipartFile.transferTo(tempFile);

            return difyClient.uploadFile(tempFile, userId);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件处理失败: " + e.getMessage());
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    /**
     * 消息反馈
     */
    public Map<String, Object> feedback(String messageId, String rating, String userId) {
        return difyClient.messageFeedback(messageId, rating, userId);
    }

    /**
     * 获取会话列表
     */
    public Map<String, Object> getConversations(String userId, String lastId, int limit) {
        return difyClient.getConversations(userId, lastId, limit);
    }

    /**
     * 获取会话详情（消息历史）
     */
    public Map<String, Object> getConversationMessages(String conversationId, String userId, String firstId,
            int limit) {
        return difyClient.getConversationMessages(conversationId, userId, firstId, limit);
    }

    /**
     * 重命名会话
     */
    public Map<String, Object> renameConversation(String conversationId, String name, String userId) {
        return difyClient.renameConversation(conversationId, name, userId);
    }

    /**
     * 删除会话
     */
    public Map<String, Object> deleteConversation(String conversationId, String userId) {
        return difyClient.deleteConversation(conversationId, userId);
    }

    private final com.aicustomer.config.DifyConfig difyConfig;

    /**
     * 上传文档到知识库
     */
    public Map<String, Object> uploadKnowledgeDocument(MultipartFile multipartFile) {
        String datasetId = difyConfig.getDatasetId();
        if (datasetId == null || datasetId.isEmpty()) {
            throw new RuntimeException("Dify Knowledge Base ID (datasetId) is not configured in application.yml");
        }

        File tempFile = null;
        try {
            // 转换MultipartFile为临时File
            String originalFilename = multipartFile.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            tempFile = File.createTempFile("kb_" + UUID.randomUUID(), extension);
            multipartFile.transferTo(tempFile);

            return difyClient.createDocumentByFile(datasetId, tempFile);
        } catch (Exception e) {
            log.error("知识库文档上传失败", e);
            throw new RuntimeException("知识库上传失败: " + e.getMessage());
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

}
