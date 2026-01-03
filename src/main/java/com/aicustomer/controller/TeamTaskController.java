package com.aicustomer.controller;

import com.aicustomer.common.PageResult;
import com.aicustomer.common.Result;
import com.aicustomer.entity.TeamTask;
import com.aicustomer.mapper.TeamTaskMapper;
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

    @Autowired
    private TeamTaskMapper taskMapper;

    @Autowired
    private com.aicustomer.service.AiChatService aiChatService;

    @Autowired
    private com.aicustomer.service.UserService userService;

    /**
     * AI 分析团队任务
     */
    @PostMapping("/analyze")
    public Result<String> analyzeTasks() {
        try {
            // 获取当前用户ID
            Long userId = 1L; // 默认为 admin
            try {
                org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                        .getContext().getAuthentication();
                if (auth != null) {
                    String username;
                    if (auth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
                        username = ((org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal())
                                .getUsername();
                    } else {
                        username = auth.getPrincipal().toString();
                    }
                    if (!"anonymousUser".equals(username)) {
                        com.aicustomer.entity.User user = userService.findByUsername(username);
                        if (user != null) {
                            userId = user.getId();
                        }
                    }
                }
            } catch (Exception e) {
                // 忽略认证错误，使用默认用户
            }

            // 获取所有未完成的任务（进行中、待分配、待审核）
            List<TeamTask> tasks = taskService.getTasks(null, null, null, 1, 100);

            if (tasks == null || tasks.isEmpty()) {
                return Result.success("当前没有任务可分析。");
            }

            StringBuilder prompt = new StringBuilder();
            prompt.append("你是一位资深的敏捷项目经理和团队效能专家。请深入分析以下团队任务数据，生成一份专业的【团队协作效能分析报告】。\n\n");
            prompt.append("请严格按照以下格式输出（通过Markdown）：\n\n");
            prompt.append("### 📊 团队效能概览\n");
            prompt.append("- 简要总结当前团队的任务进度和整体状态。\n");
            prompt.append("- 计算任务按期完成率的预估。\n\n");
            prompt.append("### ⚠️ 风险预警\n");
            prompt.append("- 识别即将超期或已超期的任务。\n");
            prompt.append("- 指出进度滞后于时间进度的风险任务。\n\n");
            prompt.append("### ⚖️ 成员负载分析\n");
            prompt.append("- 分析各成员的任务分配是否均衡。\n");
            prompt.append("- 指出谁可能工作过载，谁还有余力。\n\n");
            prompt.append("### 💡 改进建议\n");
            prompt.append("- 给出3-5条具体的、可执行的调整建议（如“建议将任务A转交给成员B”）。\n\n");
            prompt.append("--- 数据如下 ---\n\n");
            prompt.append("| 任务名称 | 负责人 | 优先级 | 状态 | 截止日期 | 进度 | 预计工时 |\n");
            prompt.append("|---|---|---|---|---|---|---|\n");

            for (TeamTask task : tasks) {
                prompt.append("| ").append(task.getName())
                        .append(" | ").append(task.getAssigneeName() != null ? task.getAssigneeName() : "待定")
                        .append(" | ").append(getPriorityText(task.getPriority()))
                        .append(" | ").append(getStatusText(task.getStatus()))
                        .append(" | ")
                        .append(task.getDeadline() != null ? task.getDeadline().toString().split(" ")[0] : "无")
                        .append(" | ").append(task.getProgress()).append("%")
                        .append(" | ").append(task.getWorkDuration() != null ? task.getWorkDuration() + "分" : "-")
                        .append(" |\n");
            }

            // 创建会话并发送消息
            String sessionId = aiChatService.createNewSession(userId, null);
            com.aicustomer.entity.AiChat response = aiChatService.sendMessage(sessionId, prompt.toString(), null);

            // 可以选择删除会话以保持清洁，或者保留作为历史
            // aiChatService.deleteSession(sessionId);

            return Result.success(response.getReplyContent());

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("AI分析失败: " + e.getMessage());
        }
    }

    private String getPriorityText(Integer p) {
        if (p == null)
            return "未知";
        return switch (p) {
            case 1 -> "低";
            case 2 -> "中";
            case 3 -> "高";
            case 4 -> "紧急";
            default -> "未知";
        };
    }

    private String getStatusText(Integer s) {
        if (s == null)
            return "未知";
        return switch (s) {
            case 1 -> "待分配";
            case 2 -> "进行中";
            case 3 -> "待审核";
            case 4 -> "已完成";
            case 5 -> "已取消";
            default -> "未知";
        };
    }

    /**
     * 获取团队任务列表（分页）
     */
    @GetMapping("/tasks")
    public Result<PageResult<TeamTask>> getTasks(@RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer priority,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            List<TeamTask> tasks = taskService.getTasks(assigneeId, status, priority, page, size);
            int total = taskMapper.countTasks(assigneeId, status, priority);
            PageResult<TeamTask> pageResult = new PageResult<>();
            pageResult.setList(tasks);
            pageResult.setTotal((long) total);
            pageResult.setPageNum(page);
            pageResult.setPageSize(size);
            pageResult.setPages((int) Math.ceil((double) total / size));
            return Result.success(pageResult);
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
            if (task == null) {
                return Result.error("任务数据不能为空");
            }
            TeamTask createdTask = taskService.createTask(task);
            return Result.success(createdTask);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
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
            if (task == null) {
                return Result.error("任务数据不能为空");
            }
            task.setId(id);
            TeamTask updatedTask = taskService.updateTask(task);
            return Result.success(updatedTask);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
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
            if (id == null) {
                return Result.error("任务ID不能为空");
            }
            taskService.deleteTask(id);
            return Result.success("删除成功");
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
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
