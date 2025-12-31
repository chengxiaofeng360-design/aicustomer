package com.aicustomer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 系统菜单定义
 * 用于权限配置时的菜单选择
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuDefinition {

    /**
     * 菜单ID
     */
    private String id;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 父菜单ID
     */
    private String parentId;

    /**
     * 子菜单
     */
    private List<MenuDefinition> children;

    /**
     * 是否默认选中（用于角色模板）
     */
    private boolean defaultSelected;

    /**
     * 获取系统所有菜单定义
     */
    public static List<MenuDefinition> getAllMenus() {
        List<MenuDefinition> menus = new ArrayList<>();

        // 1. 客户业务管理 (ID: customer)
        MenuDefinition customerMenu = new MenuDefinition("customer", "客户业务管理", "👥", null, new ArrayList<>(), true);
        customerMenu.getChildren().add(new MenuDefinition("customer-list", "客户列表", "📋", "customer", null, true));
        customerMenu.getChildren()
                .add(new MenuDefinition("customer-categories", "业务分类", "🗂️", "customer", null, true));
        customerMenu.getChildren().add(new MenuDefinition("customer-import", "数据导入", "📥", "customer", null, false));
        customerMenu.getChildren().add(new MenuDefinition("customer-export", "数据导出", "📤", "customer", null, false));
        menus.add(customerMenu);

        // 2. 消息中心 (ID: message) - 对应首页与消息
        menus.add(new MenuDefinition("message", "消息中心", "📬", null, null, true));

        // 3. 团队协作 (ID: team)
        MenuDefinition teamMenu = new MenuDefinition("team", "团队协作", "👨‍👩‍👧‍👦", null, new ArrayList<>(), true);
        teamMenu.getChildren().add(new MenuDefinition("team-task", "任务管理", "✅", "team", null, true));
        teamMenu.getChildren().add(new MenuDefinition("team-report", "进度汇报", "📈", "team", null, true));
        menus.add(teamMenu);

        // 4. AI智能分析 (ID: ai-analysis)
        menus.add(new MenuDefinition("ai-analysis", "AI智能分析", "📊", null, null, true));

        // 5. AI智能推荐 (ID: ai-recommendations)
        menus.add(new MenuDefinition("ai-recommendations", "AI智能推荐", "💡", null, null, true));

        // 6. 知识库管理 (ID: knowledge)
        MenuDefinition knowledgeMenu = new MenuDefinition("knowledge", "知识库管理", "📚", null, new ArrayList<>(), true);
        knowledgeMenu.getChildren().add(new MenuDefinition("knowledge-doc", "文档管理", "📄", "knowledge", null, true));
        knowledgeMenu.getChildren().add(new MenuDefinition("knowledge-faq", "常见问题", "❓", "knowledge", null, true));
        menus.add(knowledgeMenu);

        // 7. AI智能聊天 (ID: ai-chat)
        menus.add(new MenuDefinition("ai-chat", "AI智能聊天", "💭", null, null, true));

        // 8. 系统配置管理 (ID: system)
        MenuDefinition systemMenu = new MenuDefinition("system", "系统配置管理", "⚙️", null, new ArrayList<>(), false);
        systemMenu.getChildren().add(new MenuDefinition("system-user", "账户管理", "👤", "system", null, false));
        systemMenu.getChildren().add(new MenuDefinition("system-config", "基础配置", "🔧", "system", null, false));
        menus.add(systemMenu);

        return menus;
    }

    /**
     * 根据角色模板获取默认菜单权限
     */
    public static List<String> getMenuIdsByRoleTemplate(String roleTemplate) {
        List<String> menuIds = new ArrayList<>();

        switch (roleTemplate) {
            case "admin":
                // 管理员：所有菜单
                return getAllMenuIds();

            case "manager":
                // 经理：大部分功能，除了系统管理核心
                menuIds.addAll(Arrays.asList(
                        "customer", "customer-list", "customer-categories", "customer-import", "customer-export",
                        "message",
                        "team", "team-task", "team-report",
                        "ai-analysis",
                        "ai-recommendations",
                        "knowledge", "knowledge-doc", "knowledge-faq",
                        "ai-chat"));
                break;

            case "employee":
                // 普通员工：常用业务功能
                menuIds.addAll(Arrays.asList(
                        "customer", "customer-list", "customer-categories",
                        "message",
                        "team", "team-task",
                        "knowledge", "knowledge-doc",
                        "ai-chat"));
                break;

            case "readonly":
                // 只读用户：只能查看
                menuIds.addAll(Arrays.asList(
                        "customer", "customer-list",
                        "message",
                        "knowledge", "knowledge-doc", "knowledge-faq"));
                break;

            default:
                break;
        }

        return menuIds;
    }

    /**
     * 获取所有菜单ID
     */
    private static List<String> getAllMenuIds() {
        List<String> ids = new ArrayList<>();
        for (MenuDefinition menu : getAllMenus()) {
            ids.add(menu.getId());
            if (menu.getChildren() != null) {
                for (MenuDefinition child : menu.getChildren()) {
                    ids.add(child.getId());
                }
            }
        }
        return ids;
    }
}
