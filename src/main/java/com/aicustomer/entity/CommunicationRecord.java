package com.aicustomer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 沟通记录实体类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommunicationRecord extends BaseEntity {
    
    /**
     * 客户ID
     */
    private Long customerId;
    
    /**
     * 客户姓名
     */
    private String customerName;
    
    /**
     * 沟通类型 (1:微信, 2:电话, 3:邮件, 4:会面, 5:其他)
     */
    private Integer communicationType;
    
    /**
     * 沟通时间
     */
    private LocalDateTime communicationTime;
    
    /**
     * 沟通内容
     */
    private String content;
    
    /**
     * 沟通摘要
     */
    private String summary;
    
    /**
     * 重要程度 (1:一般, 2:重要, 3:非常重要)
     */
    private Integer importance;
    
    /**
     * 情感分析结果 (1:积极, 2:中性, 3:消极)
     */
    private Integer sentiment;
    
    /**
     * 满意度评分 (1-5分)
     */
    private Integer satisfactionScore;
    
    /**
     * 关键词 (多个关键词用逗号分隔)
     */
    private String keywords;
    
    /**
     * 重要信息标记
     */
    private String importantInfo;
    
    /**
     * 后续任务
     */
    private String followUpTask;
    
    /**
     * 任务截止时间
     */
    private LocalDateTime taskDeadline;
    
    /**
     * 任务状态 (1:待处理, 2:进行中, 3:已完成, 4:已取消)
     */
    private Integer taskStatus;
    
    /**
     * 沟通人员ID
     */
    private Long communicatorId;
    
    /**
     * 沟通人员姓名
     */
    private String communicatorName;
    
    /**
     * 操作人
     */
    private String operator;
    
    /**
     * 附件路径
     */
    private String attachmentPath;
    
    /**
     * 沟通渠道详情 (如微信ID、电话号码等)
     */
    private String channelDetail;
    
    /**
     * 沟通时长 (分钟)
     */
    private Integer duration;
    
    /**
     * 客户响应时间 (分钟)
     */
    private Integer responseTime;
    
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
}

