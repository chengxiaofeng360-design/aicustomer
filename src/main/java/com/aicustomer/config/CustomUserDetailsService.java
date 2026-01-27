package com.aicustomer.config;

import com.aicustomer.entity.User;
import com.aicustomer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义用户详情服务
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    @org.springframework.context.annotation.Lazy
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // --- 自愈机制：检测并修复明文弱密码 ---
        // 如果数据库被重置为明文 "123456"，自动将其加密修复，确保用户能正常登录
        if ("123456".equals(user.getPassword())) {
            System.out.println("⚠️ [安全自愈] 检测到用户 " + username + " 使用明文密码 '123456'，正在自动修复为加密存储...");
            try {
                // 1. 加密密码
                String encodedPassword = passwordEncoder.encode("123456");
                // 2. 更新内存对象
                user.setPassword(encodedPassword);
                // 3. 更新数据库 (UserService.update 会处理更新逻辑)
                userService.update(user);
                System.out.println("✅ [安全自愈] 用户 " + username + " 密码修复成功！");
            } catch (Exception e) {
                System.err.println("❌ [安全自愈] 密码自动修复失败: " + e.getMessage());
                // 即使保存失败，也要在本次内存中让用户登录成功，所以不要抛出异常
            }
        }
        // ------------------------------------

        // 构建权限列表
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        // 如果是管理员，添加管理员权限
        if ("admin".equals(username)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // 此时已经是加密后的密码
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
