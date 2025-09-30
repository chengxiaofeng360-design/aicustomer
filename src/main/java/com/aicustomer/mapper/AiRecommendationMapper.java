package com.aicustomer.mapper;

import com.aicustomer.entity.AiRecommendation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI推荐Mapper接口
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Mapper
public interface AiRecommendationMapper {

    /**
     * 根据ID查询AI推荐
     */
    AiRecommendation selectById(Long id);

    /**
     * 查询AI推荐列表
     */
    List<AiRecommendation> selectList(AiRecommendation recommendation);

    /**
     * 分页查询AI推荐
     */
    List<AiRecommendation> selectPage(@Param("recommendation") AiRecommendation recommendation, @Param("offset") int offset, @Param("pageSize") int pageSize);

    /**
     * 查询AI推荐总数
     */
    Long selectCount(@Param("recommendation") AiRecommendation recommendation);

    /**
     * 插入AI推荐
     */
    int insert(AiRecommendation recommendation);

    /**
     * 更新AI推荐
     */
    int updateById(AiRecommendation recommendation);

    /**
     * 根据ID删除AI推荐
     */
    int deleteById(Long id);

    /**
     * 批量删除AI推荐
     */
    int deleteByIds(@Param("ids") List<Long> ids);

    /**
     * 根据客户ID查询推荐
     */
    List<AiRecommendation> selectByCustomerId(@Param("customerId") Long customerId);

    /**
     * 根据推荐类型查询推荐
     */
    List<AiRecommendation> selectByRecommendationType(@Param("recommendationType") Integer recommendationType);

    /**
     * 更新推荐状态
     */
    int updateRecommendationStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 采纳推荐
     */
    int adoptRecommendation(@Param("id") Long id);

    /**
     * 拒绝推荐
     */
    int rejectRecommendation(@Param("id") Long id);

    /**
     * 评价推荐效果
     */
    int rateRecommendationEffect(@Param("id") Long id, @Param("effectScore") Integer effectScore, @Param("actualEffect") String actualEffect);

    /**
     * 获取推荐统计
     */
    java.util.Map<String, Object> selectStatistics(@Param("customerId") Long customerId, 
                                                   @Param("startTime") java.util.Date startTime, @Param("endTime") java.util.Date endTime);
}

