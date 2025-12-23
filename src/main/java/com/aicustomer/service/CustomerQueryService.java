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
    public String getTotalCustomerCount() {
        try {
            Long count = customerMapper.selectCount(new Customer(), null);
            return "系统目前共有 " + count + " 位客户。";
        } catch (Exception e) {
            log.error("查询客户总数失败", e);
            return "查询失败：" + e.getMessage();
        }
    }

    /**
     * 根据地区查询客户信息
     * 
     * @param region 地区名称（如“北京”、“上海”）
     * @return 客户信息列表字符串
     */
    public String getCustomersByRegion(String region) {
        try {
            Customer criteria = new Customer();
            criteria.setRegion(region);
            List<Customer> customers = customerMapper.selectPage(criteria, null, 0, 50);

            if (customers == null || customers.isEmpty()) {
                return "在地区 [" + region + "] 未找到相关客户信息。";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("在 ").append(region).append(" 地区为您找到以下 ").append(customers.size()).append(" 位客户：\n");
            for (int i = 0; i < customers.size(); i++) {
                Customer c = customers.get(i);
                sb.append(i + 1).append(". **").append(c.getCustomerName()).append("**")
                        .append(" (联系人: ").append(c.getContactPerson() != null ? c.getContactPerson() : "无")
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
     * @param customerName 客户名称
     * @return 客户详情
     */
    public String getCustomerDetail(String customerName) {
        try {
            Customer criteria = new Customer();
            criteria.setCustomerName(customerName);
            List<Customer> customers = customerMapper.selectList(criteria);

            if (customers == null || customers.isEmpty()) {
                return "未找到名为 [" + customerName + "] 的客户信息。";
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
     * 执行动态 SQL 查询（Text-to-SQL）
     * 
     * @param sql AI 生成的 SQL 语句
     * @return 查询结果格式化字符串
     */
    public String executeDynamicQuery(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return "SQL 语句为空。";
        }

        // 安全检查：仅允许 SELECT
        String upperSql = sql.trim().toUpperCase();
        if (!upperSql.startsWith("SELECT")) {
            return "出于安全考虑，仅支持 SELECT 查询语句。";
        }

        // 危险关键词过滤 (使用更精确的匹配，避免误伤 create_time 等字段)
        String[] forbidden = { "DROP", "UPDATE", "DELETE", "TRUNCATE", "ALTER", "INSERT", "CREATE", "GRANT", "EXEC" };
        for (String word : forbidden) {
            // 使用正则匹配独立单词
            if (upperSql.matches(".*\\b" + word + "\\b.*")) {
                // 特殊处理：如果关键词是 CREATE，且后面紧跟着 _TIME (如 CREATE_TIME)，则允许
                if ("CREATE".equals(word) && upperSql.matches(".*\\bCREATE_TIME\\b.*")
                        && !upperSql.matches(".*\\bCREATE\\b(?!_TIME).*")) {
                    continue;
                }
                return "SQL 包含禁止的关键词：" + word;
            }
        }

        try {
            log.info("【数据查询服务】正在执行动态 SQL: {}", sql);
            // 结果限制：如果 SQL 包含 LIMIT，则不处理，否则追加 LIMIT 50
            if (!upperSql.contains("LIMIT")) {
                sql = sql.trim();
                if (sql.endsWith(";")) {
                    sql = sql.substring(0, sql.length() - 1);
                }
                sql += " LIMIT 50";
            }

            // 使用 Mapper 执行查询
            List<Map<String, Object>> results = customerMapper.selectDynamic(sql);
            if (results == null || results.isEmpty()) {
                return "未查询到相关结果。";
            }

            // 格式化结果为字符串
            StringBuilder sb = new StringBuilder();
            sb.append("查询到 ").append(results.size()).append(" 条记录：\n");
            for (Map<String, Object> row : results) {
                sb.append("- ");
                row.forEach((k, v) -> sb.append(k).append(": ").append(v).append(", "));
                sb.setLength(sb.length() - 2); // 移除最后的逗号
                sb.append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("动态 SQL 执行失败", e);
            return "查询执行失败：" + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
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
