package com.aicustomer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 点赞实体类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SocialLike extends BaseEntity {
    
    /**
     * 点赞目标类型 (1:动态, 2:评论)
     */
    private Integer targetType;
    
    /**
     * 目标ID
     */
    private Long targetId;
    
    /**
     * 点赞者账号ID
     */
    private Long customerAccountId;
    
    /**
     * 点赞者客户ID
     */
    private Long customerId;
    
    /**
     * 点赞时间
     */
    private LocalDateTime createTime;
}
