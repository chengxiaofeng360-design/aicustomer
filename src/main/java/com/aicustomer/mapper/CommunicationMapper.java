package com.aicustomer.mapper;

import com.aicustomer.entity.CommunicationRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

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

    /**
     * 查询最近N天的沟通记录（用于业务机会识别）
     */
    List<CommunicationRecord> selectRecentCommunications(@Param("days") int days);

    /**
     * 根据客户ID查询最近N天的沟通记录
     */
    List<CommunicationRecord> selectRecentByCustomerId(@Param("customerId") Long customerId, @Param("days") int days);

    /**
     * 查询长时间未沟通的客户（超过N天）
     */
    List<Map<String, Object>> selectCustomersNoContact(@Param("days") int days);
}




