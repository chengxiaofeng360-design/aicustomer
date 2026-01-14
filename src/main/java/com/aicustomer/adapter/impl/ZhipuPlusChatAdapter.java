package com.aicustomer.adapter.impl;

import com.aicustomer.adapter.AiModelAdapter;
import com.aicustomer.model.FunctionCallRequest;
import com.aicustomer.model.FunctionCallResponse;
import com.aicustomer.model.FunctionDefinition;
import com.aicustomer.service.ZhipuPlusChatService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Zhipu Plus 适配器（GLM-4-Plus聊天优化模型）
 * 
 * 使用glm-4-plus模型，专门优化聊天性能，比glm-4-flash更适合文本对话
 * 模拟Function Calling：通过在SYSTEM_PROMPT中说明工具，让AI返回JSON格式的工具调用
 * 
 * @author AI Customer Management System
 * @version 2.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ZhipuPlusChatAdapter implements AiModelAdapter {

    private final ZhipuPlusChatService zhipuPlusChatService;

    @Override
    public FunctionCallResponse chat(FunctionCallRequest request) {
        try {
            // 1. 构建带工具说明的SYSTEM_PROMPT
            String enhancedSystemPrompt = buildToolPrompt(request);

            // 2. 调用Zhipu Plus（注意：不支持历史对话）
            String aiReply = zhipuPlusChatService.chat(request.getUserMessage(), enhancedSystemPrompt);

            if (aiReply == null || aiReply.trim().isEmpty()) {
                return FunctionCallResponse.builder()
                        .error("Zhipu Plus返回空回复")
                        .modelName("Zhipu-Plus")
                        .build();
            }

            // 3. 检查是否包含JSON工具调用
            if (aiReply.contains("{") && aiReply.contains("}")) {
                List<FunctionCallResponse.ToolCall> toolCalls = parseToolCallsFromText(aiReply);
                if (!toolCalls.isEmpty()) {
                    log.info("Zhipu Plus返回了{}个工具调用", toolCalls.size());
                    return FunctionCallResponse.builder()
                            .toolCalls(toolCalls)
                            .modelName("Zhipu-Plus")
                            .build();
                }
            }

            // 4. 直接回复
            return FunctionCallResponse.builder()
                    .content(aiReply)
                    .modelName("Zhipu-Plus")
                    .build();

        } catch (Exception e) {
            log.error("Zhipu Plus适配器调用失败", e);
            return FunctionCallResponse.builder()
                    .error("Zhipu Plus调用异常: " + e.getMessage())
                    .modelName("Zhipu-Plus")
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
     * 从AI的文本回复中解析JSON工具调用
     * （与ZhipuAdapter相同的实现）
     */
    private List<FunctionCallResponse.ToolCall> parseToolCallsFromText(String aiReply) {
        List<FunctionCallResponse.ToolCall> toolCalls = new ArrayList<>();

        try {
            // 提取JSON部分
            int braceStart = aiReply.indexOf("{");
            int bracketStart = aiReply.indexOf("[");
            int startIndex = -1;

            if (braceStart != -1 && bracketStart != -1)
                startIndex = Math.min(braceStart, bracketStart);
            else if (braceStart != -1)
                startIndex = braceStart;
            else if (bracketStart != -1)
                startIndex = bracketStart;

            if (startIndex == -1)
                return toolCalls;

            int braceEnd = aiReply.lastIndexOf("}");
            int bracketEnd = aiReply.lastIndexOf("]");
            int endIndex = -1;

            if (braceEnd != -1 && bracketEnd != -1)
                endIndex = Math.max(braceEnd, bracketEnd);
            else if (braceEnd != -1)
                endIndex = braceEnd;
            else if (bracketEnd != -1)
                endIndex = bracketEnd;

            if (endIndex == -1 || startIndex >= endIndex)
                return toolCalls;

            String jsonStr = aiReply.substring(startIndex, endIndex + 1);

            Object parsed = JSON.parse(jsonStr);
            JSONObject json = null;

            if (parsed instanceof JSONArray) {
                JSONArray array = (JSONArray) parsed;
                if (!array.isEmpty()) {
                    json = array.getJSONObject(0);
                }
            } else if (parsed instanceof JSONObject) {
                json = (JSONObject) parsed;
            }

            if (json == null)
                return toolCalls;

            // 支持两种格式
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

        } catch (Exception e) {
            log.warn("解析Zhipu Plus工具调用失败", e);
        }

        return toolCalls;
    }

    @Override
    public boolean isAvailable() {
        return zhipuPlusChatService.isAvailable();
    }

    @Override
    public String getName() {
        return "Zhipu-Plus";
    }

    @Override
    public int getPriority() {
        return 4; // 第四优先级（在所有模型之后）
    }
}
