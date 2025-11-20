package com.aicustomer.mapper;

import com.aicustomer.entity.AiChat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * AI聊天Mapper接口
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Mapper
public interface AiChatMapper {

    /**
     * 根据ID查询AI聊天记录
     */
    AiChat selectById(Long id);

    /**
     * 查询AI聊天记录列表
     */
    List<AiChat> selectList(AiChat chat);

    /**
     * 分页查询AI聊天记录
     */
    List<AiChat> selectPage(@Param("chat") AiChat chat, @Param("offset") int offset, @Param("pageSize") int pageSize);

    /**
     * 查询AI聊天记录总数
     */
    Long selectCount(@Param("chat") AiChat chat);

    /**
     * 插入AI聊天记录
     */
    int insert(AiChat chat);

    /**
     * 更新AI聊天记录
     */
    int updateById(AiChat chat);

    /**
     * 根据ID删除AI聊天记录
     */
    int deleteById(Long id);

    /**
     * 批量删除AI聊天记录
     */
    int deleteByIds(@Param("ids") List<Long> ids);

    /**
     * 根据会话ID查询聊天记录
     */
    List<AiChat> selectBySessionId(@Param("sessionId") String sessionId);

    /**
     * 根据客户ID查询聊天记录
     */
    List<AiChat> selectByCustomerId(@Param("customerId") Long customerId);

    /**
     * 更新消息状态
     */
    int updateMessageStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 标记消息为已读
     */
    int markAsRead(@Param("id") Long id);

    /**
     * 标记消息为已处理
     */
    int markAsProcessed(@Param("id") Long id, @Param("processResult") String processResult);

    /**
     * 评价AI回复
     */
    int rateAiReply(@Param("id") Long id, @Param("satisfactionScore") Integer satisfactionScore);

    /**
     * 获取AI聊天统计
     */
    java.util.Map<String, Object> selectStatistics(@Param("userId") Long userId, 
                                                     @Param("customerId") Long customerId,
                                                     @Param("startTime") java.time.LocalDateTime startTime,
                                                     @Param("endTime") java.time.LocalDateTime endTime);
    
    /**
     * 获取会话列表（按sessionId分组，返回每个会话的第一条消息）
     */
    List<Map<String, Object>> selectSessionList(@Param("userId") Long userId, 
                                                 @Param("limit") Integer limit);
    
    /**
     * 根据会话ID获取消息列表
     */
    List<AiChat> selectMessagesBySessionId(@Param("sessionId") String sessionId);
}

