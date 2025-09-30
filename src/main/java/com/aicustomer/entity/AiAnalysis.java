package com.aicustomer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * AI分析结果实体类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiAnalysis extends BaseEntity {
    
    /**
     * 分析类型 (1:客户行为分析, 2:情感分析, 3:需求预测, 4:风险预警, 5:价值评估)
     */
    private Integer analysisType;
    
    /**
     * 关联客户ID
     */
    private Long customerId;
    
    /**
     * 客户姓名
     */
    private String customerName;
    
    /**
     * 分析标题
     */
    private String title;
    
    /**
     * 分析内容
     */
    private String content;
    
    /**
     * 分析结果
     */
    private String result;
    
    /**
     * 置信度 (0-100)
     */
    private Integer confidence;
    
    /**
     * 重要程度 (1:低, 2:中, 3:高, 4:紧急)
     */
    private Integer importance;
    
    /**
     * 分析状态 (1:待处理, 2:已处理, 3:已忽略)
     */
    private Integer status;
    
    /**
     * 建议措施
     */
    private String suggestions;
    
    /**
     * 预期效果
     */
    private String expectedEffect;
    
    /**
     * 实际效果
     */
    private String actualEffect;
    
    /**
     * 效果评分 (1-5分)
     */
    private Integer effectScore;
    
    /**
     * 分析数据来源
     */
    private String dataSource;
    
    /**
     * 分析算法版本
     */
    private String algorithmVersion;
    
    /**
     * 分析参数
     */
    private String parameters;
    
    /**
     * 分析时间
     */
    private LocalDateTime analysisTime;
    
    /**
     * 处理时间
     */
    private LocalDateTime processTime;
    
    /**
     * 处理人ID
     */
    private Long processorId;
    
    /**
     * 处理人姓名
     */
    private String processorName;
    
    /**
     * 处理结果
     */
    private String processResult;
    
    /**
     * 关联业务ID
     */
    private Long businessId;
    
    /**
     * 关联业务类型
     */
    private String businessType;
    
    /**
     * 风险等级 (1:低, 2:中, 3:高, 4:极高)
     */
    private Integer riskLevel;
    
    /**
     * 风险描述
     */
    private String riskDescription;
    
    /**
     * 价值评分 (0-100)
     */
    private Integer valueScore;
    
    /**
     * 预测准确率 (0-100)
     */
    private Integer accuracy;
    
    /**
     * 分析成本
     */
    private BigDecimal cost;
    
    /**
     * 预期收益
     */
    private BigDecimal expectedRevenue;
    
    /**
     * 实际收益
     */
    private BigDecimal actualRevenue;
}




