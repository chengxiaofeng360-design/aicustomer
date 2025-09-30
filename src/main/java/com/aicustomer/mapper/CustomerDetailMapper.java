package com.aicustomer.mapper;

import com.aicustomer.entity.CustomerDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 客户详细信息Mapper接口
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Mapper
public interface CustomerDetailMapper {

    /**
     * 根据客户ID查询详细信息
     */
    CustomerDetail selectByCustomerId(@Param("customerId") Long customerId);

    /**
     * 插入客户详细信息
     */
    int insert(@Param("detail") CustomerDetail detail);

    /**
     * 更新客户详细信息
     */
    int updateByCustomerId(@Param("detail") CustomerDetail detail);

    /**
     * 根据客户ID删除详细信息
     */
    int deleteByCustomerId(@Param("customerId") Long customerId);
}



