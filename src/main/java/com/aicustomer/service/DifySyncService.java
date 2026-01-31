package com.aicustomer.service;

import com.aicustomer.config.DifyConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Dify知识库同步服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DifySyncService {

    private final DifyConfig difyConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 同步文件到Dify知识库
     * API: POST /datasets/{dataset_id}/document/create_by_file
     */
    public void syncFile(File file, String fileName) {
        if (!difyConfig.isEnabled() || difyConfig.getDatasetId() == null) {
            log.warn("Dify同步未启用或数据集ID未配置");
            return;
        }

        try {
            String url = difyConfig.getApiUrl().replace("/v1", "") + "/v1/datasets/" + difyConfig.getDatasetId()
                    + "/document/create_by_file";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Authorization", "Bearer " + difyConfig.getApiKey()); // 注意：上传文件通常需要Dataset Key，这里暂用App
                                                                              // Key尝试，如果失败需切换

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(file));

            // 配置参数
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("indexing_technique", "high_quality"); // 高质量索引
            dataMap.put("process_rule", Map.of("mode", "automatic")); // 自动分段

            body.add("data", objectMapper.writeValueAsString(dataMap));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            log.info("开始同步文件到Dify: {}", fileName);
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("✅ 文件同步成功: {}", fileName);
            } else {
                log.error("❌ 文件同步失败: {}", response.getBody());
            }

        } catch (Exception e) {
            log.error("❌ Dify同步异常: {}", e.getMessage());
        }
    }

    /**
     * 同步问答到Dify知识库 (作为文本片段上传)
     */
    public void syncQa(String question, String answer) {
        // 由于Dify没有直接的QA API，我们将其转换为文本文件上传
        try {
            String content = "Q: " + question + "\n" + "A: " + answer;
            File tempFile = File.createTempFile("qa_sync_", ".txt");
            org.apache.commons.io.FileUtils.writeStringToFile(tempFile, content, "UTF-8");

            syncFile(tempFile, "QA_" + System.currentTimeMillis() + ".txt");

            tempFile.delete(); // 清理临时文件
        } catch (Exception e) {
            log.error("QA同步失败", e);
        }
    }
}
