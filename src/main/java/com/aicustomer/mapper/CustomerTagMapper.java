package com.aicustomer.mapper;

import com.aicustomer.entity.CustomerTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客户标签Mapper接口
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Mapper
public interface CustomerTagMapper {

    /**
     * 根据客户ID查询标签列表
     */
    List<CustomerTag> selectByCustomerId(@Param("customerId") Long customerId);

    /**
     * 插入客户标签
     */
    int insert(@Param("tag") CustomerTag tag);

    /**
     * 根据ID删除客户标签
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据客户ID删除所有标签
     */
    int deleteByCustomerId(@Param("customerId") Long customerId);
}




