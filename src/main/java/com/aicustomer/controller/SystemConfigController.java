package com.aicustomer.controller;

import com.aicustomer.common.Result;
import com.aicustomer.entity.SystemConfig;
import com.aicustomer.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统配置控制器
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/system-config")
@RequiredArgsConstructor
public class SystemConfigController {
    
    private final SystemConfigService systemConfigService;
    
    /**
     * 查询所有配置
     */
    @GetMapping("/list")
    public Result<List<SystemConfig>> list() {
        try {
            List<SystemConfig> configs = systemConfigService.list();
            return Result.success(configs);
        } catch (Exception e) {
            log.error("查询配置列表失败", e);
            return Result.error("查询配置列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID查询配置
     */
    @GetMapping("/{id}")
    public Result<SystemConfig> getById(@PathVariable Long id) {
        try {
            SystemConfig config = systemConfigService.getById(id);
            if (config == null) {
                return Result.error("配置不存在");
            }
            return Result.success(config);
        } catch (Exception e) {
            log.error("查询配置失败", e);
            return Result.error("查询配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据配置键查询配置
     */
    @GetMapping("/key/{configKey}")
    public Result<SystemConfig> getByKey(@PathVariable String configKey) {
        try {
            SystemConfig config = systemConfigService.getByKey(configKey);
            if (config == null) {
                return Result.error("配置不存在");
            }
            return Result.success(config);
        } catch (Exception e) {
            log.error("查询配置失败", e);
            return Result.error("查询配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据分组查询配置
     */
    @GetMapping("/group/{configGroup}")
    public Result<List<SystemConfig>> getByGroup(@PathVariable String configGroup) {
        try {
            log.info("查询配置分组: {}", configGroup);
            List<SystemConfig> configs = systemConfigService.getByGroup(configGroup);
            log.info("查询到 {} 条配置", configs != null ? configs.size() : 0);
            return Result.success(configs);
        } catch (Exception e) {
            log.error("查询配置失败", e);
            return Result.error("查询配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索配置
     */
    @GetMapping("/search")
    public Result<List<SystemConfig>> search(@RequestParam String keyword) {
        try {
            List<SystemConfig> configs = systemConfigService.search(keyword);
            return Result.success(configs);
        } catch (Exception e) {
            log.error("搜索配置失败", e);
            return Result.error("搜索配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 新增配置
     */
    @PostMapping
    public Result<Void> create(@RequestBody SystemConfig config) {
        try {
            if (config.getConfigKey() == null || config.getConfigKey().trim().isEmpty()) {
                return Result.error("配置键不能为空");
            }
            if (config.getConfigValue() == null || config.getConfigValue().trim().isEmpty()) {
                return Result.error("配置值不能为空");
            }
            if (config.getConfigType() == null || config.getConfigType().trim().isEmpty()) {
                return Result.error("配置类型不能为空");
            }
            
            boolean success = systemConfigService.save(config);
            if (success) {
                return Result.success();
            } else {
                return Result.error("保存失败，配置键可能已存在");
            }
        } catch (Exception e) {
            log.error("新增配置失败", e);
            return Result.error("新增配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新配置
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SystemConfig config) {
        try {
            config.setId(id);
            if (config.getConfigKey() == null || config.getConfigKey().trim().isEmpty()) {
                return Result.error("配置键不能为空");
            }
            if (config.getConfigValue() == null || config.getConfigValue().trim().isEmpty()) {
                return Result.error("配置值不能为空");
            }
            if (config.getConfigType() == null || config.getConfigType().trim().isEmpty()) {
                return Result.error("配置类型不能为空");
            }
            
            boolean success = systemConfigService.save(config);
            if (success) {
                return Result.success();
            } else {
                return Result.error("更新失败，配置键可能已存在");
            }
        } catch (Exception e) {
            log.error("更新配置失败", e);
            return Result.error("更新配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除配置
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            boolean success = systemConfigService.delete(id);
            if (success) {
                return Result.success();
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除配置失败", e);
            return Result.error("删除配置失败: " + e.getMessage());
        }
    }
}

