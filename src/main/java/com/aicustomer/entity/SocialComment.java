package com.aicustomer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 社交评论实体类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SocialComment extends BaseEntity {
    
    /**
     * 动态ID
     */
    private Long postId;
    
    /**
     * 评论者账号ID
     */
    private Long customerAccountId;
    
    /**
     * 评论者客户ID
     */
    private Long customerId;
    
    /**
     * 父评论ID(回复评论时使用)
     */
    private Long parentId;
    
    /**
     * 评论内容
     */
    private String content;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 状态 (1:正常, 2:删除)
     */
    private Integer status;
    
    /**
     * 评论时间
     */
    private LocalDateTime createTime;
    
    // 扩展字段，用于前端显示
    /**
     * 评论者昵称
     */
    private String customerNickname;
    
    /**
     * 评论者头像
     */
    private String customerAvatar;
    
    /**
     * 是否已点赞
     */
    private Boolean isLiked;
}
