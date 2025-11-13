package com.aicustomer.controller;

import com.aicustomer.common.PageResult;
import com.aicustomer.common.Result;
import com.aicustomer.entity.Customer;
import com.aicustomer.service.CustomerService;
import com.aicustomer.service.ExportService;
import com.aicustomer.service.ImportService;
import com.aicustomer.service.SensitiveDataService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);
    
    private final CustomerService customerService;
    private final SensitiveDataService sensitiveDataService;
    private final ExportService exportService;
    private final ImportService importService;
    
    /**
     * 下载Excel导入模版
     */
    @GetMapping(value = "/template", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadTemplate() {
        try {
            ByteArrayOutputStream outputStream = exportService.generateCustomerTemplate();
            byte[] data = outputStream.toByteArray();
            
            String fileName = "客户导入模版_" + LocalDate.now() + ".xlsx";
            // 使用UTF-8编码文件名，兼容不同浏览器
            String encodedFileName = java.net.URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            // 同时设置标准格式和UTF-8格式，确保兼容性
            headers.add("Content-Disposition", "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''" + encodedFileName);
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(data);
                
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("下载模版失败: " + e.getMessage());
            HttpHeaders errorHeaders = new HttpHeaders();
            errorHeaders.setContentType(MediaType.APPLICATION_JSON);
            return ResponseEntity.status(500)
                .headers(errorHeaders)
                .body(("{\"error\":\"下载模版失败: " + e.getMessage().replace("\"", "\\\"") + "\"}").getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
    }
    
    /**
     * 导入客户数据（支持Excel和CSV）
     */
    @PostMapping("/import")
    public Result<Map<String, Object>> importCustomers(@RequestParam("file") MultipartFile file) {
        log.info("[IMPORT] [后端] 收到文件上传请求, fileName={}, size={} bytes, contentType={}", 
                file.getOriginalFilename(), file.getSize(), file.getContentType());
        try {
            if (file.isEmpty()) {
                log.warn("[IMPORT] [后端] 文件为空");
                return Result.error("请选择要上传的文件");
            }
            
            String fileName = file.getOriginalFilename();
            if (fileName == null) {
                log.warn("[IMPORT] [后端] 文件名为空");
                return Result.error("文件名不能为空");
            }
            
            log.info("[IMPORT] [后端] 开始解析文件, fileName={}", fileName);
            ImportService.ImportResult parseResult;
            
            // 根据文件扩展名选择解析方式
            if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                log.info("[IMPORT] [后端] 使用Excel解析器");
                parseResult = importService.parseExcel(file);
            } else if (fileName.endsWith(".csv")) {
                log.info("[IMPORT] [后端] 使用CSV解析器");
                parseResult = importService.parseCSV(file);
            } else {
                log.warn("[IMPORT] [后端] 不支持的文件格式, fileName={}", fileName);
                return Result.error("不支持的文件格式，请上传Excel（.xlsx, .xls）或CSV（.csv）文件");
            }
            
            if (!parseResult.isSuccess()) {
                log.error("[IMPORT] [后端] 文件解析失败, message={}", parseResult.getMessage());
                return Result.error(parseResult.getMessage());
            }
            
            log.info("[IMPORT] [后端] 文件解析成功, 解析到{}条数据", 
                    parseResult.getCustomers() != null ? parseResult.getCustomers().size() : 0);
            
            // 验证数据
            ImportService.ValidationResult validationResult = importService.validateCustomers(parseResult.getCustomers());
            if (!validationResult.isValid()) {
                log.warn("[IMPORT] [后端] 数据验证失败, errorCount={}", validationResult.getErrors().size());
                String errorMsg = "数据验证失败: " + String.join("; ", validationResult.getErrors().subList(0, Math.min(5, validationResult.getErrors().size())));
                if (validationResult.getErrors().size() > 5) {
                    errorMsg += "...还有" + (validationResult.getErrors().size() - 5) + "条错误";
                }
                return Result.error(errorMsg);
            }
            
            log.info("[IMPORT] [后端] 数据验证通过, 开始保存数据");
            // 保存数据
            ImportService.SaveResult saveResult = importService.saveCustomers(parseResult.getCustomers());
            
            log.info("[IMPORT] [后端] 数据保存完成, successCount={}, failureCount={}", 
                    saveResult.getSuccessCount(), saveResult.getFailureCount());
            
            Map<String, Object> response = new HashMap<>();
            response.put("successCount", saveResult.getSuccessCount());
            response.put("failureCount", saveResult.getFailureCount());
            response.put("message", saveResult.getMessage());
            if (saveResult.getErrors() != null && !saveResult.getErrors().isEmpty()) {
                response.put("errors", saveResult.getErrors());
            }
            
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("[IMPORT] [后端] 导入失败, error={}", e.getMessage(), e);
            e.printStackTrace();
            return Result.error("导入失败: " + e.getMessage());
        }
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
        try {
        PageResult<Customer> pageResult = customerService.page(pageNum, pageSize, customer);
        // 对敏感数据进行脱敏处理
        List<Customer> maskedList = pageResult.getList().stream()
                .map(sensitiveDataService::maskSensitiveData)
                .collect(java.util.stream.Collectors.toList());
        pageResult.setList(maskedList);
        return Result.success(pageResult);
        } catch (Exception e) {
            // 如果数据库连接失败或其他异常，返回空结果而不是抛出异常
            e.printStackTrace();
            PageResult<Customer> emptyResult = new PageResult<>(pageNum, pageSize, 0L, new java.util.ArrayList<>());
            return Result.success(emptyResult);
        }
    }
    
    /**
     * 获取客户统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        try {
            Map<String, Object> statistics = customerService.getCustomerStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取统计数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 导出客户数据
     * 注意：此路由必须在 /{id} 路由之前，避免路径冲突
     * 使用明确的路径匹配，确保不会被 /{id} 路由拦截
     */
    @GetMapping(value = "/export", produces = {
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            MediaType.APPLICATION_OCTET_STREAM_VALUE,
            "text/csv"
    })
    public ResponseEntity<byte[]> exportCustomersForList(
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String customerType,
            @RequestParam(required = false) String customerLevel,
            @RequestParam(required = false) String region,
            @RequestParam(defaultValue = "excel") String format) {
        try {
            // 构建查询条件
            Customer queryCustomer = new Customer();
            if (customerName != null && !customerName.trim().isEmpty()) {
                queryCustomer.setCustomerName(customerName);
            }
            if (customerType != null && !customerType.trim().isEmpty()) {
                try {
                    queryCustomer.setCustomerType(Integer.parseInt(customerType));
                } catch (NumberFormatException e) {
                    // 忽略无效的类型值
                }
            }
            if (customerLevel != null && !customerLevel.trim().isEmpty()) {
                try {
                    queryCustomer.setCustomerLevel(Integer.parseInt(customerLevel));
                } catch (NumberFormatException e) {
                    // 忽略无效的等级值
                }
            }
            if (region != null && !region.trim().isEmpty()) {
                queryCustomer.setRegion(region);
            }
            
            ByteArrayOutputStream outputStream;
            String fileName;
            String contentType;
            
            if ("csv".equalsIgnoreCase(format)) {
                outputStream = exportService.exportCustomersToCSV(queryCustomer);
                fileName = "客户数据_" + LocalDate.now() + ".csv";
                contentType = "text/csv;charset=UTF-8";
            } else {
                outputStream = exportService.exportCustomersToExcel(queryCustomer);
                fileName = "客户数据_" + LocalDate.now() + ".xlsx";
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            }
            
            byte[] data = outputStream.toByteArray();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentDispositionFormData("attachment", fileName);
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(data);
                
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("导出客户数据失败: " + e.getMessage());
            
            // 返回JSON格式的错误响应
            HttpHeaders errorHeaders = new HttpHeaders();
            errorHeaders.setContentType(MediaType.APPLICATION_JSON);
            String errorMessage = e.getMessage() != null ? e.getMessage().replace("\"", "\\\"") : "未知错误";
            String errorJson = "{\"error\":\"导出失败: " + errorMessage + "\"}";
            return ResponseEntity.status(500)
                .headers(errorHeaders)
                .body(errorJson.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
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
     * 根据ID查询客户
     * 注意：此路由必须在所有具体路径路由之后，避免路径冲突
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
     * 保存客户
     */
    @PostMapping
    public Result<String> save(@RequestBody Customer customer) {
        try {
            // 验证必填字段
            if (customer.getCustomerName() == null || customer.getCustomerName().trim().isEmpty()) {
                return Result.error("客户姓名/企业名称不能为空");
            }
            
            // 确保客户编号存在
            if (customer.getCustomerCode() == null || customer.getCustomerCode().trim().isEmpty()) {
                customer.setCustomerCode("CUST" + System.currentTimeMillis() + (int)(Math.random() * 1000));
            }
            
        // 检查客户编号是否已存在
        if (customerService.existsByCustomerCode(customer.getCustomerCode())) {
            return Result.error("客户编号已存在");
        }
            
            // 设置默认值
            if (customer.getCustomerType() == null) {
                customer.setCustomerType(1); // 默认个人客户
            }
            if (customer.getStatus() == null) {
                customer.setStatus(1); // 默认正常
            }
            if (customer.getSource() == null) {
                customer.setSource(2); // 默认线下
            }
            if (customer.getCustomerLevel() == null) {
                customer.setCustomerLevel(1); // 默认普通
            }
            
            System.out.println("保存客户数据: " + customer.getCustomerName() + ", 类型: " + customer.getCustomerType() + ", 编号: " + customer.getCustomerCode());
        
        boolean success = customerService.save(customer);
        if (success) {
            return Result.success("客户保存成功");
        }
        return Result.error("客户保存失败");
        } catch (Exception e) {
            System.err.println("保存客户异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error("客户保存失败: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
        }
    }
    
    /**
     * 更新客户
     */
    @PutMapping
    public Result<String> update(@RequestBody Customer customer) {
        try {
            // 检查客户是否存在
            if (customer.getId() == null) {
                return Result.error("客户ID不能为空");
            }
            
            Customer existingCustomer = customerService.getById(customer.getId());
            if (existingCustomer == null) {
                return Result.error("客户不存在");
            }
            
            // 如果修改了客户编号，检查新编号是否已被其他客户使用
            if (customer.getCustomerCode() != null && 
                !customer.getCustomerCode().equals(existingCustomer.getCustomerCode())) {
                if (customerService.existsByCustomerCode(customer.getCustomerCode())) {
                    return Result.error("客户编号已被使用");
                }
            }
            
        boolean success = customerService.update(customer);
        if (success) {
            return Result.success("客户更新成功");
        }
        return Result.error("客户更新失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("客户更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID删除客户
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteById(@PathVariable Long id) {
        try {
            // 检查客户是否存在
            Customer customer = customerService.getById(id);
            if (customer == null) {
                return Result.error("客户不存在");
            }
            
        boolean success = customerService.deleteById(id);
        if (success) {
            return Result.success("客户删除成功");
        }
        return Result.error("客户删除失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("客户删除失败: " + e.getMessage());
        }
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
