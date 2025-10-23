package com.aicustomer.controller;

import com.aicustomer.common.PageResult;
import com.aicustomer.common.Result;
import com.aicustomer.entity.Customer;
import com.aicustomer.service.CustomerService;
import com.aicustomer.service.SensitiveDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户控制器
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {
    
    private final CustomerService customerService;
    private final SensitiveDataService sensitiveDataService;
    
    /**
     * 根据ID查询客户
     */
    @GetMapping("/{id}")
    public Result<Customer> getById(@PathVariable Long id) {
        Customer customer = customerService.getById(id);
        if (customer != null) {
            // 对敏感数据进行脱敏处理
            Customer maskedCustomer = sensitiveDataService.maskSensitiveData(customer);
            return Result.success(maskedCustomer);
        }
        return Result.error("客户不存在");
    }
    
    /**
     * 根据客户编号查询客户
     */
    @GetMapping("/code/{customerCode}")
    public Result<Customer> getByCustomerCode(@PathVariable String customerCode) {
        Customer customer = customerService.getByCustomerCode(customerCode);
        if (customer != null) {
            return Result.success(customer);
        }
        return Result.error("客户不存在");
    }
    
    /**
     * 查询客户列表
     */
    @GetMapping("/list")
    public Result<List<Customer>> list(Customer customer) {
        List<Customer> list = customerService.list(customer);
        // 对敏感数据进行脱敏处理
        List<Customer> maskedList = list.stream()
                .map(sensitiveDataService::maskSensitiveData)
                .collect(java.util.stream.Collectors.toList());
        return Result.success(maskedList);
    }
    
    /**
     * 分页查询客户
     */
    @GetMapping("/page")
    public Result<PageResult<Customer>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                           Customer customer) {
        PageResult<Customer> pageResult = customerService.page(pageNum, pageSize, customer);
        // 对敏感数据进行脱敏处理
        List<Customer> maskedList = pageResult.getList().stream()
                .map(sensitiveDataService::maskSensitiveData)
                .collect(java.util.stream.Collectors.toList());
        pageResult.setList(maskedList);
        return Result.success(pageResult);
    }
    
    /**
     * 保存客户
     */
    @PostMapping
    public Result<String> save(@RequestBody Customer customer) {
        // 检查客户编号是否已存在
        if (customerService.existsByCustomerCode(customer.getCustomerCode())) {
            return Result.error("客户编号已存在");
        }
        
        boolean success = customerService.save(customer);
        if (success) {
            return Result.success("客户保存成功");
        }
        return Result.error("客户保存失败");
    }
    
    /**
     * 更新客户
     */
    @PutMapping
    public Result<String> update(@RequestBody Customer customer) {
        boolean success = customerService.update(customer);
        if (success) {
            return Result.success("客户更新成功");
        }
        return Result.error("客户更新失败");
    }
    
    /**
     * 根据ID删除客户
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteById(@PathVariable Long id) {
        boolean success = customerService.deleteById(id);
        if (success) {
            return Result.success("客户删除成功");
        }
        return Result.error("客户删除失败");
    }
    
    /**
     * 批量删除客户
     */
    @DeleteMapping("/batch")
    public Result<String> deleteByIds(@RequestBody List<Long> ids) {
        boolean success = customerService.deleteByIds(ids);
        if (success) {
            return Result.success("客户批量删除成功");
        }
        return Result.error("客户批量删除失败");
    }
    
    /**
     * 验证敏感数据保护密码
     */
    @PostMapping("/{id}/verify-password")
    public Result<Boolean> verifyPassword(@PathVariable Long id, @RequestBody String password) {
        try {
            boolean isValid = sensitiveDataService.verifyProtectionPassword(id, password);
            return Result.success(isValid);
        } catch (Exception e) {
            return Result.error("密码验证失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取完整的敏感客户数据（需要密码验证）
     */
    @PostMapping("/{id}/full")
    public Result<Customer> getByIdWithPassword(@PathVariable Long id, @RequestBody String password) {
        try {
            boolean isValid = sensitiveDataService.verifyProtectionPassword(id, password);
            if (isValid) {
                Customer customer = customerService.getById(id);
                return Result.success(customer);
            } else {
                return Result.error("密码错误");
            }
        } catch (Exception e) {
            return Result.error("获取数据失败: " + e.getMessage());
        }
    }
}
