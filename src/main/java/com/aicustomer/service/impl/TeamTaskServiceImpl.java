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
        if (task == null) {
            throw new RuntimeException("任务数据不能为空");
        }
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new RuntimeException("任务标题不能为空");
        }
        
        // 设置默认值
        if (task.getTaskType() == null) {
            task.setTaskType(5); // 默认任务类型：其他
        }
        if (task.getStatus() == null) {
            task.setStatus(2); // 默认状态：进行中
        }
        if (task.getPriority() == null) {
            task.setPriority(2); // 默认优先级：中
        }
        if (task.getProgress() == null) {
            task.setProgress(0); // 默认进度：0%
        }
        
        // 设置创建人和创建时间
        if (task.getCreatorId() == null) {
            task.setCreatorId(1L); // 默认创建人ID
        }
        if (task.getCreatorName() == null || task.getCreatorName().trim().isEmpty()) {
            task.setCreatorName("系统管理员"); // 默认创建人名称
        }
        
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        
        // 确保title和name同步
        if (task.getTitle() != null && task.getName() == null) {
            task.setName(task.getTitle());
        } else if (task.getName() != null && task.getTitle() == null) {
            task.setTitle(task.getName());
        }
        
        // 确保deadline和endDate同步
        if (task.getDeadline() != null && task.getEndDate() == null) {
            task.setEndDate(task.getDeadline().toLocalDate());
        } else if (task.getEndDate() != null && task.getDeadline() == null) {
            task.setDeadline(task.getEndDate().atStartOfDay());
        }
        
        // 确保startTime和startDate同步
        if (task.getStartTime() != null && task.getStartDate() == null) {
            task.setStartDate(task.getStartTime().toLocalDate());
        } else if (task.getStartDate() != null && task.getStartTime() == null) {
            task.setStartTime(task.getStartDate().atStartOfDay());
        }
        
        taskMapper.insert(task);
        return task;
    }

    @Override
    public TeamTask updateTask(TeamTask task) {
        if (task == null || task.getId() == null) {
            throw new RuntimeException("任务ID不能为空");
        }
        
        TeamTask existingTask = taskMapper.findById(task.getId());
        if (existingTask == null) {
            throw new RuntimeException("任务不存在");
        }
        
        // 保留原有的必填字段（如果前端没有传递）
        if (task.getCreatorId() == null) {
            task.setCreatorId(existingTask.getCreatorId());
        }
        if (task.getCreatorName() == null || task.getCreatorName().trim().isEmpty()) {
            task.setCreatorName(existingTask.getCreatorName());
        }
        if (task.getTaskType() == null) {
            task.setTaskType(existingTask.getTaskType());
        }
        
        // 确保title和name同步
        if (task.getTitle() != null) {
            task.setName(task.getTitle());
        } else if (task.getName() != null) {
            task.setTitle(task.getName());
        } else {
            // 如果都没有，保留原有的
            task.setTitle(existingTask.getTitle());
            task.setName(existingTask.getName());
        }
        
        // 确保deadline和endDate同步
        if (task.getDeadline() != null) {
            task.setEndDate(task.getDeadline().toLocalDate());
        } else if (task.getEndDate() != null) {
            task.setDeadline(task.getEndDate().atStartOfDay());
        } else {
            // 保留原有的
            task.setDeadline(existingTask.getDeadline());
            task.setEndDate(existingTask.getEndDate());
        }
        
        // 确保startTime和startDate同步
        if (task.getStartTime() != null) {
            task.setStartDate(task.getStartTime().toLocalDate());
        } else if (task.getStartDate() != null) {
            task.setStartTime(task.getStartDate().atStartOfDay());
        } else {
            // 保留原有的
            task.setStartTime(existingTask.getStartTime());
            task.setStartDate(existingTask.getStartDate());
        }
        
        // 如果状态和进度没有传递，保留原有的
        if (task.getStatus() == null) {
            task.setStatus(existingTask.getStatus());
        }
        if (task.getProgress() == null) {
            task.setProgress(existingTask.getProgress());
        }
        if (task.getPriority() == null) {
            task.setPriority(existingTask.getPriority());
        }
        
        task.setUpdateTime(LocalDateTime.now());
        taskMapper.update(task);
        return taskMapper.findById(task.getId());
    }

    @Override
    public void deleteTask(Long id) {
        if (id == null) {
            throw new RuntimeException("任务ID不能为空");
        }
        TeamTask task = taskMapper.findById(id);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
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
