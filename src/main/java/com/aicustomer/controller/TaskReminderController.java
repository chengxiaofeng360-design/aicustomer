package com.aicustomer.controller;

import com.aicustomer.common.PageResult;
import com.aicustomer.common.Result;
import com.aicustomer.entity.TaskReminder;
import com.aicustomer.service.TaskReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务提醒系统控制器
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/task-reminder")
public class TaskReminderController {

    @Autowired
    private TaskReminderService taskReminderService;

    /**
     * 获取任务提醒列表（分页）
     */
    @GetMapping("/list")
    public Result<PageResult<TaskReminder>> getTaskReminderList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String taskType,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String status) {
        try {
            PageResult<TaskReminder> result = taskReminderService.getTaskReminderList(
                    pageNum, pageSize, taskType, priority, status);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取任务提醒列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取任务提醒详情
     */
    @GetMapping("/{id}")
    public Result<TaskReminder> getTaskReminderById(@PathVariable Long id) {
        try {
            TaskReminder reminder = taskReminderService.getTaskReminderById(id);
            if (reminder != null) {
                return Result.success(reminder);
            } else {
                return Result.error("任务提醒不存在");
            }
        } catch (Exception e) {
            return Result.error("获取任务提醒详情失败: " + e.getMessage());
        }
    }

    /**
     * 新增任务提醒
     */
    @PostMapping
    public Result<String> addTaskReminder(@RequestBody TaskReminder reminder) {
        try {
            taskReminderService.addTaskReminder(reminder);
            return Result.success("任务提醒添加成功");
        } catch (Exception e) {
            return Result.error("任务提醒添加失败: " + e.getMessage());
        }
    }

    /**
     * 更新任务提醒
     */
    @PutMapping("/{id}")
    public Result<String> updateTaskReminder(@PathVariable Long id, @RequestBody TaskReminder reminder) {
        try {
            reminder.setId(id);
            taskReminderService.updateTaskReminder(reminder);
            return Result.success("任务提醒更新成功");
        } catch (Exception e) {
            return Result.error("任务提醒更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除任务提醒
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteTaskReminder(@PathVariable Long id) {
        try {
            taskReminderService.deleteTaskReminder(id);
            return Result.success("任务提醒删除成功");
        } catch (Exception e) {
            return Result.error("任务提醒删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取今日提醒
     */
    @GetMapping("/today")
    public Result<List<TaskReminder>> getTodayReminders() {
        try {
            List<TaskReminder> reminders = taskReminderService.getTodayReminders();
            return Result.success(reminders);
        } catch (Exception e) {
            return Result.error("获取今日提醒失败: " + e.getMessage());
        }
    }

    /**
     * 获取即将到期的提醒
     */
    @GetMapping("/upcoming")
    public Result<List<TaskReminder>> getUpcomingReminders(@RequestParam(defaultValue = "7") int days) {
        try {
            List<TaskReminder> reminders = taskReminderService.getUpcomingReminders(days);
            return Result.success(reminders);
        } catch (Exception e) {
            return Result.error("获取即将到期提醒失败: " + e.getMessage());
        }
    }

    /**
     * 标记任务完成
     */
    @PutMapping("/{id}/complete")
    public Result<String> completeTask(@PathVariable Long id) {
        try {
            taskReminderService.completeTask(id);
            return Result.success("任务标记为完成");
        } catch (Exception e) {
            return Result.error("任务完成标记失败: " + e.getMessage());
        }
    }

    /**
     * 延期任务
     */
    @PutMapping("/{id}/postpone")
    public Result<String> postponeTask(@PathVariable Long id, @RequestParam String newDueDate) {
        try {
            taskReminderService.postponeTask(id, newDueDate);
            return Result.success("任务延期成功");
        } catch (Exception e) {
            return Result.error("任务延期失败: " + e.getMessage());
        }
    }

    /**
     * 获取客户相关提醒
     */
    @GetMapping("/customer/{customerId}")
    public Result<List<TaskReminder>> getCustomerReminders(@PathVariable Long customerId) {
        try {
            List<TaskReminder> reminders = taskReminderService.getCustomerReminders(customerId);
            return Result.success(reminders);
        } catch (Exception e) {
            return Result.error("获取客户提醒失败: " + e.getMessage());
        }
    }

    /**
     * 创建关键日期提醒
     */
    @PostMapping("/key-date")
    public Result<String> createKeyDateReminder(@RequestBody TaskReminder reminder) {
        try {
            reminder.setReminderType(1); // 1:关键日期
            taskReminderService.addTaskReminder(reminder);
            return Result.success("关键日期提醒创建成功");
        } catch (Exception e) {
            return Result.error("关键日期提醒创建失败: " + e.getMessage());
        }
    }

    /**
     * 创建跟进提醒
     */
    @PostMapping("/follow-up")
    public Result<String> createFollowUpReminder(@RequestBody TaskReminder reminder) {
        try {
            reminder.setReminderType(2); // 2:跟进提醒
            taskReminderService.addTaskReminder(reminder);
            return Result.success("跟进提醒创建成功");
        } catch (Exception e) {
            return Result.error("跟进提醒创建失败: " + e.getMessage());
        }
    }

    /**
     * 创建机会提醒
     */
    @PostMapping("/opportunity")
    public Result<String> createOpportunityReminder(@RequestBody TaskReminder reminder) {
        try {
            reminder.setReminderType(3); // 3:机会提醒
            taskReminderService.addTaskReminder(reminder);
            return Result.success("机会提醒创建成功");
        } catch (Exception e) {
            return Result.error("机会提醒创建失败: " + e.getMessage());
        }
    }

    /**
     * 创建风险预警
     */
    @PostMapping("/risk-warning")
    public Result<String> createRiskWarning(@RequestBody TaskReminder reminder) {
        try {
            reminder.setReminderType(4); // 4:风险预警
            reminder.setPriority(4); // 4:紧急
            taskReminderService.addTaskReminder(reminder);
            return Result.success("风险预警创建成功");
        } catch (Exception e) {
            return Result.error("风险预警创建失败: " + e.getMessage());
        }
    }

    /**
     * 获取提醒统计
     */
    @GetMapping("/statistics")
    public Result<Object> getReminderStatistics() {
        try {
            // 这里可以添加提醒统计逻辑
            return Result.success("提醒统计功能开发中");
        } catch (Exception e) {
            return Result.error("获取提醒统计失败: " + e.getMessage());
        }
    }
}
