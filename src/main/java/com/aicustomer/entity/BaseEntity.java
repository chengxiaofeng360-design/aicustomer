package com.aicustomer.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 基础实体类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Data
public abstract class BaseEntity {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 创建人
     */
    private String createBy;
    
    /**
     * 更新人
     */
    private String updateBy;
    
    /**
     * 删除标志 (0:未删除, 1:已删除)
     */
    private Integer deleted;
    
    /**
     * 版本号
     */
    private Integer version;
}

