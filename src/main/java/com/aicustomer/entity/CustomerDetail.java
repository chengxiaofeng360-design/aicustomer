package com.aicustomer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

/**
 * 客户详细信息实体类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerDetail extends BaseEntity {
    
    /**
     * 客户ID (关联客户基本信息)
     */
    private Long customerId;
    
    /**
     * 客户编号
     */
    private String customerCode;
    
    // ========== 业务信息 ==========
    
    /**
     * 行业分类
     */
    private String industryCategory;
    
    /**
     * 业务类型
     */
    private String businessType;
    
    /**
     * 重要程度 (1:一般, 2:重要, 3:非常重要)
     */
    private Integer importance;
    
    /**
     * 客户标签 (多个标签用逗号分隔)
     */
    private String tags;
    
    // ========== 价值分析 ==========
    
    /**
     * 客户价值评分 (0-100分)
     */
    private Integer valueScore;
    
    /**
     * 生命周期阶段 (1:潜在, 2:接触, 3:合作, 4:维护, 5:流失)
     */
    private Integer lifecycleStage;
    
    /**
     * 累计消费金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 合作次数
     */
    private Integer cooperationCount;
    
    /**
     * 信用评估等级 (1:A, 2:B, 3:C, 4:D)
     */
    private Integer creditLevel;
    
    /**
     * 客户满意度评分 (1-5分)
     */
    private Integer satisfactionScore;
    
    /**
     * 信息完整度 (0-100%)
     */
    private Integer completeness;
    
    // ========== 时间记录 ==========
    
    /**
     * 客户创建时间
     */
    private java.time.LocalDateTime customerCreateTime;
    
    /**
     * 首次合作时间
     */
    private java.time.LocalDateTime firstCooperationTime;
    
    /**
     * 最后合作时间
     */
    private java.time.LocalDateTime lastCooperationTime;
    
    /**
     * 客户流失时间
     */
    private java.time.LocalDateTime churnTime;
    
    /**
     * 流失原因
     */
    private String churnReason;
    
    // ========== 扩展信息 ==========
    
    /**
     * 公司规模 (1:微型, 2:小型, 3:中型, 4:大型)
     */
    private Integer companySize;
    
    /**
     * 年营业额
     */
    private BigDecimal annualRevenue;
    
    /**
     * 员工数量
     */
    private Integer employeeCount;
    
    /**
     * 主要联系人
     */
    private String primaryContact;
    
    /**
     * 决策人
     */
    private String decisionMaker;
    
    /**
     * 采购周期 (1:月度, 2:季度, 3:半年度, 4:年度)
     */
    private Integer purchaseCycle;
    
    /**
     * 预算范围
     */
    private String budgetRange;
    
    /**
     * 竞争对手
     */
    private String competitors;
    
    /**
     * 特殊要求
     */
    private String specialRequirements;
    
    /**
     * 客户偏好
     */
    private String preferences;
    
    /**
     * 风险等级 (1:低, 2:中, 3:高)
     */
    private Integer riskLevel;
    
    /**
     * 风险描述
     */
    private String riskDescription;
}





