package com.aicustomer.service.impl;

import com.aicustomer.entity.TeamTask;
import com.aicustomer.mapper.TeamTaskMapper;
import com.aicustomer.service.TeamTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 团队任务服务实现类
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Service
public class TeamTaskServiceImpl implements TeamTaskService {

    @Autowired
    private TeamTaskMapper taskMapper;

    @Override
    public List<TeamTask> getTasks(Long assigneeId, Integer status, Integer priority, Integer page, Integer size) {
        int offset = (page - 1) * size;
        return taskMapper.findTasks(assigneeId, status, priority, offset, size);
    }

    @Override
    public TeamTask getTaskById(Long id) {
        return taskMapper.findById(id);
    }

    @Override
    public TeamTask createTask(TeamTask task) {
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        taskMapper.insert(task);
        return task;
    }

    @Override
    public TeamTask updateTask(TeamTask task) {
        task.setUpdateTime(LocalDateTime.now());
        taskMapper.update(task);
        return task;
    }

    @Override
    public void deleteTask(Long id) {
        taskMapper.delete(id);
    }

    @Override
    public TeamTask updateTaskProgress(Long taskId, Integer progress, String workLog) {
        TeamTask task = taskMapper.findById(taskId);
        if (task != null) {
            task.setProgress(progress);
            task.setWorkLog(workLog);
            task.setLastActiveTime(LocalDateTime.now());
            taskMapper.update(task);
        }
        return task;
    }

    @Override
    public TeamTask updateWorkStatus(Long taskId, Integer workStatus, String workLocation) {
        TeamTask task = taskMapper.findById(taskId);
        if (task != null) {
            task.setWorkStatus(workStatus);
            task.setWorkLocation(workLocation);
            task.setLastActiveTime(LocalDateTime.now());
            taskMapper.update(task);
        }
        return task;
    }

    @Override
    public TeamTask updateTaskStatusAndProgress(Long taskId, Integer status, Integer progress) {
        TeamTask task = taskMapper.findById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        task.setStatus(status);
        task.setProgress(progress);
        task.setUpdateTime(LocalDateTime.now());
        taskMapper.update(task);
        return task;
    }

    @Override
    public TeamTask recordWorkLog(Long taskId, String workLog) {
        TeamTask task = taskMapper.findById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        task.setWorkLog(workLog);
        task.setLastActiveTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        taskMapper.update(task);
        return task;
    }

    @Override
    public TeamTask superviseTask(Long taskId, String supervisionNote, Integer qualityScore, Integer efficiencyScore, Integer attitudeScore, Long supervisorId, String supervisorName) {
        TeamTask task = taskMapper.findById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        task.setSupervisionNote(supervisionNote);
        task.setQualityScore(qualityScore);
        task.setEfficiencyScore(efficiencyScore);
        task.setAttitudeScore(attitudeScore);
        task.setSupervisorId(supervisorId);
        task.setSupervisorName(supervisorName);
        task.setLastSupervisionTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        taskMapper.update(task);
        return task;
    }
}
