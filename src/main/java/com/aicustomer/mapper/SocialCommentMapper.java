package com.aicustomer.mapper;

import com.aicustomer.entity.SocialComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 社交评论Mapper接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Mapper
public interface SocialCommentMapper {
    
    /**
     * 查询动态的评论列表
     */
    List<SocialComment> findByPostId(@Param("postId") Long postId);
    
    /**
     * 根据ID查询评论
     */
    SocialComment findById(@Param("id") Long id);
    
    /**
     * 插入评论
     */
    int insert(SocialComment socialComment);
    
    /**
     * 更新评论
     */
    int update(SocialComment socialComment);
    
    /**
     * 删除评论
     */
    int delete(@Param("id") Long id);
    
    /**
     * 更新点赞数
     */
    int updateLikeCount(@Param("id") Long id, @Param("increment") Integer increment);
    
    /**
     * 根据用户ID查询评论列表
     */
    List<SocialComment> findByUserId(@Param("userId") Long userId);
}
