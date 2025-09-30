package com.aicustomer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    
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
     * 昵称
     */
    private String nickname;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 手机号码
     */
    private String phone;
    
    /**
     * 头像路径
     */
    private String avatar;
    
    /**
     * 用户状态 (1:正常, 2:禁用, 3:锁定)
     */
    private Integer status;
    
    /**
     * 用户类型 (1:管理员, 2:业务员, 3:助理, 4:其他)
     */
    private Integer userType;
    
    /**
     * 部门ID
     */
    private Long departmentId;
    
    /**
     * 部门名称
     */
    private String departmentName;
    
    /**
     * 职位
     */
    private String position;
    
    /**
     * 入职时间
     */
    private LocalDateTime joinTime;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 最后登录IP
     */
    private String lastLoginIp;
    
    /**
     * 登录次数
     */
    private Integer loginCount;
    
    /**
     * 密码修改时间
     */
    private LocalDateTime passwordChangeTime;
    
    /**
     * 是否首次登录
     */
    private Boolean isFirstLogin;
    
    /**
     * 个人签名
     */
    private String signature;
    
    /**
     * 个人简介
     */
    private String bio;
    
    /**
     * 工作地点
     */
    private String workLocation;
    
    /**
     * 时区
     */
    private String timezone;
    
    /**
     * 语言偏好
     */
    private String language;
    
    /**
     * 主题偏好
     */
    private String theme;
    
    /**
     * 通知设置 (JSON格式)
     */
    private String notificationSettings;
    
    /**
     * 权限设置 (JSON格式)
     */
    private String permissionSettings;
}

