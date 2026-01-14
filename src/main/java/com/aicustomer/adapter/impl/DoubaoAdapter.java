package com.aicustomer.adapter.impl;

import com.aicustomer.adapter.AiModelAdapter;
import com.aicustomer.model.FunctionCallRequest;
import com.aicustomer.model.FunctionCallResponse;
import com.aicustomer.model.FunctionDefinition;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Doubao适配器
 * 
 * 模拟Function Calling：通过在SYSTEM_PROMPT中说明工具，让AI返回JSON格式的工具调用
 * 
 * @author AI Customer Management System
 * @version 2.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DoubaoAdapter implements AiModelAdapter {

    @Qualifier("doubaoChatModel")
    private final ChatModel doubaoChatModel;

    @Override
    public FunctionCallResponse chat(FunctionCallRequest request) {
        try {
            // 1. 构建带工具说明的SYSTEM_PROMPT
            String enhancedSystemPrompt = buildToolPrompt(request);

            // 2. 构建消息
            List<org.springframework.ai.chat.messages.Message> messages = buildMessages(
                    request,
                    enhancedSystemPrompt);

            // 3. 调用Doubao
            ChatResponse chatResponse = doubaoChatModel.call(new Prompt(messages));
            String aiReply = chatResponse.getResult().getOutput().getContent();

            // 检查是否为空
            if (aiReply == null || aiReply.trim().isEmpty()) {
                return FunctionCallResponse.builder()
                        .error("Doubao返回空回复")
                        .modelName("Doubao")
                        .build();
            }

            // ⚠️ 检查是否为错误消息（Ark服务返回的错误）
            if (aiReply.startsWith("⚠️")) {
                log.warn("Doubao返回错误消息: {}", aiReply);
                return FunctionCallResponse.builder()
                        .error(aiReply)
                        .modelName("Doubao")
                        .build();
            }

            // 4. 检查是否包含JSON工具调用
            if (aiReply.contains("{") && aiReply.contains("}")) {
                List<FunctionCallResponse.ToolCall> toolCalls = parseToolCallsFromText(aiReply);
                if (!toolCalls.isEmpty()) {
                    log.info("Doubao返回了{}个工具调用", toolCalls.size());
                    return FunctionCallResponse.builder()
                            .toolCalls(toolCalls)
                            .modelName("Doubao")
                            .build();
                }
            }

            // 5. 直接回复
            return FunctionCallResponse.builder()
                    .content(aiReply)
                    .modelName("Doubao")
                    .build();

        } catch (Exception e) {
            log.error("Doubao适配器调用失败", e);
            return FunctionCallResponse.builder()
                    .error("Doubao调用异常: " + e.getMessage())
                    .modelName("Doubao")
                    .build();
        }
    }

    /**
     * 构建带工具说明的SYSTEM_PROMPT
     * 
     * 在原有的系统提示词基础上，添加函数工具的说明
     * 这样AI就知道可以通过返回JSON来调用工具
     */
    private String buildToolPrompt(FunctionCallRequest request) {
        StringBuilder prompt = new StringBuilder(request.getSystemPrompt());

        if (request.getFunctions() != null && !request.getFunctions().isEmpty()) {
            prompt.append("\n\n### 可用工具函数 ###\n");
            prompt.append("你可以通过返回JSON格式来调用以下工具：\n\n");

            for (FunctionDefinition func : request.getFunctions()) {
                if (func.getFunction() == null)
                    continue;

                FunctionDefinition.Function f = func.getFunction();
                prompt.append("**").append(f.getName()).append("**\n");
                prompt.append("- 描述: ").append(f.getDescription()).append("\n");
                if (f.getParameters() != null && f.getParameters().getProperties() != null) {
                    prompt.append("- 参数: ").append(f.getParameters().getProperties().keySet()).append("\n");
                }
                prompt.append("\n");
            }

            prompt.append("调用格式：\n");
            prompt.append("{\"tool\": \"函数名\", \"parameters\": {参数对象}}\n\n");
        }

        return prompt.toString();
    }

    /**
     * 构建Spring AI消息列表
     */
    private List<org.springframework.ai.chat.messages.Message> buildMessages(
            FunctionCallRequest request,
            String systemPrompt) {

        List<org.springframework.ai.chat.messages.Message> messages = new ArrayList<>();

        // 系统消息
        messages.add(new org.springframework.ai.chat.messages.SystemMessage(systemPrompt));

        // 历史消息
        if (request.getHistory() != null) {
            for (Map<String, String> h : request.getHistory()) {
                if ("user".equals(h.get("role"))) {
                    messages.add(new org.springframework.ai.chat.messages.UserMessage(h.get("content")));
                } else {
                    messages.add(new org.springframework.ai.chat.messages.AssistantMessage(h.get("content")));
                }
            }
        }

        // 用户消息
        messages.add(new org.springframework.ai.chat.messages.UserMessage(request.getUserMessage()));

        return messages;
    }

    /**
     * 从AI的文本回复中解析工具调用
     * 支持多种格式：
     * 1. JSON格式: {"tool": "get_file_list", "parameters": {"limit": 20}}
     * 2. 简化格式: get_file_list {"limit": 20}
     * 3. 纯函数名: get_file_list
     */
    private List<FunctionCallResponse.ToolCall> parseToolCallsFromText(String aiReply) {
        List<FunctionCallResponse.ToolCall> toolCalls = new ArrayList<>();

        try {
            // 定义已知的工具函数名
            String[] knownTools = { "get_customer_count", "get_customer_list",
                    "get_file_count", "get_file_list", "get_file_detail" };

            // 方式1: 检查简化格式 (如: get_file_list {"limit": 20})
            for (String toolName : knownTools) {
                if (aiReply.contains(toolName)) {
                    log.info("检测到工具调用: {}", toolName);

                    // 尝试提取参数
                    String arguments = "{}";
                    int toolIndex = aiReply.indexOf(toolName);
                    int braceStart = aiReply.indexOf("{", toolIndex + toolName.length());
                    if (braceStart != -1) {
                        int braceEnd = findMatchingBrace(aiReply, braceStart);
                        if (braceEnd > braceStart) {
                            arguments = aiReply.substring(braceStart, braceEnd + 1);
                            // 验证是否为有效JSON
                            try {
                                JSON.parseObject(arguments);
                            } catch (Exception e) {
                                arguments = "{}";
                            }
                        }
                    }

                    toolCalls.add(FunctionCallResponse.ToolCall.builder()
                            .functionName(toolName)
                            .arguments(arguments)
                            .build());
                    return toolCalls; // 找到一个就返回
                }
            }

            // 方式2: 尝试解析标准JSON格式 {"tool": "xxx", "parameters": {...}}
            int braceStart = aiReply.indexOf("{");
            if (braceStart != -1) {
                int braceEnd = findMatchingBrace(aiReply, braceStart);
                if (braceEnd > braceStart) {
                    String jsonStr = aiReply.substring(braceStart, braceEnd + 1);
                    JSONObject json = JSON.parseObject(jsonStr);

                    String tool = json.getString("tool");
                    if (tool == null) {
                        tool = json.getString("name");
                    }

                    if (tool != null) {
                        JSONObject params = json.getJSONObject("parameters");
                        String arguments = params != null ? params.toJSONString() : "{}";

                        toolCalls.add(FunctionCallResponse.ToolCall.builder()
                                .functionName(tool)
                                .arguments(arguments)
                                .build());
                    }
                }
            }

        } catch (Exception e) {
            log.warn("解析工具调用失败: {}", e.getMessage());
        }

        return toolCalls;
    }

    /**
     * 找到匹配的右大括号
     */
    private int findMatchingBrace(String text, int start) {
        int count = 0;
        for (int i = start; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '{')
                count++;
            else if (c == '}')
                count--;
            if (count == 0)
                return i;
        }
        return -1;
    }

    @Override
    public boolean isAvailable() {
        return doubaoChatModel != null;
    }

    @Override
    public String getName() {
        return "Doubao";
    }

    @Override
    public int getPriority() {
        return 2; // 第二优先级
    }
}
