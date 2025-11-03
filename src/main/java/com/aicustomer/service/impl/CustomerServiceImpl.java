package com.aicustomer.service.impl;

import com.aicustomer.common.PageResult;
import com.aicustomer.entity.Customer;
import com.aicustomer.entity.CustomerDetail;
import com.aicustomer.entity.CustomerTag;
import com.aicustomer.mapper.CustomerMapper;
import com.aicustomer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 客户服务实现类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    
    private final CustomerMapper customerMapper;
    
    @Override
    public Customer getById(Long id) {
        return customerMapper.selectById(id);
    }
    
    @Override
    public Customer getByCustomerCode(String customerCode) {
        return customerMapper.selectByCustomerCode(customerCode);
    }
    
    @Override
    public List<Customer> list(Customer customer) {
        return customerMapper.selectList(customer);
    }
    
    @Override
    public PageResult<Customer> page(Integer pageNum, Integer pageSize, Customer customer) {
        int offset = (pageNum - 1) * pageSize;
        List<Customer> list = customerMapper.selectPage(customer, offset, pageSize);
        Long total = customerMapper.selectCount(customer);
        return new PageResult<>(pageNum, pageSize, total, list);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(Customer customer) {
        // 设置创建时间
        customer.setCreateTime(LocalDateTime.now());
        customer.setUpdateTime(LocalDateTime.now());
        customer.setDeleted(0);
        customer.setVersion(1);
        
        return customerMapper.insert(customer) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(Customer customer) {
        // 设置更新时间
        customer.setUpdateTime(LocalDateTime.now());
        
        return customerMapper.updateById(customer) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) {
        return customerMapper.deleteById(id) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(List<Long> ids) {
        return customerMapper.deleteByIds(ids) > 0;
    }
    
    @Override
    public boolean existsByCustomerCode(String customerCode) {
        return customerMapper.selectByCustomerCode(customerCode) != null;
    }
    
    // 新增的客户档案管理方法实现
    
    @Override
    public PageResult<Customer> getCustomerList(int pageNum, int pageSize, String keyword, String customerLevel, String status) {
        // 使用真实数据库查询
        Customer queryCustomer = new Customer();
        
        // 设置查询条件
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryCustomer.setCustomerName(keyword);
        }
        if (customerLevel != null && !customerLevel.trim().isEmpty()) {
            try {
                queryCustomer.setCustomerLevel(Integer.parseInt(customerLevel));
            } catch (NumberFormatException e) {
                // 忽略无效的等级参数
            }
        }
        if (status != null && !status.trim().isEmpty()) {
            try {
                queryCustomer.setStatus(Integer.parseInt(status));
            } catch (NumberFormatException e) {
                // 忽略无效的状态参数
            }
        }
        
        // 使用page方法进行真实数据库查询
        return page(pageNum, pageSize, queryCustomer);
    }
    
    @Override
    public Customer getCustomerById(Long id) {
        // 使用真实数据库查询
        return customerMapper.selectById(id);
    }
    
    @Override
    public void addCustomer(Customer customer) {
        // 确保客户编号存在
        if (customer.getCustomerCode() == null || customer.getCustomerCode().trim().isEmpty()) {
            customer.setCustomerCode("CUST" + System.currentTimeMillis() + (int)(Math.random() * 1000));
        }
        // 设置默认值
        if (customer.getStatus() == null) {
            customer.setStatus(1); // 默认正常
        }
        if (customer.getSource() == null) {
            customer.setSource(2); // 默认线下
        }
        if (customer.getCustomerLevel() == null) {
            customer.setCustomerLevel(1); // 默认普通
        }
        save(customer);
    }
    
    @Override
    public void updateCustomer(Customer customer) {
        update(customer);
    }
    
    @Override
    public void deleteCustomer(Long id) {
        deleteById(id);
    }
    
    @Override
    public void deleteCustomers(List<Long> ids) {
        deleteByIds(ids);
    }
    
    @Override
    public List<CustomerTag> getCustomerTags(Long customerId) {
        // 模拟客户标签数据
        List<CustomerTag> tags = new ArrayList<>();
        
        CustomerTag tag1 = new CustomerTag();
        tag1.setId(1L);
        tag1.setCustomerId(customerId);
        tag1.setTagName("重要客户");
        tag1.setTagType(1); // 1:重要程度
        tag1.setCreateTime(LocalDateTime.now());
        tags.add(tag1);
        
        CustomerTag tag2 = new CustomerTag();
        tag2.setId(2L);
        tag2.setCustomerId(customerId);
        tag2.setTagName("科技行业");
        tag2.setTagType(2); // 2:行业分类
        tag2.setCreateTime(LocalDateTime.now());
        tags.add(tag2);
        
        return tags;
    }
    
    @Override
    public void addCustomerTag(CustomerTag tag) {
        // 实际项目中应该保存到数据库
        System.out.println("添加客户标签: " + tag.getTagName());
    }
    
    @Override
    public void deleteCustomerTag(Long tagId) {
        // 实际项目中应该从数据库删除
        System.out.println("删除客户标签: " + tagId);
    }
    
    @Override
    public CustomerDetail getCustomerDetail(Long customerId) {
        // 模拟客户详细信息
        CustomerDetail detail = new CustomerDetail();
        detail.setId(1L);
        detail.setCustomerId(customerId);
        detail.setIndustryCategory("科技");
        detail.setBusinessType("软件开发");
        detail.setValueScore(85);
        detail.setLifecycleStage(3); // 3:合作
        detail.setCooperationCount(5);
        detail.setCreditLevel(1); // 1:A级
        detail.setCreateTime(LocalDateTime.now());
        return detail;
    }
    
    @Override
    public void updateCustomerDetail(CustomerDetail detail) {
        // 实际项目中应该更新数据库
        System.out.println("更新客户详细信息: " + detail.getCustomerId());
    }
    
    @Override
    public void updateCustomerLifecycle(Long customerId, String lifecycleStage) {
        // 实际项目中应该更新数据库
        System.out.println("更新客户生命周期: " + customerId + " -> " + lifecycleStage);
    }
}
