package com.aicustomer.service.impl;

import com.aicustomer.entity.KnowledgeDocument;
import com.aicustomer.mapper.KnowledgeDocumentMapper;
import com.aicustomer.service.KnowledgeDocumentService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import com.aicustomer.dify.client.DifyClient;
import com.aicustomer.config.DifyConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;

/**
 * 知识库文档服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeDocumentServiceImpl implements KnowledgeDocumentService {

    private final KnowledgeDocumentMapper knowledgeDocumentMapper;
    private final DifyClient difyClient;
    private final DifyConfig difyConfig;

    @Override
    public KnowledgeDocument createDocument(KnowledgeDocument document) {
        document.setCreateTime(LocalDateTime.now());
        document.setUpdateTime(LocalDateTime.now());
        document.setDeleted(0);
        if (document.getStatus() == null)
            document.setStatus(1);
        if (document.getViewCount() == null)
            document.setViewCount(0);
        if (document.getDownloadCount() == null)
            document.setDownloadCount(0);

        // 1. 上传到Dify
        if (document.getFilePath() != null) {
            try {
                File file = new File(document.getFilePath());
                if (file.exists()) {
                    log.info("开始上传文件到Dify: {}", file.getName());
                    Map<String, Object> response = difyClient.createDocumentByFile(
                            difyConfig.getDatasetId(),
                            file,
                            "system"); // user暂定system

                    if (response != null && response.containsKey("document")) {
                        Map<String, Object> docData = (Map<String, Object>) response.get("document");
                        String difyDocId = (String) docData.get("id");
                        document.setDifyDocumentId(difyDocId);
                        log.info("Dify上传成功, Document ID: {}", difyDocId);
                    } else {
                        log.error("Dify上传返回异常: {}", response);
                    }
                }
            } catch (Exception e) {
                log.error("上传文件到Dify失败", e);
                // 此时可以选择抛异常回滚，或者继续保存本地记录但标记状态
                // 这里选择继续保存，但记录错误
            }
        }

        // 2. 保存到本地数据库
        knowledgeDocumentMapper.insert(document);
        return document;
    }

    @Override
    public KnowledgeDocument updateDocument(KnowledgeDocument document) {
        document.setUpdateTime(LocalDateTime.now());
        // TODO: Dify更新文件比较复杂(通常是替换)，暂时只更新本地字段
        // 如果重新上传了文件，建议前端走"删除 -> 新增"流程
        knowledgeDocumentMapper.update(document);
        return knowledgeDocumentMapper.selectById(document.getId());
    }

    @Override
    public void deleteDocument(Long id) {
        KnowledgeDocument doc = knowledgeDocumentMapper.selectById(id);
        if (doc != null) {
            // 1. 从Dify删除
            if (doc.getDifyDocumentId() != null) {
                try {
                    difyClient.deleteDocument(difyConfig.getDatasetId(), doc.getDifyDocumentId());
                    log.info("Dify文档删除成功: {}", doc.getDifyDocumentId());
                } catch (Exception e) {
                    log.error("Dify文档删除失败", e);
                }
            }
            // 2. 本地逻辑删除
            knowledgeDocumentMapper.deleteById(id);
        }
    }

    @Override
    public KnowledgeDocument getDocument(Long id) {
        return knowledgeDocumentMapper.selectById(id);
    }

    @Override
    public Map<String, Object> getDocumentList(String keyword, String category, String documentType, int pageNum,
            int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<KnowledgeDocument> list = knowledgeDocumentMapper.selectList(keyword, category, documentType);
        PageInfo<KnowledgeDocument> pageInfo = new PageInfo<>(list);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", pageInfo.getTotal());
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("pages", pageInfo.getPages());

        return result;
    }

    @Override
    public List<KnowledgeDocument> searchDocuments(String query, int limit) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return knowledgeDocumentMapper.searchFullText(query, limit);
    }

    @Override
    public void incrementViewCount(Long id) {
        knowledgeDocumentMapper.incrementViewCount(id);
    }

    @Override
    public void incrementDownloadCount(Long id) {
        knowledgeDocumentMapper.incrementDownloadCount(id);
    }

    @Override
    public Map<String, Long> getCategoryCounts() {
        List<Map<String, Object>> counts = knowledgeDocumentMapper.selectCategoryCounts();
        Map<String, Long> result = new HashMap<>();
        long total = 0;
        for (Map<String, Object> map : counts) {
            String name = (String) map.get("name");
            Long count = ((Number) map.get("count")).longValue();
            if (name == null || name.isEmpty()) {
                name = "其他";
            }
            result.put(name, result.getOrDefault(name, 0L) + count);
            total += count;
        }
        result.put("all", total);
        return result;
    }
}
