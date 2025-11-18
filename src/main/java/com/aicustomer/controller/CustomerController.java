package com.aicustomer.controller;

import com.aicustomer.common.PageResult;
import com.aicustomer.common.Result;
import com.aicustomer.entity.Customer;
import com.aicustomer.service.CustomerService;
import com.aicustomer.service.ExportService;
import com.aicustomer.service.ImportService;
import com.aicustomer.service.SensitiveDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {
    
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
            
            String fileName = "客户档案批量导入模板_" + LocalDate.now() + ".xlsx";
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
            log.error("下载模版失败", e);
            HttpHeaders errorHeaders = new HttpHeaders();
            errorHeaders.setContentType(MediaType.APPLICATION_JSON);
            return ResponseEntity.status(500)
                .headers(errorHeaders)
                .body(("{\"error\":\"下载模版失败: " + e.getMessage().replace("\"", "\\\"") + "\"}").getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
    }
    
    /**
     * 导出客户数据
     * 注意：此路由必须在 /{id} 之前，避免路由冲突
     */
    @GetMapping(value = "/export", produces = {"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "text/csv;charset=UTF-8", MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<byte[]> exportCustomers(
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
            // 使用UTF-8编码文件名，兼容不同浏览器
            String encodedFileName = java.net.URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
            // 同时设置标准格式和UTF-8格式，确保兼容性
            headers.add("Content-Disposition", "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''" + encodedFileName);
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(data);
                
        } catch (Exception e) {
            log.error("导出客户数据失败，格式: {}", format, e);
            HttpHeaders errorHeaders = new HttpHeaders();
            errorHeaders.setContentType(MediaType.APPLICATION_JSON);
            return ResponseEntity.status(500)
                .headers(errorHeaders)
                .body(("{\"error\":\"导出失败: " + e.getMessage() + "\"}").getBytes());
        }
    }
    
    /**
     * 导入客户数据（支持Excel和CSV）
     */
    @PostMapping("/import")
    public Result<Map<String, Object>> importCustomers(@RequestParam("file") MultipartFile file) {
        String fileName = null;
        try {
            log.info("开始导入客户数据，文件名: {}", file.getOriginalFilename());
            
            if (file.isEmpty()) {
                log.warn("导入失败：文件为空");
                return Result.error("请选择要上传的文件");
            }
            
            fileName = file.getOriginalFilename();
            if (fileName == null) {
                log.warn("导入失败：文件名为空");
                return Result.error("文件名不能为空");
            }
            
            log.info("解析文件: {}, 文件大小: {} bytes", fileName, file.getSize());
            ImportService.ImportResult parseResult;
            
            // 根据文件扩展名选择解析方式
            if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                log.info("使用Excel解析方式");
                parseResult = importService.parseExcel(file);
            } else if (fileName.endsWith(".csv")) {
                log.info("使用CSV解析方式");
                parseResult = importService.parseCSV(file);
            } else {
                log.warn("不支持的文件格式: {}", fileName);
                return Result.error("不支持的文件格式，请上传Excel（.xlsx, .xls）或CSV（.csv）文件");
            }
            
            if (!parseResult.isSuccess()) {
                log.error("文件解析失败: {}, 错误信息: {}", fileName, parseResult.getMessage());
                if (parseResult.getErrors() != null && !parseResult.getErrors().isEmpty()) {
                    log.error("解析错误详情: {}", String.join("; ", parseResult.getErrors()));
                }
                return Result.error(parseResult.getMessage());
            }
            
            log.info("文件解析成功，共解析 {} 条数据", parseResult.getCustomers() != null ? parseResult.getCustomers().size() : 0);
            
            // 验证数据
            ImportService.ValidationResult validationResult = importService.validateCustomers(parseResult.getCustomers());
            if (!validationResult.isValid()) {
                String errorMsg = "数据验证失败（必填项：客户名称、电话）: " + String.join("; ", validationResult.getErrors().subList(0, Math.min(5, validationResult.getErrors().size())));
                if (validationResult.getErrors().size() > 5) {
                    errorMsg += "...还有" + (validationResult.getErrors().size() - 5) + "条错误";
                }
                log.error("数据验证失败: {}, 错误数量: {}", fileName, validationResult.getErrors().size());
                log.error("验证错误详情: {}", String.join("; ", validationResult.getErrors()));
                return Result.error(errorMsg);
            }
            
            log.info("数据验证通过，开始保存数据");
            // 保存数据
            ImportService.SaveResult saveResult = importService.saveCustomers(parseResult.getCustomers());
            
            log.info("数据保存完成: 成功 {} 条, 失败 {} 条", saveResult.getSuccessCount(), saveResult.getFailureCount());
            if (saveResult.getErrors() != null && !saveResult.getErrors().isEmpty()) {
                log.error("保存错误详情: {}", String.join("; ", saveResult.getErrors()));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("successCount", saveResult.getSuccessCount());
            response.put("failureCount", saveResult.getFailureCount());
            response.put("message", saveResult.getMessage());
            if (saveResult.getErrors() != null && !saveResult.getErrors().isEmpty()) {
                response.put("errors", saveResult.getErrors());
            }
            
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("导入客户数据异常，文件名: {}", fileName, e);
            log.error("异常堆栈信息:", e);
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
                                           @RequestParam(required = false) String customerName,
                                           @RequestParam(required = false) String customerType,
                                           @RequestParam(required = false) String customerLevel,
                                           @RequestParam(required = false) String region,
                                           @RequestParam(required = false) List<String> businessType) {
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
            
            // 处理业务类型参数（支持多个businessType，用于IN查询）
            List<Integer> businessTypeList = null;
            if (businessType != null && !businessType.isEmpty()) {
                businessTypeList = new java.util.ArrayList<>();
                for (String typeStr : businessType) {
                    try {
                        businessTypeList.add(Integer.parseInt(typeStr));
                    } catch (NumberFormatException e) {
                        log.warn("无效的业务类型值: {}", typeStr);
                    }
                }
                if (businessTypeList.isEmpty()) {
                    businessTypeList = null;
                } else {
                    log.info("设置业务类型筛选条件: businessTypeList={}", businessTypeList);
                }
            }
            
            log.info("查询客户列表 - 页码: {}, 每页大小: {}, 查询条件: customerName={}, customerType={}, customerLevel={}, region={}, businessTypeList={}", 
                    pageNum, pageSize, customerName, customerType, customerLevel, region, businessTypeList);
            
            PageResult<Customer> pageResult = customerService.page(pageNum, pageSize, queryCustomer, businessTypeList);
            log.info("查询结果 - 总数: {}, 当前页数据量: {}", pageResult.getTotal(), pageResult.getList().size());
            
            // 如果查询结果为空，记录更详细的信息用于调试
            if (pageResult.getTotal() == 0) {
                log.warn("⚠️ 查询结果为空！查询条件: businessTypeList={}, 请检查数据库中是否有匹配的记录", businessTypeList);
            }
            // 对敏感数据进行脱敏处理
            List<Customer> maskedList = pageResult.getList().stream()
                    .map(sensitiveDataService::maskSensitiveData)
                    .collect(java.util.stream.Collectors.toList());
            pageResult.setList(maskedList);
            return Result.success(pageResult);
        } catch (Exception e) {
            // 如果数据库连接失败或其他异常，返回空结果而不是抛出异常
            log.error("操作异常", e);
            PageResult<Customer> emptyResult = new PageResult<>(pageNum, pageSize, 0L, new java.util.ArrayList<>());
            return Result.success(emptyResult);
        }
    }
    
    /**
     * 获取客户统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(@RequestParam(required = false) List<String> businessType) {
        try {
            // 处理业务类型参数（支持多个businessType，用于统计筛选）
            List<Integer> businessTypeList = null;
            if (businessType != null && !businessType.isEmpty()) {
                businessTypeList = new java.util.ArrayList<>();
                for (String typeStr : businessType) {
                    try {
                        businessTypeList.add(Integer.parseInt(typeStr));
                    } catch (NumberFormatException e) {
                        log.warn("无效的业务类型值: {}", typeStr);
                    }
                }
                if (businessTypeList.isEmpty()) {
                    businessTypeList = null;
                } else {
                    log.info("统计业务类型筛选条件: businessTypeList={}", businessTypeList);
                }
            }
            
            Map<String, Object> statistics = customerService.getCustomerStatistics(businessTypeList);
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("操作异常", e);
            return Result.error("获取统计数据失败: " + e.getMessage());
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
            
            log.info("保存客户数据: 名称={}, 类型={}, 编号={}", customer.getCustomerName(), customer.getCustomerType(), customer.getCustomerCode());
        
        boolean success = customerService.save(customer);
        if (success) {
            return Result.success("客户保存成功");
        }
        return Result.error("客户保存失败");
        } catch (Exception e) {
            log.error("保存客户异常: {}", e.getMessage(), e);
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
            log.error("操作异常", e);
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
            log.error("操作异常", e);
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
