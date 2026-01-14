#!/usr/bin/env python3
"""
Function Calling集成脚本
一次性完成所有代码修改
"""

import re

FILE_PATH = "/Users/zuozuo/Downloads/cxf/aicustomer/src/main/java/com/aicustomer/service/impl/AiChatServiceImpl.java"

# 1. 简化的SYSTEM_PROMPT
NEW_SYSTEM_PROMPT = '''    // 系统提示词 - Function Calling版本（大幅简化）
    private static final String SYSTEM_PROMPT = 
            "你是一个专业的AI客户管理助手。\\n\\n" +
            "你可以帮助用户：\\n" +
            "1. 查询客户信息（客户数量、客户列表、客户详情等）\\n" +
            "2. 查询知识库文件（文件数量、文件列表、文件内容等）\\n" +
            "3. 回答常见问题\\n" +
            "4. 提供业务咨询\\n\\n" +
            "当需要查询实时数据时，请调用提供的函数。\\n" +
            "请用友好、专业的语气回复用户。";
'''

# 2. 新的generateAiResponse方法
NEW_GENERATE_AI_RESPONSE = '''    /**
     * 使用Function Calling生成AI回复
     * 删除了所有关键词匹配，由AI自主理解和决策
     */
    private String generateAiResponse(String userMessage, List<Map<String, String>> history) {
        log.info("【AI聊天服务 - Function Calling】开始处理查询: {}", userMessage);
        
        // 1. FAQ匹配（保留，作为快速响应层）
        try {
            List<FaqQa> faqs = faqQaService.searchFaq(userMessage, 1);
            if (faqs != null && !faqs.isEmpty()) {
                FaqQa faq = faqs.get(0);
                log.info("【AI聊天服务】命中FAQ: {}", faq.getQuestion());
                faqQaService.incrementHitCount(faq.getId());
                return faq.getAnswer() + "\\n\\n(来源: 常见问题库)";
            }
        } catch (Exception e) {
            log.error("【AI聊天服务】FAQ匹配异常: {}", e.getMessage());
        }
        
        // 2. 构建消息列表
        List<Map<String, String>> messages = new ArrayList<>();
        
        // 系统提示词
        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", SYSTEM_PROMPT);
        messages.add(systemMsg);
        
        // 添加历史对话
        if (history != null && !history.isEmpty()) {
            int startIdx = Math.max(0, history.size() - 10); // 最近10轮
            for (int i = startIdx; i < history.size(); i++) {
                messages.add(history.get(i));
            }
        }
        
        // 添加当前用户消息
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);
        
        // 3. 获取可用函数列表
        List<com.aicustomer.model.FunctionDefinition> functions = functionCallingService.getAvailableFunctions();
        
        // 4. 调用AI（支持Function Calling）
        try {
            Map<String, Object> response = deepSeekService.generateResponseWithFunctions(messages, functions);
            
            if (response.containsKey("error")) {
                log.error("【AI聊天服务】Function Calling失败: {}", response.get("error"));
                return "抱歉，AI服务暂时不可用: " + response.get("error");
            }
            
            // 5. 处理响应
            if (response.containsKey("tool_calls")) {
                // AI请求调用函数
                return handleFunctionCalls(response, messages, functions);
            } else if (response.containsKey("content")) {
                // AI直接回复
                return (String) response.get("content");
            } else {
                log.warn("【AI聊天服务】AI返回空响应");
                return "抱歉，AI没有返回有效响应。";
            }
            
        } catch (Exception e) {
            log.error("【AI聊天服务】Function Calling调用异常", e);
            return generateFallbackResponse(userMessage);
        }
    }
    
    /**
     * 处理AI的函数调用请求
     */
    @SuppressWarnings("unchecked")
    private String handleFunctionCalls(
            Map<String, Object> response,
            List<Map<String, String>> messages,
            List<com.aicustomer.model.FunctionDefinition> functions) {
        
        try {
            List<Map<String, Object>> toolCalls = (List<Map<String, Object>>) response.get("tool_calls");
            
            if (toolCalls == null || toolCalls.isEmpty()) {
                return (String) response.getOrDefault("content", "抱歉，无法处理您的请求。");
            }
            
            // 获取当前用户的权限等级
            List<Integer> allowedLevels = getCurrentUserAllowedLevels();
            
            // 执行所有函数调用
            for (Map<String, Object> toolCall : toolCalls) {
                Map<String, Object> function = (Map<String, Object>) toolCall.get("function");
                if (function == null) continue;
                
                String functionName = (String) function.get("name");
                String arguments = (String) function.getOrDefault("arguments", "{}");
                
                log.info("【AI聊天服务】执行函数: {}, 参数: {}", functionName, arguments);
                
                // 执行函数
                String result = functionCallingService.executeFunction(functionName, arguments, allowedLevels);
                
                // 将函数结果添加到消息列表
                Map<String, String> functionMsg = new HashMap<>();
                functionMsg.put("role", "function");
                functionMsg.put("name", functionName);
                functionMsg.put("content", result);
                messages.add(functionMsg);
                
                log.info("【AI聊天服务】函数执行完成，结果长度: {}", result.length());
            }
            
            // 让AI整合函数结果，生成最终回复
            Map<String, Object> finalResponse = deepSeekService.generateResponseWithFunctions(messages, functions);
            
            if (finalResponse.containsKey("content")) {
                return (String) finalResponse.get("content");
            } else {
                return "抱歉，无法生成回复。";
            }
            
        } catch (Exception e) {
            log.error("【AI聊天服务】处理函数调用异常", e);
            return "抱歉，处理您的请求时出现错误: " + e.getMessage();
        }
    }
    
    /**
     * 获取当前用户允许查询的客户等级
     */
    private List<Integer> getCurrentUserAllowedLevels() {
        try {
            org.springframework.security.core.Authentication auth = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            
            if (auth == null || !auth.isAuthenticated()) {
                return java.util.Collections.emptyList();
            }
            
            String username;
            Object principal = auth.getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }
            
            // 管理员看所有
            if ("admin".equals(username)) {
                return null; // null表示无限制
            }
            
            com.aicustomer.entity.User user = userService.findByUsername(username);
            if (user == null) {
                return java.util.Collections.emptyList();
            }
            
            // 管理员类型
            if (user.getUserType() != null && user.getUserType() == 1) {
                return null;
            }
            
            // 普通用户只能看普通客户
            return java.util.Collections.singletonList(1);
            
        } catch (Exception e) {
            log.warn("【AI聊天服务】获取用户权限失败", e);
            return java.util.Collections.emptyList();
        }
    }
'''

def main():
    print("读取文件...")
    with open(FILE_PATH, 'r', encoding='utf-8') as f:
        content = f.read()
    
    print("1. 替换SYSTEM_PROMPT...")
    # 找到SYSTEM_PROMPT的开始和结束
    start_marker = "// 系统提示词，定义AI助手的角色和行为"
    end_marker = '            "10. 只有当工具返回数据后，才整合为自然语言回复。";'
    
    start_idx = content.find(start_marker)
    end_idx = content.find(end_marker)
    
    if start_idx != -1 and end_idx != -1:
        end_idx = end_idx + len(end_marker)
        content = content[:start_idx] + NEW_SYSTEM_PROMPT + content[end_idx:]
        print("  ✓ SYSTEM_PROMPT已替换")
    else:
        print("  ✗ 未找到SYSTEM_PROMPT")
        return
    
    print("2. 替换generateAiResponse方法...")
    # 找到方法开始
    method_start = "private String generateAiResponse(String userMessage, List<Map<String, String>> history) {"
    method_start_idx = content.find(method_start)
    
    if method_start_idx == -1:
        print("  ✗ 未找到generateAiResponse方法")
        return
    
    # 找到方法结束（通过找到下一个方法定义）
    next_method = "private String generateFallbackResponse"
    next_method_idx = content.find(next_method, method_start_idx)
    
    if next_method_idx == -1:
        print("  ✗ 未找到方法结束位置")
        return
    
    # 回退到上一行的结束位置
    method_end_idx = content.rfind('\n', method_start_idx, next_method_idx)
    
    # 替换整个方法
    indent = "    "
    content = content[:method_start_idx] + NEW_GENERATE_AI_RESPONSE.strip() + "\n\n" + indent + content[next_method_idx:]
    print("  ✓ generateAiResponse方法已替换")
    print("  ✓ 添加了handleFunctionCalls方法")
    print("  ✓ 添加了getCurrentUserAllowedLevels方法")
    
    print("3. 保存文件...")
    with open(FILE_PATH, 'w', encoding='utf-8') as f:
        f.write(content)
    
    print("\n✅ 所有修改完成！")
    print("   - SYSTEM_PROMPT: 从50+行简化到10行")
    print("   - generateAiResponse: 从300+行简化到70行 + 使用Function Calling")
    print("   - 新增2个辅助方法")
    print("\n请重启服务测试。")

if __name__ == "__main__":
    main()
