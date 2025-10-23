package com.aicustomer.controller;

import com.aicustomer.common.Result;
import com.aicustomer.entity.TaskProgressReport;
import com.aicustomer.service.TaskProgressReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务进度汇报控制器
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/task-progress-report")
public class TaskProgressReportController {

    @Autowired
    private TaskProgressReportService reportService;

    /**
     * 获取任务进度汇报列表
     */
    @GetMapping("/reports")
    public Result<List<TaskProgressReport>> getReports(@RequestParam(required = false) Long taskId,
                                                       @RequestParam(required = false) Long employeeId,
                                                       @RequestParam(required = false) Integer reportType,
                                                       @RequestParam(required = false) Integer reportStatus,
                                                       @RequestParam(defaultValue = "1") Integer page,
                                                       @RequestParam(defaultValue = "10") Integer size) {
        try {
            List<TaskProgressReport> reports = reportService.getReports(taskId, employeeId, reportType, reportStatus, page, size);
            return Result.success(reports);
        } catch (Exception e) {
            return Result.error("获取任务进度汇报失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取任务进度汇报
     */
    @GetMapping("/reports/{id}")
    public Result<TaskProgressReport> getReportById(@PathVariable Long id) {
        try {
            TaskProgressReport report = reportService.getReportById(id);
            if (report != null) {
                return Result.success(report);
            } else {
                return Result.error("汇报不存在");
            }
        } catch (Exception e) {
            return Result.error("获取任务进度汇报失败: " + e.getMessage());
        }
    }

    /**
     * 创建任务进度汇报
     */
    @PostMapping("/reports")
    public Result<TaskProgressReport> createReport(@RequestBody TaskProgressReport report) {
        try {
            TaskProgressReport createdReport = reportService.createReport(report);
            return Result.success(createdReport);
        } catch (Exception e) {
            return Result.error("创建任务进度汇报失败: " + e.getMessage());
        }
    }

    /**
     * 更新任务进度汇报
     */
    @PutMapping("/reports/{id}")
    public Result<TaskProgressReport> updateReport(@PathVariable Long id, @RequestBody TaskProgressReport report) {
        try {
            report.setId(id);
            TaskProgressReport updatedReport = reportService.updateReport(report);
            return Result.success(updatedReport);
        } catch (Exception e) {
            return Result.error("更新任务进度汇报失败: " + e.getMessage());
        }
    }

    /**
     * 删除任务进度汇报
     */
    @DeleteMapping("/reports/{id}")
    public Result<String> deleteReport(@PathVariable Long id) {
        try {
            reportService.deleteReport(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除任务进度汇报失败: " + e.getMessage());
        }
    }

    /**
     * 审核任务进度汇报
     */
    @PostMapping("/reports/{id}/review")
    public Result<TaskProgressReport> reviewReport(@PathVariable Long id,
                                                   @RequestParam Long reviewerId,
                                                   @RequestParam String reviewerName,
                                                   @RequestParam Integer reportStatus,
                                                   @RequestParam(required = false) String reviewComment,
                                                   @RequestParam(required = false) Integer qualityScore,
                                                   @RequestParam(required = false) Integer efficiencyScore,
                                                   @RequestParam(required = false) Integer attitudeScore) {
        try {
            TaskProgressReport report = reportService.reviewReport(id, reviewerId, reviewerName, reportStatus, reviewComment, qualityScore, efficiencyScore, attitudeScore);
            return Result.success(report);
        } catch (Exception e) {
            return Result.error("审核任务进度汇报失败: " + e.getMessage());
        }
    }

    /**
     * 获取汇报统计信息
     */
    @GetMapping("/statistics")
    public Result<Object> getReportStatistics(@RequestParam(required = false) Long employeeId,
                                              @RequestParam(required = false) Long taskId) {
        try {
            Object statistics;
            if (employeeId != null) {
                statistics = reportService.getReportStats(employeeId);
            } else if (taskId != null) {
                statistics = reportService.getTaskProgressStats(taskId);
            } else {
                return Result.error("请指定员工ID或任务ID");
            }
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error("获取汇报统计失败: " + e.getMessage());
        }
    }
}