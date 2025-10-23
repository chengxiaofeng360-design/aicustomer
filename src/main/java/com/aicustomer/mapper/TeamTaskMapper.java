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
     * 查询团队任务列表
     */
    List<TeamTask> findTasks(@Param("assigneeId") Long assigneeId,
                             @Param("status") Integer status,
                             @Param("priority") Integer priority,
                             @Param("offset") Integer offset,
                             @Param("limit") Integer limit);

    /**
     * 根据ID查询团队任务
     */
    TeamTask findById(@Param("id") Long id);

    /**
     * 插入团队任务
     */
    int insert(TeamTask task);

    /**
     * 更新团队任务
     */
    int update(TeamTask task);

    /**
     * 删除团队任务
     */
    int delete(@Param("id") Long id);

    /**
     * 获取任务总数
     */
    int countTasks(@Param("assigneeId") Long assigneeId,
                   @Param("status") Integer status,
                   @Param("priority") Integer priority);
}