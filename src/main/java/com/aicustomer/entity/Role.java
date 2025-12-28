package com.aicustomer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity {

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 状态 (1:正常, 2:禁用)
     */
    private Integer status;
}
