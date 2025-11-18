package com.aicustomer.mapper;

import com.aicustomer.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 客户Mapper接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Mapper
public interface CustomerMapper {
    
    /**
     * 根据ID查询客户
     */
    Customer selectById(@Param("id") Long id);
    
    /**
     * 根据客户编号查询客户
     */
    Customer selectByCustomerCode(@Param("customerCode") String customerCode);
    
    /**
     * 查询客户列表
     */
    List<Customer> selectList(@Param("customer") Customer customer);
    
    /**
     * 分页查询客户
     */
    List<Customer> selectPage(@Param("customer") Customer customer, 
                             @Param("businessTypeList") List<Integer> businessTypeList,
                             @Param("offset") Integer offset, 
                             @Param("limit") Integer limit);
    
    /**
     * 查询客户总数
     */
    Long selectCount(@Param("customer") Customer customer, 
                    @Param("businessTypeList") List<Integer> businessTypeList);
    
    /**
     * 插入客户
     */
    int insert(@Param("customer") Customer customer);
    
    /**
     * 更新客户
     */
    int updateById(@Param("customer") Customer customer);
    
    /**
     * 根据ID删除客户
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 批量删除客户
     */
    int deleteByIds(@Param("ids") List<Long> ids);
}

