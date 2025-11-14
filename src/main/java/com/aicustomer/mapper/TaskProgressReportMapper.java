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
    List<TaskProgressReport> findReports(@Param("taskId") Long taskId,
                                        @Param("employeeName") String employeeName,
                                        @Param("reportType") Integer reportType,
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
     * 获取汇报总数
     */
    int countReports(@Param("taskId") Long taskId,
                    @Param("employeeName") String employeeName,
                    @Param("reportType") Integer reportType);
    
    /**
     * 根据任务ID查询汇报列表
     */
    List<TaskProgressReport> findByTaskId(@Param("taskId") Long taskId);
}
