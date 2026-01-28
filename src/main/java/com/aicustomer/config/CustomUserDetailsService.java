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
        System.out.println("🔍 [登录] 尝试加载用户: " + username);

        User user = userService.findByUsername(username);
        if (user == null) {
            System.out.println("❌ [登录失败] 用户不存在: " + username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        System.out.println("✅ [登录] 找到用户: " + username);
        System.out.println(
                "   - 密码hash: " + user.getPassword().substring(0, Math.min(20, user.getPassword().length())) + "...");
        System.out.println("   - 密码长度: " + user.getPassword().length());
        System.out.println("   - 状态(status): " + user.getStatus());
        System.out.println("   - 删除标记(deleted): " + user.getDeleted());

        // 检查用户是否被删除
        if (user.getDeleted() != null && user.getDeleted() != 0) {
            System.out.println("❌ [登录失败] 用户已被删除: " + username);
            throw new UsernameNotFoundException("用户已被删除: " + username);
        }

        // 构建权限列表
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        // 如果是管理员，添加管理员权限
        if ("admin".equals(username)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        // 检查用户状态：1=正常，0=禁用
        boolean isEnabled = (user.getStatus() != null && user.getStatus() == 1);
        System.out.println("   - 账户启用状态: " + isEnabled);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // 此时已经是加密后的密码
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!isEnabled) // 根据status字段设置
                .build();
    }
}
