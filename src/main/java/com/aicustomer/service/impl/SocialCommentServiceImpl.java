package com.aicustomer.service.impl;

import com.aicustomer.entity.SocialComment;
import com.aicustomer.entity.SocialLike;
import com.aicustomer.mapper.SocialCommentMapper;
import com.aicustomer.mapper.SocialLikeMapper;
import com.aicustomer.mapper.SocialPostMapper;
import com.aicustomer.service.SocialCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 社交评论服务实现类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Service
public class SocialCommentServiceImpl implements SocialCommentService {

    @Autowired
    private SocialCommentMapper socialCommentMapper;

    @Autowired
    private SocialLikeMapper socialLikeMapper;

    @Autowired
    private SocialPostMapper socialPostMapper;

    @Override
    public List<SocialComment> getCommentsByPostId(Long postId) {
        return socialCommentMapper.findByPostId(postId);
    }

    @Override
    public SocialComment addComment(SocialComment comment) {
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        comment.setLikeCount(0);
        comment.setStatus(1);
        
        socialCommentMapper.insert(comment);
        
        // 更新动态的评论数
        socialPostMapper.updateCommentCount(comment.getPostId(), 1);
        
        return comment;
    }

    @Override
    public boolean toggleLike(Long commentId, Long customerAccountId) {
        SocialLike existingLike = socialLikeMapper.findByTargetAndCustomer(2, commentId, customerAccountId);
        
        if (existingLike != null) {
            // 取消点赞
            socialLikeMapper.delete(2, commentId, customerAccountId);
            socialCommentMapper.updateLikeCount(commentId, -1);
            return false;
        } else {
            // 添加点赞
            SocialLike like = new SocialLike();
            like.setTargetType(2);
            like.setTargetId(commentId);
            like.setCustomerAccountId(customerAccountId);
            like.setCustomerId(customerAccountId); // 临时使用customerAccountId作为customerId
            like.setCreateTime(LocalDateTime.now());
            
            socialLikeMapper.insert(like);
            socialCommentMapper.updateLikeCount(commentId, 1);
            return true;
        }
    }

    @Override
    public List<SocialComment> getCommentsByUserId(Long userId) {
        return socialCommentMapper.findByUserId(userId);
    }
}
