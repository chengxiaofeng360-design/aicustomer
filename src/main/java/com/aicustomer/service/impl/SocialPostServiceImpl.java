package com.aicustomer.service.impl;

import com.aicustomer.entity.SocialPost;
import com.aicustomer.entity.SocialLike;
import com.aicustomer.mapper.SocialPostMapper;
import com.aicustomer.mapper.SocialLikeMapper;
import com.aicustomer.service.SocialPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 社交动态服务实现类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Service
public class SocialPostServiceImpl implements SocialPostService {

    @Autowired
    private SocialPostMapper socialPostMapper;

    @Autowired
    private SocialLikeMapper socialLikeMapper;

    @Override
    public List<SocialPost> getPosts(Integer page, Integer size) {
        Integer offset = page * size;
        return socialPostMapper.findPosts(null, offset, size);
    }

    @Override
    public SocialPost getPostById(Long id) {
        return socialPostMapper.findById(id);
    }

    @Override
    public SocialPost createPost(SocialPost socialPost) {
        socialPost.setCreateTime(LocalDateTime.now());
        socialPost.setUpdateTime(LocalDateTime.now());
        socialPost.setLikeCount(0);
        socialPost.setCommentCount(0);
        socialPost.setShareCount(0);
        socialPost.setStatus(1);
        
        socialPostMapper.insert(socialPost);
        return socialPost;
    }

    @Override
    public boolean toggleLike(Long postId, Long customerAccountId) {
        SocialLike existingLike = socialLikeMapper.findByTargetAndCustomer(1, postId, customerAccountId);
        
        if (existingLike != null) {
            // 取消点赞
            socialLikeMapper.delete(1, postId, customerAccountId);
            socialPostMapper.updateLikeCount(postId, -1);
            return false;
        } else {
            // 添加点赞
            SocialLike like = new SocialLike();
            like.setTargetType(1);
            like.setTargetId(postId);
            like.setCustomerAccountId(customerAccountId);
            like.setCustomerId(customerAccountId); // 临时使用customerAccountId作为customerId
            like.setCreateTime(LocalDateTime.now());
            
            socialLikeMapper.insert(like);
            socialPostMapper.updateLikeCount(postId, 1);
            return true;
        }
    }

    @Override
    public List<SocialPost> getPostsByUserId(Long userId) {
        return socialPostMapper.findByUserId(userId);
    }
}
