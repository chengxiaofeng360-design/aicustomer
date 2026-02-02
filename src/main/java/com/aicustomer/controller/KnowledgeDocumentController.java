package com.aicustomer.controller;

import com.aicustomer.common.Result;
import com.aicustomer.entity.KnowledgeDocument;
import com.aicustomer.service.KnowledgeDocumentService;
import com.aicustomer.service.VectorSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * 知识库文档控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/knowledge-doc")
@RequiredArgsConstructor
public class KnowledgeDocumentController {

    private final KnowledgeDocumentService knowledgeDocumentService;
    private final VectorSearchService vectorSearchService;

    @Value("${file.upload.path:uploads/knowledge}")
    private String uploadPath;

    /**
     * 创建文档
     */
    @PostMapping
    public Result<KnowledgeDocument> createDocument(@RequestBody KnowledgeDocument document) {
        KnowledgeDocument created = knowledgeDocumentService.createDocument(document);
        // 自动索引到ES向量库
        indexToVectorSearch(created);
        return Result.success(created);
    }

    /**
     * 更新文档
     */
    @PutMapping
    public Result<KnowledgeDocument> updateDocument(@RequestBody KnowledgeDocument document) {
        return Result.success(knowledgeDocumentService.updateDocument(document));
    }

    /**
     * 删除文档
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteDocument(@PathVariable Long id) {
        knowledgeDocumentService.deleteDocument(id);
        return Result.success();
    }

    /**
     * 获取文档列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getDocumentList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String documentType,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result
                .success(knowledgeDocumentService.getDocumentList(keyword, category, documentType, pageNum, pageSize));
    }

    /**
     * 获取分类统计
     */
    @GetMapping("/statistics/category-counts")
    public Result<Map<String, Long>> getCategoryCounts() {
        return Result.success(knowledgeDocumentService.getCategoryCounts());
    }

    /**
     * 获取文档详情
     */
    @GetMapping("/{id}")
    public Result<KnowledgeDocument> getDocument(@PathVariable Long id) {
        knowledgeDocumentService.incrementViewCount(id);
        return Result.success(knowledgeDocumentService.getDocument(id));
    }

    /**
     * 增加下载次数
     */
    @PostMapping("/{id}/download")
    public Result<Void> incrementDownloadCount(@PathVariable Long id) {
        knowledgeDocumentService.incrementDownloadCount(id);
        return Result.success();
    }

    /**
     * 预览文档
     */
    @GetMapping("/{id}/preview")
    public ResponseEntity<Resource> getDocumentPreview(@PathVariable Long id) {
        KnowledgeDocument doc = knowledgeDocumentService.getDocument(id);
        if (doc == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path filePath = Paths.get(doc.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = "application/octet-stream";
                String fileName = doc.getFileName().toLowerCase();

                if (fileName.endsWith(".pdf")) {
                    contentType = "application/pdf";
                } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                } else if (fileName.endsWith(".png")) {
                    contentType = "image/png";
                } else if (fileName.endsWith(".txt")) {
                    contentType = "text/plain";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 上传文档
     */
    @PostMapping("/upload")
    public Result<KnowledgeDocument> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", defaultValue = "other") String category,
            @RequestParam(value = "documentType", defaultValue = "other") String documentType,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "tags", required = false) String tags) {

        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }

        try {
            // 1. 保存文件
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            String newFilename = UUID.randomUUID().toString() + "." + extension;

            // 确保上传目录存在（使用配置的路径）
            String uploadDir = uploadPath.endsWith("/") ? uploadPath : uploadPath + "/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            Path filePath = Paths.get(uploadDir + newFilename);
            Files.write(filePath, file.getBytes());
            log.info("文件保存到: {}", filePath.toAbsolutePath());

            // 2. 提取文本内容
            String content = "";
            if ("pdf".equals(extension)) {
                try (PDDocument document = PDDocument.load(file.getInputStream())) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    content = stripper.getText(document);
                }
            } else if ("docx".equals(extension)) {
                try (XWPFDocument document = new XWPFDocument(file.getInputStream());
                        XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
                    content = extractor.getText();
                }
            } else if ("txt".equals(extension)) {
                content = new String(file.getBytes());
            } else {
                // 其他格式暂不支持提取内容，只保存文件
                content = "暂不支持提取该文件格式的内容: " + originalFilename;
            }

            // 3. 创建文档记录
            KnowledgeDocument document = new KnowledgeDocument();
            document.setTitle(title != null && !title.isEmpty() ? title : originalFilename);
            document.setFileName(originalFilename);
            document.setFileType(extension);
            document.setFileSize(file.getSize());
            document.setFilePath(filePath.toString());
            document.setContent(content);
            document.setCategory(category);
            document.setDocumentType(documentType);
            document.setTags(tags);

            // 生成摘要（简单截取前200字）
            if (content.length() > 200) {
                document.setSummary(content.substring(0, 200) + "...");
            } else {
                document.setSummary(content);
            }

            KnowledgeDocument created = knowledgeDocumentService.createDocument(document);
            // 自动索引到ES向量库
            indexToVectorSearch(created);
            return Result.success(created);

        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 批量索引所有文档到向量库
     */
    @PostMapping("/index-all")
    public Result<Map<String, Object>> indexAllDocuments() {
        try {
            if (!vectorSearchService.isAvailable()) {
                return Result.error("向量搜索服务不可用，请检查ES和Embedding服务是否启动");
            }

            // 获取所有文档
            Map<String, Object> docs = knowledgeDocumentService.getDocumentList(null, null, null, 1, 1000);
            @SuppressWarnings("unchecked")
            List<KnowledgeDocument> list = (List<KnowledgeDocument>) docs.get("list");

            int successCount = 0;
            int failCount = 0;

            for (KnowledgeDocument doc : list) {
                try {
                    indexToVectorSearch(doc);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    log.error("索引文档失败: id={}, error={}", doc.getId(), e.getMessage());
                }
            }

            Map<String, Object> result = Map.of(
                    "total", list.size(),
                    "success", successCount,
                    "fail", failCount,
                    "message", "索引完成");

            log.info("批量索引完成: {}", result);
            return Result.success(result);

        } catch (Exception e) {
            log.error("批量索引失败", e);
            return Result.error("批量索引失败: " + e.getMessage());
        }
    }

    /**
     * 索引单个文档到向量库
     */
    private void indexToVectorSearch(KnowledgeDocument doc) {
        try {
            if (vectorSearchService.isAvailable() && doc != null && doc.getId() != null) {
                vectorSearchService.indexDocument(
                        doc.getId(),
                        doc.getTitle(),
                        doc.getContent(),
                        doc.getCategory(),
                        doc.getTags());
                log.info("文档已索引到向量库: id={}, title={}", doc.getId(), doc.getTitle());
            }
        } catch (Exception e) {
            log.warn("索引到向量库失败，不影响正常使用: {}", e.getMessage());
        }
    }
}
