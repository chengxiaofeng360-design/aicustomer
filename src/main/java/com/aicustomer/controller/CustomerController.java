package com.aicustomer.controller;

import com.aicustomer.common.PageResult;
import com.aicustomer.common.Result;
import com.aicustomer.entity.Customer;
import com.aicustomer.service.CustomerService;
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
    
    /**
     * 根据ID查询客户
     */
    @GetMapping("/{id}")
    public Result<Customer> getById(@PathVariable Long id) {
        Customer customer = customerService.getById(id);
        if (customer != null) {
            return Result.success(customer);
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
        return Result.success(list);
    }
    
    /**
     * 分页查询客户
     */
    @GetMapping("/page")
    public Result<PageResult<Customer>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                           Customer customer) {
        PageResult<Customer> pageResult = customerService.page(pageNum, pageSize, customer);
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
}
