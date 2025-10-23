package com.aicustomer.service;

import com.aicustomer.entity.SocialComment;

import java.util.List;

/**
 * 社交评论服务接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
public interface SocialCommentService {
    
    /**
     * 获取动态的评论列表
     */
    List<SocialComment> getCommentsByPostId(Long postId);
    
    /**
     * 添加评论
     */
    SocialComment addComment(SocialComment comment);
    
    /**
     * 点赞/取消点赞评论
     */
    boolean toggleLike(Long commentId, Long customerAccountId);
    
    /**
     * 获取用户的评论列表
     */
    List<SocialComment> getCommentsByUserId(Long userId);
}
