package com.aicustomer.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统配置实体类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Data
public class SystemConfig {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 配置键（唯一标识）
     */
    private String configKey;
    
    /**
     * 配置值
     */
    private String configValue;
    
    /**
     * 配置类型：STRING, NUMBER, BOOLEAN, JSON
     */
    private String configType;
    
    /**
     * 配置描述
     */
    private String description;
    
    /**
     * 配置分组（用于归类）
     */
    private String configGroup;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 是否删除（0:未删除, 1:已删除）
     */
    private Integer deleted;
}

