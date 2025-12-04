package com.aicustomer.mapper;

import com.aicustomer.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(@Param("username") String username);

    /**
     * 查询用户列表（用于下拉选择等场景）
     * 
     * @param user 查询条件
     * @return 用户列表
     */
    List<User> selectList(@Param("user") User user);
}





