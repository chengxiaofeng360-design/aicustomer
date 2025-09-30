package com.aicustomer.controller;

import com.aicustomer.common.PageResult;
import com.aicustomer.common.Result;
import com.aicustomer.entity.CommunicationRecord;
import com.aicustomer.service.CommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 沟通记录管理控制器
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/communication")
public class CommunicationController {

    @Autowired
    private CommunicationService communicationService;

    /**
     * 获取沟通记录列表（分页）
     */
    @GetMapping("/list")
    public Result<PageResult<CommunicationRecord>> getCommunicationList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String communicationType,
            @RequestParam(required = false) String keyword) {
        try {
            PageResult<CommunicationRecord> result = communicationService.getCommunicationList(
                    pageNum, pageSize, customerId, communicationType, keyword);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取沟通记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取沟通记录详情
     */
    @GetMapping("/{id}")
    public Result<CommunicationRecord> getCommunicationById(@PathVariable Long id) {
        try {
            CommunicationRecord record = communicationService.getCommunicationById(id);
            if (record != null) {
                return Result.success(record);
            } else {
                return Result.error("沟通记录不存在");
            }
        } catch (Exception e) {
            return Result.error("获取沟通记录详情失败: " + e.getMessage());
        }
    }

    /**
     * 新增沟通记录
     */
    @PostMapping
    public Result<String> addCommunication(@RequestBody CommunicationRecord record) {
        try {
            communicationService.addCommunication(record);
            return Result.success("沟通记录添加成功");
        } catch (Exception e) {
            return Result.error("沟通记录添加失败: " + e.getMessage());
        }
    }

    /**
     * 更新沟通记录
     */
    @PutMapping("/{id}")
    public Result<String> updateCommunication(@PathVariable Long id, @RequestBody CommunicationRecord record) {
        try {
            record.setId(id);
            communicationService.updateCommunication(record);
            return Result.success("沟通记录更新成功");
        } catch (Exception e) {
            return Result.error("沟通记录更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除沟通记录
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteCommunication(@PathVariable Long id) {
        try {
            communicationService.deleteCommunication(id);
            return Result.success("沟通记录删除成功");
        } catch (Exception e) {
            return Result.error("沟通记录删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取客户的沟通记录
     */
    @GetMapping("/customer/{customerId}")
    public Result<List<CommunicationRecord>> getCustomerCommunications(@PathVariable Long customerId) {
        try {
            List<CommunicationRecord> records = communicationService.getCustomerCommunications(customerId);
            return Result.success(records);
        } catch (Exception e) {
            return Result.error("获取客户沟通记录失败: " + e.getMessage());
        }
    }

    /**
     * 智能信息提取
     */
    @PostMapping("/{id}/extract-info")
    public Result<Object> extractCommunicationInfo(@PathVariable Long id) {
        try {
            // 这里可以添加智能信息提取逻辑
            return Result.success("智能信息提取功能开发中");
        } catch (Exception e) {
            return Result.error("智能信息提取失败: " + e.getMessage());
        }
    }

    /**
     * 情感分析
     */
    @PostMapping("/{id}/sentiment-analysis")
    public Result<Object> analyzeSentiment(@PathVariable Long id) {
        try {
            // 这里可以添加情感分析逻辑
            return Result.success("情感分析功能开发中");
        } catch (Exception e) {
            return Result.error("情感分析失败: " + e.getMessage());
        }
    }

    /**
     * 生成后续任务
     */
    @PostMapping("/{id}/generate-task")
    public Result<Object> generateFollowUpTask(@PathVariable Long id) {
        try {
            // 这里可以添加后续任务生成逻辑
            return Result.success("后续任务生成功能开发中");
        } catch (Exception e) {
            return Result.error("后续任务生成失败: " + e.getMessage());
        }
    }

    /**
     * 标记重要信息
     */
    @PutMapping("/{id}/mark-important")
    public Result<String> markAsImportant(@PathVariable Long id, @RequestParam boolean important) {
        try {
            communicationService.markAsImportant(id, important);
            return Result.success("标记成功");
        } catch (Exception e) {
            return Result.error("标记失败: " + e.getMessage());
        }
    }

    /**
     * 获取沟通统计
     */
    @GetMapping("/statistics")
    public Result<Object> getCommunicationStatistics(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            // 这里可以添加沟通统计逻辑
            return Result.success("沟通统计功能开发中");
        } catch (Exception e) {
            return Result.error("获取沟通统计失败: " + e.getMessage());
        }
    }
}




