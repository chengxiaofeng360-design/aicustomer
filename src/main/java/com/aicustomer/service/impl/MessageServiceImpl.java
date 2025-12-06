package com.aicustomer.service.impl;

import com.aicustomer.entity.Message;
import com.aicustomer.mapper.MessageMapper;
import com.aicustomer.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息服务实现类
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Message createMessage(Message message) {
        if (message.getCreateTime() == null) {
            message.setCreateTime(LocalDateTime.now());
        }
        if (message.getUpdateTime() == null) {
            message.setUpdateTime(LocalDateTime.now());
        }
        if (message.getIsRead() == null) {
            message.setIsRead(0);
        }
        if (message.getIsProcessed() == null) {
            message.setIsProcessed(0);
        }

        messageMapper.insert(message);
        return message;
    }

    @Override
    public int batchCreateMessages(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return 0;
        }
        messages.forEach(msg -> {
            if (msg.getCreateTime() == null)
                msg.setCreateTime(LocalDateTime.now());
            if (msg.getUpdateTime() == null)
                msg.setUpdateTime(LocalDateTime.now());
            if (msg.getIsRead() == null)
                msg.setIsRead(0);
        });
        return messageMapper.batchInsert(messages);
    }

    @Override
    public Map<String, Object> getUserMessages(Long userId, Integer messageType, Integer isRead, Integer pageNum,
            Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Message> list = messageMapper.selectUserMessages(userId, messageType, isRead, offset, pageSize);
        int total = messageMapper.countUserMessages(userId, messageType, isRead);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("pages", (int) Math.ceil((double) total / pageSize));
        return result;
    }

    @Override
    public int getUnreadCount(Long userId) {
        return messageMapper.countUnread(userId);
    }

    @Override
    public boolean markAsRead(Long messageId, Long userId) {
        return messageMapper.updateAsRead(messageId, userId) > 0;
    }

    @Override
    public int batchMarkAsRead(List<Long> messageIds, Long userId) {
        if (messageIds == null || messageIds.isEmpty()) {
            return 0;
        }
        return messageMapper.batchUpdateAsRead(messageIds, userId);
    }

    @Override
    public int markAllAsRead(Long userId) {
        return messageMapper.updateAllAsRead(userId);
    }

    @Override
    public boolean processMessage(Long messageId, Long userId, String processResult) {
        return messageMapper.updateProcessed(messageId, userId, processResult) > 0;
    }

    @Override
    public boolean deleteMessage(Long messageId, Long userId) {
        return messageMapper.deleteById(messageId, userId) > 0;
    }

    @Override
    public int batchDeleteMessages(List<Long> messageIds, Long userId) {
        if (messageIds == null || messageIds.isEmpty()) {
            return 0;
        }
        return messageMapper.batchDelete(messageIds, userId);
    }

    @Override
    public Map<String, Object> getMessageStatistics(Long userId) {
        return messageMapper.getStatistics(userId);
    }

    @Override
    public void createCustomerMessage(String type, Long customerId, String customerName, Long userId,
            Map<String, Object> extraInfo) {
        Message message = new Message();
        message.setBusinessType("customer");
        message.setBusinessId(customerId);
        message.setBusinessName(customerName);
        message.setReceiverId(userId);
        message.setSenderName("客户管理系统");
        message.setMessageType(4); // 客户消息

        switch (type) {
            case "birthday":
                message.setTitle("客户生日提醒");
                message.setContent("今天是客户 " + customerName + " 的生日，请及时发送祝福。");
                message.setIcon("bi-gift");
                message.setColor("danger");
                message.setImportance(2);
                break;
            case "no_contact":
                message.setTitle("客户久未联系提醒");
                message.setContent("您已超过30天未联系客户 " + customerName + "，建议及时跟进。");
                message.setIcon("bi-clock-history");
                message.setColor("warning");
                message.setImportance(2);
                break;
            default:
                message.setTitle("客户消息提醒");
                message.setContent("客户 " + customerName + " 有新的动态。");
                message.setIcon("bi-person");
                message.setColor("info");
        }

        setExtraInfo(message, extraInfo);
        createMessage(message);
    }

    @Override
    public void createCommunicationMessage(String type, Long communicationId, Long customerId, String customerName,
            Long userId, Map<String, Object> extraInfo) {
        // Implementation
    }

    @Override
    public void createTaskMessage(String type, Long taskId, String taskTitle, Long assigneeId,
            Map<String, Object> extraInfo) {
        Message message = new Message();
        message.setBusinessType("task");
        message.setBusinessId(taskId);
        message.setBusinessName(taskTitle);
        message.setReceiverId(assigneeId);
        message.setSenderName("任务管理系统");
        message.setMessageType(3); // 任务通知

        switch (type) {
            case "assigned":
                message.setTitle("新任务分配");
                message.setContent("您有一个新任务：" + taskTitle);
                message.setIcon("bi-list-check");
                message.setColor("info");
                break;
            case "due_soon":
                message.setTitle("任务即将截止");
                message.setContent("任务 " + taskTitle + " 即将截止，请及时处理。");
                message.setIcon("bi-alarm");
                message.setColor("danger");
                message.setImportance(3);
                break;
        }

        setExtraInfo(message, extraInfo);
        createMessage(message);
    }

    @Override
    public void createAiAnalysisMessage(String type, Long analysisId, Long customerId, String customerName, Long userId,
            Map<String, Object> extraInfo) {
        // Implementation
    }

    @Override
    public void createSystemMessage(String type, Long userId, String title, String content,
            Map<String, Object> extraInfo) {
        Message message = new Message();
        message.setBusinessType("system");
        message.setReceiverId(userId);
        message.setSenderName("系统通知");
        message.setMessageType(1); // 系统消息
        message.setTitle(title);
        message.setContent(content);
        message.setIcon("bi-bell");
        message.setColor("info");

        setExtraInfo(message, extraInfo);
        createMessage(message);
    }

    private void setExtraInfo(Message message, Map<String, Object> extraInfo) {
        if (extraInfo != null && !extraInfo.isEmpty()) {
            try {
                message.setExtraInfo(objectMapper.writeValueAsString(extraInfo));
            } catch (JsonProcessingException e) {
                log.error("JSON processing error", e);
            }
        }
    }
}
