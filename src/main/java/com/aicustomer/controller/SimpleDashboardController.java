package com.aicustomer.controller;

import com.aicustomer.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 简化版仪表板控制器
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/dashboard")
public class SimpleDashboardController {

    /**
     * 获取仪表板统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 客户统计
        Map<String, Object> customerStats = new HashMap<>();
        customerStats.put("total", 1250);
        customerStats.put("active", 980);
        customerStats.put("vip", 156);
        customerStats.put("newThisMonth", 89);
        customerStats.put("growthRate", 12.5);
        stats.put("customers", customerStats);
        
        // 销售统计
        Map<String, Object> salesStats = new HashMap<>();
        salesStats.put("totalRevenue", 2580000.00);
        salesStats.put("monthlyRevenue", 185000.00);
        salesStats.put("growthRate", 8.3);
        salesStats.put("avgOrderValue", 12500.00);
        stats.put("sales", salesStats);
        
        // 任务统计
        Map<String, Object> taskStats = new HashMap<>();
        taskStats.put("total", 45);
        taskStats.put("completed", 32);
        taskStats.put("pending", 13);
        taskStats.put("overdue", 3);
        stats.put("tasks", taskStats);
        
        // 沟通统计
        Map<String, Object> communicationStats = new HashMap<>();
        communicationStats.put("totalCalls", 1250);
        communicationStats.put("totalEmails", 3200);
        communicationStats.put("totalMeetings", 89);
        communicationStats.put("responseRate", 85.6);
        stats.put("communication", communicationStats);
        
        return Result.success(stats);
    }
    
    /**
     * 获取客户趋势数据
     */
    @GetMapping("/trends")
    public Result<Map<String, Object>> getTrends() {
        Map<String, Object> trends = new HashMap<>();
        
        // 客户增长趋势（最近12个月）
        List<Map<String, Object>> customerGrowth = new ArrayList<>();
        String[] months = {"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
        int[] values = {45, 52, 48, 61, 55, 67, 72, 68, 75, 82, 78, 89};
        
        for (int i = 0; i < months.length; i++) {
            Map<String, Object> month = new HashMap<>();
            month.put("month", months[i]);
            month.put("value", values[i]);
            customerGrowth.add(month);
        }
        trends.put("customerGrowth", customerGrowth);
        
        // 收入趋势
        List<Map<String, Object>> revenueTrend = new ArrayList<>();
        double[] revenues = {120000, 135000, 128000, 145000, 138000, 162000, 175000, 168000, 182000, 195000, 188000, 210000};
        
        for (int i = 0; i < months.length; i++) {
            Map<String, Object> month = new HashMap<>();
            month.put("month", months[i]);
            month.put("revenue", revenues[i]);
            revenueTrend.add(month);
        }
        trends.put("revenueTrend", revenueTrend);
        
        // 客户来源分布
        List<Map<String, Object>> sourceDistribution = new ArrayList<>();
        Map<String, Object> source1 = new HashMap<>();
        source1.put("name", "线上推广");
        source1.put("value", 45);
        source1.put("color", "#667eea");
        sourceDistribution.add(source1);
        
        Map<String, Object> source2 = new HashMap<>();
        source2.put("name", "线下活动");
        source2.put("value", 30);
        source2.put("color", "#764ba2");
        sourceDistribution.add(source2);
        
        Map<String, Object> source3 = new HashMap<>();
        source3.put("name", "客户推荐");
        source3.put("value", 15);
        source3.put("color", "#f093fb");
        sourceDistribution.add(source3);
        
        Map<String, Object> source4 = new HashMap<>();
        source4.put("name", "其他渠道");
        source4.put("value", 10);
        source4.put("color", "#4facfe");
        sourceDistribution.add(source4);
        
        trends.put("sourceDistribution", sourceDistribution);
        
        return Result.success(trends);
    }
    
    /**
     * 获取最近活动
     */
    @GetMapping("/recent-activities")
    public Result<List<Map<String, Object>>> getRecentActivities() {
        List<Map<String, Object>> activities = new ArrayList<>();
        
        Map<String, Object> activity1 = new HashMap<>();
        activity1.put("id", 1);
        activity1.put("type", "customer");
        activity1.put("action", "新增客户");
        activity1.put("description", "张三 已添加为新客户");
        activity1.put("time", "2024-01-15 14:30");
        activity1.put("user", "李经理");
        activity1.put("icon", "fas fa-user-plus");
        activity1.put("color", "success");
        activities.add(activity1);
        
        Map<String, Object> activity2 = new HashMap<>();
        activity2.put("id", 2);
        activity2.put("type", "task");
        activity2.put("action", "完成任务");
        activity2.put("description", "客户回访任务已完成");
        activity2.put("time", "2024-01-15 11:20");
        activity2.put("user", "王助理");
        activity2.put("icon", "fas fa-check-circle");
        activity2.put("color", "info");
        activities.add(activity2);
        
        Map<String, Object> activity3 = new HashMap<>();
        activity3.put("id", 3);
        activity3.put("type", "communication");
        activity3.put("action", "电话沟通");
        activity3.put("description", "与客户李四进行了电话沟通");
        activity3.put("time", "2024-01-15 09:45");
        activity3.put("user", "张销售");
        activity3.put("icon", "fas fa-phone");
        activity3.put("color", "primary");
        activities.add(activity3);
        
        Map<String, Object> activity4 = new HashMap<>();
        activity4.put("id", 4);
        activity4.put("type", "meeting");
        activity4.put("action", "会议安排");
        activity4.put("description", "明天上午10点与客户会面");
        activity4.put("time", "2024-01-15 08:15");
        activity4.put("user", "陈经理");
        activity4.put("icon", "fas fa-calendar");
        activity4.put("color", "warning");
        activities.add(activity4);
        
        return Result.success(activities);
    }
    
    /**
     * 获取待办事项
     */
    @GetMapping("/todos")
    public Result<List<Map<String, Object>>> getTodos() {
        List<Map<String, Object>> todos = new ArrayList<>();
        
        Map<String, Object> todo1 = new HashMap<>();
        todo1.put("id", 1);
        todo1.put("title", "客户回访");
        todo1.put("description", "对VIP客户进行月度回访");
        todo1.put("priority", "high");
        todo1.put("dueDate", "2024-01-16");
        todo1.put("assignee", "李经理");
        todo1.put("status", "pending");
        todos.add(todo1);
        
        Map<String, Object> todo2 = new HashMap<>();
        todo2.put("id", 2);
        todo2.put("title", "合同续签");
        todo2.put("description", "处理即将到期的合同续签事宜");
        todo2.put("priority", "medium");
        todo2.put("dueDate", "2024-01-20");
        todo2.put("assignee", "王助理");
        todo2.put("status", "pending");
        todos.add(todo2);
        
        Map<String, Object> todo3 = new HashMap<>();
        todo3.put("id", 3);
        todo3.put("title", "数据分析");
        todo3.put("description", "完成月度客户数据分析报告");
        todo3.put("priority", "low");
        todo3.put("dueDate", "2024-01-25");
        todo3.put("assignee", "张分析师");
        todo3.put("status", "in_progress");
        todos.add(todo3);
        
        return Result.success(todos);
    }
}





