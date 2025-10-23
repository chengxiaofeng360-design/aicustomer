package com.aicustomer.service.impl;

import com.aicustomer.entity.TaskProgressReport;
import com.aicustomer.mapper.TaskProgressReportMapper;
import com.aicustomer.service.TaskProgressReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public List<TaskProgressReport> getReports(Long employeeId, Long taskId, Integer reportType,
                                               Integer reportStatus, Integer page, Integer size) {
        Integer offset = page * size;
        return taskProgressReportMapper.findReports(employeeId, taskId, reportType, reportStatus, offset, size);
    }

    @Override
    public TaskProgressReport getReportById(Long id) {
        return taskProgressReportMapper.findById(id);
    }

    @Override
    public TaskProgressReport createReport(TaskProgressReport report) {
        report.setCreateTime(LocalDateTime.now());
        report.setUpdateTime(LocalDateTime.now());
        report.setReportStatus(1); // 默认待审核状态
        
        // 计算综合评分
        if (report.getQualityScore() != null && report.getEfficiencyScore() != null && report.getAttitudeScore() != null) {
            double overallScore = (report.getQualityScore() + report.getEfficiencyScore() + report.getAttitudeScore()) / 3.0;
            report.setOverallScore(overallScore);
        }
        
        taskProgressReportMapper.insert(report);
        return report;
    }

    @Override
    public TaskProgressReport updateReport(TaskProgressReport report) {
        report.setUpdateTime(LocalDateTime.now());
        
        // 重新计算综合评分
        if (report.getQualityScore() != null && report.getEfficiencyScore() != null && report.getAttitudeScore() != null) {
            double overallScore = (report.getQualityScore() + report.getEfficiencyScore() + report.getAttitudeScore()) / 3.0;
            report.setOverallScore(overallScore);
        }
        
        taskProgressReportMapper.update(report);
        return report;
    }

    @Override
    public boolean deleteReport(Long id) {
        return taskProgressReportMapper.delete(id) > 0;
    }

    @Override
    public TaskProgressReport reviewReport(Long id, Long reviewerId, String reviewerName,
                                         Integer reportStatus, String reviewComment,
                                         Integer qualityScore, Integer efficiencyScore,
                                         Integer attitudeScore) {
        
        // 计算综合评分
        Double overallScore = null;
        if (qualityScore != null && efficiencyScore != null && attitudeScore != null) {
            overallScore = (qualityScore + efficiencyScore + attitudeScore) / 3.0;
        }
        
        taskProgressReportMapper.reviewReport(id, reviewerId, reviewerName, reportStatus,
                                           reviewComment, qualityScore, efficiencyScore,
                                           attitudeScore, overallScore);
        
        return taskProgressReportMapper.findById(id);
    }

    @Override
    public List<TaskProgressReport> getReportsByEmployeeId(Long employeeId) {
        return taskProgressReportMapper.findByEmployeeId(employeeId);
    }

    @Override
    public List<TaskProgressReport> getReportsByTaskId(Long taskId) {
        return taskProgressReportMapper.findByTaskId(taskId);
    }

    @Override
    public List<TaskProgressReport> getPendingReports(Long reviewerId) {
        return taskProgressReportMapper.findPendingReports(reviewerId);
    }

    @Override
    public List<TaskProgressReport> getAbnormalReports() {
        return taskProgressReportMapper.findAbnormalReports();
    }

    @Override
    public Map<String, Object> getReportStats(Long employeeId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取员工汇报总数
        Integer totalReports = taskProgressReportMapper.countReportsByEmployee(employeeId);
        stats.put("totalReports", totalReports != null ? totalReports : 0);
        
        // 获取员工汇报列表
        List<TaskProgressReport> reports = taskProgressReportMapper.findByEmployeeId(employeeId);
        
        // 统计各种状态的汇报数量
        long pendingCount = reports.stream().filter(r -> r.getReportStatus() == 1).count();
        long approvedCount = reports.stream().filter(r -> r.getReportStatus() == 2).count();
        long rejectedCount = reports.stream().filter(r -> r.getReportStatus() == 4).count();
        long abnormalCount = reports.stream().filter(r -> r.getIsAbnormal() != null && r.getIsAbnormal()).count();
        
        stats.put("pendingReports", pendingCount);
        stats.put("approvedReports", approvedCount);
        stats.put("rejectedReports", rejectedCount);
        stats.put("abnormalReports", abnormalCount);
        
        // 计算平均评分
        double avgQualityScore = reports.stream()
                .filter(r -> r.getQualityScore() != null)
                .mapToInt(TaskProgressReport::getQualityScore)
                .average()
                .orElse(0.0);
        
        double avgEfficiencyScore = reports.stream()
                .filter(r -> r.getEfficiencyScore() != null)
                .mapToInt(TaskProgressReport::getEfficiencyScore)
                .average()
                .orElse(0.0);
        
        double avgAttitudeScore = reports.stream()
                .filter(r -> r.getAttitudeScore() != null)
                .mapToInt(TaskProgressReport::getAttitudeScore)
                .average()
                .orElse(0.0);
        
        double avgOverallScore = reports.stream()
                .filter(r -> r.getOverallScore() != null)
                .mapToDouble(TaskProgressReport::getOverallScore)
                .average()
                .orElse(0.0);
        
        stats.put("avgQualityScore", Math.round(avgQualityScore * 100.0) / 100.0);
        stats.put("avgEfficiencyScore", Math.round(avgEfficiencyScore * 100.0) / 100.0);
        stats.put("avgAttitudeScore", Math.round(avgAttitudeScore * 100.0) / 100.0);
        stats.put("avgOverallScore", Math.round(avgOverallScore * 100.0) / 100.0);
        
        return stats;
    }

    @Override
    public Map<String, Object> getTaskProgressStats(Long taskId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取任务汇报总数
        Integer totalReports = taskProgressReportMapper.countReportsByTask(taskId);
        stats.put("totalReports", totalReports != null ? totalReports : 0);
        
        // 获取任务汇报列表
        List<TaskProgressReport> reports = taskProgressReportMapper.findByTaskId(taskId);
        
        if (!reports.isEmpty()) {
            // 获取最新进度
            TaskProgressReport latestReport = reports.stream()
                    .max((r1, r2) -> r1.getCreateTime().compareTo(r2.getCreateTime()))
                    .orElse(null);
            
            if (latestReport != null) {
                stats.put("latestProgress", latestReport.getProgress());
                stats.put("latestWorkDuration", latestReport.getWorkDuration());
                stats.put("latestWorkResults", latestReport.getWorkResults());
            }
            
            // 计算平均进度
            double avgProgress = reports.stream()
                    .filter(r -> r.getProgress() != null)
                    .mapToInt(TaskProgressReport::getProgress)
                    .average()
                    .orElse(0.0);
            
            stats.put("avgProgress", Math.round(avgProgress * 100.0) / 100.0);
            
            // 统计工作时长
            int totalWorkDuration = reports.stream()
                    .filter(r -> r.getWorkDuration() != null)
                    .mapToInt(TaskProgressReport::getWorkDuration)
                    .sum();
            
            stats.put("totalWorkDuration", totalWorkDuration);
        }
        
        return stats;
    }
}
