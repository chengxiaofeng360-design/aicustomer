package com.aicustomer.entity;

import com.aicustomer.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 知识库文档实体类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KbDocument extends BaseEntity {
    
    /**
     * 文档标题
     */
    private String title;
    
    /**
     * 文档内容(文本提取后)
     */
    private String content;
    
    /**
     * 原始内容(HTML格式)
     */
    private String originalContent;
    
    /**
     * 文件存储路径
     */
    private String filePath;
    
    /**
     * 原始文件名
     */
    private String fileName;
    
    /**
     * 文件类型: text,pdf,word,excel,image
     */
    private String fileType;
    
    /**
     * MIME类型
     */
    private String fileMime;
    
    /**
     * 文件大小(字节)
     */
    private Long fileSize;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 标签，逗号分隔
     */
    private String tags;
    
    /**
     * AI自动生成的标签
     */
    private String autoTags;
    
    /**
     * 提取的关键词
     */
    private String keywords;
    
    /**
     * 自动摘要
     */
    private String summary;
    
    /**
     * 优先级，数值越大越重要
     */
    private Integer priority;
    
    /**
     * 查看次数
     */
    private Integer viewCount;
    
    /**
     * 下载次数
     */
    private Integer downloadCount;
    
    /**
     * 点赞次数
     */
    private Integer likeCount;
    
    /**
     * 是否启用
     */
    private Boolean isActive;
    
    /**
     * 是否公开
     */
    private Boolean isPublic;
}
