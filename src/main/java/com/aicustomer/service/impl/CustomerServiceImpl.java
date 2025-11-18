package com.aicustomer.service.impl;

import com.aicustomer.common.PageResult;
import com.aicustomer.entity.Customer;
import com.aicustomer.entity.CustomerDetail;
import com.aicustomer.entity.CustomerTag;
import com.aicustomer.mapper.CustomerMapper;
import com.aicustomer.mapper.CustomerDetailMapper;
import com.aicustomer.mapper.CustomerTagMapper;
import com.aicustomer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final CustomerTagMapper customerTagMapper;
    private final CustomerDetailMapper customerDetailMapper;
    
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
    public PageResult<Customer> page(Integer pageNum, Integer pageSize, Customer customer, List<Integer> businessTypeList) {
        int offset = (pageNum - 1) * pageSize;
        List<Customer> list = customerMapper.selectPage(customer, businessTypeList, offset, pageSize);
        Long total = customerMapper.selectCount(customer, businessTypeList);
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
        
        // 设置默认业务类型（第一种类型：品种权申请客户）
        if (customer.getBusinessType() == null) {
            customer.setBusinessType(1);
        }
        
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
        return page(pageNum, pageSize, queryCustomer, null);
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
        // 从数据库查询客户标签
        return customerTagMapper.selectByCustomerId(customerId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCustomerTag(CustomerTag tag) {
        // 设置创建时间和默认值
        if (tag.getCreateTime() == null) {
            tag.setCreateTime(LocalDateTime.now());
        }
        if (tag.getUpdateTime() == null) {
            tag.setUpdateTime(LocalDateTime.now());
        }
        if (tag.getDeleted() == null) {
            tag.setDeleted(0);
        }
        if (tag.getVersion() == null) {
            tag.setVersion(1);
        }
        if (tag.getTagType() == null) {
            tag.setTagType(2); // 默认自定义标签
        }
        if (tag.getIsVisible() == null) {
            tag.setIsVisible(1); // 默认显示
        }
        if (tag.getWeight() == null) {
            tag.setWeight(0); // 默认权重
        }
        
        customerTagMapper.insert(tag);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCustomerTag(Long tagId) {
        // 逻辑删除客户标签
        customerTagMapper.deleteById(tagId);
    }
    
    @Override
    public CustomerDetail getCustomerDetail(Long customerId) {
        // 从数据库查询客户详细信息
        CustomerDetail detail = customerDetailMapper.selectByCustomerId(customerId);
        // 如果不存在，返回null而不是创建新对象
        return detail;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCustomerDetail(CustomerDetail detail) {
        // 检查是否存在
        CustomerDetail existing = customerDetailMapper.selectByCustomerId(detail.getCustomerId());
        if (existing == null) {
            // 不存在则插入
            if (detail.getCreateTime() == null) {
                detail.setCreateTime(LocalDateTime.now());
            }
            if (detail.getUpdateTime() == null) {
                detail.setUpdateTime(LocalDateTime.now());
            }
            if (detail.getDeleted() == null) {
                detail.setDeleted(0);
            }
            if (detail.getVersion() == null) {
                detail.setVersion(1);
            }
            customerDetailMapper.insert(detail);
        } else {
            // 存在则更新
            detail.setUpdateTime(LocalDateTime.now());
            detail.setVersion(existing.getVersion()); // 使用现有版本号，更新时会自动+1
            customerDetailMapper.updateByCustomerId(detail);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCustomerLifecycle(Long customerId, String lifecycleStage) {
        // 解析生命周期阶段字符串为数字
        Integer stage = null;
        if (lifecycleStage != null) {
            switch (lifecycleStage.toLowerCase()) {
                case "潜在":
                case "1":
                    stage = 1;
                    break;
                case "接触":
                case "2":
                    stage = 2;
                    break;
                case "合作":
                case "3":
                    stage = 3;
                    break;
                case "维护":
                case "4":
                    stage = 4;
                    break;
                case "流失":
                case "5":
                    stage = 5;
                    break;
                default:
                    try {
                        stage = Integer.parseInt(lifecycleStage);
                    } catch (NumberFormatException e) {
                        // 忽略无效值
                    }
            }
        }
        
        if (stage != null) {
            // 获取或创建客户详细信息
            CustomerDetail detail = customerDetailMapper.selectByCustomerId(customerId);
            if (detail == null) {
                detail = new CustomerDetail();
                detail.setCustomerId(customerId);
                detail.setCreateTime(LocalDateTime.now());
                detail.setDeleted(0);
                detail.setVersion(1);
            }
            detail.setLifecycleStage(stage);
            detail.setUpdateTime(LocalDateTime.now());
            
            if (detail.getId() == null) {
                customerDetailMapper.insert(detail);
            } else {
                detail.setVersion(detail.getVersion()); // 保持版本号，更新时会自动+1
                customerDetailMapper.updateByCustomerId(detail);
            }
        }
    }
    
    @Override
    public java.util.Map<String, Object> getCustomerStatistics() {
        return getCustomerStatistics(null);
    }
    
    @Override
    public java.util.Map<String, Object> getCustomerStatistics(List<Integer> businessTypeList) {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        
        // 客户总数
        Customer emptyQuery = new Customer();
        Long totalCustomers = customerMapper.selectCount(emptyQuery, businessTypeList);
        stats.put("totalCustomers", totalCustomers != null ? totalCustomers : 0L);
        
        // 按客户类型统计
        Customer personalQuery = new Customer();
        personalQuery.setCustomerType(1); // 个人
        Long personalCount = customerMapper.selectCount(personalQuery, businessTypeList);
        stats.put("personalCount", personalCount != null ? personalCount : 0L);
        
        Customer enterpriseQuery = new Customer();
        enterpriseQuery.setCustomerType(2); // 企业
        Long enterpriseCount = customerMapper.selectCount(enterpriseQuery, businessTypeList);
        stats.put("enterpriseCount", enterpriseCount != null ? enterpriseCount : 0L);
        
        Customer researchQuery = new Customer();
        researchQuery.setCustomerType(3); // 科研院所
        Long researchCount = customerMapper.selectCount(researchQuery, businessTypeList);
        stats.put("researchCount", researchCount != null ? researchCount : 0L);
        
        // 按客户等级统计
        Customer normalQuery = new Customer();
        normalQuery.setCustomerLevel(1); // 普通
        Long normalCount = customerMapper.selectCount(normalQuery, businessTypeList);
        stats.put("normalCount", normalCount != null ? normalCount : 0L);
        
        Customer vipQuery = new Customer();
        vipQuery.setCustomerLevel(2); // VIP
        Long vipCount = customerMapper.selectCount(vipQuery, businessTypeList);
        stats.put("vipCount", vipCount != null ? vipCount : 0L);
        
        Customer diamondQuery = new Customer();
        diamondQuery.setCustomerLevel(3); // 钻石
        Long diamondCount = customerMapper.selectCount(diamondQuery, businessTypeList);
        stats.put("diamondCount", diamondCount != null ? diamondCount : 0L);
        
        // 本月新增客户数（需要查询本月创建的客户）
        // 由于selectCount不支持日期范围，我们需要使用selectList然后过滤
        // 或者添加新的查询方法，这里先用简单方式：查询所有客户，然后在Service层过滤
        List<Customer> allCustomers = customerMapper.selectList(emptyQuery);
        // 如果指定了业务类型，需要过滤
        if (businessTypeList != null && !businessTypeList.isEmpty()) {
            allCustomers = allCustomers.stream()
                .filter(c -> {
                    Integer businessType = c.getBusinessType() != null ? c.getBusinessType() : 1;
                    return businessTypeList.contains(businessType);
                })
                .collect(java.util.stream.Collectors.toList());
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        long newThisMonth = allCustomers.stream()
            .filter(c -> c.getCreateTime() != null && c.getCreateTime().isAfter(startOfMonth))
            .count();
        stats.put("newThisMonth", newThisMonth);
        
        // 潜在客户数（status=0或特定条件）
        Customer potentialQuery = new Customer();
        potentialQuery.setStatus(0); // 假设0表示潜在客户
        Long potentialCount = customerMapper.selectCount(potentialQuery, businessTypeList);
        stats.put("potentialCount", potentialCount != null ? potentialCount : 0L);
        
        return stats;
    }
}
