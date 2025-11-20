package com.aicustomer.service;

import com.aicustomer.entity.Message;

import java.util.List;
import java.util.Map;

/**
 * 消息服务接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
public interface MessageService {
    
    /**
     * 创建消息
     * 
     * @param message 消息对象
     * @return 创建的消息
     */
    Message createMessage(Message message);
    
    /**
     * 批量创建消息
     * 
     * @param messages 消息列表
     * @return 创建成功的数量
     */
    int batchCreateMessages(List<Message> messages);
    
    /**
     * 获取用户消息列表
     * 
     * @param userId 用户ID
     * @param messageType 消息类型（可选）
     * @param isRead 是否已读（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 消息列表
     */
    Map<String, Object> getUserMessages(Long userId, Integer messageType, Integer isRead, Integer pageNum, Integer pageSize);
    
    /**
     * 获取未读消息数量
     * 
     * @param userId 用户ID
     * @return 未读消息数量
     */
    int getUnreadCount(Long userId);
    
    /**
     * 标记消息为已读
     * 
     * @param messageId 消息ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markAsRead(Long messageId, Long userId);
    
    /**
     * 批量标记为已读
     * 
     * @param messageIds 消息ID列表
     * @param userId 用户ID
     * @return 成功数量
     */
    int batchMarkAsRead(List<Long> messageIds, Long userId);
    
    /**
     * 标记所有消息为已读
     * 
     * @param userId 用户ID
     * @return 成功数量
     */
    int markAllAsRead(Long userId);
    
    /**
     * 处理消息
     * 
     * @param messageId 消息ID
     * @param userId 用户ID
     * @param processResult 处理结果
     * @return 是否成功
     */
    boolean processMessage(Long messageId, Long userId, String processResult);
    
    /**
     * 删除消息
     * 
     * @param messageId 消息ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteMessage(Long messageId, Long userId);
    
    /**
     * 批量删除消息
     * 
     * @param messageIds 消息ID列表
     * @param userId 用户ID
     * @return 成功数量
     */
    int batchDeleteMessages(List<Long> messageIds, Long userId);
    
    /**
     * 获取消息统计
     * 
     * @param userId 用户ID
     * @return 统计信息
     */
    Map<String, Object> getMessageStatistics(Long userId);
    
    /**
     * 创建客户相关消息
     * 
     * @param type 消息类型（birthday, no_contact, updated, imported）
     * @param customerId 客户ID
     * @param customerName 客户名称
     * @param userId 用户ID
     * @param extraInfo 额外信息
     */
    void createCustomerMessage(String type, Long customerId, String customerName, Long userId, Map<String, Object> extraInfo);
    
    /**
     * 创建沟通记录相关消息
     * 
     * @param type 消息类型（created, updated, reminder）
     * @param communicationId 沟通记录ID
     * @param customerId 客户ID
     * @param customerName 客户名称
     * @param userId 用户ID
     * @param extraInfo 额外信息
     */
    void createCommunicationMessage(String type, Long communicationId, Long customerId, String customerName, Long userId, Map<String, Object> extraInfo);
    
    /**
     * 创建任务相关消息
     * 
     * @param type 消息类型（assigned, due_soon, completed, updated）
     * @param taskId 任务ID
     * @param taskTitle 任务标题
     * @param assigneeId 负责人ID
     * @param extraInfo 额外信息
     */
    void createTaskMessage(String type, Long taskId, String taskTitle, Long assigneeId, Map<String, Object> extraInfo);
    
    /**
     * 创建AI分析相关消息
     * 
     * @param type 消息类型（opportunity, reminder, risk_warning）
     * @param analysisId 分析ID
     * @param customerId 客户ID
     * @param customerName 客户名称
     * @param userId 用户ID
     * @param extraInfo 额外信息
     */
    void createAiAnalysisMessage(String type, Long analysisId, Long customerId, String customerName, Long userId, Map<String, Object> extraInfo);
    
    /**
     * 创建系统操作消息
     * 
     * @param type 消息类型（import_success, import_failed, export_success, system_update）
     * @param userId 用户ID
     * @param title 标题
     * @param content 内容
     * @param extraInfo 额外信息
     */
    void createSystemMessage(String type, Long userId, String title, String content, Map<String, Object> extraInfo);
}

