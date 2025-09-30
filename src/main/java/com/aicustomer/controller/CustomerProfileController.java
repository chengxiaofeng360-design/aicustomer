package com.aicustomer.controller;

import com.aicustomer.common.PageResult;
import com.aicustomer.common.Result;
import com.aicustomer.entity.Customer;
import com.aicustomer.entity.CustomerDetail;
import com.aicustomer.entity.CustomerTag;
import com.aicustomer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户档案管理控制器
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/customer-profile")
public class CustomerProfileController {

    @Autowired
    private CustomerService customerService;

    /**
     * 获取客户列表（分页）
     */
    @GetMapping("/list")
    public Result<PageResult<Customer>> getCustomerList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String customerLevel,
            @RequestParam(required = false) String status) {
        try {
            PageResult<Customer> result = customerService.getCustomerList(pageNum, pageSize, keyword, customerLevel, status);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取客户列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取客户详情
     */
    @GetMapping("/{id}")
    public Result<Customer> getCustomerById(@PathVariable Long id) {
        try {
            Customer customer = customerService.getCustomerById(id);
            if (customer != null) {
                return Result.success(customer);
            } else {
                return Result.error("客户不存在");
            }
        } catch (Exception e) {
            return Result.error("获取客户详情失败: " + e.getMessage());
        }
    }

    /**
     * 新增客户
     */
    @PostMapping
    public Result<String> addCustomer(@RequestBody Customer customer) {
        try {
            customerService.addCustomer(customer);
            return Result.success("客户添加成功");
        } catch (Exception e) {
            return Result.error("客户添加失败: " + e.getMessage());
        }
    }

    /**
     * 更新客户信息
     */
    @PutMapping("/{id}")
    public Result<String> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        try {
            customer.setId(id);
            customerService.updateCustomer(customer);
            return Result.success("客户信息更新成功");
        } catch (Exception e) {
            return Result.error("客户信息更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除客户
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteCustomer(@PathVariable Long id) {
        try {
            customerService.deleteCustomer(id);
            return Result.success("客户删除成功");
        } catch (Exception e) {
            return Result.error("客户删除失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除客户
     */
    @DeleteMapping("/batch")
    public Result<String> deleteCustomers(@RequestBody List<Long> ids) {
        try {
            customerService.deleteCustomers(ids);
            return Result.success("批量删除成功");
        } catch (Exception e) {
            return Result.error("批量删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取客户标签列表
     */
    @GetMapping("/{customerId}/tags")
    public Result<List<CustomerTag>> getCustomerTags(@PathVariable Long customerId) {
        try {
            List<CustomerTag> tags = customerService.getCustomerTags(customerId);
            return Result.success(tags);
        } catch (Exception e) {
            return Result.error("获取客户标签失败: " + e.getMessage());
        }
    }

    /**
     * 添加客户标签
     */
    @PostMapping("/{customerId}/tags")
    public Result<String> addCustomerTag(@PathVariable Long customerId, @RequestBody CustomerTag tag) {
        try {
            tag.setCustomerId(customerId);
            customerService.addCustomerTag(tag);
            return Result.success("标签添加成功");
        } catch (Exception e) {
            return Result.error("标签添加失败: " + e.getMessage());
        }
    }

    /**
     * 删除客户标签
     */
    @DeleteMapping("/{customerId}/tags/{tagId}")
    public Result<String> deleteCustomerTag(@PathVariable Long customerId, @PathVariable Long tagId) {
        try {
            customerService.deleteCustomerTag(tagId);
            return Result.success("标签删除成功");
        } catch (Exception e) {
            return Result.error("标签删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取客户详细信息
     */
    @GetMapping("/{customerId}/detail")
    public Result<CustomerDetail> getCustomerDetail(@PathVariable Long customerId) {
        try {
            CustomerDetail detail = customerService.getCustomerDetail(customerId);
            return Result.success(detail);
        } catch (Exception e) {
            return Result.error("获取客户详细信息失败: " + e.getMessage());
        }
    }

    /**
     * 更新客户详细信息
     */
    @PutMapping("/{customerId}/detail")
    public Result<String> updateCustomerDetail(@PathVariable Long customerId, @RequestBody CustomerDetail detail) {
        try {
            detail.setCustomerId(customerId);
            customerService.updateCustomerDetail(detail);
            return Result.success("客户详细信息更新成功");
        } catch (Exception e) {
            return Result.error("客户详细信息更新失败: " + e.getMessage());
        }
    }

    /**
     * 客户价值分析
     */
    @GetMapping("/{customerId}/value-analysis")
    public Result<Object> getCustomerValueAnalysis(@PathVariable Long customerId) {
        try {
            // 这里可以添加价值分析逻辑
            return Result.success("客户价值分析功能开发中");
        } catch (Exception e) {
            return Result.error("获取客户价值分析失败: " + e.getMessage());
        }
    }

    /**
     * 客户生命周期管理
     */
    @PutMapping("/{customerId}/lifecycle")
    public Result<String> updateCustomerLifecycle(@PathVariable Long customerId, @RequestParam String lifecycleStage) {
        try {
            customerService.updateCustomerLifecycle(customerId, lifecycleStage);
            return Result.success("客户生命周期更新成功");
        } catch (Exception e) {
            return Result.error("客户生命周期更新失败: " + e.getMessage());
        }
    }
}




