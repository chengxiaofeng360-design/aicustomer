package com.aicustomer.service.impl;

import com.aicustomer.entity.Customer;
import com.aicustomer.mapper.CustomerMapper;
import com.aicustomer.service.SensitiveDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 敏感数据保护服务实现
 */
@Service
public class SensitiveDataServiceImpl implements SensitiveDataService {
    
    @Autowired
    private CustomerMapper customerMapper;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public Customer maskSensitiveData(Customer customer) {
        if (customer == null) {
            return null;
        }
        
        // 如果不是敏感数据，直接返回
        if (customer.getIsSensitive() == null || !customer.getIsSensitive()) {
            return customer;
        }
        
        // 创建脱敏后的客户对象
        Customer maskedCustomer = new Customer();
        maskedCustomer.setId(customer.getId());
        maskedCustomer.setCustomerCode(customer.getCustomerCode());
        maskedCustomer.setCustomerName(maskString(customer.getCustomerName(), 1, 1));
        maskedCustomer.setCustomerType(customer.getCustomerType());
        maskedCustomer.setPhone(maskString(customer.getPhone(), 3, 4));
        maskedCustomer.setEmail(maskString(customer.getEmail(), 2, 2));
        maskedCustomer.setAddress(maskString(customer.getAddress(), 2, 2));
        maskedCustomer.setCustomerLevel(customer.getCustomerLevel());
        maskedCustomer.setStatus(customer.getStatus());
        maskedCustomer.setRemark(customer.getRemark());
        maskedCustomer.setSource(customer.getSource());
        maskedCustomer.setLastContactTime(customer.getLastContactTime());
        maskedCustomer.setCreateTime(customer.getCreateTime());
        maskedCustomer.setUpdateTime(customer.getUpdateTime());
        maskedCustomer.setCreateBy(customer.getCreateBy());
        maskedCustomer.setUpdateBy(customer.getUpdateBy());
        maskedCustomer.setDeleted(customer.getDeleted());
        maskedCustomer.setVersion(customer.getVersion());
        maskedCustomer.setContactPerson(maskString(customer.getContactPerson(), 1, 1));
        maskedCustomer.setPostalCode(customer.getPostalCode());
        maskedCustomer.setFax(customer.getFax());
        maskedCustomer.setOrganizationCode(maskString(customer.getOrganizationCode(), 2, 2));
        maskedCustomer.setNationality(customer.getNationality());
        maskedCustomer.setPosition(customer.getPosition());
        maskedCustomer.setQqWeixin(customer.getQqWeixin());
        maskedCustomer.setCooperationContent(customer.getCooperationContent());
        maskedCustomer.setAgencyName(customer.getAgencyName());
        maskedCustomer.setAgencyCode(customer.getAgencyCode());
        maskedCustomer.setAgencyAddress(maskString(customer.getAgencyAddress(), 2, 2));
        maskedCustomer.setAgencyPostalCode(customer.getAgencyPostalCode());
        maskedCustomer.setAgentName(customer.getAgentName());
        maskedCustomer.setAgentPhone(maskString(customer.getAgentPhone(), 3, 4));
        maskedCustomer.setAgentFax(customer.getAgentFax());
        maskedCustomer.setAgentMobile(maskString(customer.getAgentMobile(), 3, 4));
        maskedCustomer.setAgentEmail(maskString(customer.getAgentEmail(), 2, 2));
        maskedCustomer.setIsSensitive(customer.getIsSensitive());
        maskedCustomer.setProtectionPassword(customer.getProtectionPassword());
        
        return maskedCustomer;
    }
    
    @Override
    public boolean verifyProtectionPassword(Long customerId, String password) {
        try {
            Customer customer = customerMapper.selectById(customerId);
            if (customer == null || customer.getProtectionPassword() == null) {
                return false;
            }
            return passwordEncoder.matches(password, customer.getProtectionPassword());
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 字符串脱敏处理
     * @param originalString 原始字符串
     * @param startLength 保留开头字符数
     * @param endLength 保留结尾字符数
     * @return 脱敏后的字符串
     */
    private String maskString(String originalString, int startLength, int endLength) {
        if (originalString == null || originalString.isEmpty()) {
            return originalString;
        }
        
        int length = originalString.length();
        if (length <= startLength + endLength) {
            // 如果字符串太短，只保留第一个字符
            if (length == 2) {
                return originalString.substring(0, 1) + "*";
            }
            // 使用循环代替String.repeat()以兼容Java 8
            StringBuilder masked = new StringBuilder();
            for (int i = 0; i < length; i++) {
                masked.append("*");
            }
            return masked.toString();
        }
        
        StringBuilder masked = new StringBuilder();
        masked.append(originalString.substring(0, startLength));
        
        // 使用循环代替String.repeat()以兼容Java 8
        for (int i = 0; i < length - startLength - endLength; i++) {
            masked.append("*");
        }
        
        masked.append(originalString.substring(length - endLength));
        return masked.toString();
    }
}