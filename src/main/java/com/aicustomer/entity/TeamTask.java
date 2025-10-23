package com.aicustomer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
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
     * 任务名称
     */
    private String name;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 负责人ID
     */
    private Long assigneeId;

    /**
     * 负责人姓名
     */
    private String assigneeName;

    /**
     * 优先级 (1:高, 2:中, 3:低)
     */
    private Integer priority;

    /**
     * 任务状态 (1:待开始, 2:进行中, 3:已完成, 4:已暂停, 5:已取消)
     */
    private Integer status;

    /**
     * 完成进度 (%)
     */
    private Integer progress;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 工作模式 (1:办公室, 2:远程, 3:混合)
     */
    private Integer workMode;

    /**
     * 预计工作时长 (分钟)
     */
    private Integer workDuration;

    /**
     * 实际开始时间
     */
    private LocalDateTime actualStartTime;

    /**
     * 实际结束时间
     */
    private LocalDateTime actualEndTime;

    /**
     * 工作状态 (1:工作中, 2:休息中, 3:会议中, 4:离线, 5:异常)
     */
    private Integer workStatus;

    /**
     * 最后活跃时间
     */
    private LocalDateTime lastActiveTime;

    /**
     * 工作地点
     */
    private String workLocation;

    /**
     * 工作证据 (图片、文档等)
     */
    private String workEvidence;

    /**
     * 工作日志
     */
    private String workLog;

    /**
     * 监督人ID
     */
    private Long supervisorId;

    /**
     * 监督人姓名
     */
    private String supervisorName;

    /**
     * 是否需要监督
     */
    private Boolean needSupervision;

    /**
     * 监督间隔 (分钟)
     */
    private Integer supervisionInterval;

    /**
     * 最后监督时间
     */
    private LocalDateTime lastSupervisionTime;

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
     * 监督备注
     */
    private String supervisionNote;
}