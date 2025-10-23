package com.aicustomer.service;

import com.aicustomer.entity.SocialPost;

import java.util.List;

/**
 * 社交动态服务接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
public interface SocialPostService {
    
    /**
     * 获取动态列表
     */
    List<SocialPost> getPosts(Integer page, Integer size);
    
    /**
     * 根据ID获取动态
     */
    SocialPost getPostById(Long id);
    
    /**
     * 创建动态
     */
    SocialPost createPost(SocialPost socialPost);
    
    /**
     * 点赞/取消点赞
     */
    boolean toggleLike(Long postId, Long customerAccountId);
    
    /**
     * 获取用户的动态列表
     */
    List<SocialPost> getPostsByUserId(Long userId);
}
