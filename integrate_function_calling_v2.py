#!/usr/bin/env python3
"""
Function Calling集成脚本 V2
保留Doubao回退 + 保持原有权限逻辑
"""

import re

FILE_PATH = "/Users/zuozuo/Downloads/cxf/aicustomer/src/main/java/com/aicustomer/service/impl/AiChatServiceImpl.java"

# 新的generateAiResponse方法 - 保留Doubao回退
NEW_GENERATE_AI_RESPONSE = '''    /**
     * 使用Function Calling生成AI回复
     * 保留DeepSeek→Doubao回退机制
     */
    private String generateAiResponse(String userMessage, List<Map<String, String>> history) {
        log.info("【AI聊天服务 - Function Calling】开始处理查询: {}", userMessage);
        
        // 1. FAQ匹配（保留）
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
        messages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));
        
        if (history != null && !history.isEmpty()) {
            int startIdx = Math.max(0, history.size() - 10);
            for (int i = startIdx; i < history.size(); i++) {
                messages.add(history.get(i));
            }
        }
        
        messages.add(Map.of("role", "user", "content", userMessage));
        
        // 3. 获取函数列表
        List<com.aicustomer.model.FunctionDefinition> functions = functionCallingService.getAvailableFunctions();
        
        // 4. 尝试DeepSeek (Function Calling)
        if (deepSeekService.isAvailable()) {
            try {
                Map<String, Object> response = deepSeekService.generateResponseWithFunctions(messages, functions);
                
                if (!response.containsKey("error")) {
                    if (response.containsKey("tool_calls")) {
                        return handleFunctionCalls(response, messages, functions);
                    } else if (response.containsKey("content")) {
                        return (String) response.get("content");
                    }
                }
                log.warn("【AI聊天服务】DeepSeek失败: {}, 切换到Doubao", response.get("error"));
            } catch (Exception e) {
                log.warn("【AI聊天服务】DeepSeek异常: {}, 切换到Doubao", e.getMessage());
            }
        }
        
        // 5. 回退到Doubao (不支持Function Calling，使用传统方式)
        try {
            log.info("【AI聊天服务】使用Doubao模型");
            List<org.springframework.ai.chat.messages.Message> springMessages = new ArrayList<>();
            springMessages.add(new org.springframework.ai.chat.messages.SystemMessage(SYSTEM_PROMPT));
            
            if (history != null) {
                for (Map<String, String> h : history) {
                    if ("user".equals(h.get("role")))
                        springMessages.add(new org.springframework.ai.chat.messages.UserMessage(h.get("content")));
                    else
                        springMessages.add(new org.springframework.ai.chat.messages.AssistantMessage(h.get("content")));
                }
            }
            springMessages.add(new org.springframework.ai.chat.messages.UserMessage(userMessage));

            ChatResponse chatResponse = doubaoChatModel.call(new Prompt(springMessages));
            String aiReply = chatResponse.getResult().getOutput().getContent();
            
            if (aiReply != null && !aiReply.trim().isEmpty()) {
                return aiReply;
            }
        } catch (Exception e) {
            log.error("【AI聊天服务】Doubao也失败了", e);
        }
        
        return generateFallbackResponse(userMessage);
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
            
            // 执行所有函数调用（不涉及权限，由FunctionCallingService内部处理）
            for (Map<String, Object> toolCall : toolCalls) {
                Map<String, Object> function = (Map<String, Object>) toolCall.get("function");
                if (function == null) continue;
                
                String functionName = (String) function.get("name");
                String arguments = (String) function.getOrDefault("arguments", "{}");
                
                log.info("【AI聊天服务】执行函数: {}, 参数: {}", functionName, arguments);
                
                // 执行函数（传null让Service自己处理权限）
                String result = functionCallingService.executeFunction(functionName, arguments, null);
                
                Map<String, String> functionMsg = new HashMap<>();
                functionMsg.put("role", "function");
                functionMsg.put("name", functionName);
                functionMsg.put("content", result);
                messages.add(functionMsg);
            }
            
            // AI整合结果
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
'''

# 简化的SYSTEM_PROMPT
NEW_SYSTEM_PROMPT = '''    // 系统提示词 - Function Calling版本
    private static final String SYSTEM_PROMPT = 
            "你是一个专业的AI客户管理助手。\\n\\n" +
            "你可以帮助用户：\\n" +
            "1. 查询客户信息（数量、列表、详情等）\\n" +
            "2. 查询知识库文件（数量、列表、内容等）\\n" +
            "3. 回答常见问题\\n" +
            "4. 提供业务咨询\\n\\n" +
            "当需要查询实时数据时，请调用提供的函数。\\n" +
            "请用友好、专业的语气回复用户。";
'''

def main():
    print("读取文件...")
    with open(FILE_PATH, 'r', encoding='utf-8') as f:
        content = f.read()
    
    print("1. 替换SYSTEM_PROMPT...")
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
    method_start = "private String generateAiResponse(String userMessage, List<Map<String, String>> history) {"
    method_start_idx = content.find(method_start)
    
    if method_start_idx == -1:
        print("  ✗ 未找到generateAiResponse方法")
        return
    
    next_method = "private String generateFallbackResponse"
    next_method_idx = content.find(next_method, method_start_idx)
    
    if next_method_idx == -1:
        print("  ✗ 未找到方法结束位置")
        return
    
    method_end_idx = content.rfind('\n', method_start_idx, next_method_idx)
    
    indent = "    "
    content = content[:method_start_idx] + NEW_GENERATE_AI_RESPONSE.strip() + "\n\n" + indent + content[next_method_idx:]
    print("  ✓ generateAiResponse方法已替换")
    print("  ✓ handleFunctionCalls方法已添加")
    print("  ✓ 保留了Doubao回退逻辑")
    
    print("3. 保存文件...")
    with open(FILE_PATH, 'w', encoding='utf-8') as f:
        f.write(content)
    
    print("\n✅ 所有修改完成！")
    print("   - SYSTEM_PROMPT简化")
    print("   - generateAiResponse使用Function Calling")
    print("   - DeepSeek失败自动切换Doubao")
    print("   - 权限逻辑保持不变")
    print("\n请重启服务测试。")

if __name__ == "__main__":
    main()
