package com.aicustomer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI客户管理系统主启动类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@SpringBootApplication
@MapperScan("com.aicustomer.mapper")
public class AiCustomerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiCustomerApplication.class, args);
    }
}

