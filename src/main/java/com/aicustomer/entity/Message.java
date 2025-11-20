package com.aicustomer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 消息实体类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Message extends BaseEntity {
    
    /**
     * 消息标题
     */
    private String title;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型 (1:系统通知, 2:业务提醒, 3:任务通知, 4:客户消息)
     */
    private Integer messageType;
    
    /**
     * 消息重要性 (1:普通, 2:重要, 3:紧急)
     */
    private Integer importance;
    
    /**
     * 关联业务类型 (customer, communication, task, ai_analysis, team_task, system)
     */
    private String businessType;
    
    /**
     * 关联业务ID
     */
    private Long businessId;
    
    /**
     * 关联业务名称
     */
    private String businessName;
    
    /**
     * 接收用户ID
     */
    private Long receiverId;
    
    /**
     * 接收用户姓名
     */
    private String receiverName;
    
    /**
     * 发送用户ID（系统消息为null）
     */
    private Long senderId;
    
    /**
     * 发送用户姓名
     */
    private String senderName;
    
    /**
     * 是否已读 (0:未读, 1:已读)
     */
    private Integer isRead;
    
    /**
     * 已读时间
     */
    private LocalDateTime readTime;
    
    /**
     * 是否已处理 (0:未处理, 1:已处理)
     */
    private Integer isProcessed;
    
    /**
     * 处理时间
     */
    private LocalDateTime processedTime;
    
    /**
     * 处理结果
     */
    private String processResult;
    
    /**
     * 跳转链接
     */
    private String linkUrl;
    
    /**
     * 跳转参数（JSON格式）
     */
    private String linkParams;
    
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
    
    /**
     * 消息图标
     */
    private String icon;
    
    /**
     * 消息颜色
     */
    private String color;
    
    /**
     * 扩展信息（JSON格式）
     */
    private String extraInfo;
}

