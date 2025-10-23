package com.aicustomer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 任务进度汇报实体类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskProgressReport extends BaseEntity {
    
    /**
     * 任务ID
     */
    private Long taskId;
    
    /**
     * 任务名称
     */
    private String taskName;
    
    /**
     * 员工ID
     */
    private Long employeeId;
    
    /**
     * 员工姓名
     */
    private String employeeName;
    
    /**
     * 汇报类型 (1:日报, 2:周报, 3:月报, 4:项目汇报, 5:紧急汇报)
     */
    private Integer reportType;
    
    /**
     * 汇报标题
     */
    private String reportTitle;
    
    /**
     * 汇报内容
     */
    private String reportContent;
    
    /**
     * 完成进度 (%)
     */
    private Integer progress;
    
    /**
     * 工作时长 (分钟)
     */
    private Integer workDuration;
    
    /**
     * 工作成果
     */
    private String workResults;
    
    /**
     * 遇到的问题
     */
    private String problems;
    
    /**
     * 解决方案
     */
    private String solutions;
    
    /**
     * 下一步计划
     */
    private String nextPlan;
    
    /**
     * 需要支持
     */
    private String supportNeeded;
    
    /**
     * 工作地点
     */
    private String workLocation;
    
    /**
     * 工作模式 (1:办公室, 2:远程, 3:混合)
     */
    private Integer workMode;
    
    /**
     * 附件文件
     */
    private String attachments;
    
    /**
     * 汇报状态 (1:待审核, 2:已通过, 3:需修改, 4:已驳回)
     */
    private Integer reportStatus;
    
    /**
     * 审核人ID
     */
    private Long reviewerId;
    
    /**
     * 审核人姓名
     */
    private String reviewerName;
    
    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;
    
    /**
     * 审核意见
     */
    private String reviewComment;
    
    /**
     * 质量评分 (1-5分)
     */
    private Integer qualityScore;
    
    /**
     * 效率评分 (1-5分)
     */
    private Integer efficiencyScore;
    
    /**
     * 态度评分 (1-5分)
     */
    private Integer attitudeScore;
    
    /**
     * 综合评分
     */
    private Double overallScore;
    
    /**
     * 是否异常
     */
    private Boolean isAbnormal;
    
    /**
     * 异常原因
     */
    private String abnormalReason;
    
    /**
     * 监督备注
     */
    private String supervisionNote;
}
