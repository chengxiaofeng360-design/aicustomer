package com.aicustomer.mapper;

import com.aicustomer.entity.TeamTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 团队任务Mapper接口
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Mapper
public interface TeamTaskMapper {

    /**
     * 根据ID查询团队任务
     */
    TeamTask selectById(@Param("id") Long id);

    /**
     * 查询团队任务列表
     */
    List<TeamTask> selectList(@Param("task") TeamTask task);

    /**
     * 分页查询团队任务
     */
    List<TeamTask> selectPage(@Param("task") TeamTask task, 
                             @Param("offset") int offset, 
                             @Param("limit") int limit);

    /**
     * 查询团队任务总数
     */
    Long selectCount(@Param("task") TeamTask task);

    /**
     * 插入团队任务
     */
    int insert(@Param("task") TeamTask task);

    /**
     * 更新团队任务
     */
    int updateById(@Param("task") TeamTask task);

    /**
     * 根据ID删除团队任务
     */
    int deleteById(@Param("id") Long id);

    /**
     * 分配任务
     */
    int assignTask(@Param("id") Long id, @Param("assignee") String assignee);

    /**
     * 更新任务状态
     */
    int updateTaskStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 更新任务进度
     */
    int updateTaskProgress(@Param("id") Long id, @Param("progress") int progress);

    /**
     * 查询我的任务
     */
    List<TeamTask> selectMyTasks(@Param("assignee") String assignee, @Param("status") String status);

    /**
     * 查询我分配的任务
     */
    List<TeamTask> selectAssignedTasks(@Param("creator") String creator, @Param("status") String status);

    /**
     * 根据客户ID查询任务
     */
    List<TeamTask> selectByCustomerId(@Param("customerId") Long customerId);
}



