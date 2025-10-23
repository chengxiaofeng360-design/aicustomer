package com.aicustomer.service;

import com.aicustomer.entity.Customer;

/**
 * 敏感数据保护服务接口
 */
public interface SensitiveDataService {
    
    /**
     * 对敏感数据进行脱敏处理
     * @param customer 客户信息
     * @return 脱敏后的客户信息
     */
    Customer maskSensitiveData(Customer customer);
    
    /**
     * 验证保护密码
     * @param customerId 客户ID
     * @param password 密码
     * @return 验证结果
     */
    boolean verifyProtectionPassword(Long customerId, String password);
}