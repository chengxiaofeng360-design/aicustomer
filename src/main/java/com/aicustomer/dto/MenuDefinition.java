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

        // 首页
        menus.add(new MenuDefinition("home", "首页", "🏠", null, null, true));

        // 客户管理
        MenuDefinition customerMenu = new MenuDefinition("customer", "客户管理", "👥", null, new ArrayList<>(), true);
        customerMenu.getChildren().add(new MenuDefinition("customer-list", "客户列表", "📋", "customer", null, true));
        customerMenu.getChildren().add(new MenuDefinition("customer-add", "新增客户", "➕", "customer", null, true));
        customerMenu.getChildren().add(new MenuDefinition("customer-import", "批量导入", "📥", "customer", null, false));
        customerMenu.getChildren().add(new MenuDefinition("customer-export", "数据导出", "📤", "customer", null, false));
        menus.add(customerMenu);

        // 沟通记录
        MenuDefinition communicationMenu = new MenuDefinition("communication", "沟通记录", "💬", null, new ArrayList<>(),
                true);
        communicationMenu.getChildren()
                .add(new MenuDefinition("communication-list", "记录列表", "📝", "communication", null, true));
        communicationMenu.getChildren()
                .add(new MenuDefinition("communication-add", "新增记录", "✍️", "communication", null, true));
        menus.add(communicationMenu);

        // AI功能
        MenuDefinition aiMenu = new MenuDefinition("ai", "AI功能", "🤖", null, new ArrayList<>(), true);
        aiMenu.getChildren().add(new MenuDefinition("ai-chat", "AI对话", "💭", "ai", null, true));
        aiMenu.getChildren().add(new MenuDefinition("ai-analysis", "智能分析", "📊", "ai", null, false));
        menus.add(aiMenu);

        // 团队协作
        MenuDefinition teamMenu = new MenuDefinition("team", "团队协作", "👨‍👩‍👧‍👦", null, new ArrayList<>(), true);
        teamMenu.getChildren().add(new MenuDefinition("team-task", "任务管理", "✅", "team", null, true));
        teamMenu.getChildren().add(new MenuDefinition("team-report", "进度汇报", "📈", "team", null, true));
        menus.add(teamMenu);

        // 知识库
        MenuDefinition knowledgeMenu = new MenuDefinition("knowledge", "知识库", "📚", null, new ArrayList<>(), true);
        knowledgeMenu.getChildren().add(new MenuDefinition("knowledge-doc", "文档管理", "📄", "knowledge", null, true));
        knowledgeMenu.getChildren().add(new MenuDefinition("knowledge-faq", "常见问题", "❓", "knowledge", null, true));
        menus.add(knowledgeMenu);

        // 消息中心
        menus.add(new MenuDefinition("message", "消息中心", "📬", null, null, true));

        // 数据报表
        MenuDefinition reportMenu = new MenuDefinition("report", "数据报表", "📊", null, new ArrayList<>(), false);
        reportMenu.getChildren().add(new MenuDefinition("report-customer", "客户统计", "📈", "report", null, false));
        reportMenu.getChildren().add(new MenuDefinition("report-business", "业务分析", "💼", "report", null, false));
        menus.add(reportMenu);

        // 系统管理（仅管理员）
        MenuDefinition systemMenu = new MenuDefinition("system", "系统管理", "⚙️", null, new ArrayList<>(), false);
        systemMenu.getChildren().add(new MenuDefinition("system-user", "用户管理", "👤", "system", null, false));
        systemMenu.getChildren().add(new MenuDefinition("system-config", "系统配置", "🔧", "system", null, false));
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
                // 经理：除了系统管理的所有菜单
                menuIds.addAll(Arrays.asList(
                        "home", "customer", "customer-list", "customer-add", "customer-import", "customer-export",
                        "communication", "communication-list", "communication-add",
                        "ai", "ai-chat", "ai-analysis",
                        "team", "team-task", "team-report",
                        "knowledge", "knowledge-doc", "knowledge-faq",
                        "message",
                        "report", "report-customer", "report-business"));
                break;

            case "employee":
                // 普通员工：基础功能
                menuIds.addAll(Arrays.asList(
                        "home", "customer", "customer-list", "customer-add",
                        "communication", "communication-list", "communication-add",
                        "ai", "ai-chat",
                        "team", "team-task", "team-report",
                        "knowledge", "knowledge-doc", "knowledge-faq",
                        "message"));
                break;

            case "readonly":
                // 只读用户：只能查看
                menuIds.addAll(Arrays.asList(
                        "home", "customer", "customer-list",
                        "communication", "communication-list",
                        "knowledge", "knowledge-doc", "knowledge-faq",
                        "message"));
                break;

            default:
                // 自定义：返回空，需要手动选择
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
