package com.aicustomer.mapper;

import com.aicustomer.entity.SocialPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 社交动态Mapper接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Mapper
public interface SocialPostMapper {
    
    /**
     * 查询动态列表
     */
    List<SocialPost> findPosts(@Param("customerAccountId") Long customerAccountId, 
                              @Param("offset") Integer offset, 
                              @Param("limit") Integer limit);
    
    /**
     * 根据ID查询动态
     */
    SocialPost findById(@Param("id") Long id);
    
    /**
     * 插入动态
     */
    int insert(SocialPost socialPost);
    
    /**
     * 更新动态
     */
    int update(SocialPost socialPost);
    
    /**
     * 删除动态
     */
    int delete(@Param("id") Long id);
    
    /**
     * 更新点赞数
     */
    int updateLikeCount(@Param("id") Long id, @Param("increment") Integer increment);
    
    /**
     * 更新评论数
     */
    int updateCommentCount(@Param("id") Long id, @Param("increment") Integer increment);
    
    /**
     * 根据用户ID查询动态列表
     */
    List<SocialPost> findByUserId(@Param("userId") Long userId);
}
