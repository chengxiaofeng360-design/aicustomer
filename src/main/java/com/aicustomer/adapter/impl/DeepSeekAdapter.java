package com.aicustomer.adapter.impl;

import com.aicustomer.adapter.AiModelAdapter;
import com.aicustomer.model.FunctionCallRequest;
import com.aicustomer.model.FunctionCallResponse;
import com.aicustomer.service.DeepSeekService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DeepSeek适配器
 * 
 * 使用DeepSeek原生的Function Calling API
 * 优先级最高，因为支持原生Function Calling
 * 
 * @author AI Customer Management System
 * @version 2.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeepSeekAdapter implements AiModelAdapter {

    private final DeepSeekService deepSeekService;

    @Override
    public FunctionCallResponse chat(FunctionCallRequest request) {
        try {
            // 1. 构建消息列表
            List<Map<String, String>> messages = buildMessages(request);

            // 2. 调用DeepSeek原生Function Calling API
            Map<String, Object> response = deepSeekService.generateResponseWithFunctions(
                    messages,
                    request.getFunctions());

            // 3. 检查错误
            if (response.containsKey("error")) {
                return FunctionCallResponse.builder()
                        .error((String) response.get("error"))
                        .modelName("DeepSeek")
                        .build();
            }

            // 4. 检查是否有函数调用
            if (response.containsKey("tool_calls")) {
                List<FunctionCallResponse.ToolCall> toolCalls = parseToolCalls(response);
                return FunctionCallResponse.builder()
                        .toolCalls(toolCalls)
                        .modelName("DeepSeek")
                        .build();
            }

            // 5. 直接回复
            if (response.containsKey("content")) {
                return FunctionCallResponse.builder()
                        .content((String) response.get("content"))
                        .modelName("DeepSeek")
                        .build();
            }

            // 6. 未知响应格式
            return FunctionCallResponse.builder()
                    .error("DeepSeek返回了未知格式的响应")
                    .modelName("DeepSeek")
                    .build();

        } catch (Exception e) {
            log.error("DeepSeek适配器调用失败", e);
            return FunctionCallResponse.builder()
                    .error("DeepSeek调用异常: " + e.getMessage())
                    .modelName("DeepSeek")
                    .build();
        }
    }

    /**
     * 构建消息列表
     */
    private List<Map<String, String>> buildMessages(FunctionCallRequest request) {
        List<Map<String, String>> messages = new ArrayList<>();

        // 系统消息
        messages.add(Map.of("role", "system", "content", request.getSystemPrompt()));

        // 历史消息
        if (request.getHistory() != null && !request.getHistory().isEmpty()) {
            int startIdx = Math.max(0, request.getHistory().size() - 10); // 最近10轮
            for (int i = startIdx; i < request.getHistory().size(); i++) {
                messages.add(request.getHistory().get(i));
            }
        }

        // 用户消息
        messages.add(Map.of("role", "user", "content", request.getUserMessage()));

        return messages;
    }

    /**
     * 解析工具调用
     */
    @SuppressWarnings("unchecked")
    private List<FunctionCallResponse.ToolCall> parseToolCalls(Map<String, Object> response) {
        List<FunctionCallResponse.ToolCall> toolCalls = new ArrayList<>();

        List<Map<String, Object>> rawToolCalls = (List<Map<String, Object>>) response.get("tool_calls");
        if (rawToolCalls == null)
            return toolCalls;

        for (Map<String, Object> rawCall : rawToolCalls) {
            Map<String, Object> function = (Map<String, Object>) rawCall.get("function");
            if (function == null)
                continue;

            toolCalls.add(FunctionCallResponse.ToolCall.builder()
                    .functionName((String) function.get("name"))
                    .arguments((String) function.getOrDefault("arguments", "{}"))
                    .id((String) rawCall.get("id"))
                    .build());
        }

        return toolCalls;
    }

    @Override
    public boolean isAvailable() {
        return deepSeekService.isAvailable();
    }

    @Override
    public String getName() {
        return "DeepSeek";
    }

    @Override
    public int getPriority() {
        return 1; // 最高优先级
    }
}
