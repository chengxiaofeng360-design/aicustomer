package com.aicustomer.service.impl;

import com.aicustomer.service.VectorSearchService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.util.*;

/**
 * 向量搜索服务实现
 * 使用Elasticsearch + Python Embedding服务
 */
@Slf4j
@Service
public class VectorSearchServiceImpl implements VectorSearchService {

    @Value("${vector.elasticsearch.url:http://localhost:9200}")
    private String esUrl;

    @Value("${vector.embedding.url:http://localhost:5001}")
    private String embeddingUrl;

    @Value("${vector.index.name:knowledge_docs}")
    private String indexName;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.aicustomer.config.DifyConfig difyConfig;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private boolean serviceAvailable = false;

    /**
     * @deprecated 请优先使用 Dify 引擎的知识库检索能力
     */
    @Deprecated
    @PostConstruct
    public void init() {
        if (difyConfig != null && difyConfig.isEnabled()) {
            log.info("Dify 引擎已启用，自动停用本地向量搜索服务 (VectorSearchService)");
            this.serviceAvailable = false;
            return;
        }
        checkServiceAvailability();
    }

    private void checkServiceAvailability() {
        try {
            // 检查ES
            ResponseEntity<String> esResponse = restTemplate.getForEntity(esUrl, String.class);
            boolean esOk = esResponse.getStatusCode().is2xxSuccessful();

            // 检查Embedding服务
            boolean embeddingOk = false;
            try {
                ResponseEntity<String> embResponse = restTemplate.getForEntity(embeddingUrl + "/health", String.class);
                embeddingOk = embResponse.getStatusCode().is2xxSuccessful();
            } catch (Exception e) {
                log.warn("Embedding服务不可用: {}", e.getMessage());
            }

            serviceAvailable = esOk && embeddingOk;
            log.info("向量搜索服务状态 - ES: {}, Embedding: {}, 总体: {}", esOk, embeddingOk, serviceAvailable);
        } catch (Exception e) {
            log.error("向量搜索服务检查失败: {}", e.getMessage());
            serviceAvailable = false;
        }
    }

    @Override
    public boolean isAvailable() {
        return serviceAvailable;
    }

    @Override
    public void indexDocument(Long docId, String title, String content, String category, String tags) {
        if (!serviceAvailable) {
            log.warn("向量搜索服务不可用，跳过索引");
            return;
        }

        try {
            // 1. 获取文本向量
            String textToEmbed = title + " "
                    + (content != null ? content.substring(0, Math.min(content.length(), 1000)) : "");
            List<Double> vector = getEmbedding(textToEmbed);

            if (vector == null || vector.isEmpty()) {
                log.error("获取向量失败，docId: {}", docId);
                return;
            }

            // 2. 索引到ES
            Map<String, Object> doc = new HashMap<>();
            doc.put("id", docId);
            doc.put("title", title);
            doc.put("content", content);
            doc.put("category", category);
            doc.put("tags", tags);
            doc.put("vector", vector);
            doc.put("create_time", new Date());

            String url = esUrl + "/" + indexName + "/_doc/" + docId;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(doc), headers);

            restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
            log.info("文档索引成功，docId: {}", docId);

        } catch (Exception e) {
            log.error("文档索引失败，docId: {}, error: {}", docId, e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> searchByVector(String query, int limit) {
        List<Map<String, Object>> results = new ArrayList<>();

        if (!serviceAvailable) {
            checkServiceAvailability(); // 重新检查
            if (!serviceAvailable) {
                log.warn("向量搜索服务不可用");
                return results;
            }
        }

        try {
            // 1. 获取查询向量
            List<Double> queryVector = getEmbedding(query);
            if (queryVector == null || queryVector.isEmpty()) {
                log.error("获取查询向量失败");
                return results;
            }

            // 2. ES向量搜索
            Map<String, Object> knnQuery = new HashMap<>();
            knnQuery.put("field", "vector");
            knnQuery.put("query_vector", queryVector);
            knnQuery.put("k", limit);
            knnQuery.put("num_candidates", limit * 2);

            Map<String, Object> searchBody = new HashMap<>();
            searchBody.put("knn", knnQuery);
            searchBody.put("_source", Arrays.asList("id", "title", "content", "category", "tags"));

            String url = esUrl + "/" + indexName + "/_search";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(searchBody), headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode hits = root.path("hits").path("hits");

                for (JsonNode hit : hits) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("id", hit.path("_source").path("id").asLong());
                    result.put("title", hit.path("_source").path("title").asText());
                    result.put("content", hit.path("_source").path("content").asText());
                    result.put("category", hit.path("_source").path("category").asText());
                    result.put("score", hit.path("_score").asDouble());
                    results.add(result);
                }

                log.info("向量搜索完成，查询: {}, 结果数: {}", query, results.size());
            }

        } catch (Exception e) {
            log.error("向量搜索失败: {}", e.getMessage(), e);
        }

        return results;
    }

    @Override
    public void deleteDocument(Long docId) {
        if (!serviceAvailable)
            return;

        try {
            String url = esUrl + "/" + indexName + "/_doc/" + docId;
            restTemplate.delete(url);
            log.info("文档删除成功，docId: {}", docId);
        } catch (Exception e) {
            log.error("文档删除失败，docId: {}", docId);
        }
    }

    /**
     * 调用Python服务获取文本向量
     */
    @SuppressWarnings("unchecked")
    private List<Double> getEmbedding(String text) {
        try {
            Map<String, String> body = new HashMap<>();
            body.put("text", text);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(body), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    embeddingUrl + "/embed", request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode vectorNode = root.path("vector");

                List<Double> vector = new ArrayList<>();
                for (JsonNode v : vectorNode) {
                    vector.add(v.asDouble());
                }
                return vector;
            }
        } catch (Exception e) {
            log.error("获取Embedding失败: {}", e.getMessage());
        }
        return null;
    }
}
