package com.aicustomer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 社交动态实体类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SocialPost extends BaseEntity {
    
    /**
     * 发布者账号ID
     */
    private Long customerAccountId;
    
    /**
     * 发布者客户ID
     */
    private Long customerId;
    
    /**
     * 动态内容
     */
    private String content;
    
    /**
     * 图片URLs(JSON格式)
     */
    private String images;
    
    /**
     * 动态类型 (1:文字, 2:图片, 3:图文)
     */
    private Integer postType;
    
    /**
     * 可见性 (1:公开, 2:仅好友, 3:私密)
     */
    private Integer visibility;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 评论数
     */
    private Integer commentCount;
    
    /**
     * 分享数
     */
    private Integer shareCount;
    
    /**
     * 状态 (1:正常, 2:删除, 3:隐藏)
     */
    private Integer status;
    
    /**
     * 发布时间
     */
    private LocalDateTime createTime;
    
    // 扩展字段，用于前端显示
    /**
     * 发布者昵称
     */
    private String customerNickname;
    
    /**
     * 发布者头像
     */
    private String customerAvatar;
    
    /**
     * 是否已点赞
     */
    private Boolean isLiked;
}
