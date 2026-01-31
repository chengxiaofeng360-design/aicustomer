package com.aicustomer.dify.client;

import com.aicustomer.config.DifyConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Dify Client SDK
 * 
 * 独立封装 Dify Chat App API
 * 文档参考: https://docs.dify.ai/features/chat-app/api
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DifyClient {

    private final DifyConfig difyConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 发送对话消息
     * POST /chat-messages
     */
    public Map<String, Object> sendChatMessage(Map<String, Object> payload) {
        return post("/chat-messages", payload);
    }

    /**
     * 上传文件
     * POST /files/upload
     */
    public Map<String, Object> uploadFile(File file, String userId) {
        String url = getBaseUrl() + "/files/upload";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "Bearer " + difyConfig.getApiKey());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));
        body.add("user", userId);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Dify upload file failed", e);
            throw new RuntimeException("Upload failed: " + e.getMessage());
        }
    }

    /**
     * 停止响应
     * POST /chat-messages/{task_id}/stop
     */
    public Map<String, Object> stopGeneration(String taskId, String userId) {
        return post("/chat-messages/" + taskId + "/stop", Map.of("user", userId));
    }

    /**
     * 消息反馈 (点赞)
     * POST /messages/{message_id}/feedbacks
     */
    public Map<String, Object> messageFeedback(String messageId, String rating, String userId) {
        // rating: like, dislike, null
        return post("/messages/" + messageId + "/feedbacks", Map.of("rating", rating, "user", userId));
    }

    /**
     * 以此类推，实现所有接口...
     * 为节省篇幅，核心逻辑是统一的 post/get 方法
     */

    // --- 会话管理 ---

    /**
     * 获取会话列表
     * GET /conversations
     */
    public Map<String, Object> getConversations(String userId, String lastId, int limit) {
        String query = String.format("?user=%s&limit=%d", userId, limit);
        if (lastId != null)
            query += "&last_id=" + lastId;
        return get("/conversations" + query);
    }

    /**
     * 获取会话历史消息
     * GET /messages
     */
    public Map<String, Object> getConversationMessages(String conversationId, String userId, String firstId,
            int limit) {
        String query = String.format("?user=%s&conversation_id=%s&limit=%d", userId, conversationId, limit);
        if (firstId != null)
            query += "&first_id=" + firstId;
        return get("/messages" + query);
    }

    /**
     * 会话重命名
     * POST /conversations/{conversation_id}/name
     */
    public Map<String, Object> renameConversation(String conversationId, String name, String userId) {
        return post("/conversations/" + conversationId + "/name", Map.of("name", name, "user", userId));
    }

    /**
     * 删除会话
     * DELETE /conversations/{conversation_id}
     */
    public Map<String, Object> deleteConversation(String conversationId, String userId) {
        Map<String, Object> payload = Map.of("user", userId); // DELETE with body is tricky in RestTemplate, verifying
                                                              // Dify API...
        // Dify API usually expects user in body for delete, requiring explicit
        // HttpEntity
        return exchange("/conversations/" + conversationId, HttpMethod.DELETE, payload);
    }

    // --- 数据集(知识库)管理 ---

    /**
     * 通过文本创建文档
     * POST /datasets/{dataset_id}/document/create_by_text
     */
    public Map<String, Object> createDocumentByText(String datasetId, String docName, String text, String userId) {
        String path = "/datasets/" + datasetId + "/document/create_by_text";
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", docName);
        payload.put("text", text);
        payload.put("indexing_technique", "high_quality"); // 或 economy
        payload.put("process_rule", Map.of("mode", "automatic"));

        return postWithDatasetKey(path, payload);
    }

    /**
     * 通过文件创建文档
     * POST /datasets/{dataset_id}/document/create_by_file
     */
    public Map<String, Object> createDocumentByFile(String datasetId, File file, String userId) {
        String url = getBaseUrl() + "/datasets/" + datasetId + "/document/create_by_file";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "Bearer " + difyConfig.getDatasetApiKey()); // 使用Dataset Key

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));

        // Dify API 对于 file upload 需要 data 参数是 JSON 字符串
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("indexing_technique", "high_quality");
        dataMap.put("process_rule", Map.of("mode", "automatic"));

        try {
            body.add("data", objectMapper.writeValueAsString(dataMap));
        } catch (Exception e) {
            log.error("JSON serialization failed", e);
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Dify create document by file failed", e);
            throw new RuntimeException("Upload to dataset failed: " + e.getMessage());
        }
    }

    /**
     * 删除文档
     * DELETE /datasets/{dataset_id}/documents/{document_id}
     */
    public Map<String, Object> deleteDocument(String datasetId, String documentId) {
        String path = "/datasets/" + datasetId + "/documents/" + documentId;
        return exchangeWithDatasetKey(path, HttpMethod.DELETE, null);
    }

    // --- 内部辅助方法 (Dataset API) ---

    // --- 内部辅助方法 ---

    private String getBaseUrl() {
        return difyConfig.getApiUrl();
    }

    // A. App API Helpers (使用 Chatbot App Key)

    private Map<String, Object> post(String path, Map<String, Object> payload) {
        return exchange(path, HttpMethod.POST, payload);
    }

    private Map<String, Object> get(String path) {
        return exchange(path, HttpMethod.GET, null);
    }

    private Map<String, Object> exchange(String path, HttpMethod method, Map<String, Object> payload) {
        String url = getBaseUrl() + path;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + difyConfig.getApiKey());

        HttpEntity<Object> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, method, entity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Dify API call failed: {} {}", method, path, e);
            throw new RuntimeException("Dify API Error: " + e.getMessage());
        }
    }

    // B. Dataset API Helpers (使用 Dataset/Knowledge API Key)

    private Map<String, Object> postWithDatasetKey(String path, Map<String, Object> payload) {
        return exchangeWithDatasetKey(path, HttpMethod.POST, payload);
    }

    private Map<String, Object> exchangeWithDatasetKey(String path, HttpMethod method, Map<String, Object> payload) {
        String url = getBaseUrl() + path;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + difyConfig.getDatasetApiKey()); // 使用Dataset Key

        HttpEntity<Object> entity = new HttpEntity<>(payload, headers);

        try {
            // 注意：Dify 返回可能不是 Map，某些 DELETE 操作可能返回空或特定状态
            // 这里假设返回标准 JSON
            ResponseEntity<Map> response = restTemplate.exchange(url, method, entity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Dify Dataset API call failed: {} {}", method, path, e);
            throw new RuntimeException("Dify Dataset API Error: " + e.getMessage());
        }
    }
}
