package com.aicustomer.controller;

import com.aicustomer.entity.KbCategory;
import com.aicustomer.entity.KbDocument;
import com.aicustomer.service.KbCategoryService;
import com.aicustomer.service.KbDocumentService;
import com.aicustomer.common.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 知识库控制器
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {
    
    private final KbCategoryService categoryService;
    private final KbDocumentService documentService;
    
    /**
     * 获取所有分类
     */
    @GetMapping("/categories")
    public Result<List<KbCategory>> getCategories() {
        try {
            List<KbCategory> categories = categoryService.getAllCategories();
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取分类失败", e);
            return Result.error("获取分类失败");
        }
    }
    
    /**
     * 获取分类树
     */
    @GetMapping("/category-tree")
    public Result<List<KbCategory>> getCategoryTree() {
        try {
            List<KbCategory> tree = categoryService.getCategoryTree();
            return Result.success(tree);
        } catch (Exception e) {
            log.error("获取分类树失败", e);
            return Result.error("获取分类树失败");
        }
    }
    
    /**
     * 创建分类
     */
    @PostMapping("/categories")
    public Result<KbCategory> createCategory(@RequestBody KbCategory category) {
        try {
            KbCategory created = categoryService.createCategory(category);
            return Result.success(created);
        } catch (Exception e) {
            log.error("创建分类失败", e);
            return Result.error("创建分类失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新分类
     */
    @PutMapping("/categories/{id}")
    public Result<KbCategory> updateCategory(@PathVariable Long id, @RequestBody KbCategory category) {
        try {
            category.setId(id);
            KbCategory updated = categoryService.updateCategory(category);
            return Result.success(updated);
        } catch (Exception e) {
            log.error("更新分类失败", e);
            return Result.error("更新分类失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除分类
     */
    @DeleteMapping("/categories/{id}")
    public Result<Boolean> deleteCategory(@PathVariable Long id) {
        try {
            boolean result = categoryService.deleteCategory(id);
            return Result.success(result);
        } catch (Exception e) {
            log.error("删除分类失败", e);
            return Result.error("删除分类失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传文档
     */
    @PostMapping("/upload")
    public Result<KbDocument> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "tags", required = false) String tags) {
        try {
            KbDocument document = documentService.uploadDocument(file, categoryId, tags);
            return Result.success(document);
        } catch (Exception e) {
            log.error("上传文档失败", e);
            return Result.error("上传文档失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量上传文档
     */
    @PostMapping("/upload-batch")
    public Result<List<KbDocument>> uploadDocuments(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "tags", required = false) String tags) {
        try {
            List<KbDocument> results = new ArrayList<>();
            for (MultipartFile file : files) {
                KbDocument document = documentService.uploadDocument(file, categoryId, tags);
                results.add(document);
            }
            return Result.success(results);
        } catch (Exception e) {
            log.error("批量上传文档失败", e);
            return Result.error("批量上传文档失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索文档
     */
    @GetMapping("/search")
    public Result<List<KbDocument>> searchDocuments(
            @RequestParam String query,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        try {
            List<KbDocument> documents = documentService.searchDocuments(query, categoryId, tags, limit);
            return Result.success(documents);
        } catch (Exception e) {
            log.error("搜索文档失败", e);
            return Result.error("搜索文档失败");
        }
    }
    
    /**
     * 获取文档列表
     */
    @GetMapping("/documents")
    public Result<List<KbDocument>> getDocuments(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "fileType", required = false) String fileType) {
        try {
            KbDocument condition = new KbDocument();
            condition.setCategoryId(categoryId);
            condition.setFileType(fileType);
            
            List<KbDocument> documents = documentService.getDocuments(condition, pageNum, pageSize);
            return Result.success(documents);
        } catch (Exception e) {
            log.error("获取文档列表失败", e);
            return Result.error("获取文档列表失败");
        }
    }
    
    /**
     * 根据ID获取文档
     */
    @GetMapping("/documents/{id}")
    public Result<KbDocument> getDocumentById(@PathVariable Long id) {
        try {
            KbDocument document = documentService.getDocumentById(id);
            if (document != null) {
                return Result.success(document);
            } else {
                return Result.error("文档不存在");
            }
        } catch (Exception e) {
            log.error("获取文档失败", e);
            return Result.error("获取文档失败");
        }
    }
    
    /**
     * 删除文档
     */
    @DeleteMapping("/documents/{id}")
    public Result<Boolean> deleteDocument(@PathVariable Long id) {
        try {
            boolean result = documentService.deleteDocument(id);
            return Result.success(result);
        } catch (Exception e) {
            log.error("删除文档失败", e);
            return Result.error("删除文档失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取热门文档
     */
    @GetMapping("/documents/hot")
    public Result<List<KbDocument>> getHotDocuments(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        try {
            List<KbDocument> documents = documentService.getHotDocuments(limit);
            return Result.success(documents);
        } catch (Exception e) {
            log.error("获取热门文档失败", e);
            return Result.error("获取热门文档失败");
        }
    }
    
    /**
     * 智能分类
     */
    @PostMapping("/smart-classify")
    public Result<Long> smartClassify(@RequestBody SmartClassifyRequest request) {
        try {
            Long categoryId = documentService.smartClassify(request.getFileName(), request.getContent());
            return Result.success(categoryId);
        } catch (Exception e) {
            log.error("智能分类失败", e);
            return Result.error("智能分类失败");
        }
    }
    
    /**
     * 生成标签
     */
    @PostMapping("/generate-tags")
    public Result<String> generateTags(@RequestBody GenerateTagsRequest request) {
        try {
            String tags = documentService.generateTags(request.getContent());
            return Result.success(tags);
        } catch (Exception e) {
            log.error("生成标签失败", e);
            return Result.error("生成标签失败");
        }
    }
    
    // 内部类
    public static class SmartClassifyRequest {
        private String fileName;
        private String content;
        
        // getters and setters
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
    
    public static class GenerateTagsRequest {
        private String content;
        
        // getters and setters
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}
