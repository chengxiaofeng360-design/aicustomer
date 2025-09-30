package com.aicustomer.service.impl;

import com.aicustomer.common.PageResult;
import com.aicustomer.entity.TaskReminder;
import com.aicustomer.service.TaskReminderService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务提醒服务实现类
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Service
public class TaskReminderServiceImpl implements TaskReminderService {

    @Override
    public PageResult<TaskReminder> getTaskReminderList(int pageNum, int pageSize, String taskType, String priority, String status) {
        // 模拟数据，实际项目中应该从数据库查询
        List<TaskReminder> reminders = new ArrayList<>();
        
        // 创建模拟数据
        TaskReminder reminder1 = new TaskReminder();
        reminder1.setId(1L);
        reminder1.setReminderType(taskType != null ? 2 : 2); // 2:跟进提醒
        reminder1.setTitle("客户回访");
        reminder1.setContent("需要回访客户张三，了解合作进展");
        reminder1.setPriority(priority != null ? 3 : 3); // 3:高
        reminder1.setStatus(status != null ? 1 : 1); // 1:待提醒
        reminder1.setDeadline(LocalDateTime.now().plusDays(5));
        reminder1.setCustomerId(1L);
        reminders.add(reminder1);
        
        TaskReminder reminder2 = new TaskReminder();
        reminder2.setId(2L);
        reminder2.setReminderType(1); // 1:关键日期
        reminder2.setTitle("合同到期提醒");
        reminder2.setContent("客户李四的合同即将到期，需要续签");
        reminder2.setPriority(2); // 2:中
        reminder2.setStatus(1); // 1:待提醒
        reminder2.setDeadline(LocalDateTime.now().plusDays(10));
        reminder2.setCustomerId(2L);
        reminders.add(reminder2);
        
        PageResult<TaskReminder> result = new PageResult<>();
        result.setList(reminders);
        result.setTotal((long) reminders.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        
        return result;
    }

    @Override
    public TaskReminder getTaskReminderById(Long id) {
        // 模拟数据
        TaskReminder reminder = new TaskReminder();
        reminder.setId(id);
        reminder.setReminderType(2); // 2:跟进提醒
        reminder.setTitle("客户回访");
        reminder.setContent("需要回访客户张三，了解合作进展");
        reminder.setPriority(3); // 3:高
        reminder.setStatus(1); // 1:待提醒
        reminder.setDeadline(LocalDateTime.now().plusDays(5));
        reminder.setCustomerId(1L);
        return reminder;
    }

    @Override
    public void addTaskReminder(TaskReminder reminder) {
        // 实际项目中应该保存到数据库
        System.out.println("添加任务提醒: " + reminder.getTitle());
    }

    @Override
    public void updateTaskReminder(TaskReminder reminder) {
        // 实际项目中应该更新数据库
        System.out.println("更新任务提醒: " + reminder.getId());
    }

    @Override
    public void deleteTaskReminder(Long id) {
        // 实际项目中应该从数据库删除
        System.out.println("删除任务提醒: " + id);
    }

    @Override
    public List<TaskReminder> getTodayReminders() {
        // 模拟今日提醒数据
        List<TaskReminder> reminders = new ArrayList<>();
        
        TaskReminder reminder = new TaskReminder();
        reminder.setId(1L);
        reminder.setReminderType(2); // 2:跟进提醒
        reminder.setTitle("今日客户回访");
        reminder.setContent("需要回访客户张三");
        reminder.setPriority(3); // 3:高
        reminder.setStatus(1); // 1:待提醒
        reminder.setDeadline(LocalDateTime.now());
        reminder.setCustomerId(1L);
        reminders.add(reminder);
        
        return reminders;
    }

    @Override
    public List<TaskReminder> getUpcomingReminders(int days) {
        // 模拟即将到期提醒数据
        List<TaskReminder> reminders = new ArrayList<>();
        
        TaskReminder reminder = new TaskReminder();
        reminder.setId(2L);
        reminder.setReminderType(1); // 1:关键日期
        reminder.setTitle("合同到期提醒");
        reminder.setContent("客户李四的合同即将到期");
        reminder.setPriority(2); // 2:中
        reminder.setStatus(1); // 1:待提醒
        reminder.setDeadline(LocalDateTime.now().plusDays(5));
        reminder.setCustomerId(2L);
        reminders.add(reminder);
        
        return reminders;
    }

    @Override
    public void completeTask(Long id) {
        // 实际项目中应该更新数据库状态
        System.out.println("完成任务: " + id);
    }

    @Override
    public void postponeTask(Long id, String newDueDate) {
        // 实际项目中应该更新数据库
        System.out.println("延期任务: " + id + " -> " + newDueDate);
    }

    @Override
    public List<TaskReminder> getCustomerReminders(Long customerId) {
        // 模拟客户相关提醒数据
        List<TaskReminder> reminders = new ArrayList<>();
        
        TaskReminder reminder = new TaskReminder();
        reminder.setId(1L);
        reminder.setReminderType(2); // 2:跟进提醒
        reminder.setTitle("客户回访");
        reminder.setContent("需要回访客户");
        reminder.setPriority(3); // 3:高
        reminder.setStatus(1); // 1:待提醒
        reminder.setDeadline(LocalDateTime.now().plusDays(5));
        reminder.setCustomerId(customerId);
        reminders.add(reminder);
        
        return reminders;
    }
}
