package com.aicustomer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限实体类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Permission extends BaseEntity {

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 权限编码
     */
    private String permissionCode;

    /**
     * 权限类型 (1:菜单, 2:按钮, 3:接口)
     */
    private Integer permissionType;

    /**
     * 父权限ID
     */
    private Long parentId;

    /**
     * 路径
     */
    private String path;

    /**
     * 组件
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态 (1:正常, 2:禁用)
     */
    private Integer status;
}
