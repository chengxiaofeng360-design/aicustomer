package com.aicustomer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 客户账号实体类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerAccount extends BaseEntity {
    
    /**
     * 关联客户ID
     */
    private Long customerId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 个人简介
     */
    private String bio;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 状态 (1:正常, 2:禁用)
     */
    private Integer status;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 所在地
     */
    private String location;
    
    /**
     * 兴趣爱好
     */
    private String interests;
    
    /**
     * 公司
     */
    private String company;
    
    /**
     * 职位
     */
    private String position;
    
    /**
     * 个人网站
     */
    private String website;
}
