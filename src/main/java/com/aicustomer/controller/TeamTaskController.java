package com.aicustomer.controller;

import com.aicustomer.common.Result;
import com.aicustomer.entity.TeamTask;
import com.aicustomer.service.TeamTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 团队任务控制器
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/team-task")
public class TeamTaskController {

    @Autowired
    private TeamTaskService taskService;

    /**
     * 获取团队任务列表
     */
    @GetMapping("/tasks")
    public Result<List<TeamTask>> getTasks(@RequestParam(required = false) Long assigneeId,
                                           @RequestParam(required = false) Integer status,
                                           @RequestParam(required = false) Integer priority,
                                           @RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size) {
        try {
            List<TeamTask> tasks = taskService.getTasks(assigneeId, status, priority, page, size);
            return Result.success(tasks);
        } catch (Exception e) {
            return Result.error("获取团队任务失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取团队任务
     */
    @GetMapping("/tasks/{id}")
    public Result<TeamTask> getTaskById(@PathVariable Long id) {
        try {
            TeamTask task = taskService.getTaskById(id);
            if (task != null) {
                return Result.success(task);
            } else {
                return Result.error("任务不存在");
            }
        } catch (Exception e) {
            return Result.error("获取团队任务失败: " + e.getMessage());
        }
    }

    /**
     * 创建团队任务
     */
    @PostMapping("/tasks")
    public Result<TeamTask> createTask(@RequestBody TeamTask task) {
        try {
            TeamTask createdTask = taskService.createTask(task);
            return Result.success(createdTask);
        } catch (Exception e) {
            return Result.error("创建团队任务失败: " + e.getMessage());
        }
    }

    /**
     * 更新团队任务
     */
    @PutMapping("/tasks/{id}")
    public Result<TeamTask> updateTask(@PathVariable Long id, @RequestBody TeamTask task) {
        try {
            task.setId(id);
            TeamTask updatedTask = taskService.updateTask(task);
            return Result.success(updatedTask);
        } catch (Exception e) {
            return Result.error("更新团队任务失败: " + e.getMessage());
        }
    }

    /**
     * 删除团队任务
     */
    @DeleteMapping("/tasks/{id}")
    public Result<String> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除团队任务失败: " + e.getMessage());
        }
    }

    /**
     * 更新任务进度
     */
    @PostMapping("/tasks/{id}/progress")
    public Result<TeamTask> updateTaskProgress(@PathVariable Long id,
                                               @RequestParam Integer progress,
                                               @RequestParam(required = false) String workLog) {
        try {
            TeamTask task = taskService.updateTaskProgress(id, progress, workLog);
            return Result.success(task);
        } catch (Exception e) {
            return Result.error("更新任务进度失败: " + e.getMessage());
        }
    }

    /**
     * 更新工作状态
     */
    @PostMapping("/tasks/{id}/work-status")
    public Result<TeamTask> updateWorkStatus(@PathVariable Long id,
                                             @RequestParam Integer workStatus,
                                             @RequestParam(required = false) String workLocation) {
        try {
            TeamTask task = taskService.updateWorkStatus(id, workStatus, workLocation);
            return Result.success(task);
        } catch (Exception e) {
            return Result.error("更新工作状态失败: " + e.getMessage());
        }
    }
}
