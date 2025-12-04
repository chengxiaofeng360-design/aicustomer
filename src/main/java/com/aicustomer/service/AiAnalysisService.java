package com.aicustomer.service;

import com.aicustomer.entity.AiAnalysis;

import java.util.List;
import java.util.Map;

/**
 * AI分析服务接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
public interface AiAnalysisService {
    
    /**
     * 获取分析统计
     */
    Map<String, Object> getAnalysisStatistics(Long customerId);
    
    /**
     * 获取风险预警
     */
    List<AiAnalysis> getRiskWarnings();
    
    /**
     * 执行行为分析
     */
    AiAnalysis analyzeBehavior(Long customerId);
    
    /**
     * 执行情感分析
     */
    AiAnalysis analyzeSentiment(Long customerId, String content);
    
    /**
     * 执行需求预测
     */
    AiAnalysis analyzeNeeds(Long customerId);
    
    /**
     * 执行风险评估
     */
    AiAnalysis analyzeRisk(Long customerId);
    
    /**
     * 执行价值评估
     */
    AiAnalysis analyzeValue(Long customerId);
    
    /**
     * 批量分析
     */
    List<AiAnalysis> batchAnalyze(List<Long> customerIds);
    
    /**
     * 获取分析历史
     */
    Map<String, Object> getAnalysisHistory(int pageNum, int pageSize);

    /**
     * 获取业务机会列表
     */
    List<Map<String, Object>> getBusinessOpportunities();

    /**
     * 获取客户维护提醒（生日、长时间未沟通）
     */
    Map<String, Object> getCustomerReminders();

    /**
     * 分析客户合作潜力
     */
    Map<String, Object> analyzeCooperationPotential(Long customerId);
}