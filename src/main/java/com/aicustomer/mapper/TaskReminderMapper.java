package com.aicustomer.mapper;

import com.aicustomer.entity.TaskReminder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务提醒Mapper接口
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Mapper
public interface TaskReminderMapper {

    /**
     * 根据ID查询任务提醒
     */
    TaskReminder selectById(@Param("id") Long id);

    /**
     * 查询任务提醒列表
     */
    List<TaskReminder> selectList(@Param("reminder") TaskReminder reminder);

    /**
     * 分页查询任务提醒
     */
    List<TaskReminder> selectPage(@Param("reminder") TaskReminder reminder, 
                                 @Param("offset") int offset, 
                                 @Param("limit") int limit);

    /**
     * 查询任务提醒总数
     */
    Long selectCount(@Param("reminder") TaskReminder reminder);

    /**
     * 插入任务提醒
     */
    int insert(@Param("reminder") TaskReminder reminder);

    /**
     * 更新任务提醒
     */
    int updateById(@Param("reminder") TaskReminder reminder);

    /**
     * 根据ID删除任务提醒
     */
    int deleteById(@Param("id") Long id);

    /**
     * 查询今日提醒
     */
    List<TaskReminder> selectTodayReminders();

    /**
     * 查询即将到期的提醒
     */
    List<TaskReminder> selectUpcomingReminders(@Param("days") int days);

    /**
     * 根据客户ID查询提醒
     */
    List<TaskReminder> selectByCustomerId(@Param("customerId") Long customerId);

    /**
     * 标记任务完成
     */
    int completeTask(@Param("id") Long id);

    /**
     * 延期任务
     */
    int postponeTask(@Param("id") Long id, @Param("newDueDate") String newDueDate);
}




