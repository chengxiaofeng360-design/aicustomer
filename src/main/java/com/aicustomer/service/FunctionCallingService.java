package com.aicustomer.service;

import com.aicustomer.model.FunctionDefinition;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Function Calling 执行服务
 * 
 * 负责执行AI请求的函数调用，返回结果
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FunctionCallingService {

    private final CustomerQueryService customerQueryService;
    private final KnowledgeQueryService knowledgeQueryService;

    /**
     * 执行函数调用
     * 
     * @param functionName  函数名称
     * @param arguments     函数参数（JSON字符串）
     * @param allowedLevels 允许查询的客户等级（权限控制）
     * @return 函数执行结果
     */
    public String executeFunction(String functionName, String arguments, List<Integer> allowedLevels) {
        log.info("【Function Calling】执行函数: {}, 参数: {}", functionName, arguments);

        try {
            // 解析参数
            JSONObject params = new JSONObject();
            if (arguments != null && !arguments.trim().isEmpty() && !arguments.equals("{}")) {
                params = JSON.parseObject(arguments);
            }

            // 根据函数名分发执行
            return switch (functionName) {
                case "get_customer_count" -> getCustomerCount(allowedLevels);
                case "get_customer_list" -> getCustomerList(params, allowedLevels);
                case "get_file_count" -> getFileCount();
                case "get_file_list" -> getFileList(params);
                case "get_file_detail" -> getFileDetail(params);
                default -> {
                    log.warn("【Function Calling】未知函数: {}", functionName);
                    yield "{\"error\": \"未知函数: " + functionName + "\"}";
                }
            };
        } catch (Exception e) {
            log.error("【Function Calling】执行函数异常: {}", e.getMessage(), e);
            return "{\"error\": \"函数执行失败: " + e.getMessage() + "\"}";
        }
    }

    /**
     * 获取客户总数
     */
    private String getCustomerCount(List<Integer> allowedLevels) {
        try {
            String result = customerQueryService.getTotalCustomerCount(allowedLevels);
            return result; // 已经是格式化的字符串
        } catch (Exception e) {
            log.error("获取客户总数失败", e);
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * 获取客户列表
     */
    private String getCustomerList(JSONObject params, List<Integer> allowedLevels) {
        try {
            String region = params.getString("region");
            Integer level = params.getInteger("level");
            Integer limit = params.getInteger("limit");

            if (limit == null || limit <= 0) {
                limit = 10;
            } else if (limit > 50) {
                limit = 50;
            }

            // 构建SQL查询
            StringBuilder sql = new StringBuilder(
                    "SELECT customer_name, contact_person, phone, region, customer_level FROM customer WHERE 1=1");

            if (region != null && !region.trim().isEmpty()) {
                sql.append(" AND region = '").append(region.replace("'", "''")).append("'");
            }

            if (level != null) {
                sql.append(" AND customer_level = ").append(level);
            }

            // 权限过滤
            if (allowedLevels != null && !allowedLevels.isEmpty()) {
                sql.append(" AND customer_level IN (");
                for (int i = 0; i < allowedLevels.size(); i++) {
                    if (i > 0)
                        sql.append(",");
                    sql.append(allowedLevels.get(i));
                }
                sql.append(")");
            }

            sql.append(" LIMIT ").append(limit);

            return customerQueryService.executeDynamicQuery(sql.toString(), allowedLevels);
        } catch (Exception e) {
            log.error("获取客户列表失败", e);
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * 获取文件总数
     */
    private String getFileCount() {
        try {
            return knowledgeQueryService.getKnowledgeCount();
        } catch (Exception e) {
            log.error("获取文件总数失败", e);
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * 获取文件列表
     */
    private String getFileList(JSONObject params) {
        try {
            Integer limit = params.getInteger("limit");
            if (limit == null || limit <= 0) {
                limit = 20;
            } else if (limit > 100) {
                limit = 100;
            }

            return knowledgeQueryService.getKnowledgeList(limit);
        } catch (Exception e) {
            log.error("获取文件列表失败", e);
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * 获取文件详情
     */
    private String getFileDetail(JSONObject params) {
        try {
            String fileName = params.getString("fileName");
            if (fileName == null || fileName.trim().isEmpty()) {
                return "{\"error\": \"缺少必需参数: fileName\"}";
            }

            return knowledgeQueryService.getKnowledgeDetail(fileName);
        } catch (Exception e) {
            log.error("获取文件详情失败", e);
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * 获取所有可用函数定义（用于发送给AI）
     */
    public List<FunctionDefinition> getAvailableFunctions() {
        return FunctionDefinition.getAllFunctions();
    }
}
