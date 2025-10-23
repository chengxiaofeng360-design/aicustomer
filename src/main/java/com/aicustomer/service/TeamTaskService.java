package com.aicustomer.service;

import com.aicustomer.entity.TeamTask;

import java.util.List;

/**
 * 团队任务服务接口
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
public interface TeamTaskService {

    /**
     * 获取团队任务列表
     */
    List<TeamTask> getTasks(Long assigneeId, Integer status, Integer priority, Integer page, Integer size);

    /**
     * 根据ID获取团队任务
     */
    TeamTask getTaskById(Long id);

    /**
     * 创建团队任务
     */
    TeamTask createTask(TeamTask task);

    /**
     * 更新团队任务
     */
    TeamTask updateTask(TeamTask task);

    /**
     * 删除团队任务
     */
    void deleteTask(Long id);

    /**
     * 更新任务进度
     */
    TeamTask updateTaskProgress(Long taskId, Integer progress, String workLog);

    /**
     * 更新任务状态和进度
     */
    TeamTask updateTaskStatusAndProgress(Long taskId, Integer status, Integer progress);

    /**
     * 记录工作日志
     */
    TeamTask recordWorkLog(Long taskId, String workLog);

    /**
     * 监督任务
     */
    TeamTask superviseTask(Long taskId, String supervisionNote, Integer qualityScore, Integer efficiencyScore, Integer attitudeScore, Long supervisorId, String supervisorName);

    /**
     * 更新工作状态
     */
    TeamTask updateWorkStatus(Long taskId, Integer workStatus, String workLocation);
}
