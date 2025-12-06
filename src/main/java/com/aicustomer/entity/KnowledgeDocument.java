package com.aicustomer.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 知识库文档实体类
 * 对应数据库表: knowledge_document
 */
@Data
public class KnowledgeDocument {
    /** 主键ID */
    private Long id;

    /** 文档标题 */
    private String title;

    /** 文档内容 */
    private String content;

    /** 文件名 */
    private String fileName;

    /** 文件类型(pdf/word/excel/txt) */
    private String fileType;

    /** 文件大小(字节) */
    private Long fileSize;

    /** 文件路径 */
    private String filePath;

    /** 文档类型 */
    private String documentType;

    /** 标签(逗号分隔) */
    private String tags;

    /** 分类 */
    private String category;

    /** 摘要 */
    private String summary;

    /** 关键词 */
    private String keywords;

    /** 查看次数 */
    private Integer viewCount;

    /** 下载次数 */
    private Integer downloadCount;

    /** 状态(1:启用 0:禁用) */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 创建人 */
    private String createBy;

    /** 更新人 */
    private String updateBy;

    /** 删除标志(0:未删除,1:已删除) */
    private Integer deleted;
}
