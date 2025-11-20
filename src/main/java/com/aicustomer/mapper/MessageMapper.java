package com.aicustomer.mapper;

import com.aicustomer.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 消息Mapper接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Mapper
public interface MessageMapper {
    
    /**
     * 插入消息
     */
    int insert(Message message);
    
    /**
     * 批量插入消息
     */
    int batchInsert(List<Message> messages);
    
    /**
     * 根据ID查询消息
     */
    Message selectById(Long id);
    
    /**
     * 查询用户消息列表
     */
    List<Message> selectUserMessages(@Param("userId") Long userId, 
                                      @Param("messageType") Integer messageType,
                                      @Param("isRead") Integer isRead,
                                      @Param("offset") Integer offset,
                                      @Param("limit") Integer limit);
    
    /**
     * 统计用户消息数量
     */
    int countUserMessages(@Param("userId") Long userId,
                          @Param("messageType") Integer messageType,
                          @Param("isRead") Integer isRead);
    
    /**
     * 统计未读消息数量
     */
    int countUnread(@Param("userId") Long userId);
    
    /**
     * 更新消息为已读
     */
    int updateAsRead(@Param("id") Long id, @Param("userId") Long userId);
    
    /**
     * 批量更新为已读
     */
    int batchUpdateAsRead(@Param("ids") List<Long> ids, @Param("userId") Long userId);
    
    /**
     * 更新所有消息为已读
     */
    int updateAllAsRead(@Param("userId") Long userId);
    
    /**
     * 更新消息处理状态
     */
    int updateProcessed(@Param("id") Long id, 
                        @Param("userId") Long userId,
                        @Param("processResult") String processResult);
    
    /**
     * 删除消息（逻辑删除）
     */
    int deleteById(@Param("id") Long id, @Param("userId") Long userId);
    
    /**
     * 批量删除消息
     */
    int batchDelete(@Param("ids") List<Long> ids, @Param("userId") Long userId);
    
    /**
     * 获取消息统计
     */
    Map<String, Object> getStatistics(@Param("userId") Long userId);
}

