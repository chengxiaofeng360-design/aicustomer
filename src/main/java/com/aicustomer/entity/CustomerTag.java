package com.aicustomer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户标签实体类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerTag extends BaseEntity {
    
    /**
     * 客户ID
     */
    private Long customerId;
    
    /**
     * 标签名称
     */
    private String tagName;
    
    /**
     * 标签类型 (1:系统标签, 2:自定义标签, 3:AI标签)
     */
    private Integer tagType;
    
    /**
     * 标签颜色
     */
    private String tagColor;
    
    /**
     * 标签描述
     */
    private String tagDescription;
    
    /**
     * 标签权重 (用于排序)
     */
    private Integer weight;
    
    /**
     * 是否显示 (0:隐藏, 1:显示)
     */
    private Integer isVisible;
}





