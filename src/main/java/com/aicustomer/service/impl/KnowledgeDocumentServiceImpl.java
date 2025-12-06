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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 知识库文档服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeDocumentServiceImpl implements KnowledgeDocumentService {

    private final KnowledgeDocumentMapper knowledgeDocumentMapper;

    @Override
    public KnowledgeDocument createDocument(KnowledgeDocument document) {
        document.setCreateTime(LocalDateTime.now());
        document.setUpdateTime(LocalDateTime.now());
        document.setDeleted(0);
        if (document.getStatus() == null) {
            document.setStatus(1);
        }
        if (document.getViewCount() == null) {
            document.setViewCount(0);
        }
        if (document.getDownloadCount() == null) {
            document.setDownloadCount(0);
        }

        knowledgeDocumentMapper.insert(document);
        return document;
    }

    @Override
    public KnowledgeDocument updateDocument(KnowledgeDocument document) {
        document.setUpdateTime(LocalDateTime.now());
        knowledgeDocumentMapper.update(document);
        return knowledgeDocumentMapper.selectById(document.getId());
    }

    @Override
    public void deleteDocument(Long id) {
        knowledgeDocumentMapper.deleteById(id);
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
}
