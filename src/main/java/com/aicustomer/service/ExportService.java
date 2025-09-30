package com.aicustomer.service;

import java.io.ByteArrayOutputStream;

/**
 * 导出服务接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
public interface ExportService {
    
    /**
     * 导出客户数据到Excel
     */
    ByteArrayOutputStream exportCustomersToExcel();
    
    /**
     * 导出沟通记录到Excel
     */
    ByteArrayOutputStream exportCommunicationsToExcel();
    
    /**
     * 导出任务数据到Excel
     */
    ByteArrayOutputStream exportTasksToExcel();
}



