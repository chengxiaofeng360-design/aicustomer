package com.aicustomer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI客户管理系统主启动类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 * 
 * # 1）杀掉占用 8085 端口的进程（一次性搞定）
lsof -ti:8085 | xargs kill -9
# 2）在项目目录下启动服务（/Users/zuozuo/Downloads/cxf/aicustomer）

lsof -ti:8085 | xargs kill -9
mvn spring-boot:run
 * 
 */
@SpringBootApplication(exclude = {
    org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration.class
})
@MapperScan("com.aicustomer.mapper")
public class AiCustomerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiCustomerApplication.class, args);
    }
}

