package com.aicustomer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置类
 * 适配 Spring Boot 3.x（不再使用WebSecurityConfigurerAdapter）
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private CustomUserDetailsService userDetailsService;

        public void configureGlobal(AuthenticationManagerBuilder auth, @Lazy PasswordEncoder passwordEncoder)
                        throws Exception {
                auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        }

        // Dify 工具专用 FilterChain（完全豁免认证）
        @Bean
        @org.springframework.core.annotation.Order(1)
        public SecurityFilterChain difyToolsSecurityFilterChain(HttpSecurity http) throws Exception {
                http
                                .securityMatcher("/api/dify/tools/**")
                                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(
                                                                org.springframework.security.config.http.SessionCreationPolicy.STATELESS));
                return http.build();
        }

        // 主应用 FilterChain
        @Bean
        @org.springframework.core.annotation.Order(2)
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("/login", "/register", "/lib/**", "/css/**", "/js/**",
                                                                "/api/customer/login",
                                                                "/api/user/login")
                                                .permitAll()
                                                .requestMatchers("/index.html", "/").authenticated()
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/api/user/login")
                                                .successHandler((request, response, authentication) -> {
                                                        System.out.println("Login success for user: "
                                                                        + authentication.getName());
                                                        response.setContentType("application/json;charset=UTF-8");
                                                        response.setStatus(200);
                                                        String json = "{\"code\": 200, \"message\": \"登录成功\", \"data\": {\"username\": \""
                                                                        + authentication.getName() + "\"}}";
                                                        response.getWriter().write(json);
                                                        response.getWriter().flush();
                                                        response.getWriter().close();
                                                })
                                                .failureHandler((request, response, exception) -> {
                                                        System.out.println("Login failure: " + exception.getMessage());
                                                        response.setContentType("application/json;charset=UTF-8");
                                                        response.setStatus(200);
                                                        String json = "{\"code\": 401, \"message\": \"用户名或密码错误\"}";
                                                        response.getWriter().write(json);
                                                        response.getWriter().flush();
                                                        response.getWriter().close();
                                                })
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/api/user/logout")
                                                .logoutSuccessHandler((request, response, authentication) -> {
                                                        response.setContentType("application/json;charset=UTF-8");
                                                        response.setStatus(200);
                                                        response.getWriter().write(
                                                                        "{\"code\": 200, \"message\": \"退出成功\"}");
                                                        response.getWriter().flush();
                                                        response.getWriter().close();
                                                })
                                                .permitAll())
                                .csrf(csrf -> csrf.disable())
                                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                                .cors(org.springframework.security.config.Customizer.withDefaults());

                return http.build();
        }
}
