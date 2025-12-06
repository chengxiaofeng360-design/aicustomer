package com.aicustomer.service;

import com.aicustomer.entity.KnowledgeDocument;
import java.util.List;
import java.util.Map;

/**
 * 知识库文档服务接口
 */
public interface KnowledgeDocumentService {

    /**
     * 创建文档
     */
    KnowledgeDocument createDocument(KnowledgeDocument document);

    /**
     * 更新文档
     */
    KnowledgeDocument updateDocument(KnowledgeDocument document);

    /**
     * 删除文档
     */
    void deleteDocument(Long id);

    /**
     * 获取文档详情
     */
    KnowledgeDocument getDocument(Long id);

    /**
     * 获取文档列表
     */
    Map<String, Object> getDocumentList(String keyword, String category, String documentType, int pageNum,
            int pageSize);

    /**
     * 全文搜索文档
     * 
     * @param query 搜索关键词
     * @param limit 返回数量
     * @return 匹配的文档列表
     */
    List<KnowledgeDocument> searchDocuments(String query, int limit);

    /**
     * 增加查看次数
     */
    void incrementViewCount(Long id);

    /**
     * 增加下载次数
     */
    void incrementDownloadCount(Long id);
}
