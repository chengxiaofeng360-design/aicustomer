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
        // 模拟数据，实际项目中应该从数据库查询
        List<Customer> customers = new ArrayList<>();
        
        // 创建模拟数据
        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setCustomerName("ABC科技有限公司");
        customer1.setContactPerson("张伟");
        customer1.setPhone("13812345678");
        customer1.setEmail("zhangwei@abctech.com");
        customer1.setPostalCode("518000");
        customer1.setFax("0755-12345678");
        customer1.setAddress("广东省深圳市南山区科技园");
        customer1.setOrganizationCode("91440300123456789X");
        customer1.setNationality("中国");
        customer1.setApplicantNature(2); // 2:企业
        customer1.setAgencyName("深圳知识产权代理有限公司");
        customer1.setAgencyCode("91440300987654321Y");
        customer1.setAgencyAddress("深圳市福田区知识产权大厦");
        customer1.setAgencyPostalCode("518000");
        customer1.setAgentName("王律师");
        customer1.setAgentPhone("0755-87654321");
        customer1.setAgentFax("0755-87654322");
        customer1.setAgentMobile("13987654321");
        customer1.setAgentEmail("wang@ipagency.com");
        customer1.setCustomerLevel(customerLevel != null ? Integer.parseInt(customerLevel) : 2); // 2:VIP
        customer1.setStatus(status != null ? Integer.parseInt(status) : 1); // 1:正常
        customer1.setRemark("重要客户，需要重点关注");
        customer1.setCreateTime(LocalDateTime.now());
        customers.add(customer1);
        
        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setCustomerName("XYZ咨询公司");
        customer2.setContactPerson("李娜");
        customer2.setPhone("13987654321");
        customer2.setEmail("lina@xyzconsult.com");
        customer2.setPostalCode("100000");
        customer2.setFax("010-12345678");
        customer2.setAddress("北京市朝阳区国贸大厦");
        customer2.setOrganizationCode("91110000123456789X");
        customer2.setNationality("中国");
        customer2.setApplicantNature(2); // 2:企业
        customer2.setAgencyName("北京知识产权代理事务所");
        customer2.setAgencyCode("91110000987654321Y");
        customer2.setAgencyAddress("北京市海淀区中关村大街");
        customer2.setAgencyPostalCode("100000");
        customer2.setAgentName("刘律师");
        customer2.setAgentPhone("010-87654321");
        customer2.setAgentFax("010-87654322");
        customer2.setAgentMobile("13812345678");
        customer2.setAgentEmail("liu@bjip.com");
        customer2.setCustomerLevel(1); // 1:普通
        customer2.setStatus(1); // 1:正常
        customer2.setRemark("咨询类客户");
        customer2.setCreateTime(LocalDateTime.now());
        customers.add(customer2);
        
        // 添加个人客户
        Customer customer3 = new Customer();
        customer3.setId(3L);
        customer3.setCustomerName("张三");
        customer3.setContactPerson("张三");
        customer3.setPhone("13612345678");
        customer3.setEmail("zhangsan@email.com");
        customer3.setPostalCode("200000");
        customer3.setFax("");
        customer3.setAddress("上海市浦东新区陆家嘴");
        customer3.setOrganizationCode("310101199001011234");
        customer3.setNationality("中国");
        customer3.setApplicantNature(1); // 1:个人
        customer3.setAgencyName("");
        customer3.setAgencyCode("");
        customer3.setAgencyAddress("");
        customer3.setAgencyPostalCode("");
        customer3.setAgentName("");
        customer3.setAgentPhone("");
        customer3.setAgentFax("");
        customer3.setAgentMobile("");
        customer3.setAgentEmail("");
        customer3.setCustomerLevel(1); // 1:普通
        customer3.setStatus(1); // 1:正常
        customer3.setRemark("个人发明者");
        customer3.setCreateTime(LocalDateTime.now());
        customers.add(customer3);
        
        // 添加科研院所客户
        Customer customer4 = new Customer();
        customer4.setId(4L);
        customer4.setCustomerName("中科院植物研究所");
        customer4.setContactPerson("李教授");
        customer4.setPhone("010-12345678");
        customer4.setEmail("li@cas.cn");
        customer4.setPostalCode("100000");
        customer4.setFax("010-12345679");
        customer4.setAddress("北京市海淀区中关村南大街");
        customer4.setOrganizationCode("12100000400000000X");
        customer4.setNationality("中国");
        customer4.setApplicantNature(3); // 3:科研院所
        customer4.setAgencyName("中科院知识产权代理中心");
        customer4.setAgencyCode("91110000123456789X");
        customer4.setAgencyAddress("北京市海淀区中关村");
        customer4.setAgencyPostalCode("100000");
        customer4.setAgentName("陈博士");
        customer4.setAgentPhone("010-87654321");
        customer4.setAgentFax("010-87654322");
        customer4.setAgentMobile("13712345678");
        customer4.setAgentEmail("chen@casip.com");
        customer4.setCustomerLevel(3); // 3:钻石
        customer4.setStatus(1); // 1:正常
        customer4.setRemark("国家级科研院所，重点客户");
        customer4.setCreateTime(LocalDateTime.now());
        customers.add(customer4);
        
        PageResult<Customer> result = new PageResult<>();
        result.setList(customers);
        result.setTotal((long) customers.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        
        return result;
    }
    
    @Override
    public Customer getCustomerById(Long id) {
        // 模拟数据
        Customer customer = new Customer();
        customer.setId(id);
        customer.setCustomerName("ABC科技有限公司");
        customer.setContactPerson("张伟");
        customer.setPhone("13812345678");
        customer.setEmail("zhangwei@abctech.com");
        customer.setPostalCode("518000");
        customer.setFax("0755-12345678");
        customer.setAddress("广东省深圳市南山区科技园");
        customer.setOrganizationCode("91440300123456789X");
        customer.setNationality("中国");
        customer.setApplicantNature(2); // 2:企业
        customer.setAgencyName("深圳知识产权代理有限公司");
        customer.setAgencyCode("91440300987654321Y");
        customer.setAgencyAddress("深圳市福田区知识产权大厦");
        customer.setAgencyPostalCode("518000");
        customer.setAgentName("王律师");
        customer.setAgentPhone("0755-87654321");
        customer.setAgentFax("0755-87654322");
        customer.setAgentMobile("13987654321");
        customer.setAgentEmail("wang@ipagency.com");
        customer.setCustomerLevel(2); // 2:VIP
        customer.setStatus(1); // 1:正常
        customer.setRemark("重要客户，需要重点关注");
        customer.setCreateTime(LocalDateTime.now());
        return customer;
    }
    
    @Override
    public void addCustomer(Customer customer) {
        // 实际项目中应该保存到数据库
        System.out.println("添加客户: " + customer.getCustomerName());
    }
    
    @Override
    public void updateCustomer(Customer customer) {
        // 实际项目中应该更新数据库
        System.out.println("更新客户: " + customer.getId());
    }
    
    @Override
    public void deleteCustomer(Long id) {
        // 实际项目中应该从数据库删除
        System.out.println("删除客户: " + id);
    }
    
    @Override
    public void deleteCustomers(List<Long> ids) {
        // 实际项目中应该批量删除
        System.out.println("批量删除客户: " + ids);
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
