package com.aicustomer.dto;

import lombok.Data;
import java.util.List;

/**
 * 用户权限DTO - 简化版
 * 用于创建/更新用户时直接配置权限
 */
@Data
public class UserPermissionDTO {

    /**
     * 用户ID（更新时使用）
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户状态(1:正常,2:禁用)
     */
    private Integer status;

    /**
     * 角色模板
     * admin: 管理员（所有权限）
     * manager: 经理（大部分权限，不能管理用户）
     * employee: 普通员工（基础权限）
     * readonly: 只读用户（只能查看）
     * custom: 自定义（需要手动选择菜单）
     */
    private String roleTemplate;

    /**
     * 可访问的菜单ID列表
     * 当 roleTemplate = 'custom' 时使用
     */
    private List<String> menuPermissions;

    /**
     * 数据权限配置
     */
    private DataPermissionConfig dataPermission;

    /**
     * 数据权限配置
     */
    @Data
    public static class DataPermissionConfig {
        /**
         * 可访问的客户等级
         * 1: 普通客户
         * 2: VIP客户
         * 3: 钻石客户
         * 如果为空或包含0，表示可以访问所有等级
         */
        private List<Integer> customerLevels;

        /**
         * 数据范围
         * all: 所有数据
         * department: 本部门数据
         * self: 仅自己的数据
         */
        private String dataScope;

        /**
         * 是否可以导出数据
         */
        private Boolean canExport;

        /**
         * 是否可以删除数据
         */
        private Boolean canDelete;
    }
}
