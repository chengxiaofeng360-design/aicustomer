package com.aicustomer.controller;

import com.aicustomer.common.Result;
import com.aicustomer.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

/**
 * 导出控制器
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class ExportController {
    
    private final ExportService exportService;
    
    /**
     * 导出客户数据
     */
    @GetMapping("/customers")
    public ResponseEntity<byte[]> exportCustomers() {
        try {
            ByteArrayOutputStream outputStream = exportService.exportCustomersToExcel();
            byte[] data = outputStream.toByteArray();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", 
                "customers_" + LocalDate.now() + ".xlsx");
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(data);
                
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 导出沟通记录
     */
    @GetMapping("/communications")
    public ResponseEntity<byte[]> exportCommunications() {
        try {
            ByteArrayOutputStream outputStream = exportService.exportCommunicationsToExcel();
            byte[] data = outputStream.toByteArray();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", 
                "communications_" + LocalDate.now() + ".xlsx");
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(data);
                
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 导出任务提醒
     */
    @GetMapping("/tasks")
    public ResponseEntity<byte[]> exportTasks() {
        try {
            ByteArrayOutputStream outputStream = exportService.exportTasksToExcel();
            byte[] data = outputStream.toByteArray();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", 
                "tasks_" + LocalDate.now() + ".xlsx");
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(data);
                
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
