package com.aicustomer.service;

import com.aicustomer.common.PageResult;
import com.aicustomer.entity.TeamTask;

import java.util.List;

/**
 * 团队协作服务接口
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
public interface TeamCollaborationService {

    /**
     * 获取团队任务列表（分页）
     */
    PageResult<TeamTask> getTeamTaskList(int pageNum, int pageSize, String taskStatus, String assignee, String priority);

    /**
     * 根据ID获取团队任务
     */
    TeamTask getTeamTaskById(Long id);

    /**
     * 创建团队任务
     */
    void createTeamTask(TeamTask task);

    /**
     * 更新团队任务
     */
    void updateTeamTask(TeamTask task);

    /**
     * 删除团队任务
     */
    void deleteTeamTask(Long id);

    /**
     * 分配任务
     */
    void assignTask(Long id, String assignee);

    /**
     * 更新任务状态
     */
    void updateTaskStatus(Long id, String status);

    /**
     * 更新任务进度
     */
    void updateTaskProgress(Long id, int progress);

    /**
     * 获取我的任务
     */
    List<TeamTask> getMyTasks(String status);

    /**
     * 获取我分配的任务
     */
    List<TeamTask> getAssignedTasks(String status);

    /**
     * 获取客户相关任务
     */
    List<TeamTask> getCustomerTasks(Long customerId);
}



