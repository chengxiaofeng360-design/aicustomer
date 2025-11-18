package com.aicustomer.service.impl;

import com.aicustomer.entity.SystemConfig;
import com.aicustomer.mapper.SystemConfigMapper;
import com.aicustomer.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统配置服务实现类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl implements SystemConfigService {
    
    private final SystemConfigMapper systemConfigMapper;
    
    @Override
    public List<SystemConfig> list() {
        return systemConfigMapper.selectAll();
    }
    
    @Override
    public SystemConfig getById(Long id) {
        return systemConfigMapper.selectById(id);
    }
    
    @Override
    public SystemConfig getByKey(String configKey) {
        return systemConfigMapper.selectByKey(configKey);
    }
    
    @Override
    public List<SystemConfig> getByGroup(String configGroup) {
        return systemConfigMapper.selectByGroup(configGroup);
    }
    
    @Override
    public List<SystemConfig> search(String keyword) {
        return systemConfigMapper.search(keyword);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(SystemConfig config) {
        try {
            // 检查配置键是否已存在
            SystemConfig existing = systemConfigMapper.selectByKey(config.getConfigKey());
            if (existing != null && !existing.getId().equals(config.getId())) {
                log.warn("配置键已存在: {}", config.getConfigKey());
                return false;
            }
            
            if (config.getId() == null) {
                // 新增
                config.setCreateTime(LocalDateTime.now());
                config.setUpdateTime(LocalDateTime.now());
                config.setDeleted(0);
                return systemConfigMapper.insert(config) > 0;
            } else {
                // 更新
                config.setUpdateTime(LocalDateTime.now());
                return systemConfigMapper.updateById(config) > 0;
            }
        } catch (Exception e) {
            log.error("保存配置失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SystemConfig config) {
        config.setUpdateTime(LocalDateTime.now());
        return systemConfigMapper.updateById(config) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        return systemConfigMapper.deleteById(id) > 0;
    }
}

