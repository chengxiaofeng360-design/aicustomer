package com.aicustomer.mapper;

import com.aicustomer.entity.CustomerAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 客户账号Mapper接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Mapper
public interface CustomerAccountMapper {
    
    /**
     * 根据用户名查找客户账号
     */
    CustomerAccount findByUsername(@Param("username") String username);
    
    /**
     * 根据ID查找客户账号
     */
    CustomerAccount findById(@Param("id") Long id);
    
    /**
     * 根据客户ID查找客户账号
     */
    CustomerAccount findByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 插入客户账号
     */
    int insert(CustomerAccount customerAccount);
    
    /**
     * 更新客户账号
     */
    int update(CustomerAccount customerAccount);
    
    /**
     * 更新最后登录时间
     */
    int updateLastLoginTime(@Param("id") Long id);
    
    /**
     * 获取用户动态数
     */
    Integer getUserPostCount(@Param("userId") Long userId);
    
    /**
     * 获取用户获赞数
     */
    Integer getUserLikeCount(@Param("userId") Long userId);
    
    /**
     * 获取用户评论数
     */
    Integer getUserCommentCount(@Param("userId") Long userId);
}
