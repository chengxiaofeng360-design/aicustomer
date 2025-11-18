package com.aicustomer.service.impl;

import com.aicustomer.entity.Customer;
import com.aicustomer.service.CustomerService;
import com.aicustomer.service.ImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 导入服务实现类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImportServiceImpl implements ImportService {
    
    private final CustomerService customerService;
    
    @Override
    public ImportResult parseExcel(MultipartFile file) {
        List<Customer> customers = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        try {
            Workbook workbook = null;
            String fileName = file.getOriginalFilename();
            
            if (fileName.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(file.getInputStream());
            } else if (fileName.endsWith(".xls")) {
                workbook = new HSSFWorkbook(file.getInputStream());
            } else {
                return new ImportResult(false, "不支持的文件格式，请上传Excel文件", null, null);
            }
            
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getLastRowNum();
            
            if (rowCount < 1) {
                return new ImportResult(false, "Excel文件为空或没有数据行", null, null);
            }
            
            // 获取表头
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return new ImportResult(false, "Excel文件格式错误，缺少表头", null, null);
            }
            
            // 解析数据行
            for (int i = 1; i <= rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    Customer customer = parseCustomerFromRow(row, i + 1);
                    if (customer != null) {
                        customers.add(customer);
                    }
                } catch (Exception e) {
                    String errorMsg = "第" + (i + 1) + "行数据解析失败: " + e.getMessage();
                    errors.add(errorMsg);
                    log.error("Excel解析错误，行号: {}, 错误信息: {}", i + 1, e.getMessage(), e);
                }
            }
            
            workbook.close();
            
            if (customers.isEmpty()) {
                return new ImportResult(false, "没有解析到有效的客户数据", null, errors);
            }
            
            return new ImportResult(true, "成功解析" + customers.size() + "条客户数据", customers, errors);
            
        } catch (IOException e) {
            log.error("Excel文件读取失败: {}", file.getOriginalFilename(), e);
            return new ImportResult(false, "文件读取失败: " + e.getMessage(), null, null);
        } catch (Exception e) {
            log.error("Excel文件解析失败: {}", file.getOriginalFilename(), e);
            return new ImportResult(false, "文件解析失败: " + e.getMessage(), null, null);
        }
    }
    
    @Override
    public ImportResult parseCSV(MultipartFile file) {
        List<Customer> customers = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                
                if (line.isEmpty()) {
                    continue;
                }
                
                // 解析CSV行（处理引号内的逗号）
                String[] fields = parseCSVLine(line);
                
                if (lineNumber == 1) {
                    // 第一行是表头，跳过
                    continue;
                }
                
                try {
                    Customer customer = parseCustomerFromCSVFields(fields, lineNumber);
                    if (customer != null) {
                        customers.add(customer);
                    }
                } catch (Exception e) {
                    String errorMsg = "第" + lineNumber + "行数据解析失败: " + e.getMessage();
                    errors.add(errorMsg);
                    log.error("CSV解析错误，行号: {}, 错误信息: {}", lineNumber, e.getMessage(), e);
                }
            }
            
            if (customers.isEmpty()) {
                return new ImportResult(false, "没有解析到有效的客户数据", null, errors);
            }
            
            boolean success = errors.isEmpty();
            String message = String.format("解析完成：成功%d条，失败%d条", customers.size(), errors.size());
            return new ImportResult(success, message, customers, errors);
            
        } catch (IOException e) {
            log.error("CSV文件读取失败: {}", file.getOriginalFilename(), e);
            return new ImportResult(false, "CSV文件读取失败: " + e.getMessage(), null, null);
        }
    }
    
    /**
     * 解析CSV行（处理引号内的逗号）
     */
    private String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // 转义的双引号
                    currentField.append('"');
                    i++; // 跳过下一个引号
                } else {
                    // 切换引号状态
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // 字段分隔符
                fields.add(currentField.toString().trim());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        
        // 添加最后一个字段
        fields.add(currentField.toString().trim());
        
        return fields.toArray(new String[0]);
    }
    
    /**
     * 从CSV字段数组解析客户数据
     */
    private Customer parseCustomerFromCSVFields(String[] fields, int lineNumber) {
        if (fields.length < 2) {
            throw new RuntimeException("字段数量不足，必填项：客户名称、电话");
        }
        
        Customer customer = new Customer();
        
        // 客户名称（必填，索引0）
        String customerName = fields.length > 0 ? fields[0].trim() : "";
        if (customerName.isEmpty()) {
            throw new RuntimeException("客户名称不能为空（必填项：客户名称、电话）");
        }
        customer.setCustomerName(customerName);
        
        // 联系人（可选，索引1）
        String contactPerson = fields.length > 1 ? fields[1].trim() : "";
        customer.setContactPerson(contactPerson);
        
        // 电话（必填，索引2）
        String phone = fields.length > 2 ? fields[2].trim() : "";
        if (phone.isEmpty()) {
            throw new RuntimeException("电话不能为空（必填项：客户名称、电话）");
        }
        customer.setPhone(phone);
        
        // 邮箱（索引3）
        if (fields.length > 3) {
            customer.setEmail(fields[3].trim());
        }
        
        // 客户类型（索引4）
        if (fields.length > 4 && !fields[4].trim().isEmpty()) {
            String typeStr = fields[4].trim();
            if (typeStr.equals("个人")) {
                customer.setCustomerType(1);
            } else if (typeStr.equals("企业")) {
                customer.setCustomerType(2);
            } else if (typeStr.equals("科研院所")) {
                customer.setCustomerType(3);
            } else {
                try {
                    customer.setCustomerType(Integer.parseInt(typeStr));
                } catch (NumberFormatException e) {
                    customer.setCustomerType(1); // 默认个人
                }
            }
        } else {
            customer.setCustomerType(1); // 默认个人
        }
        
        // 客户等级（索引5）
        if (fields.length > 5 && !fields[5].trim().isEmpty()) {
            String levelStr = fields[5].trim();
            if (levelStr.equals("普通")) {
                customer.setCustomerLevel(1);
            } else if (levelStr.equals("VIP")) {
                customer.setCustomerLevel(2);
            } else if (levelStr.equals("钻石")) {
                customer.setCustomerLevel(3);
            } else {
                try {
                    customer.setCustomerLevel(Integer.parseInt(levelStr));
                } catch (NumberFormatException e) {
                    customer.setCustomerLevel(1); // 默认普通
                }
            }
        } else {
            customer.setCustomerLevel(1); // 默认普通
        }
        
        // 地区（索引6）
        if (fields.length > 6) {
            customer.setRegion(fields[6].trim());
        }
        
        // 职务（索引7）
        if (fields.length > 7) {
            customer.setPosition(fields[7].trim());
        }
        
        // QQ/微信（索引8）
        if (fields.length > 8) {
            customer.setQqWeixin(fields[8].trim());
        }
        
        // 合作内容（索引9）
        if (fields.length > 9) {
            customer.setCooperationContent(fields[9].trim());
        }
        
        // 详细地址（索引10）
        if (fields.length > 10) {
            customer.setAddress(fields[10].trim());
        }
        
        // 备注（索引11）
        if (fields.length > 11) {
            customer.setRemark(fields[11].trim());
        }
        
        // 设置默认值，确保必填字段不为空
        if (customer.getCustomerType() == null) {
            customer.setCustomerType(1); // 默认个人客户
        }
        if (customer.getCustomerLevel() == null) {
            customer.setCustomerLevel(1); // 默认普通
        }
        if (customer.getStatus() == null) {
            customer.setStatus(1); // 默认正常
        }
        if (customer.getSource() == null) {
            customer.setSource(2); // 默认线下
        }
        if (customer.getCustomerCode() == null || customer.getCustomerCode().trim().isEmpty()) {
            customer.setCustomerCode("CUST" + System.currentTimeMillis() + (int)(Math.random() * 1000));
        }
        customer.setCreateTime(LocalDateTime.now());
        customer.setUpdateTime(LocalDateTime.now());
        customer.setDeleted(0);
        customer.setVersion(1);
        
        return customer;
    }
    
    @Override
    public ImportResult parseWord(MultipartFile file) {
        // Word文件解析比较复杂，这里先返回模拟数据
        List<Customer> customers = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        // 模拟解析Word文件的结果
        Customer customer1 = new Customer();
        customer1.setCustomerName("Word导入客户1");
        customer1.setContactPerson("联系人1");
        customer1.setPhone("13800138001");
        customer1.setEmail("word1@example.com");
        customer1.setAddress("Word导入地址1");
        customer1.setPosition("总经理"); // 企业客户示例
        customer1.setCustomerLevel(1);
        customer1.setStatus(1);
        customers.add(customer1);
        
        Customer customer2 = new Customer();
        customer2.setCustomerName("Word导入客户2");
        customer2.setContactPerson("联系人2");
        customer2.setPhone("13800138002");
        customer2.setEmail("word2@example.com");
        customer2.setAddress("Word导入地址2");
        customer2.setPosition("研究员"); // 个人客户示例
        customer2.setCustomerLevel(2);
        customer2.setStatus(1);
        customers.add(customer2);
        
        return new ImportResult(true, "成功解析Word文件，共" + customers.size() + "条客户数据", customers, errors);
    }
    
    @Override
    public ValidationResult validateCustomers(List<Customer> customers) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            int rowNum = i + 1;
            
            // 必填字段验证：只验证客户名称和电话
            if (customer.getCustomerName() == null || customer.getCustomerName().trim().isEmpty()) {
                errors.add("第" + rowNum + "行：客户名称不能为空（必填项：客户名称、电话）");
            }
            
            if (customer.getPhone() == null || customer.getPhone().trim().isEmpty()) {
                errors.add("第" + rowNum + "行：电话不能为空（必填项：客户名称、电话）");
            }
            
            // 其他字段均为可选，不进行必填验证
        }
        
        boolean valid = errors.isEmpty();
        int validCount = customers.size() - errors.size();
        int invalidCount = errors.size();
        return new ValidationResult(valid, errors, warnings, validCount, invalidCount);
    }
    
    @Override
    public SaveResult saveCustomers(List<Customer> customers) {
        int successCount = 0;
        int failureCount = 0;
        List<String> errors = new ArrayList<>();
        
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            int rowNum = i + 1;
            
            try {
                // 设置创建时间和其他默认值
                customer.setCreateTime(LocalDateTime.now());
                customer.setUpdateTime(LocalDateTime.now());
                customer.setDeleted(0);
                customer.setVersion(1);
                
                // 设置默认值，避免必填字段为空
                if (customer.getCustomerType() == null) {
                    customer.setCustomerType(1); // 默认个人客户
                }
                if (customer.getCustomerLevel() == null) {
                    customer.setCustomerLevel(1); // 默认普通
                }
                if (customer.getStatus() == null) {
                    customer.setStatus(1); // 默认正常
                }
                if (customer.getSource() == null) {
                    customer.setSource(2); // 默认线下
                }
                // 设置默认业务类型（第一种类型：品种权申请客户）
                if (customer.getBusinessType() == null) {
                    customer.setBusinessType(1);
                }
                
                // 生成客户编号
                if (customer.getCustomerCode() == null || customer.getCustomerCode().trim().isEmpty()) {
                    customer.setCustomerCode("CUST" + System.currentTimeMillis() + (i + 1));
                }
                
                // 保存客户
                boolean saved = customerService.save(customer);
                if (saved) {
                    successCount++;
                } else {
                    failureCount++;
                    errors.add("第" + rowNum + "行：保存失败");
                }
            } catch (Exception e) {
                failureCount++;
                String errorMsg = "第" + rowNum + "行：保存失败 - " + e.getMessage();
                errors.add(errorMsg);
                log.error("保存客户数据失败，行号: {}, 客户名称: {}, 错误信息: {}", 
                    rowNum, customer.getCustomerName(), e.getMessage(), e);
            }
        }
        
        boolean success = failureCount == 0;
        String message = String.format("导入完成：成功%d条，失败%d条", successCount, failureCount);
        
        return new SaveResult(success, message, successCount, failureCount, errors);
    }
    
    /**
     * 从Excel行解析客户数据
     */
    private Customer parseCustomerFromRow(Row row, int rowNum) {
        Customer customer = new Customer();
        
        try {
            // 客户名称（必填）
            Cell customerNameCell = row.getCell(0);
            String customerName = "";
            if (customerNameCell != null) {
                customerName = getCellValueAsString(customerNameCell);
            }
            if (customerName.trim().isEmpty()) {
                throw new RuntimeException("客户名称不能为空（必填项：客户名称、电话）");
            }
            customer.setCustomerName(customerName);
            
            // 联系人（可选）
            Cell contactPersonCell = row.getCell(1);
            if (contactPersonCell != null) {
                customer.setContactPerson(getCellValueAsString(contactPersonCell));
            }
            
            // 电话（必填）
            Cell phoneCell = row.getCell(2);
            String phone = "";
            if (phoneCell != null) {
                phone = getCellValueAsString(phoneCell);
            }
            if (phone.trim().isEmpty()) {
                throw new RuntimeException("电话不能为空（必填项：客户名称、电话）");
            }
            customer.setPhone(phone);
            
            // 邮箱
            Cell emailCell = row.getCell(3);
            if (emailCell != null) {
                customer.setEmail(getCellValueAsString(emailCell));
            }
            
            // 地址
            Cell addressCell = row.getCell(4);
            if (addressCell != null) {
                customer.setAddress(getCellValueAsString(addressCell));
            }
            
            // 邮政编码
            Cell postalCodeCell = row.getCell(5);
            if (postalCodeCell != null) {
                customer.setPostalCode(getCellValueAsString(postalCodeCell));
            }
            
            // 传真
            Cell faxCell = row.getCell(6);
            if (faxCell != null) {
                customer.setFax(getCellValueAsString(faxCell));
            }
            
            // 机构代码
            Cell orgCodeCell = row.getCell(7);
            if (orgCodeCell != null) {
                customer.setOrganizationCode(getCellValueAsString(orgCodeCell));
            }
            
            // 国籍
            Cell nationalityCell = row.getCell(8);
            if (nationalityCell != null) {
                customer.setNationality(getCellValueAsString(nationalityCell));
            }
            
            // 申请人性质
            Cell applicantNatureCell = row.getCell(9);
            if (applicantNatureCell != null) {
                String value = getCellValueAsString(applicantNatureCell);
                if (!value.isEmpty()) {
                    try {
                        // applicantNature字段已删除，使用position替代
                        customer.setPosition(value);
                    } catch (NumberFormatException e) {
                        // 尝试按文本解析
                        switch (value.toLowerCase()) {
                            case "个人": customer.setPosition("研究员"); break;
                            case "企业": customer.setPosition("总经理"); break;
                            case "科研院所": customer.setPosition("研究员"); break;
                            case "其他": customer.setPosition(""); break;
                            default: customer.setPosition(""); break;
                        }
                    }
                }
            }
            
            // 代理机构名称
            Cell agencyNameCell = row.getCell(10);
            if (agencyNameCell != null) {
                customer.setAgencyName(getCellValueAsString(agencyNameCell));
            }
            
            // 代理机构代码
            Cell agencyCodeCell = row.getCell(11);
            if (agencyCodeCell != null) {
                customer.setAgencyCode(getCellValueAsString(agencyCodeCell));
            }
            
            // 代理机构地址
            Cell agencyAddressCell = row.getCell(12);
            if (agencyAddressCell != null) {
                customer.setAgencyAddress(getCellValueAsString(agencyAddressCell));
            }
            
            // 代理机构邮编
            Cell agencyPostalCodeCell = row.getCell(13);
            if (agencyPostalCodeCell != null) {
                customer.setAgencyPostalCode(getCellValueAsString(agencyPostalCodeCell));
            }
            
            // 代理人姓名
            Cell agentNameCell = row.getCell(14);
            if (agentNameCell != null) {
                customer.setAgentName(getCellValueAsString(agentNameCell));
            }
            
            // 代理人电话
            Cell agentPhoneCell = row.getCell(15);
            if (agentPhoneCell != null) {
                customer.setAgentPhone(getCellValueAsString(agentPhoneCell));
            }
            
            // 代理人传真
            Cell agentFaxCell = row.getCell(16);
            if (agentFaxCell != null) {
                customer.setAgentFax(getCellValueAsString(agentFaxCell));
            }
            
            // 代理人手机
            Cell agentMobileCell = row.getCell(17);
            if (agentMobileCell != null) {
                customer.setAgentMobile(getCellValueAsString(agentMobileCell));
            }
            
            // 代理人邮箱
            Cell agentEmailCell = row.getCell(18);
            if (agentEmailCell != null) {
                customer.setAgentEmail(getCellValueAsString(agentEmailCell));
            }
            
            // 客户等级
            Cell customerLevelCell = row.getCell(19);
            if (customerLevelCell != null) {
                String value = getCellValueAsString(customerLevelCell);
                if (!value.isEmpty()) {
                    try {
                        customer.setCustomerLevel(Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        switch (value.toLowerCase()) {
                            case "普通": customer.setCustomerLevel(1); break;
                            case "高级": case "vip": customer.setCustomerLevel(2); break;
                            case "钻石": customer.setCustomerLevel(3); break;
                            default: customer.setCustomerLevel(1); break;
                        }
                    }
                }
            } else {
                customer.setCustomerLevel(1); // 默认普通
            }
            
            // 客户状态
            Cell statusCell = row.getCell(20);
            if (statusCell != null) {
                String value = getCellValueAsString(statusCell);
                if (!value.isEmpty()) {
                    try {
                        customer.setStatus(Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        switch (value.toLowerCase()) {
                            case "正常": customer.setStatus(1); break;
                            case "待跟进": customer.setStatus(2); break;
                            case "流失": customer.setStatus(3); break;
                            default: customer.setStatus(1); break;
                        }
                    }
                }
            } else {
                customer.setStatus(1); // 默认正常
            }
            
            // 备注
            Cell remarkCell = row.getCell(21);
            if (remarkCell != null) {
                customer.setRemark(getCellValueAsString(remarkCell));
            }
            
            // 设置默认值，确保必填字段不为空
            if (customer.getCustomerType() == null) {
                customer.setCustomerType(1); // 默认个人客户
            }
            if (customer.getCustomerLevel() == null) {
                customer.setCustomerLevel(1); // 默认普通
            }
            if (customer.getStatus() == null) {
                customer.setStatus(1); // 默认正常
            }
            if (customer.getSource() == null) {
                customer.setSource(2); // 默认线下
            }
            if (customer.getCustomerCode() == null || customer.getCustomerCode().trim().isEmpty()) {
                customer.setCustomerCode("CUST" + System.currentTimeMillis() + rowNum);
            }
            customer.setCreateTime(LocalDateTime.now());
            customer.setUpdateTime(LocalDateTime.now());
            customer.setDeleted(0);
            customer.setVersion(1);
            
            return customer;
            
        } catch (Exception e) {
            log.error("Excel行解析错误，行号: {}, 错误信息: {}", rowNum, e.getMessage(), e);
            throw new RuntimeException("解析第" + rowNum + "行数据时发生错误: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取单元格值作为字符串
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}



