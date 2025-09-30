package com.aicustomer.mapper;

import com.aicustomer.entity.CommunicationRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 沟通记录Mapper接口
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Mapper
public interface CommunicationMapper {

    /**
     * 根据ID查询沟通记录
     */
    CommunicationRecord selectById(@Param("id") Long id);

    /**
     * 查询沟通记录列表
     */
    List<CommunicationRecord> selectList(@Param("record") CommunicationRecord record);

    /**
     * 分页查询沟通记录
     */
    List<CommunicationRecord> selectPage(@Param("record") CommunicationRecord record, 
                                        @Param("offset") int offset, 
                                        @Param("limit") int limit);

    /**
     * 查询沟通记录总数
     */
    Long selectCount(@Param("record") CommunicationRecord record);

    /**
     * 插入沟通记录
     */
    int insert(@Param("record") CommunicationRecord record);

    /**
     * 更新沟通记录
     */
    int updateById(@Param("record") CommunicationRecord record);

    /**
     * 根据ID删除沟通记录
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据客户ID查询沟通记录
     */
    List<CommunicationRecord> selectByCustomerId(@Param("customerId") Long customerId);

    /**
     * 标记重要信息
     */
    int markAsImportant(@Param("id") Long id, @Param("important") boolean important);
}




