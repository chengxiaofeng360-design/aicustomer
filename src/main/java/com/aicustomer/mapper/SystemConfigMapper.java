package com.aicustomer.mapper;

import com.aicustomer.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 系统配置Mapper接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Mapper
public interface SystemConfigMapper {
    
    /**
     * 根据ID查询配置
     */
    SystemConfig selectById(@Param("id") Long id);
    
    /**
     * 根据配置键查询配置
     */
    SystemConfig selectByKey(@Param("configKey") String configKey);
    
    /**
     * 查询所有配置
     */
    List<SystemConfig> selectAll();
    
    /**
     * 根据分组查询配置
     */
    List<SystemConfig> selectByGroup(@Param("configGroup") String configGroup);
    
    /**
     * 搜索配置（根据键或描述）
     */
    List<SystemConfig> search(@Param("keyword") String keyword);
    
    /**
     * 插入配置
     */
    int insert(@Param("config") SystemConfig config);
    
    /**
     * 更新配置
     */
    int updateById(@Param("config") SystemConfig config);
    
    /**
     * 删除配置（逻辑删除）
     */
    int deleteById(@Param("id") Long id);
}

