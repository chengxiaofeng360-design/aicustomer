package com.aicustomer.controller;

import com.aicustomer.common.PageResult;
import com.aicustomer.common.Result;
import com.aicustomer.entity.TaskProgressReport;
import com.aicustomer.mapper.TaskProgressReportMapper;
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

    @Autowired
    private TaskProgressReportMapper reportMapper;

    /**
     * 获取任务进度汇报列表（分页）
     */
    @GetMapping("/reports")
    public Result<PageResult<TaskProgressReport>> getReports(@RequestParam(required = false) Long taskId,
                                                             @RequestParam(required = false) String employeeName,
                                                             @RequestParam(required = false) Integer reportType,
                                                             @RequestParam(defaultValue = "1") Integer page,
                                                             @RequestParam(defaultValue = "10") Integer size) {
        try {
            List<TaskProgressReport> reports = reportService.getReports(taskId, employeeName, reportType, page, size);
            int total = reportMapper.countReports(taskId, employeeName, reportType);
            PageResult<TaskProgressReport> pageResult = new PageResult<>();
            pageResult.setList(reports);
            pageResult.setTotal((long) total);
            pageResult.setPageNum(page);
            pageResult.setPageSize(size);
            pageResult.setPages((int) Math.ceil((double) total / size));
            return Result.success(pageResult);
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
            if (report.getTaskId() == null) {
                return Result.error("任务ID不能为空");
            }
            if (report.getReportType() == null) {
                return Result.error("汇报类型不能为空");
            }
            if (report.getReportTitle() == null || report.getReportTitle().trim().isEmpty()) {
                return Result.error("汇报标题不能为空");
            }
            if (report.getReportContent() == null || report.getReportContent().trim().isEmpty()) {
                return Result.error("汇报内容不能为空");
            }
            if (report.getEmployeeName() == null || report.getEmployeeName().trim().isEmpty()) {
                return Result.error("员工姓名不能为空");
            }
            TaskProgressReport createdReport = reportService.createReport(report);
            return Result.success(createdReport);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
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
            if (report == null) {
                return Result.error("汇报数据不能为空");
            }
            report.setId(id);
            TaskProgressReport updatedReport = reportService.updateReport(report);
            return Result.success(updatedReport);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
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
            if (id == null) {
                return Result.error("汇报ID不能为空");
            }
            reportService.deleteReport(id);
            return Result.success("删除成功");
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("删除任务进度汇报失败: " + e.getMessage());
        }
    }
}