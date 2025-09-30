package com.aicustomer.service;

import com.aicustomer.entity.AiRecommendation;

import java.util.List;
import java.util.Map;

/**
 * AI推荐服务接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
public interface AiRecommendationService {
    
    /**
     * 获取推荐统计
     */
    Map<String, Object> getRecommendationStatistics(Long customerId);
    
    /**
     * 获取个性化推荐
     */
    List<AiRecommendation> getPersonalizedRecommendations(Long customerId);
    
    /**
     * 生成产品推荐
     */
    AiRecommendation generateProductRecommendation(Long customerId);
    
    /**
     * 生成服务推荐
     */
    AiRecommendation generateServiceRecommendation(Long customerId);
    
    /**
     * 生成营销推荐
     */
    AiRecommendation generateMarketingRecommendation(Long customerId);
    
    /**
     * 生成维护推荐
     */
    AiRecommendation generateMaintenanceRecommendation(Long customerId);
    
    /**
     * 生成风控推荐
     */
    AiRecommendation generateRiskControlRecommendation(Long customerId);
    
    /**
     * 批量生成推荐
     */
    List<AiRecommendation> batchGenerateRecommendations(List<Long> customerIds);
    
    /**
     * 采纳推荐
     */
    void adoptRecommendation(Long id, String processResult);
    
    /**
     * 拒绝推荐
     */
    void rejectRecommendation(Long id, String reason);
    
    /**
     * 获取推荐历史
     */
    Map<String, Object> getRecommendationHistory(int pageNum, int pageSize);
}