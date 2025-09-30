package com.aicustomer.service;

import com.aicustomer.common.PageResult;
import com.aicustomer.entity.TaskReminder;

import java.util.List;

/**
 * 任务提醒服务接口
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
public interface TaskReminderService {

    /**
     * 获取任务提醒列表（分页）
     */
    PageResult<TaskReminder> getTaskReminderList(int pageNum, int pageSize, String taskType, String priority, String status);

    /**
     * 根据ID获取任务提醒
     */
    TaskReminder getTaskReminderById(Long id);

    /**
     * 添加任务提醒
     */
    void addTaskReminder(TaskReminder reminder);

    /**
     * 更新任务提醒
     */
    void updateTaskReminder(TaskReminder reminder);

    /**
     * 删除任务提醒
     */
    void deleteTaskReminder(Long id);

    /**
     * 获取今日提醒
     */
    List<TaskReminder> getTodayReminders();

    /**
     * 获取即将到期的提醒
     */
    List<TaskReminder> getUpcomingReminders(int days);

    /**
     * 标记任务完成
     */
    void completeTask(Long id);

    /**
     * 延期任务
     */
    void postponeTask(Long id, String newDueDate);

    /**
     * 获取客户相关提醒
     */
    List<TaskReminder> getCustomerReminders(Long customerId);
}



