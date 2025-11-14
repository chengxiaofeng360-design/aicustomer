package com.aicustomer.service.impl;

import com.aicustomer.entity.TaskProgressReport;
import com.aicustomer.entity.TeamTask;
import com.aicustomer.mapper.TaskProgressReportMapper;
import com.aicustomer.mapper.TeamTaskMapper;
import com.aicustomer.service.TaskProgressReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务进度汇报服务实现类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Service
public class TaskProgressReportServiceImpl implements TaskProgressReportService {

    @Autowired
    private TaskProgressReportMapper taskProgressReportMapper;

    @Autowired
    private TeamTaskMapper teamTaskMapper;

    @Override
    public List<TaskProgressReport> getReports(Long taskId, String employeeName, Integer reportType,
                                               Integer page, Integer size) {
        Integer offset = (page - 1) * size;
        List<TaskProgressReport> reports = taskProgressReportMapper.findReports(taskId, employeeName, reportType, offset, size);
        // 确保每个汇报都有taskName（如果SQL查询没有返回，从team_task表查询）
        for (TaskProgressReport report : reports) {
            if (report.getTaskId() != null && (report.getTaskName() == null || report.getTaskName().trim().isEmpty())) {
                TeamTask task = teamTaskMapper.findById(report.getTaskId());
                if (task != null) {
                    String taskName = task.getTitle() != null ? task.getTitle() : task.getName();
                    if (taskName != null && !taskName.trim().isEmpty()) {
                        report.setTaskName(taskName);
                    } else {
                        report.setTaskName("任务" + report.getTaskId());
                    }
                } else {
                    report.setTaskName("任务" + report.getTaskId());
                }
            }
        }
        return reports;
    }

    @Override
    public TaskProgressReport getReportById(Long id) {
        TaskProgressReport report = taskProgressReportMapper.findById(id);
        // 确保有taskName（如果SQL查询没有返回，从team_task表查询）
        if (report != null && report.getTaskId() != null && 
            (report.getTaskName() == null || report.getTaskName().trim().isEmpty())) {
            TeamTask task = teamTaskMapper.findById(report.getTaskId());
            if (task != null) {
                String taskName = task.getTitle() != null ? task.getTitle() : task.getName();
                if (taskName != null && !taskName.trim().isEmpty()) {
                    report.setTaskName(taskName);
                } else {
                    report.setTaskName("任务" + report.getTaskId());
                }
            } else {
                report.setTaskName("任务" + report.getTaskId());
            }
        }
        return report;
    }

    @Override
    public TaskProgressReport createReport(TaskProgressReport report) {
        // 如果提供了taskId，从team_task表获取task_name
        if (report.getTaskId() != null && (report.getTaskName() == null || report.getTaskName().trim().isEmpty())) {
            TeamTask task = teamTaskMapper.findById(report.getTaskId());
            if (task != null) {
                report.setTaskName(task.getTitle() != null ? task.getTitle() : task.getName());
            }
        }
        
        report.setCreateTime(LocalDateTime.now());
        report.setUpdateTime(LocalDateTime.now());
        
        taskProgressReportMapper.insert(report);
        return report;
    }

    @Override
    public TaskProgressReport updateReport(TaskProgressReport report) {
        // 如果提供了taskId，从team_task表获取task_name
        if (report.getTaskId() != null && (report.getTaskName() == null || report.getTaskName().trim().isEmpty())) {
            TeamTask task = teamTaskMapper.findById(report.getTaskId());
            if (task != null) {
                report.setTaskName(task.getTitle() != null ? task.getTitle() : task.getName());
            }
        }
        
        report.setUpdateTime(LocalDateTime.now());
        taskProgressReportMapper.update(report);
        return taskProgressReportMapper.findById(report.getId());
    }

    @Override
    public boolean deleteReport(Long id) {
        return taskProgressReportMapper.delete(id) > 0;
    }
}
