package com.aicustomer.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户基本信息实体类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Customer extends BaseEntity {
    
    /**
     * 客户编号
     */
    private String customerCode;
    
    /**
     * 客户姓名/企业名称
     */
    private String customerName;
    
    /**
     * 联系人
     */
    private String contactPerson;
    
    /**
     * 客户类型 (1:个人客户, 2:企业客户)
     */
    private Integer customerType;
    
    /**
     * 手机号码
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 地址
     */
    private String address;
    
    /**
     * 邮政编码
     */
    private String postalCode;
    
    /**
     * 传真
     */
    private String fax;
    
    /**
     * 机构代码或身份证号码
     */
    private String organizationCode;
    
    /**
     * 国籍或所在国（地区）
     */
    private String nationality;
    
    /**
     * 职务
     */
    private String position;
    
    /**
     * QQ/微信
     */
    private String qqWeixin;
    
    /**
     * 合作内容
     */
    private String cooperationContent;
    
    /**
     * 地区
     */
    private String region;
    
    /**
     * 代理机构名称
     */
    private String agencyName;
    
    /**
     * 代理机构组织机构代码
     */
    private String agencyCode;
    
    /**
     * 代理机构地址
     */
    private String agencyAddress;
    
    /**
     * 代理机构邮政编码
     */
    private String agencyPostalCode;
    
    /**
     * 代理人姓名
     */
    private String agentName;
    
    /**
     * 代理人电话
     */
    private String agentPhone;
    
    /**
     * 代理人传真
     */
    private String agentFax;
    
    /**
     * 代理人手机
     */
    private String agentMobile;
    
    /**
     * 代理人邮箱
     */
    private String agentEmail;
    
    /**
     * 是否为敏感数据
     */
    private Boolean isSensitive;
    
    /**
     * 保护密码
     */
    private String protectionPassword;
    
    /**
     * 客户等级 (1:普通, 2:VIP, 3:钻石)
     */
    private Integer customerLevel;
    
    /**
     * 客户状态 (1:正常, 2:冻结, 3:注销)
     */
    private Integer status;
    
    /**
     * 客户来源 (1:线上, 2:线下, 3:推荐)
     */
    private Integer source;
    
    /**
     * 负责业务员ID
     */
    private Long assignedUserId;
    
    /**
     * 负责业务员姓名
     */
    private String assignedUserName;
    
    /**
     * 最后联系时间
     */
    private java.time.LocalDateTime lastContactTime;
    
    /**
     * 备注
     */
    private String remark;
}
