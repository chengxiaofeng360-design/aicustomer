package com.aicustomer.controller;

import com.aicustomer.common.Result;
import com.aicustomer.entity.AiAnalysis;
import com.aicustomer.service.AiAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI分析控制器
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/ai-analysis")
@RequiredArgsConstructor
public class AiAnalysisController {
    
    private final AiAnalysisService aiAnalysisService;
    
    /**
     * 获取分析统计
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(@RequestParam(required = false) Long customerId) {
        try {
            Map<String, Object> stats = aiAnalysisService.getAnalysisStatistics(customerId);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取分析统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取风险预警
     */
    @GetMapping("/risk-warnings")
    public Result<List<AiAnalysis>> getRiskWarnings() {
        try {
            List<AiAnalysis> warnings = aiAnalysisService.getRiskWarnings();
            return Result.success(warnings);
        } catch (Exception e) {
            return Result.error("获取风险预警失败: " + e.getMessage());
        }
    }
    
    /**
     * 执行行为分析
     */
    @PostMapping("/behavior/{customerId}")
    public Result<AiAnalysis> analyzeBehavior(@PathVariable Long customerId) {
        try {
            AiAnalysis analysis = aiAnalysisService.analyzeBehavior(customerId);
            return Result.success(analysis);
        } catch (Exception e) {
            return Result.error("行为分析失败: " + e.getMessage());
        }
    }
    
    /**
     * 执行情感分析
     */
    @PostMapping("/sentiment/{customerId}")
    public Result<AiAnalysis> analyzeSentiment(@PathVariable Long customerId, @RequestParam String content) {
        try {
            AiAnalysis analysis = aiAnalysisService.analyzeSentiment(customerId, content);
            return Result.success(analysis);
        } catch (Exception e) {
            return Result.error("情感分析失败: " + e.getMessage());
        }
    }
    
    /**
     * 执行需求预测
     */
    @PostMapping("/needs/{customerId}")
    public Result<AiAnalysis> analyzeNeeds(@PathVariable Long customerId) {
        try {
            AiAnalysis analysis = aiAnalysisService.analyzeNeeds(customerId);
            return Result.success(analysis);
        } catch (Exception e) {
            return Result.error("需求预测失败: " + e.getMessage());
        }
    }
    
    /**
     * 执行风险评估
     */
    @PostMapping("/risk/{customerId}")
    public Result<AiAnalysis> analyzeRisk(@PathVariable Long customerId) {
        try {
            AiAnalysis analysis = aiAnalysisService.analyzeRisk(customerId);
            return Result.success(analysis);
        } catch (Exception e) {
            return Result.error("风险评估失败: " + e.getMessage());
        }
    }
    
    /**
     * 执行价值评估
     */
    @PostMapping("/value/{customerId}")
    public Result<AiAnalysis> analyzeValue(@PathVariable Long customerId) {
        try {
            AiAnalysis analysis = aiAnalysisService.analyzeValue(customerId);
            return Result.success(analysis);
        } catch (Exception e) {
            return Result.error("价值评估失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量分析
     */
    @PostMapping("/batch-analyze")
    public Result<List<AiAnalysis>> batchAnalyze(@RequestBody List<Long> customerIds) {
        try {
            List<AiAnalysis> analyses = aiAnalysisService.batchAnalyze(customerIds);
            return Result.success(analyses);
        } catch (Exception e) {
            return Result.error("批量分析失败: " + e.getMessage());
        }
    }
    
    /**
     * 执行分析（前端通用分析接口）
     */
    @PostMapping("/analyze")
    public Result<Map<String, Object>> analyze(@RequestBody Map<String, Object> request) {
        try {
            Long customerId = Long.valueOf(request.get("customerId").toString());
            String analysisType = request.get("analysisType").toString();
            String timeRange = request.get("timeRange").toString();
            String description = request.get("description").toString();
            
            // 根据分析类型调用不同的分析方法
            AiAnalysis analysis = null;
            switch (analysisType) {
                case "customer_behavior":
                    analysis = aiAnalysisService.analyzeBehavior(customerId);
                    break;
                case "market_trend":
                    analysis = aiAnalysisService.analyzeNeeds(customerId);
                    break;
                case "risk_assessment":
                    analysis = aiAnalysisService.analyzeRisk(customerId);
                    break;
                case "profitability":
                    analysis = aiAnalysisService.analyzeValue(customerId);
                    break;
                case "satisfaction":
                    analysis = aiAnalysisService.analyzeSentiment(customerId, description);
                    break;
                default:
                    analysis = aiAnalysisService.analyzeBehavior(customerId);
            }
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("analysisId", analysis.getId());
            result.put("customerId", customerId);
            result.put("analysisType", analysisType);
            result.put("score", 85);
            result.put("risk", "低");
            result.put("insight", "基于数据分析，该客户表现出良好的合作潜力，建议加强沟通频率，提供个性化服务方案。");
            result.put("recommendations", Arrays.asList(
                "增加沟通频率",
                "提供定制化服务",
                "定期回访跟进"
            ));
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("分析失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取分析历史
     */
    @GetMapping("/history")
    public Result<Map<String, Object>> getAnalysisHistory(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            Map<String, Object> history = aiAnalysisService.getAnalysisHistory(pageNum, pageSize);
            return Result.success(history);
        } catch (Exception e) {
            return Result.error("获取分析历史失败: " + e.getMessage());
        }
    }

    /**
     * 获取业务机会列表
     */
    @GetMapping("/business-opportunities")
    public Result<List<Map<String, Object>>> getBusinessOpportunities() {
        try {
            List<Map<String, Object>> opportunities = aiAnalysisService.getBusinessOpportunities();
            return Result.success(opportunities);
        } catch (Exception e) {
            return Result.error("获取业务机会失败: " + e.getMessage());
        }
    }

    /**
     * 获取客户维护提醒
     */
    @GetMapping("/customer-reminders")
    public Result<Map<String, Object>> getCustomerReminders() {
        try {
            Map<String, Object> reminders = aiAnalysisService.getCustomerReminders();
            return Result.success(reminders);
        } catch (Exception e) {
            return Result.error("获取客户提醒失败: " + e.getMessage());
        }
    }

    /**
     * 分析客户合作潜力
     */
    @GetMapping("/cooperation-potential/{customerId}")
    public Result<Map<String, Object>> analyzeCooperationPotential(@PathVariable Long customerId) {
        try {
            Map<String, Object> analysis = aiAnalysisService.analyzeCooperationPotential(customerId);
            return Result.success(analysis);
        } catch (Exception e) {
            return Result.error("分析合作潜力失败: " + e.getMessage());
        }
    }
}