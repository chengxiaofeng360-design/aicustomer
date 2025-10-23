package com.aicustomer.service;

import com.aicustomer.entity.TaskProgressReport;

import java.util.List;
import java.util.Map;

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
    List<TaskProgressReport> getReports(Long employeeId, Long taskId, Integer reportType, 
                                       Integer reportStatus, Integer page, Integer size);
    
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
    
    /**
     * 审核任务进度汇报
     */
    TaskProgressReport reviewReport(Long id, Long reviewerId, String reviewerName,
                                  Integer reportStatus, String reviewComment,
                                  Integer qualityScore, Integer efficiencyScore,
                                  Integer attitudeScore);
    
    /**
     * 根据员工ID获取汇报列表
     */
    List<TaskProgressReport> getReportsByEmployeeId(Long employeeId);
    
    /**
     * 根据任务ID获取汇报列表
     */
    List<TaskProgressReport> getReportsByTaskId(Long taskId);
    
    /**
     * 获取待审核的汇报
     */
    List<TaskProgressReport> getPendingReports(Long reviewerId);
    
    /**
     * 获取异常汇报
     */
    List<TaskProgressReport> getAbnormalReports();
    
    /**
     * 获取汇报统计信息
     */
    Map<String, Object> getReportStats(Long employeeId);
    
    /**
     * 获取任务进度统计
     */
    Map<String, Object> getTaskProgressStats(Long taskId);
}
