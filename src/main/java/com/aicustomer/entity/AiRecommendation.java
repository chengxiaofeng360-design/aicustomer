package com.aicustomer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * AI推荐实体类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiRecommendation extends BaseEntity {
    
    /**
     * 推荐类型 (1:产品推荐, 2:服务推荐, 3:营销策略, 4:客户维护, 5:风险控制)
     */
    private Integer recommendationType;
    
    /**
     * 关联客户ID
     */
    private Long customerId;
    
    /**
     * 客户姓名
     */
    private String customerName;
    
    /**
     * 推荐标题
     */
    private String title;
    
    /**
     * 推荐内容
     */
    private String content;
    
    /**
     * 推荐理由
     */
    private String reason;
    
    /**
     * 推荐优先级 (1:低, 2:中, 3:高, 4:紧急)
     */
    private Integer priority;
    
    /**
     * 推荐状态 (1:待处理, 2:已采纳, 3:已拒绝, 4:已过期)
     */
    private Integer status;
    
    /**
     * 置信度 (0-100)
     */
    private Integer confidence;
    
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
     * 推荐算法版本
     */
    private String algorithmVersion;
    
    /**
     * 推荐参数
     */
    private String parameters;
    
    /**
     * 推荐时间
     */
    private LocalDateTime recommendationTime;
    
    /**
     * 有效期至
     */
    private LocalDateTime expireTime;
    
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
     * 推荐产品ID
     */
    private Long productId;
    
    /**
     * 推荐产品名称
     */
    private String productName;
    
    /**
     * 推荐服务ID
     */
    private Long serviceId;
    
    /**
     * 推荐服务名称
     */
    private String serviceName;
    
    /**
     * 预期收益
     */
    private BigDecimal expectedRevenue;
    
    /**
     * 实际收益
     */
    private BigDecimal actualRevenue;
    
    /**
     * 推荐成本
     */
    private BigDecimal cost;
    
    /**
     * 投资回报率 (0-100)
     */
    private Integer roi;
    
    /**
     * 风险等级 (1:低, 2:中, 3:高, 4:极高)
     */
    private Integer riskLevel;
    
    /**
     * 风险描述
     */
    private String riskDescription;
    
    /**
     * 适用场景
     */
    private String applicableScenario;
    
    /**
     * 实施难度 (1:容易, 2:中等, 3:困难, 4:极难)
     */
    private Integer difficulty;
    
    /**
     * 所需资源
     */
    private String requiredResources;
    
    /**
     * 实施周期 (天)
     */
    private Integer implementationPeriod;
    
    /**
     * 成功概率 (0-100)
     */
    private Integer successProbability;
}



