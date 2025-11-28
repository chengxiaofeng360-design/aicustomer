package com.aicustomer.entity;

import com.aicustomer.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 知识库问答记录实体类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KbQaRecord extends BaseEntity {
    
    /**
     * 用户问题
     */
    private String userQuestion;
    
    /**
     * 匹配的分块ID列表
     */
    private String matchedChunks;
    
    /**
     * 匹配的文档ID列表
     */
    private String matchedDocuments;
    
    /**
     * 检索到的上下文内容
     */
    private String contextContent;
    
    /**
     * AI生成的回答
     */
    private String aiAnswer;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 客户ID
     */
    private Long customerId;
    
    /**
     * 响应时间(毫秒)
     */
    private Integer responseTime;
    
    /**
     * 满意度评分(1-5分)
     */
    private Integer satisfactionScore;
    
    /**
     * 是否有帮助
     */
    private Boolean isHelpful;
    
    /**
     * 用户反馈
     */
    private String feedback;
}
