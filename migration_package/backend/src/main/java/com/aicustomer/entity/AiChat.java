package com.aicustomer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * AI聊天记录实体类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiChat extends BaseEntity {
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户姓名
     */
    private String userName;
    
    /**
     * 客户ID
     */
    private Long customerId;
    
    /**
     * 客户姓名
     */
    private String customerName;
    
    /**
     * 消息类型 (1:用户消息, 2:AI回复, 3:系统消息)
     */
    private Integer messageType;
    
    /**
     * 用户消息
     */
    private String userMessage;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息状态 (1:正常, 2:已删除, 3:已屏蔽)
     */
    private Integer status;
    
    /**
     * 情感分析结果 (1:积极, 2:中性, 3:消极)
     */
    private Integer sentiment;
    
    /**
     * 关键词 (多个关键词用逗号分隔)
     */
    private String keywords;
    
    /**
     * 对话上下文
     */
    private String context;
    
    /**
     * 意图识别结果
     */
    private String intent;
    
    /**
     * 置信度 (0-100)
     */
    private Integer confidence;
    
    /**
     * 回复时间
     */
    private LocalDateTime replyTime;
    
    /**
     * 回复内容
     */
    private String replyContent;
    
    /**
     * 满意度评分 (1-5分)
     */
    private Integer satisfactionScore;
    
    /**
     * 是否满意
     */
    private Boolean isSatisfied;
    
    /**
     * 用户反馈
     */
    private String feedback;
    
    /**
     * 是否已读
     */
    private Boolean isRead;
    
    /**
     * 是否已处理
     */
    private Boolean isProcessed;
    
    /**
     * 处理结果
     */
    private String processResult;
    
    /**
     * 处理时间
     */
    private LocalDateTime processTime;
    
    /**
     * 关联业务ID
     */
    private Long businessId;
    
    /**
     * 关联业务类型
     */
    private String businessType;
    
    /**
     * 附件路径
     */
    private String attachmentPath;
    
    /**
     * 消息来源 (1:网页, 2:微信, 3:APP, 4:电话)
     */
    private Integer source;
    
    /**
     * 设备信息
     */
    private String deviceInfo;
    
    /**
     * IP地址
     */
    private String ipAddress;
    
    /**
     * 地理位置
     */
    private String location;
}

