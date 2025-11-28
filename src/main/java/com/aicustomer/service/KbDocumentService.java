package com.aicustomer.service;

import com.aicustomer.entity.KbDocument;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 知识库文档服务接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
public interface KbDocumentService {
    
    /**
     * 上传文档
     */
    KbDocument uploadDocument(MultipartFile file, Long categoryId, String tags);
    
    /**
     * 分页查询文档
     */
    List<KbDocument> getDocuments(KbDocument document, int pageNum, int pageSize);
    
    /**
     * 根据ID获取文档
     */
    KbDocument getDocumentById(Long id);
    
    /**
     * 搜索文档
     */
    List<KbDocument> searchDocuments(String query, Long categoryId, String tags, int limit);
    
    /**
     * 删除文档
     */
    boolean deleteDocument(Long id);
    
    /**
     * 智能分类
     */
    Long smartClassify(String fileName, String content);
    
    /**
     * 生成标签
     */
    String generateTags(String content);
    
    /**
     * 提取关键词
     */
    String extractKeywords(String content);
    
    /**
     * 生成摘要
     */
    String generateSummary(String content);
    
    /**
     * 更新查看次数
     */
    void updateViewCount(Long id);
    
    /**
     * 更新下载次数
     */
    void updateDownloadCount(Long id);
    
    /**
     * 获取热门文档
     */
    List<KbDocument> getHotDocuments(int limit);
}
