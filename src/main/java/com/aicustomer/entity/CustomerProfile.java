package com.aicustomer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

/**
 * 客户个人资料实体类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerProfile extends BaseEntity {
    
    /**
     * 客户账号ID
     */
    private Long customerAccountId;
    
    /**
     * 客户ID
     */
    private Long customerId;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 性别 (1:男, 2:女, 3:其他)
     */
    private Integer gender;
    
    /**
     * 生日
     */
    private LocalDate birthday;
    
    /**
     * 所在地
     */
    private String location;
    
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
    
    /**
     * 兴趣爱好(JSON格式)
     */
    private String interests;
    
    /**
     * 背景图片
     */
    private String backgroundImage;
    
    /**
     * 隐私设置(JSON格式)
     */
    private String privacySettings;
}
