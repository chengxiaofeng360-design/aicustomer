package com.aicustomer.service;

import com.aicustomer.entity.Customer;

import java.io.ByteArrayOutputStream;

/**
 * 导出服务接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
public interface ExportService {
    
    /**
     * 生成客户导入Excel模版
     */
    ByteArrayOutputStream generateCustomerTemplate();
    
    /**
     * 导出客户数据到Excel（根据查询条件）
     */
    ByteArrayOutputStream exportCustomersToExcel(Customer queryCustomer);
    
    /**
     * 导出客户数据到CSV（根据查询条件）
     */
    ByteArrayOutputStream exportCustomersToCSV(Customer queryCustomer);
    
    /**
     * 导出客户数据到Excel（无参数，兼容旧接口）
     */
    default ByteArrayOutputStream exportCustomersToExcel() {
        return exportCustomersToExcel(new Customer());
    }
    
    /**
     * 导出沟通记录到Excel
     */
    ByteArrayOutputStream exportCommunicationsToExcel();
    
    /**
     * 导出任务数据到Excel
     */
    ByteArrayOutputStream exportTasksToExcel();
}



