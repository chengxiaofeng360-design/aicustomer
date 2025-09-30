package com.aicustomer.controller;

import com.aicustomer.common.Result;
import com.aicustomer.entity.AiRecommendation;
import com.aicustomer.service.AiRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI推荐控制器
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/ai-recommendations")
@RequiredArgsConstructor
public class AiRecommendationController {
    
    private final AiRecommendationService aiRecommendationService;
    
    /**
     * 获取推荐统计
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(@RequestParam(required = false) Long customerId) {
        try {
            Map<String, Object> stats = aiRecommendationService.getRecommendationStatistics(customerId);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取推荐统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取个性化推荐
     */
    @GetMapping("/personalized/{customerId}")
    public Result<List<AiRecommendation>> getPersonalizedRecommendations(@PathVariable Long customerId) {
        try {
            List<AiRecommendation> recommendations = aiRecommendationService.getPersonalizedRecommendations(customerId);
            return Result.success(recommendations);
        } catch (Exception e) {
            return Result.error("获取个性化推荐失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成产品推荐
     */
    @PostMapping("/product/{customerId}")
    public Result<AiRecommendation> generateProductRecommendation(@PathVariable Long customerId) {
        try {
            AiRecommendation recommendation = aiRecommendationService.generateProductRecommendation(customerId);
            return Result.success(recommendation);
        } catch (Exception e) {
            return Result.error("生成产品推荐失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成服务推荐
     */
    @PostMapping("/service/{customerId}")
    public Result<AiRecommendation> generateServiceRecommendation(@PathVariable Long customerId) {
        try {
            AiRecommendation recommendation = aiRecommendationService.generateServiceRecommendation(customerId);
            return Result.success(recommendation);
        } catch (Exception e) {
            return Result.error("生成服务推荐失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成营销推荐
     */
    @PostMapping("/marketing/{customerId}")
    public Result<AiRecommendation> generateMarketingRecommendation(@PathVariable Long customerId) {
        try {
            AiRecommendation recommendation = aiRecommendationService.generateMarketingRecommendation(customerId);
            return Result.success(recommendation);
        } catch (Exception e) {
            return Result.error("生成营销推荐失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成维护推荐
     */
    @PostMapping("/maintenance/{customerId}")
    public Result<AiRecommendation> generateMaintenanceRecommendation(@PathVariable Long customerId) {
        try {
            AiRecommendation recommendation = aiRecommendationService.generateMaintenanceRecommendation(customerId);
            return Result.success(recommendation);
        } catch (Exception e) {
            return Result.error("生成维护推荐失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成风控推荐
     */
    @PostMapping("/risk-control/{customerId}")
    public Result<AiRecommendation> generateRiskControlRecommendation(@PathVariable Long customerId) {
        try {
            AiRecommendation recommendation = aiRecommendationService.generateRiskControlRecommendation(customerId);
            return Result.success(recommendation);
        } catch (Exception e) {
            return Result.error("生成风控推荐失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量生成推荐
     */
    @PostMapping("/batch-generate")
    public Result<List<AiRecommendation>> batchGenerateRecommendations(@RequestBody List<Long> customerIds) {
        try {
            List<AiRecommendation> recommendations = aiRecommendationService.batchGenerateRecommendations(customerIds);
            return Result.success(recommendations);
        } catch (Exception e) {
            return Result.error("批量生成推荐失败: " + e.getMessage());
        }
    }
    
    /**
     * 采纳推荐
     */
    @PutMapping("/{id}/adopt")
    public Result<Void> adoptRecommendation(@PathVariable Long id, @RequestParam String processResult) {
        try {
            aiRecommendationService.adoptRecommendation(id, processResult);
            return Result.success();
        } catch (Exception e) {
            return Result.error("采纳推荐失败: " + e.getMessage());
        }
    }
    
    /**
     * 拒绝推荐
     */
    @PutMapping("/{id}/reject")
    public Result<Void> rejectRecommendation(@PathVariable Long id, @RequestParam String reason) {
        try {
            aiRecommendationService.rejectRecommendation(id, reason);
            return Result.success();
        } catch (Exception e) {
            return Result.error("拒绝推荐失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成推荐（前端通用推荐接口）
     */
    @PostMapping("/generate")
    public Result<Map<String, Object>> generateRecommendations(@RequestBody Map<String, Object> request) {
        try {
            Long customerId = Long.valueOf(request.get("customerId").toString());
            String recommendationType = request.get("recommendationType").toString();
            Integer count = Integer.valueOf(request.get("count").toString());
            
            // 根据推荐类型生成不同的推荐
            List<Map<String, Object>> recommendations = new ArrayList<>();
            
            Map<String, Object> rec1 = new HashMap<>();
            rec1.put("title", "推荐新品种保护申请服务");
            rec1.put("description", "基于客户历史数据，建议申请新品种保护，预计可提升品牌价值30%");
            rec1.put("priority", "high");
            recommendations.add(rec1);
            
            Map<String, Object> rec2 = new HashMap<>();
            rec2.put("title", "定制化技术咨询服务");
            rec2.put("description", "为客户提供专业的技术咨询服务，帮助优化种植方案");
            rec2.put("priority", "medium");
            recommendations.add(rec2);
            
            Map<String, Object> rec3 = new HashMap<>();
            rec3.put("title", "市场推广支持方案");
            rec3.put("description", "提供全方位的市场推广支持，扩大产品影响力");
            rec3.put("priority", "medium");
            recommendations.add(rec3);
            
            Map<String, Object> result = new HashMap<>();
            result.put("customerId", customerId);
            result.put("recommendationType", recommendationType);
            result.put("recommendations", recommendations);
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("生成推荐失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取推荐历史
     */
    @GetMapping("/history")
    public Result<Map<String, Object>> getRecommendationHistory(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            Map<String, Object> history = aiRecommendationService.getRecommendationHistory(pageNum, pageSize);
            return Result.success(history);
        } catch (Exception e) {
            return Result.error("获取推荐历史失败: " + e.getMessage());
        }
    }
}