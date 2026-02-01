package com.aicustomer.service;

import java.util.List;
import java.util.Map;

/**
 * 向量搜索服务接口
 * 
 * @deprecated 请优先使用 Dify 平台提供的向量检索和 RAG 能力
 */
@Deprecated
public interface VectorSearchService {

    /**
     * 将文档索引到ES（包括向量化）
     * 
     * @param docId    文档ID
     * @param title    标题
     * @param content  内容
     * @param category 分类
     * @param tags     标签
     */
    void indexDocument(Long docId, String title, String content, String category, String tags);

    /**
     * 向量相似度搜索
     * 
     * @param query 搜索查询
     * @param limit 返回数量
     * @return 匹配的文档ID和分数列表
     */
    List<Map<String, Object>> searchByVector(String query, int limit);

    /**
     * 删除文档索引
     * 
     * @param docId 文档ID
     */
    void deleteDocument(Long docId);

    /**
     * 检查服务是否可用
     */
    boolean isAvailable();
}
