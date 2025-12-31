package com.aicustomer.dto;

import lombok.Data;
import java.util.List;

/**
 * 用户权限DTO - 简化版
 * 直接配置菜单权限和数据权限，不使用角色模板
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
     * 可访问的菜单ID列表
     * 例如: ["home", "customer-list", "customer-add", ...]
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
         * 是否可以查看敏感数据
         */
        private Boolean canViewSensitive;

        /**
         * 是否可以访问VIP客户
         */
        private Boolean canAccessVip;

        /**
         * 是否可以访问钻石客户
         */
        private Boolean canAccessDiamond;

        /**
         * 是否可以导出数据
         */
        private Boolean canExport;

        /**
         * 是否可以删除数据
         */
        private Boolean canDelete;

        /**
         * 是否可以导入数据
         */
        private Boolean canImport;

        /**
         * 是否可以查看所有数据
         */
        private Boolean canViewAllData;

        /**
         * 是否可以查看本部门数据
         */
        private Boolean canViewDepartmentData;
    }
}
