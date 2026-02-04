package com.aicustomer.dify.controller;

import com.aicustomer.config.DifyConfig;
import com.aicustomer.entity.User;
import com.aicustomer.service.CustomerQueryService;
import com.aicustomer.service.KnowledgeQueryService;
import com.aicustomer.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * AI 工具控制器 (供 Dify 回调使用)
 * 允许 Dify 通过 HTTP 请求调用本系统的内部查询能力
 */
@Slf4j
@RestController
@RequestMapping("/api/dify/tools")
@RequiredArgsConstructor
public class AiToolController {

    private final CustomerQueryService customerQueryService;
    private final KnowledgeQueryService knowledgeQueryService;
    private final com.aicustomer.service.FaqQaService faqQaService;
    private final UserService userService;
    private final DifyConfig difyConfig;

    /**
     * 获取当前用户权限等级
     * 逻辑：Admin/UserType1 -> null (全部)，其他 -> [1] (普通)
     */
    private List<Integer> getUserPermission(String userIdentifier) {
        if (userIdentifier == null || userIdentifier.isEmpty()) {
            throw new RuntimeException("User verification failed: Missing user identifier");
        }

        // 1. 尝试按用户名查找
        User user = userService.findByUsername(userIdentifier);

        // 2. 如果没找到且是数字，尝试按ID查找
        if (user == null && userIdentifier.matches("\\d+")) {
            user = userService.getById(Long.valueOf(userIdentifier));
        }

        if (user == null) {
            throw new RuntimeException("User verification failed: User not found (" + userIdentifier + ")");
        }

        // Admin 用户名检查
        if ("admin".equals(user.getUsername())) {
            return null;
        }

        // 管理员类型看所有
        if (user.getUserType() != null && user.getUserType() == 1) {
            return null;
        }

        // 普通用户/业务员看普通客户(等级1)
        return Collections.singletonList(1);
    }

    /**
     * 获取客户总数
     */
    @PostMapping("/customer/count")
    public String getCustomerCount(@RequestBody Map<String, Object> body) {
        log.info("🔔 Dify Tool Call: getCustomerCount - Body: {}", body);

        // 从 body 中获取 user_id，如果为空则默认为 "admin" (紧急兼容方案)
        String userId = body != null ? String.valueOf(body.get("user_id")) : null;
        if (userId == null || "null".equals(userId) || userId.trim().isEmpty()) {
            log.warn("⚠️ Security Alert: Tool call missing user identity! Using DEFAULT 'admin' permission.");
            userId = "admin";
        }

        log.info("✅ User ID: {}", userId);
        return customerQueryService.getTotalCustomerCount(getUserPermission(userId));
    }

    /**
     * 获取客户列表 (支持多条件筛选)
     */
    @PostMapping("/customer/list")
    public String getCustomerList(@RequestBody Map<String, Object> body) {
        log.info("🔔 Dify Tool Call: getCustomerList - Body: {}", body);

        // 从 body 中获取 user_id，如果为空则默认为 "admin" (紧急兼容方案)
        String userId = body != null ? String.valueOf(body.get("user_id")) : null;
        if (userId == null || "null".equals(userId) || userId.trim().isEmpty()) {
            log.warn("⚠️ Security Alert: Tool call missing user identity! Using DEFAULT 'admin' permission.");
            userId = "admin";
        }

        // body 可能为空，需要处理
        if (body == null) {
            body = new java.util.HashMap<>();
        }

        // 构建查询实体
        com.aicustomer.entity.Customer params = new com.aicustomer.entity.Customer();

        String region = (String) body.get("region");
        if (region != null && !region.isEmpty()) {
            params.setRegion(region);
        }

        if (body.get("level") != null) {
            try {
                params.setCustomerLevel(Integer.parseInt(body.get("level").toString()));
            } catch (Exception e) {
            }
        }

        Integer limit = 20;
        if (body.get("limit") != null) {
            try {
                limit = Integer.parseInt(body.get("limit").toString());
            } catch (Exception e) {
            }
        }

        return customerQueryService.queryCustomers(params, limit, getUserPermission(userId));
    }

    /**
     * 获取客户详情
     */
    @PostMapping("/customer/detail")
    public String getCustomerDetail(@RequestBody Map<String, Object> body) {
        log.info("🔔 Dify Tool Call: getCustomerDetail - Body: {}", body);

        // 从 body 中获取 user_id，如果为空则默认为 "admin" (紧急兼容方案)
        String userId = body != null ? String.valueOf(body.get("user_id")) : null;
        if (userId == null || "null".equals(userId) || userId.trim().isEmpty()) {
            log.warn("⚠️ Security Alert: Tool call missing user identity! Using DEFAULT 'admin' permission.");
            userId = "admin";
        }
        if (body == null) {
            return "Error: Missing request body with customer name.";
        }
        String name = (String) body.get("name");
        return customerQueryService.getCustomerDetail(name, getUserPermission(userId));
    }

    // 注意：dynamicQuery 已移除，不再支持任意 SQL 执行

    // --- 知识库查询接口 ---

    /**
     * 知识库统计
     */
    @PostMapping("/knowledge/count")
    public String getKnowledgeCount() {

        return knowledgeQueryService.getKnowledgeCount();
    }

    /**
     * 知识库列表
     */
    @PostMapping("/knowledge/list")
    public String getKnowledgeList(@RequestBody Map<String, Object> body) {

        Integer limit = 20;
        if (body.get("limit") != null) {
            try {
                limit = Integer.parseInt(body.get("limit").toString());
            } catch (Exception e) {
            }
        }
        if (limit > 100)
            limit = 100;

        return knowledgeQueryService.getKnowledgeList(limit);
    }

    /**
     * 知识库详情
     */
    @PostMapping("/knowledge/detail")
    public String getKnowledgeDetail(@RequestBody Map<String, Object> body) {

        String fileName = (String) body.get("fileName");
        if (fileName == null)
            return "Missing fileName argument";
        return knowledgeQueryService.getKnowledgeDetail(fileName);
    }

    /**
     * FAQ 搜索 (优先匹配官方问题库)
     */
    @PostMapping("/faq/search")
    public String searchFaq(@RequestBody Map<String, Object> body) {

        String query = (String) body.get("query");
        if (query == null || query.trim().isEmpty()) {
            return "Missing query argument";
        }

        log.info("🔔 Dify Tool Call: searchFaq - Query: {}", query);
        List<com.aicustomer.entity.FaqQa> results = faqQaService.searchFaq(query, 5);

        if (results == null || results.isEmpty()) {
            return "在 FAQ 库中未找到与 \"" + query + "\" 相关的标准问题。";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("在 FAQ 库中为您找到以下相关问答：\n\n");
        for (int i = 0; i < results.size(); i++) {
            com.aicustomer.entity.FaqQa faq = results.get(i);
            sb.append(i + 1).append(". **问题**：").append(faq.getQuestion()).append("\n");
            sb.append("   **回答**：").append(faq.getAnswer()).append("\n\n");
        }
        return sb.toString();
    }
}
