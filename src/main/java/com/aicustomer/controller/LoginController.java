package com.aicustomer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 登录控制器
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Controller
public class LoginController {

    /**
     * 显示登录页面
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}




