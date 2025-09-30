package com.aicustomer.service.impl;

import com.aicustomer.common.PageResult;
import com.aicustomer.entity.TeamTask;
import com.aicustomer.service.TeamCollaborationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 团队协作服务实现类
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Service
public class TeamCollaborationServiceImpl implements TeamCollaborationService {

    @Override
    public PageResult<TeamTask> getTeamTaskList(int pageNum, int pageSize, String taskStatus, String assignee, String priority) {
        // 模拟数据，实际项目中应该从数据库查询
        List<TeamTask> tasks = new ArrayList<>();
        
        // 创建模拟数据
        TeamTask task1 = new TeamTask();
        task1.setId(1L);
        task1.setTaskType(1); // 1:客户跟进
        task1.setTitle("客户张三跟进");
        task1.setDescription("负责客户张三的日常跟进工作");
        task1.setStatus(taskStatus != null ? 2 : 2); // 2:进行中
        task1.setPriority(priority != null ? 3 : 3); // 3:高
        task1.setAssigneeName(assignee != null ? assignee : "李经理");
        task1.setCreatorName("王总");
        task1.setCustomerId(1L);
        task1.setProgress(60);
        task1.setCreateTime(LocalDateTime.now());
        task1.setDeadline(LocalDateTime.now().plusDays(15));
        tasks.add(task1);
        
        TeamTask task2 = new TeamTask();
        task2.setId(2L);
        task2.setTaskType(2); // 2:项目推进
        task2.setTitle("合同审核");
        task2.setDescription("审核客户李四的合同条款");
        task2.setStatus(1); // 1:待分配
        task2.setPriority(2); // 2:中
        task2.setAssigneeName("张助理");
        task2.setCreatorName("李经理");
        task2.setCustomerId(2L);
        task2.setProgress(0);
        task2.setCreateTime(LocalDateTime.now());
        task2.setDeadline(LocalDateTime.now().plusDays(10));
        tasks.add(task2);
        
        PageResult<TeamTask> result = new PageResult<>();
        result.setList(tasks);
        result.setTotal((long) tasks.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        
        return result;
    }

    @Override
    public TeamTask getTeamTaskById(Long id) {
        // 模拟数据
        TeamTask task = new TeamTask();
        task.setId(id);
        task.setTaskType(1); // 1:客户跟进
        task.setTitle("客户张三跟进");
        task.setDescription("负责客户张三的日常跟进工作");
        task.setStatus(2); // 2:进行中
        task.setPriority(3); // 3:高
        task.setAssigneeName("李经理");
        task.setCreatorName("王总");
        task.setCustomerId(1L);
        task.setProgress(60);
        task.setCreateTime(LocalDateTime.now());
        task.setDeadline(LocalDateTime.now().plusDays(15));
        return task;
    }

    @Override
    public void createTeamTask(TeamTask task) {
        // 实际项目中应该保存到数据库
        System.out.println("创建团队任务: " + task.getTitle());
    }

    @Override
    public void updateTeamTask(TeamTask task) {
        // 实际项目中应该更新数据库
        System.out.println("更新团队任务: " + task.getId());
    }

    @Override
    public void deleteTeamTask(Long id) {
        // 实际项目中应该从数据库删除
        System.out.println("删除团队任务: " + id);
    }

    @Override
    public void assignTask(Long id, String assignee) {
        // 实际项目中应该更新数据库
        System.out.println("分配任务: " + id + " -> " + assignee);
    }

    @Override
    public void updateTaskStatus(Long id, String status) {
        // 实际项目中应该更新数据库
        System.out.println("更新任务状态: " + id + " -> " + status);
    }

    @Override
    public void updateTaskProgress(Long id, int progress) {
        // 实际项目中应该更新数据库
        System.out.println("更新任务进度: " + id + " -> " + progress + "%");
    }

    @Override
    public List<TeamTask> getMyTasks(String status) {
        // 模拟我的任务数据
        List<TeamTask> tasks = new ArrayList<>();
        
        TeamTask task = new TeamTask();
        task.setId(1L);
        task.setTaskType(1); // 1:客户跟进
        task.setTitle("客户张三跟进");
        task.setDescription("负责客户张三的日常跟进工作");
        task.setStatus(status != null ? 2 : 2); // 2:进行中
        task.setPriority(3); // 3:高
        task.setAssigneeName("当前用户");
        task.setCreatorName("王总");
        task.setCustomerId(1L);
        task.setProgress(60);
        task.setCreateTime(LocalDateTime.now());
        task.setDeadline(LocalDateTime.now().plusDays(15));
        tasks.add(task);
        
        return tasks;
    }

    @Override
    public List<TeamTask> getAssignedTasks(String status) {
        // 模拟我分配的任务数据
        List<TeamTask> tasks = new ArrayList<>();
        
        TeamTask task = new TeamTask();
        task.setId(2L);
        task.setTaskType(2); // 2:项目推进
        task.setTitle("合同审核");
        task.setDescription("审核客户李四的合同条款");
        task.setStatus(status != null ? 1 : 1); // 1:待分配
        task.setPriority(2); // 2:中
        task.setAssigneeName("张助理");
        task.setCreatorName("当前用户");
        task.setCustomerId(2L);
        task.setProgress(0);
        task.setCreateTime(LocalDateTime.now());
        task.setDeadline(LocalDateTime.now().plusDays(10));
        tasks.add(task);
        
        return tasks;
    }

    @Override
    public List<TeamTask> getCustomerTasks(Long customerId) {
        // 模拟客户相关任务数据
        List<TeamTask> tasks = new ArrayList<>();
        
        TeamTask task = new TeamTask();
        task.setId(1L);
        task.setTaskType(1); // 1:客户跟进
        task.setTitle("客户跟进");
        task.setDescription("负责客户的日常跟进工作");
        task.setStatus(2); // 2:进行中
        task.setPriority(3); // 3:高
        task.setAssigneeName("李经理");
        task.setCreatorName("王总");
        task.setCustomerId(customerId);
        task.setProgress(60);
        task.setCreateTime(LocalDateTime.now());
        task.setDeadline(LocalDateTime.now().plusDays(15));
        tasks.add(task);
        
        return tasks;
    }
}
