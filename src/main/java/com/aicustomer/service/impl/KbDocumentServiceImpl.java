package com.aicustomer.service.impl;

import com.aicustomer.entity.KbDocument;
import com.aicustomer.mapper.KbDocumentMapper;
import com.aicustomer.service.KbDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 知识库文档服务实现类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KbDocumentServiceImpl implements KbDocumentService {
    
    private final KbDocumentMapper documentMapper;
    
    // 文件存储路径
    private static final String UPLOAD_PATH = "uploads/knowledge/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    
    @Override
    public KbDocument uploadDocument(MultipartFile file, Long categoryId, String tags) {
        try {
            // 1. 验证文件
            validateFile(file);
            
            // 2. 保存文件
            String filePath = saveFile(file);
            
            // 3. 提取文本内容
            String content = extractTextContent(file, filePath);
            
            // 4. 智能分类
            if (categoryId == null) {
                categoryId = smartClassify(file.getOriginalFilename(), content);
            }
            
            // 5. 生成标签
            if (tags == null || tags.trim().isEmpty()) {
                tags = generateTags(content);
            }
            
            // 6. 提取关键词
            String keywords = extractKeywords(content);
            
            // 7. 生成摘要
            String summary = generateSummary(content);
            
            // 8. 创建文档对象
            KbDocument document = new KbDocument();
            document.setTitle(file.getOriginalFilename());
            document.setContent(content);
            document.setFilePath(filePath);
            document.setFileName(file.getOriginalFilename());
            document.setFileType(detectFileType(file));
            document.setFileMime(file.getContentType());
            document.setFileSize(file.getSize());
            document.setCategoryId(categoryId);
            document.setTags(tags);
            document.setAutoTags(generateAutoTags(content));
            document.setKeywords(keywords);
            document.setSummary(summary);
            document.setPriority(0);
            document.setViewCount(0);
            document.setDownloadCount(0);
            document.setLikeCount(0);
            document.setIsActive(true);
            document.setIsPublic(true);
            document.setCreateBy("system");
            
            // 9. 保存到数据库
            int result = documentMapper.insert(document);
            if (result > 0) {
                log.info("文档上传成功: {}", file.getOriginalFilename());
                return document;
            } else {
                throw new RuntimeException("保存文档失败");
            }
            
        } catch (Exception e) {
            log.error("文档上传失败: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("文档上传失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<KbDocument> getDocuments(KbDocument document, int pageNum, int pageSize) {
        try {
            int offset = (pageNum - 1) * pageSize;
            return documentMapper.selectPage(document, offset, pageSize);
        } catch (Exception e) {
            log.error("获取文档列表失败", e);
            throw new RuntimeException("获取文档列表失败", e);
        }
    }
    
    @Override
    public KbDocument getDocumentById(Long id) {
        try {
            KbDocument document = documentMapper.selectById(id);
            if (document != null) {
                // 更新查看次数
                updateViewCount(id);
            }
            return document;
        } catch (Exception e) {
            log.error("根据ID获取文档失败: {}", id, e);
            throw new RuntimeException("获取文档失败", e);
        }
    }
    
    @Override
    public List<KbDocument> searchDocuments(String query, Long categoryId, String tags, int limit) {
        try {
            List<KbDocument> results = new ArrayList<>();
            
            // 1. 全文搜索
            if (query != null && !query.trim().isEmpty()) {
                results.addAll(documentMapper.fullTextSearch(query, limit));
            }
            
            // 2. 分类过滤
            if (categoryId != null) {
                results.addAll(documentMapper.selectByCategoryId(categoryId, limit));
            }
            
            // 3. 标签过滤
            if (tags != null && !tags.trim().isEmpty()) {
                results.addAll(documentMapper.selectByTags(tags, limit));
            }
            
            // 去重并限制数量
            return results.stream()
                    .distinct()
                    .limit(limit)
                    .collect(java.util.stream.Collectors.toList());
            
        } catch (Exception e) {
            log.error("搜索文档失败", e);
            throw new RuntimeException("搜索文档失败", e);
        }
    }
    
    @Override
    public boolean deleteDocument(Long id) {
        try {
            // 1. 获取文档信息
            KbDocument document = documentMapper.selectById(id);
            if (document == null) {
                return false;
            }
            
            // 2. 删除文件
            if (document.getFilePath() != null) {
                try {
                    Files.deleteIfExists(Paths.get(document.getFilePath()));
                } catch (IOException e) {
                    log.warn("删除文件失败: {}", document.getFilePath(), e);
                }
            }
            
            // 3. 删除数据库记录
            int result = documentMapper.deleteById(id);
            if (result > 0) {
                log.info("删除文档成功: {}", id);
                return true;
            } else {
                return false;
            }
            
        } catch (Exception e) {
            log.error("删除文档失败: {}", id, e);
            throw new RuntimeException("删除文档失败", e);
        }
    }
    
    @Override
    public Long smartClassify(String fileName, String content) {
        // 智能分类逻辑
        String text = (fileName + " " + content).toLowerCase();
        
        // 分类规则
        if (text.contains("产品") || text.contains("种子") || text.contains("品种")) {
            return 1L; // 产品知识
        } else if (text.contains("技术") || text.contains("指南") || text.contains("操作")) {
            return 2L; // 技术文档
        } else if (text.contains("流程") || text.contains("制度") || text.contains("规定")) {
            return 3L; // 业务流程
        } else if (text.contains("问题") || text.contains("faq") || text.contains("解答")) {
            return 4L; // 常见问题
        } else if (text.contains("合同") || text.contains("协议") || text.contains("模板")) {
            return 5L; // 合同模板
        } else {
            return 6L; // 其他
        }
    }
    
    @Override
    public String generateTags(String content) {
        // 简单的标签生成逻辑
        List<String> tags = new ArrayList<>();
        
        // 预定义关键词
        String[] keywords = {"种子", "玉米", "水稻", "小麦", "大豆", "种植", "施肥", 
                           "病虫害", "合同", "质量", "技术", "产品", "规格", "操作"};
        
        for (String keyword : keywords) {
            if (content.toLowerCase().contains(keyword)) {
                tags.add(keyword);
            }
        }
        
        return String.join(",", tags);
    }
    
    @Override
    public String extractKeywords(String content) {
        // 简单的关键词提取
        return generateTags(content);
    }
    
    @Override
    public String generateSummary(String content) {
        // 简单的摘要生成
        if (content.length() <= 200) {
            return content;
        }
        return content.substring(0, 200) + "...";
    }
    
    @Override
    public void updateViewCount(Long id) {
        try {
            documentMapper.updateViewCount(id);
        } catch (Exception e) {
            log.warn("更新查看次数失败: {}", id, e);
        }
    }
    
    @Override
    public void updateDownloadCount(Long id) {
        try {
            documentMapper.updateDownloadCount(id);
        } catch (Exception e) {
            log.warn("更新下载次数失败: {}", id, e);
        }
    }
    
    @Override
    public List<KbDocument> getHotDocuments(int limit) {
        try {
            return documentMapper.selectHotDocuments(limit);
        } catch (Exception e) {
            log.error("获取热门文档失败", e);
            throw new RuntimeException("获取热门文档失败", e);
        }
    }
    
    // 私有方法
    
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("文件大小不能超过10MB");
        }
        
        String contentType = file.getContentType();
        if (!isAllowedFileType(contentType)) {
            throw new RuntimeException("不支持的文件类型");
        }
    }
    
    private boolean isAllowedFileType(String contentType) {
        List<String> allowedTypes = Arrays.asList(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "image/jpeg",
            "image/png",
            "text/plain"
        );
        return allowedTypes.contains(contentType);
    }
    
    private String saveFile(MultipartFile file) throws IOException {
        // 创建上传目录
        Path uploadDir = Paths.get(UPLOAD_PATH);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID().toString() + extension;
        
        // 保存文件
        Path filePath = uploadDir.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath);
        
        return filePath.toString();
    }
    
    private String extractTextContent(MultipartFile file, String filePath) {
        // 简化版本：直接返回文件名作为内容
        // 实际项目中应该使用Apache Tika等工具提取文本
        return "文件内容提取：" + file.getOriginalFilename();
    }
    
    private String detectFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null) {
            if (contentType.equals("application/pdf")) {
                return "pdf";
            } else if (contentType.contains("word")) {
                return "word";
            } else if (contentType.contains("excel")) {
                return "excel";
            } else if (contentType.startsWith("image/")) {
                return "image";
            } else if (contentType.equals("text/plain")) {
                return "text";
            }
        }
        return "unknown";
    }
    
    private String generateAutoTags(String content) {
        // AI自动标签生成逻辑
        return generateTags(content);
    }
}
