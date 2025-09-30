package com.aicustomer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 团队任务实体类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TeamTask extends BaseEntity {
    
    /**
     * 任务标题
     */
    private String title;
    
    /**
     * 任务描述
     */
    private String description;
    
    /**
     * 任务类型 (1:客户跟进, 2:项目推进, 3:问题处理, 4:会议安排, 5:其他)
     */
    private Integer taskType;
    
    /**
     * 任务状态 (1:待分配, 2:进行中, 3:待审核, 4:已完成, 5:已取消)
     */
    private Integer status;
    
    /**
     * 优先级 (1:低, 2:中, 3:高, 4:紧急)
     */
    private Integer priority;
    
    /**
     * 关联客户ID
     */
    private Long customerId;
    
    /**
     * 关联客户姓名
     */
    private String customerName;
    
    /**
     * 创建人ID
     */
    private Long creatorId;
    
    /**
     * 创建人姓名
     */
    private String creatorName;
    
    /**
     * 负责人ID
     */
    private Long assigneeId;
    
    /**
     * 负责人姓名
     */
    private String assigneeName;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 截止时间
     */
    private LocalDateTime deadline;
    
    /**
     * 完成时间
     */
    private LocalDateTime completedTime;
    
    /**
     * 预计工时 (小时)
     */
    private Integer estimatedHours;
    
    /**
     * 实际工时 (小时)
     */
    private Integer actualHours;
    
    /**
     * 任务进度 (0-100%)
     */
    private Integer progress;
    
    /**
     * 完成结果
     */
    private String result;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 父任务ID
     */
    private Long parentTaskId;
    
    /**
     * 任务层级
     */
    private Integer level;
    
    /**
     * 是否需要审批
     */
    private Boolean needApproval;
    
    /**
     * 审批人ID
     */
    private Long approverId;
    
    /**
     * 审批人姓名
     */
    private String approverName;
    
    /**
     * 审批时间
     */
    private LocalDateTime approvalTime;
    
    /**
     * 审批结果 (1:通过, 2:拒绝, 3:待审批)
     */
    private Integer approvalResult;
    
    /**
     * 审批意见
     */
    private String approvalComment;
    
    /**
     * 任务标签
     */
    private String tags;
    
    /**
     * 关联文件路径
     */
    private String attachmentPath;
    
    /**
     * 是否公开
     */
    private Boolean isPublic;
    
    /**
     * 可见用户ID列表 (多个用逗号分隔)
     */
    private String visibleUserIds;
}

