package com.aicustomer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
     * 员工姓名
     */
    private String employeeName;
}
