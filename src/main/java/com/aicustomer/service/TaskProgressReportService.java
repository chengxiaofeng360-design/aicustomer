package com.aicustomer.service;

import com.aicustomer.entity.TaskProgressReport;

import java.util.List;

/**
 * 任务进度汇报服务接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
public interface TaskProgressReportService {
    
    /**
     * 获取任务进度汇报列表
     */
    List<TaskProgressReport> getReports(Long taskId, String employeeName, Integer reportType, 
                                       Integer page, Integer size);
    
    /**
     * 根据ID获取任务进度汇报
     */
    TaskProgressReport getReportById(Long id);
    
    /**
     * 创建任务进度汇报
     */
    TaskProgressReport createReport(TaskProgressReport report);
    
    /**
     * 更新任务进度汇报
     */
    TaskProgressReport updateReport(TaskProgressReport report);
    
    /**
     * 删除任务进度汇报
     */
    boolean deleteReport(Long id);
}
