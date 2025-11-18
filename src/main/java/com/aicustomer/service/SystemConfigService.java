package com.aicustomer.service;

import com.aicustomer.entity.SystemConfig;
import java.util.List;

/**
 * 系统配置服务接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
public interface SystemConfigService {
    
    /**
     * 查询所有配置
     */
    List<SystemConfig> list();
    
    /**
     * 根据ID查询配置
     */
    SystemConfig getById(Long id);
    
    /**
     * 根据配置键查询配置
     */
    SystemConfig getByKey(String configKey);
    
    /**
     * 根据分组查询配置
     */
    List<SystemConfig> getByGroup(String configGroup);
    
    /**
     * 搜索配置
     */
    List<SystemConfig> search(String keyword);
    
    /**
     * 保存配置（新增或更新）
     */
    boolean save(SystemConfig config);
    
    /**
     * 更新配置
     */
    boolean update(SystemConfig config);
    
    /**
     * 删除配置
     */
    boolean delete(Long id);
}

