package com.aicustomer.service;

import com.aicustomer.common.PageResult;
import com.aicustomer.entity.Customer;
import com.aicustomer.entity.CustomerDetail;
import com.aicustomer.entity.CustomerTag;
import java.util.List;

/**
 * 客户服务接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
public interface CustomerService {
    
    /**
     * 根据ID查询客户
     */
    Customer getById(Long id);
    
    /**
     * 根据客户编号查询客户
     */
    Customer getByCustomerCode(String customerCode);
    
    /**
     * 查询客户列表
     */
    List<Customer> list(Customer customer);
    
    /**
     * 分页查询客户
     */
    PageResult<Customer> page(Integer pageNum, Integer pageSize, Customer customer);
    
    /**
     * 保存客户
     */
    boolean save(Customer customer);
    
    /**
     * 更新客户
     */
    boolean update(Customer customer);
    
    /**
     * 根据ID删除客户
     */
    boolean deleteById(Long id);
    
    /**
     * 批量删除客户
     */
    boolean deleteByIds(List<Long> ids);
    
    /**
     * 检查客户编号是否存在
     */
    boolean existsByCustomerCode(String customerCode);
    
    // 新增的客户档案管理方法
    
    /**
     * 获取客户列表（分页，支持搜索和筛选）
     */
    PageResult<Customer> getCustomerList(int pageNum, int pageSize, String keyword, String customerLevel, String status);
    
    /**
     * 根据ID获取客户
     */
    Customer getCustomerById(Long id);
    
    /**
     * 添加客户
     */
    void addCustomer(Customer customer);
    
    /**
     * 更新客户
     */
    void updateCustomer(Customer customer);
    
    /**
     * 删除客户
     */
    void deleteCustomer(Long id);
    
    /**
     * 批量删除客户
     */
    void deleteCustomers(List<Long> ids);
    
    /**
     * 获取客户标签列表
     */
    List<CustomerTag> getCustomerTags(Long customerId);
    
    /**
     * 添加客户标签
     */
    void addCustomerTag(CustomerTag tag);
    
    /**
     * 删除客户标签
     */
    void deleteCustomerTag(Long tagId);
    
    /**
     * 获取客户详细信息
     */
    CustomerDetail getCustomerDetail(Long customerId);
    
    /**
     * 更新客户详细信息
     */
    void updateCustomerDetail(CustomerDetail detail);
    
    /**
     * 更新客户生命周期
     */
    void updateCustomerLifecycle(Long customerId, String lifecycleStage);
}
