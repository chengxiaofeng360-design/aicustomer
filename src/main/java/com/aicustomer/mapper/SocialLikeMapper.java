package com.aicustomer.mapper;

import com.aicustomer.entity.SocialLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 点赞Mapper接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Mapper
public interface SocialLikeMapper {
    
    /**
     * 检查是否已点赞
     */
    SocialLike findByTargetAndCustomer(@Param("targetType") Integer targetType, 
                                     @Param("targetId") Long targetId, 
                                     @Param("customerAccountId") Long customerAccountId);
    
    /**
     * 插入点赞
     */
    int insert(SocialLike socialLike);
    
    /**
     * 删除点赞
     */
    int delete(@Param("targetType") Integer targetType, 
               @Param("targetId") Long targetId, 
               @Param("customerAccountId") Long customerAccountId);
    
    /**
     * 统计点赞数
     */
    int countByTarget(@Param("targetType") Integer targetType, @Param("targetId") Long targetId);
}
