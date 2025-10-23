package com.aicustomer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 团队成员实体类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TeamMember extends BaseEntity {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 职位
     */
    private String position;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 电话
     */
    private String phone;
    
    /**
     * 部门
     */
    private String department;
    
    /**
     * 角色
     */
    private String role;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 员工编号
     */
    private String employeeId;
    
    /**
     * 入职日期
     */
    private LocalDateTime joinDate;
    
    /**
     * 工作状态 (1:在职, 2:离职, 3:休假)
     */
    private Integer workStatus;
    
    // ========== 远程办公相关字段 ==========
    
    /**
     * 工作模式 (1:办公室, 2:远程, 3:混合)
     */
    private Integer workMode;
    
    /**
     * 主要工作地点
     */
    private String primaryLocation;
    
    /**
     * 远程办公地址
     */
    private String remoteAddress;
    
    /**
     * 时区
     */
    private String timezone;
    
    /**
     * 工作时间 (JSON格式，如：{"start":"09:00","end":"18:00","days":[1,2,3,4,5]})
     */
    private String workSchedule;
    
    /**
     * 远程办公工具偏好 (JSON格式，如：{"video":"zoom","chat":"slack","docs":"google"})
     */
    private String toolPreferences;
    
    /**
     * 在线状态 (1:在线, 2:忙碌, 3:离开, 4:离线)
     */
    private Integer onlineStatus;
    
    /**
     * 最后在线时间
     */
    private LocalDateTime lastOnlineTime;
    
    /**
     * 是否接受远程协作
     */
    private Boolean acceptRemoteCollaboration;
    
    /**
     * 协作偏好说明
     */
    private String collaborationPreference;
    
    /**
     * 紧急联系方式
     */
    private String emergencyContact;
    
    /**
     * 远程办公设备信息
     */
    private String remoteEquipment;
    
    /**
     * 网络环境描述
     */
    private String networkEnvironment;
    
    /**
     * 远程办公备注
     */
    private String remoteNote;
}
