package com.aicustomer.service;

import com.aicustomer.entity.CustomerAccount;
import java.util.Map;

/**
 * 客户账号服务接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
public interface CustomerAccountService {
    
    /**
     * 客户注册
     */
    CustomerAccount register(CustomerAccount customerAccount);
    
    /**
     * 客户登录
     */
    CustomerAccount login(String username, String password);
    
    /**
     * 根据ID获取客户账号
     */
    CustomerAccount getAccountById(Long id);
    
    /**
     * 根据用户名查找客户账号
     */
    CustomerAccount findByUsername(String username);
    
    /**
     * 根据ID查找客户账号
     */
    CustomerAccount findById(Long id);
    
    /**
     * 保存客户账号
     */
    CustomerAccount save(CustomerAccount customerAccount);
    
    /**
     * 更新客户账号
     */
    CustomerAccount update(CustomerAccount customerAccount);
    
    /**
     * 获取用户统计信息
     */
    Map<String, Object> getUserStats(Long userId);
}
