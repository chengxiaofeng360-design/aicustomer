package com.aicustomer.service;

import com.aicustomer.entity.Customer;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 导入服务接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
public interface ImportService {
    
    /**
     * 解析Excel文件
     * 
     * @param file Excel文件
     * @return 解析结果
     */
    ImportResult parseExcel(MultipartFile file);
    
    /**
     * 解析Word文件
     * 
     * @param file Word文件
     * @return 解析结果
     */
    ImportResult parseWord(MultipartFile file);
    
    /**
     * 验证导入数据
     * 
     * @param customers 客户数据列表
     * @return 验证结果
     */
    ValidationResult validateCustomers(List<Customer> customers);
    
    /**
     * 批量保存客户数据
     * 
     * @param customers 客户数据列表
     * @return 保存结果
     */
    SaveResult saveCustomers(List<Customer> customers);
    
    /**
     * 导入结果类
     */
    class ImportResult {
        private boolean success;
        private String message;
        private List<Customer> customers;
        private List<String> errors;
        
        public ImportResult(boolean success, String message, List<Customer> customers, List<String> errors) {
            this.success = success;
            this.message = message;
            this.customers = customers;
            this.errors = errors;
        }
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public List<Customer> getCustomers() { return customers; }
        public void setCustomers(List<Customer> customers) { this.customers = customers; }
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
        public int getValidCount() { return customers != null ? customers.size() : 0; }
    }
    
    /**
     * 验证结果类
     */
    class ValidationResult {
        private boolean valid;
        private List<String> errors;
        private List<String> warnings;
        
        public ValidationResult(boolean valid, List<String> errors, List<String> warnings) {
            this.valid = valid;
            this.errors = errors;
            this.warnings = warnings;
        }
        
        // Getters and Setters
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
        public List<String> getWarnings() { return warnings; }
        public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    }
    
    /**
     * 保存结果类
     */
    class SaveResult {
        private boolean success;
        private String message;
        private int successCount;
        private int failureCount;
        private List<String> errors;
        
        public SaveResult(boolean success, String message, int successCount, int failureCount, List<String> errors) {
            this.success = success;
            this.message = message;
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.errors = errors;
        }
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        public int getFailureCount() { return failureCount; }
        public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
    }
}
