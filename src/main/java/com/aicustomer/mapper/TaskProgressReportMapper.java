package com.aicustomer.mapper;

import com.aicustomer.entity.TaskProgressReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务进度汇报Mapper接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Mapper
public interface TaskProgressReportMapper {
    
    /**
     * 查询任务进度汇报列表
     */
    List<TaskProgressReport> findReports(@Param("employeeId") Long employeeId,
                                        @Param("taskId") Long taskId,
                                        @Param("reportType") Integer reportType,
                                        @Param("reportStatus") Integer reportStatus,
                                        @Param("offset") Integer offset,
                                        @Param("limit") Integer limit);
    
    /**
     * 根据ID查询任务进度汇报
     */
    TaskProgressReport findById(@Param("id") Long id);
    
    /**
     * 插入任务进度汇报
     */
    int insert(TaskProgressReport report);
    
    /**
     * 更新任务进度汇报
     */
    int update(TaskProgressReport report);
    
    /**
     * 删除任务进度汇报
     */
    int delete(@Param("id") Long id);
    
    /**
     * 审核任务进度汇报
     */
    int reviewReport(@Param("id") Long id,
                    @Param("reviewerId") Long reviewerId,
                    @Param("reviewerName") String reviewerName,
                    @Param("reportStatus") Integer reportStatus,
                    @Param("reviewComment") String reviewComment,
                    @Param("qualityScore") Integer qualityScore,
                    @Param("efficiencyScore") Integer efficiencyScore,
                    @Param("attitudeScore") Integer attitudeScore,
                    @Param("overallScore") Double overallScore);
    
    /**
     * 根据员工ID查询汇报列表
     */
    List<TaskProgressReport> findByEmployeeId(@Param("employeeId") Long employeeId);
    
    /**
     * 根据任务ID查询汇报列表
     */
    List<TaskProgressReport> findByTaskId(@Param("taskId") Long taskId);
    
    /**
     * 统计员工汇报数量
     */
    Integer countReportsByEmployee(@Param("employeeId") Long employeeId);
    
    /**
     * 统计任务汇报数量
     */
    Integer countReportsByTask(@Param("taskId") Long taskId);
    
    /**
     * 查询待审核的汇报
     */
    List<TaskProgressReport> findPendingReports(@Param("reviewerId") Long reviewerId);
    
    /**
     * 查询异常汇报
     */
    List<TaskProgressReport> findAbnormalReports();
}
