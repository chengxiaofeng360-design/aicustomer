package com.aicustomer.adapter.impl;

import com.aicustomer.adapter.AiModelAdapter;
import com.aicustomer.model.FunctionCallRequest;
import com.aicustomer.model.FunctionCallResponse;
import com.aicustomer.model.FunctionDefinition;
import com.aicustomer.service.ZhipuChatService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Zhipu适配器
 * 
 * 模拟Function Calling：通过在SYSTEM_PROMPT中说明工具，让AI返回JSON格式的工具调用
 * 实现方式与Doubao类似
 * 
 * @author AI Customer Management System
 * @version 2.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ZhipuAdapter implements AiModelAdapter {

    private final ZhipuChatService zhipuChatService;

    @Override
    public FunctionCallResponse chat(FunctionCallRequest request) {
        try {
            // 1. 构建带工具说明的SYSTEM_PROMPT
            String enhancedSystemPrompt = buildToolPrompt(request);

            // 2. 调用Zhipu（注意：不支持历史对话）
            String aiReply = zhipuChatService.chat(request.getUserMessage(), enhancedSystemPrompt);

            if (aiReply == null || aiReply.trim().isEmpty()) {
                return FunctionCallResponse.builder()
                        .error("Zhipu返回空回复")
                        .modelName("Zhipu")
                        .build();
            }

            // 3. 检查是否包含JSON工具调用
            if (aiReply.contains("{") && aiReply.contains("}")) {
                List<FunctionCallResponse.ToolCall> toolCalls = parseToolCallsFromText(aiReply);
                if (!toolCalls.isEmpty()) {
                    log.info("Zhipu返回了{}个工具调用", toolCalls.size());
                    return FunctionCallResponse.builder()
                            .toolCalls(toolCalls)
                            .modelName("Zhipu")
                            .build();
                }
            }

            // 4. 直接回复
            return FunctionCallResponse.builder()
                    .content(aiReply)
                    .modelName("Zhipu")
                    .build();

        } catch (Exception e) {
            log.error("Zhipu适配器调用失败", e);
            return FunctionCallResponse.builder()
                    .error("Zhipu调用异常: " + e.getMessage())
                    .modelName("Zhipu")
                    .build();
        }
    }

    /**
     * 构建带工具说明的SYSTEM_PROMPT
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
                    return toolCalls;
                }
            }

            // 方式2: 尝试解析标准JSON格式
            int braceStart = aiReply.indexOf("{");
            if (braceStart != -1) {
                int braceEnd = findMatchingBrace(aiReply, braceStart);
                if (braceEnd > braceStart) {
                    String jsonStr = aiReply.substring(braceStart, braceEnd + 1);
                    JSONObject json = JSON.parseObject(jsonStr);

                    String tool = json.getString("tool");
                    if (tool == null)
                        tool = json.getString("name");

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
        return zhipuChatService.isAvailable();
    }

    @Override
    public String getName() {
        return "Zhipu";
    }

    @Override
    public int getPriority() {
        return 3; // 第三优先级
    }
}
