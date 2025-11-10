package com.aicustomer.service.impl;

import com.aicustomer.entity.Customer;
import com.aicustomer.service.CustomerService;
import com.aicustomer.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 导出服务实现类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {
    
    private final CustomerService customerService;
    
    @Override
    public ByteArrayOutputStream generateCustomerTemplate() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("客户导入模版");
        
        // 创建表头样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        
        // 创建表头
        Row headerRow = sheet.createRow(0);
        String[] headers = {"客户名称", "联系人", "电话", "邮箱", "客户类型", "客户等级", "地区", "职务", "QQ/微信", "合作内容", "详细地址", "备注"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // 创建示例数据行
        Row exampleRow = sheet.createRow(1);
        String[] exampleData = {"示例客户", "张三", "13800138000", "example@email.com", "企业", "普通", "北京", "经理", "123456789", "产品合作", "北京市朝阳区", "示例备注"};
        for (int i = 0; i < exampleData.length; i++) {
            exampleRow.createCell(i).setCellValue(exampleData[i]);
        }
        
        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
        }
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("生成模版失败", e);
        }
        return outputStream;
    }
    
    @Override
    public ByteArrayOutputStream exportCustomersToExcel(Customer queryCustomer) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("客户数据");
        
        // 创建表头样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        
        // 创建表头
        Row headerRow = sheet.createRow(0);
        String[] headers = {"客户编号", "客户名称", "联系人", "电话", "邮箱", "客户类型", "客户等级", "地区", "职务", "QQ/微信", "合作内容", "详细地址", "备注", "创建时间"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // 查询客户数据
        List<Customer> customers = customerService.list(queryCustomer);
        
        // 填充数据
        int rowNum = 1;
        for (Customer customer : customers) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;
            
            row.createCell(colNum++).setCellValue(customer.getCustomerCode() != null ? customer.getCustomerCode() : "");
            row.createCell(colNum++).setCellValue(customer.getCustomerName() != null ? customer.getCustomerName() : "");
            row.createCell(colNum++).setCellValue(customer.getContactPerson() != null ? customer.getContactPerson() : "");
            row.createCell(colNum++).setCellValue(customer.getPhone() != null ? customer.getPhone() : "");
            row.createCell(colNum++).setCellValue(customer.getEmail() != null ? customer.getEmail() : "");
            row.createCell(colNum++).setCellValue(getCustomerTypeText(customer.getCustomerType()));
            row.createCell(colNum++).setCellValue(getCustomerLevelText(customer.getCustomerLevel()));
            row.createCell(colNum++).setCellValue(customer.getRegion() != null ? customer.getRegion() : "");
            row.createCell(colNum++).setCellValue(customer.getPosition() != null ? customer.getPosition() : "");
            row.createCell(colNum++).setCellValue(customer.getQqWeixin() != null ? customer.getQqWeixin() : "");
            row.createCell(colNum++).setCellValue(customer.getCooperationContent() != null ? customer.getCooperationContent() : "");
            row.createCell(colNum++).setCellValue(customer.getAddress() != null ? customer.getAddress() : "");
            row.createCell(colNum++).setCellValue(customer.getRemark() != null ? customer.getRemark() : "");
            row.createCell(colNum++).setCellValue(customer.getCreateTime() != null ? customer.getCreateTime().toString() : "");
        }
        
        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
        }
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("导出客户数据失败", e);
        }
        return outputStream;
    }
    
    @Override
    public ByteArrayOutputStream exportCustomersToCSV(Customer queryCustomer) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            // 写入BOM以支持Excel正确识别UTF-8
            outputStream.write(0xEF);
            outputStream.write(0xBB);
            outputStream.write(0xBF);
            
            // 写入表头
            writer.write("客户编号,客户名称,联系人,电话,邮箱,客户类型,客户等级,地区,职务,QQ/微信,合作内容,详细地址,备注,创建时间\n");
            
            // 查询客户数据
            List<Customer> customers = customerService.list(queryCustomer);
            
            // 写入数据
            for (Customer customer : customers) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    escapeCSV(customer.getCustomerCode()),
                    escapeCSV(customer.getCustomerName()),
                    escapeCSV(customer.getContactPerson()),
                    escapeCSV(customer.getPhone()),
                    escapeCSV(customer.getEmail()),
                    escapeCSV(getCustomerTypeText(customer.getCustomerType())),
                    escapeCSV(getCustomerLevelText(customer.getCustomerLevel())),
                    escapeCSV(customer.getRegion()),
                    escapeCSV(customer.getPosition()),
                    escapeCSV(customer.getQqWeixin()),
                    escapeCSV(customer.getCooperationContent()),
                    escapeCSV(customer.getAddress()),
                    escapeCSV(customer.getRemark()),
                    customer.getCreateTime() != null ? customer.getCreateTime().toString() : ""
                ));
            }
            
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("导出CSV失败", e);
        }
        return outputStream;
    }
    
    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    private String getCustomerTypeText(Integer type) {
        if (type == null) return "";
        switch (type) {
            case 1: return "个人";
            case 2: return "企业";
            case 3: return "科研院所";
            default: return "";
        }
    }
    
    private String getCustomerLevelText(Integer level) {
        if (level == null) return "";
        switch (level) {
            case 1: return "普通";
            case 2: return "VIP";
            case 3: return "钻石";
            default: return "";
        }
    }
    
    @Override
    public ByteArrayOutputStream exportCommunicationsToExcel() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            // 模拟Excel数据生成
            String csvData = "客户名称,沟通类型,主题,内容,沟通时间,重要性\n";
            csvData += "张三,电话,产品咨询,询问新品种保护流程,2025-09-29,高\n";
            csvData += "李四,邮件,合同确认,确认服务条款,2025-09-28,中\n";
            
            outputStream.write(csvData.getBytes("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("导出沟通记录失败", e);
        }
        return outputStream;
    }
    
    @Override
    public ByteArrayOutputStream exportTasksToExcel() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            // 模拟Excel数据生成
            String csvData = "任务标题,任务类型,负责人,优先级,状态,截止时间\n";
            csvData += "客户回访,跟进任务,张三,高,进行中,2025-10-01\n";
            csvData += "合同审核,审批任务,李四,中,待处理,2025-10-02\n";
            
            outputStream.write(csvData.getBytes("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("导出任务数据失败", e);
        }
        return outputStream;
    }
}



