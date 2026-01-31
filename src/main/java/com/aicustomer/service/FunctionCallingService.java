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
    private final WebSearchService webSearchService;

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
                // search_web 已移除 - 改为fallback机制
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
            Integer limit = params.getInteger("limit");

            // 构建查询实体
            com.aicustomer.entity.Customer criteria = new com.aicustomer.entity.Customer();

            String region = params.getString("region");
            if (region != null && !region.trim().isEmpty()) {
                criteria.setRegion(region);
            }

            Integer level = params.getInteger("level");
            if (level != null) {
                criteria.setCustomerLevel(level);
            }

            int limitVal = (limit != null && limit > 0) ? limit : 10;

            return customerQueryService.queryCustomers(criteria, limitVal, allowedLevels);
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
     * Web搜索
     */
    private String searchWeb(JSONObject params) {
        try {
            String query = params.getString("query");
            if (query == null || query.trim().isEmpty()) {
                return "{\"error\": \"缺少必需参数: query\"}";
            }

            return webSearchService.searchAndFormat(query);
        } catch (Exception e) {
            log.error("Web搜索失败", e);
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
