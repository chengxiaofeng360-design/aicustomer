package com.aicustomer.service;

import com.aicustomer.entity.Customer;
import com.aicustomer.mapper.CustomerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 客户数据查询服务，专门为 AI 聊天工具调用（Function Calling）提供支持
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerQueryService {

    private final CustomerMapper customerMapper;

    /**
     * 获取客户总数
     * 
     * @return 客户统计摘要
     */
    /**
     * 获取客户总数
     * 
     * @param allowedLevels 允许访问的客户等级列表 (null表示无限制)
     * @return 客户统计摘要
     */
    public String getTotalCustomerCount(List<Integer> allowedLevels) {
        try {
            Customer criteria = new Customer();
            Long count = customerMapper.selectCount(criteria, null, allowedLevels);
            return "系统目前共有 " + count + " 位客户" + (allowedLevels != null ? " (您权限范围内)" : "") + "。";
        } catch (Exception e) {
            log.error("查询客户总数失败", e);
            return "查询失败：" + e.getMessage();
        }
    }

    /**
     * 根据地区查询客户信息
     * 
     * @param region        地区名称
     * @param allowedLevels 允许访问的客户等级列表
     * @return 客户信息列表
     */
    public String getCustomersByRegion(String region, List<Integer> allowedLevels) {
        try {
            Customer criteria = new Customer();
            criteria.setRegion(region);
            // CustomerMapper.selectPage already supports allowedLevels via XML modification
            List<Customer> customers = customerMapper.selectPage(criteria, null, allowedLevels, 0, 50);

            if (customers == null || customers.isEmpty()) {
                return "在地区 [" + region + "] 未找到相关客户信息" + (allowedLevels != null ? " (或无权访问)" : "") + "。";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("在 ").append(region).append(" 地区为您找到以下 ").append(customers.size()).append(" 位客户：\n");
            for (int i = 0; i < customers.size(); i++) {
                Customer c = customers.get(i);
                sb.append(i + 1).append(". **").append(c.getCustomerName()).append("**")
                        .append(" (联系人: ").append(c.getContactPerson() != null ? c.getContactPerson() : "无")
                        .append(", 等级: ").append(getCustomerLevelName(c.getCustomerLevel()))
                        .append(")\n");
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("按地区查询客户失败", e);
            return "查询失败：" + e.getMessage();
        }
    }

    /**
     * 查询指定客户的详细信息
     * 
     * @param customerName  客户名称
     * @param allowedLevels 允许访问的客户等级列表
     * @return 客户详情
     */
    public String getCustomerDetail(String customerName, List<Integer> allowedLevels) {
        try {
            Customer criteria = new Customer();
            criteria.setCustomerName(customerName);
            // Use selectPage logic to leverage dynamic filtering, or filter manually
            List<Customer> customers = customerMapper.selectPage(criteria, null, allowedLevels, 0, 10);

            if (customers == null || customers.isEmpty()) {
                return "未找到名为 [" + customerName + "] 的客户信息" + (allowedLevels != null ? " (或无权访问)" : "") + "。";
            }

            Customer c = customers.get(0);
            StringBuilder sb = new StringBuilder();
            sb.append("### 客户详情：").append(c.getCustomerName()).append("\n")
                    .append("- **编号**: ").append(c.getCustomerCode()).append("\n")
                    .append("- **地区**: ").append(c.getRegion() != null ? c.getRegion() : "未填写").append("\n")
                    .append("- **联系人**: ").append(c.getContactPerson() != null ? c.getContactPerson() : "未填写")
                    .append("\n")
                    .append("- **电话**: ").append(c.getPhone() != null ? c.getPhone() : "未填写").append("\n")
                    .append("- **邮箱**: ").append(c.getEmail() != null ? c.getEmail() : "未填写").append("\n")
                    .append("- **等级**: ").append(getCustomerLevelName(c.getCustomerLevel())).append("\n")
                    .append("- **业务类型**: ").append(getBusinessTypeName(c.getBusinessType())).append("\n")
                    .append("- **备注**: ").append(c.getRemark() != null ? c.getRemark() : "无");

            return sb.toString();
        } catch (Exception e) {
            log.error("查询客户详情失败", e);
            return "查询失败：" + e.getMessage();
        }
    }

    /**
     * 通用客户查询（安全模式，不使用动态SQL）
     * 
     * @param params        查询参数实体
     * @param limit         最大条数
     * @param allowedLevels 权限
     * @return 格式化结果
     */
    public String queryCustomers(Customer params, int limit, List<Integer> allowedLevels) {
        try {
            // 参数校验
            if (limit <= 0)
                limit = 10;
            if (limit > 50)
                limit = 50;

            List<Customer> customers = customerMapper.selectPage(params, null, allowedLevels, 0, limit);

            if (customers == null || customers.isEmpty()) {
                return "未查询到符合条件的客户信息" + (allowedLevels != null ? " (或无权访问)" : "") + "。";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("找到 ").append(customers.size()).append(" 位客户：\n");
            for (Customer c : customers) {
                sb.append("- **").append(c.getCustomerName()).append("**");

                if (c.getRegion() != null)
                    sb.append(" | 地区: ").append(c.getRegion());
                if (c.getContactPerson() != null)
                    sb.append(" | 联系人: ").append(c.getContactPerson());
                if (c.getCustomerLevel() != null)
                    sb.append(" | 等级: ").append(getCustomerLevelName(c.getCustomerLevel()));

                sb.append("\n");
            }
            return sb.toString();

        } catch (Exception e) {
            log.error("客户查询失败", e);
            return "查询失败：" + e.getMessage();
        }
    }

    private String getCustomerLevelName(Integer level) {
        if (level == null)
            return "未知";
        return switch (level) {
            case 1 -> "普通";
            case 2 -> "VIP";
            case 3 -> "钻石";
            default -> "未知";
        };
    }

    private String getBusinessTypeName(Integer type) {
        if (type == null)
            return "未知";
        return switch (type) {
            case 1 -> "品种权申请客户";
            case 2 -> "品种权转化推广客户";
            case 3 -> "知识产权互补协作客户";
            case 4 -> "科普教育合作客户";
            case 5 -> "景观设计服务客户";
            case 6 -> "图书出版客户";
            default -> "通用业务客户";
        };
    }
}
