package com.aicustomer.controller;

import com.aicustomer.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("Hello, AI Customer Management System!");
    }
    
    @GetMapping("/status")
    public Result<Map<String, Object>> status() {
        Map<String, Object> status = new HashMap<>();
        status.put("application", "AI Customer Management System");
        status.put("version", "1.0.0");
        status.put("status", "running");
        status.put("timestamp", System.currentTimeMillis());
        return Result.success(status);
    }
}



