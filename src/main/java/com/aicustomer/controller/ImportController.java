package com.aicustomer.controller;

import com.aicustomer.common.Result;
import com.aicustomer.entity.Customer;
import com.aicustomer.service.ImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 导入控制器
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportController {
    
    private final ImportService importService;
    
    /**
     * 上传并解析Excel文件
     */
    @PostMapping("/excel")
    public Result<ImportService.ImportResult> importExcel(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }
            
            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
                return Result.error("请上传Excel文件（.xlsx或.xls格式）");
            }
            
            ImportService.ImportResult result = importService.parseExcel(file);
            return Result.success(result);
            
        } catch (Exception e) {
            return Result.error("文件解析失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传并解析Word文件
     */
    @PostMapping("/word")
    public Result<ImportService.ImportResult> importWord(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }
            
            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.endsWith(".docx") && !fileName.endsWith(".doc"))) {
                return Result.error("请上传Word文件（.docx或.doc格式）");
            }
            
            ImportService.ImportResult result = importService.parseWord(file);
            return Result.success(result);
            
        } catch (Exception e) {
            return Result.error("文件解析失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证导入数据
     */
    @PostMapping("/validate")
    public Result<ImportService.ValidationResult> validateData(@RequestBody List<Customer> customers) {
        try {
            ImportService.ValidationResult result = importService.validateCustomers(customers);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("数据验证失败: " + e.getMessage());
        }
    }
    
    /**
     * 保存导入数据
     */
    @PostMapping("/save")
    public Result<ImportService.SaveResult> saveData(@RequestBody List<Customer> customers) {
        try {
            ImportService.SaveResult result = importService.saveCustomers(customers);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("数据保存失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取导入模板
     */
    @GetMapping("/template")
    public Result<String> getTemplate() {
        String template = "客户名称,联系人,电话,邮箱,地址,邮政编码,传真,机构代码,国籍,申请人性质,代理机构名称,代理机构代码,代理机构地址,代理机构邮编,代理人姓名,代理人电话,代理人传真,代理人手机,代理人邮箱,客户等级,客户状态,备注";
        return Result.success(template);
    }
    
    /**
     * 导入客户数据
     */
    @PostMapping("/customers")
    public Result<Map<String, Object>> importCustomers(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }
            
            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
                return Result.error("请上传Excel文件（.xlsx或.xls格式）");
            }
            
            ImportService.ImportResult result = importService.parseExcel(file);
            
            // 模拟保存数据
            Map<String, Object> response = new HashMap<>();
            response.put("count", result.getValidCount());
            response.put("message", "导入成功");
            
            return Result.success(response);
            
        } catch (Exception e) {
            return Result.error("导入失败: " + e.getMessage());
        }
    }
}
