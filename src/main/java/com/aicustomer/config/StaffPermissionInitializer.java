package com.aicustomer.config;

import com.aicustomer.dto.UserPermissionDTO;
import com.aicustomer.service.UserPermissionService;
import com.aicustomer.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化员工权限
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StaffPermissionInitializer implements CommandLineRunner {

    private final UserService userService;
    private final UserPermissionService userPermissionService;

    @Override
    public void run(String... args) throws Exception {
        updateStaffPermission();
    }

    private void updateStaffPermission() {
        try {
            String username = "staff";
            com.aicustomer.entity.User user = userService.findByUsername(username);
            if (user == null) {
                log.info("用户 {} 不存在，跳过权限初始化", username);
                return;
            }

            UserPermissionDTO dto = userPermissionService.getUserPermissionByUsername(username);
            UserPermissionDTO.DataPermissionConfig dataPerm = dto.getDataPermission();
            if (dataPerm == null) {
                dataPerm = new UserPermissionDTO.DataPermissionConfig();
            }

            // 设置允许新增和编辑
            // 同时确保只能操作普通客户 (VIP/Diamond设为false)
            dataPerm.setCanAdd(true);
            dataPerm.setCanEdit(true);
            dataPerm.setCanAccessVip(false);
            dataPerm.setCanAccessDiamond(false);

            // 下面这些保留或按需开启
            dataPerm.setCanViewAllData(true); // 是否允许看所有数据? 假设是的，或者只能看自己的

            dto.setDataPermission(dataPerm);

            userPermissionService.updateUserPermission(dto);
            log.info("已更新 {} 用户权限：允许新增和编辑普通客户", username);

        } catch (Exception e) {
            log.error("初始化员工权限失败", e);
        }
    }
}
