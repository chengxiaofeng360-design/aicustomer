package com.aicustomer.mapper;

import com.aicustomer.entity.CustomerProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 客户个人资料Mapper接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Mapper
public interface CustomerProfileMapper {
    
    /**
     * 根据客户账号ID查询个人资料
     */
    CustomerProfile findByCustomerAccountId(@Param("customerAccountId") Long customerAccountId);
    
    /**
     * 根据客户ID查询个人资料
     */
    CustomerProfile findByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 插入个人资料
     */
    int insert(CustomerProfile customerProfile);
    
    /**
     * 更新个人资料
     */
    int update(CustomerProfile customerProfile);
}
