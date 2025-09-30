package com.aicustomer.mapper;

import com.aicustomer.entity.AiAnalysis;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI分析Mapper接口
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Mapper
public interface AiAnalysisMapper {

    /**
     * 根据ID查询AI分析结果
     */
    AiAnalysis selectById(Long id);

    /**
     * 查询AI分析结果列表
     */
    List<AiAnalysis> selectList(AiAnalysis analysis);

    /**
     * 分页查询AI分析结果
     */
    List<AiAnalysis> selectPage(@Param("analysis") AiAnalysis analysis, @Param("offset") int offset, @Param("pageSize") int pageSize);

    /**
     * 查询AI分析结果总数
     */
    Long selectCount(@Param("analysis") AiAnalysis analysis);

    /**
     * 插入AI分析结果
     */
    int insert(AiAnalysis analysis);

    /**
     * 更新AI分析结果
     */
    int updateById(AiAnalysis analysis);

    /**
     * 根据ID删除AI分析结果
     */
    int deleteById(Long id);

    /**
     * 批量删除AI分析结果
     */
    int deleteByIds(@Param("ids") List<Long> ids);

    /**
     * 根据客户ID查询分析结果
     */
    List<AiAnalysis> selectByCustomerId(@Param("customerId") Long customerId);

    /**
     * 根据分析类型查询分析结果
     */
    List<AiAnalysis> selectByAnalysisType(@Param("analysisType") Integer analysisType);

    /**
     * 更新分析状态
     */
    int updateAnalysisStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 处理分析结果
     */
    int processAnalysisResult(@Param("id") Long id, @Param("processResult") String processResult, @Param("processorId") Long processorId, @Param("processorName") String processorName);

    /**
     * 评价分析效果
     */
    int rateAnalysisEffect(@Param("id") Long id, @Param("effectScore") Integer effectScore, @Param("actualEffect") String actualEffect);

    /**
     * 获取分析统计
     */
    java.util.Map<String, Object> selectStatistics(@Param("customerId") Long customerId, 
                                                   @Param("startTime") java.util.Date startTime, @Param("endTime") java.util.Date endTime);
}

