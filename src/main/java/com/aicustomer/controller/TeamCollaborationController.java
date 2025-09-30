package com.aicustomer.controller;

import com.aicustomer.common.PageResult;
import com.aicustomer.common.Result;
import com.aicustomer.entity.TeamTask;
import com.aicustomer.service.TeamCollaborationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 团队协作管理控制器
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/team-collaboration")
public class TeamCollaborationController {

    @Autowired
    private TeamCollaborationService teamCollaborationService;

    /**
     * 获取团队任务列表（分页）
     */
    @GetMapping("/tasks")
    public Result<PageResult<TeamTask>> getTeamTaskList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String taskStatus,
            @RequestParam(required = false) String assignee,
            @RequestParam(required = false) String priority) {
        try {
            PageResult<TeamTask> result = teamCollaborationService.getTeamTaskList(
                    pageNum, pageSize, taskStatus, assignee, priority);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取团队任务列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取团队任务详情
     */
    @GetMapping("/tasks/{id}")
    public Result<TeamTask> getTeamTaskById(@PathVariable Long id) {
        try {
            TeamTask task = teamCollaborationService.getTeamTaskById(id);
            if (task != null) {
                return Result.success(task);
            } else {
                return Result.error("团队任务不存在");
            }
        } catch (Exception e) {
            return Result.error("获取团队任务详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建团队任务
     */
    @PostMapping("/tasks")
    public Result<String> createTeamTask(@RequestBody TeamTask task) {
        try {
            teamCollaborationService.createTeamTask(task);
            return Result.success("团队任务创建成功");
        } catch (Exception e) {
            return Result.error("团队任务创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新团队任务
     */
    @PutMapping("/tasks/{id}")
    public Result<String> updateTeamTask(@PathVariable Long id, @RequestBody TeamTask task) {
        try {
            task.setId(id);
            teamCollaborationService.updateTeamTask(task);
            return Result.success("团队任务更新成功");
        } catch (Exception e) {
            return Result.error("团队任务更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除团队任务
     */
    @DeleteMapping("/tasks/{id}")
    public Result<String> deleteTeamTask(@PathVariable Long id) {
        try {
            teamCollaborationService.deleteTeamTask(id);
            return Result.success("团队任务删除成功");
        } catch (Exception e) {
            return Result.error("团队任务删除失败: " + e.getMessage());
        }
    }

    /**
     * 分配任务
     */
    @PutMapping("/tasks/{id}/assign")
    public Result<String> assignTask(@PathVariable Long id, @RequestParam String assignee) {
        try {
            teamCollaborationService.assignTask(id, assignee);
            return Result.success("任务分配成功");
        } catch (Exception e) {
            return Result.error("任务分配失败: " + e.getMessage());
        }
    }

    /**
     * 更新任务状态
     */
    @PutMapping("/tasks/{id}/status")
    public Result<String> updateTaskStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            teamCollaborationService.updateTaskStatus(id, status);
            return Result.success("任务状态更新成功");
        } catch (Exception e) {
            return Result.error("任务状态更新失败: " + e.getMessage());
        }
    }

    /**
     * 更新任务进度
     */
    @PutMapping("/tasks/{id}/progress")
    public Result<String> updateTaskProgress(@PathVariable Long id, @RequestParam int progress) {
        try {
            teamCollaborationService.updateTaskProgress(id, progress);
            return Result.success("任务进度更新成功");
        } catch (Exception e) {
            return Result.error("任务进度更新失败: " + e.getMessage());
        }
    }

    /**
     * 获取我的任务
     */
    @GetMapping("/my-tasks")
    public Result<List<TeamTask>> getMyTasks(@RequestParam(required = false) String status) {
        try {
            List<TeamTask> tasks = teamCollaborationService.getMyTasks(status);
            return Result.success(tasks);
        } catch (Exception e) {
            return Result.error("获取我的任务失败: " + e.getMessage());
        }
    }

    /**
     * 获取我分配的任务
     */
    @GetMapping("/assigned-tasks")
    public Result<List<TeamTask>> getAssignedTasks(@RequestParam(required = false) String status) {
        try {
            List<TeamTask> tasks = teamCollaborationService.getAssignedTasks(status);
            return Result.success(tasks);
        } catch (Exception e) {
            return Result.error("获取分配的任务失败: " + e.getMessage());
        }
    }

    /**
     * 获取客户相关任务
     */
    @GetMapping("/customer/{customerId}/tasks")
    public Result<List<TeamTask>> getCustomerTasks(@PathVariable Long customerId) {
        try {
            List<TeamTask> tasks = teamCollaborationService.getCustomerTasks(customerId);
            return Result.success(tasks);
        } catch (Exception e) {
            return Result.error("获取客户任务失败: " + e.getMessage());
        }
    }

    /**
     * 创建客户分配任务
     */
    @PostMapping("/customer-assignment")
    public Result<String> createCustomerAssignment(@RequestBody TeamTask task) {
        try {
            task.setTaskType(1); // 1:客户跟进
            teamCollaborationService.createTeamTask(task);
            return Result.success("客户分配任务创建成功");
        } catch (Exception e) {
            return Result.error("客户分配任务创建失败: " + e.getMessage());
        }
    }

    /**
     * 创建协作流程任务
     */
    @PostMapping("/workflow-task")
    public Result<String> createWorkflowTask(@RequestBody TeamTask task) {
        try {
            task.setTaskType(2); // 2:项目推进
            teamCollaborationService.createTeamTask(task);
            return Result.success("协作流程任务创建成功");
        } catch (Exception e) {
            return Result.error("协作流程任务创建失败: " + e.getMessage());
        }
    }

    /**
     * 获取团队绩效统计
     */
    @GetMapping("/performance")
    public Result<Object> getTeamPerformance(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            // 这里可以添加团队绩效统计逻辑
            return Result.success("团队绩效统计功能开发中");
        } catch (Exception e) {
            return Result.error("获取团队绩效统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取个人绩效统计
     */
    @GetMapping("/my-performance")
    public Result<Object> getMyPerformance(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            // 这里可以添加个人绩效统计逻辑
            return Result.success("个人绩效统计功能开发中");
        } catch (Exception e) {
            return Result.error("获取个人绩效统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取团队沟通记录
     */
    @GetMapping("/communications")
    public Result<Object> getTeamCommunications(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            // 这里可以添加团队沟通记录逻辑
            return Result.success("团队沟通记录功能开发中");
        } catch (Exception e) {
            return Result.error("获取团队沟通记录失败: " + e.getMessage());
        }
    }

    /**
     * 发送团队消息
     */
    @PostMapping("/send-message")
    public Result<String> sendTeamMessage(@RequestBody Object message) {
        try {
            // 这里可以添加发送团队消息逻辑
            return Result.success("团队消息发送功能开发中");
        } catch (Exception e) {
            return Result.error("发送团队消息失败: " + e.getMessage());
        }
    }
}
