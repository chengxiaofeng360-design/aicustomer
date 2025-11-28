package com.aicustomer.entity;

import com.aicustomer.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 知识库标签实体类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KbTag extends BaseEntity {
    
    /**
     * 标签名称
     */
    private String name;
    
    /**
     * 标签描述
     */
    private String description;
    
    /**
     * 标签颜色
     */
    private String color;
    
    /**
     * 使用次数
     */
    private Integer usageCount;
    
    /**
     * 标签分类: general,product,tech,business
     */
    private String category;
    
    /**
     * 是否启用
     */
    private Boolean isActive;
}
