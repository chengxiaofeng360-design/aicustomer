package com.aicustomer.controller;

import com.aicustomer.entity.Message;
import com.aicustomer.entity.TeamTask;
import com.aicustomer.service.MessageService;
import com.aicustomer.service.TeamTaskService;
import com.aicustomer.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 消息中心控制器
 */
@RestController
@RequestMapping("/api/message-center")
public class MessageCenterController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private TeamTaskService teamTaskService;

    /**
     * 获取消息统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        // 获取当前用户ID (暂时硬编码为1，实际应从Session/Token获取)
        Long userId = 1L;

        Map<String, Object> stats = new HashMap<>();

        // 获取消息统计
        Map<String, Object> msgStats = messageService.getMessageStatistics(userId);

        // 获取待办任务统计
        List<TeamTask> pendingTasks = teamTaskService.getTasks(userId, 2, null, 1, 100); // 状态2=进行中

        stats.put("pendingTasks", pendingTasks.size());
        stats.put("urgentReminders", Integer.parseInt(msgStats.getOrDefault("urgent_count", 0).toString()));
        stats.put("weekActivities", 0); // 暂时为0
        stats.put("notifications", Integer.parseInt(msgStats.getOrDefault("system_count", 0).toString()));

        return Result.success(stats);
    }

    /**
     * 获取待办任务
     */
    @GetMapping("/pending-tasks")
    public Result<List<Map<String, Object>>> getPendingTasks(@RequestParam(defaultValue = "10") Integer limit) {
        Long userId = 1L;
        // 获取进行中的任务
        List<TeamTask> tasks = teamTaskService.getTasks(userId, 2, null, 1, limit);

        List<Map<String, Object>> result = tasks.stream().map(task -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", task.getId());
            map.put("taskType", getTaskTypeName(task.getTaskType()));
            map.put("title", task.getTitle());

            map.put("priorityLevel", task.getPriority());
            map.put("deadline", task.getDeadline());
            map.put("assigneeName", task.getAssigneeName());
            map.put("statusCode", task.getStatus());
            map.put("isUrgent", task.getPriority() != null && task.getPriority() >= 3);
            return map;
        }).collect(Collectors.toList());

        return Result.success(result);
    }

    /**
     * 获取最近活动 (暂时复用消息列表)
     */
    @GetMapping("/recent-activities")
    public Result<List<Map<String, Object>>> getRecentActivities(@RequestParam(defaultValue = "20") Integer limit) {
        Long userId = 1L;
        // 获取业务类型的消息
        Map<String, Object> messages = messageService.getUserMessages(userId, null, null, 1, limit);
        List<Message> list = (List<Message>) messages.get("list");

        List<Map<String, Object>> result = list.stream().map(msg -> {
            Map<String, Object> map = new HashMap<>();
            map.put("action", msg.getTitle());
            map.put("description", msg.getContent());
            map.put("time", msg.getCreateTime());
            map.put("icon", msg.getIcon());
            map.put("color", msg.getColor());
            map.put("customerId", msg.getBusinessId()); // 假设是客户相关的ID
            return map;
        }).collect(Collectors.toList());

        return Result.success(result);
    }

    /**
     * 获取重要提醒
     */
    @GetMapping("/important-reminders")
    public Result<List<Map<String, Object>>> getImportantReminders() {
        Long userId = 1L;
        // 获取未读的重要消息
        Map<String, Object> messages = messageService.getUserMessages(userId, null, 0, 1, 100);
        List<Message> list = (List<Message>) messages.get("list");

        List<Map<String, Object>> result = list.stream()
                .filter(msg -> msg.getImportance() != null && msg.getImportance() >= 2)
                .map(msg -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("title", msg.getTitle());
                    map.put("description", msg.getContent());
                    map.put("level", msg.getImportance() >= 3 ? "urgent" : "important");
                    return map;
                }).collect(Collectors.toList());

        return Result.success(result);
    }

    private String getTaskTypeName(Integer type) {
        if (type == null)
            return "其他";
        switch (type) {
            case 1:
                return "客户跟进";
            case 2:
                return "项目推进";
            case 3:
                return "问题处理";
            case 4:
                return "会议安排";
            default:
                return "其他";
        }
    }
}
