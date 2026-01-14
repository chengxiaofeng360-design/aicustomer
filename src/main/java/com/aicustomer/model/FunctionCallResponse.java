package com.aicustomer.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Function Calling响应模型
 * 
 * 统一封装AI模型的响应，可能是直接回复或函数调用
 * 
 * @author AI Customer Management System
 * @version 2.0.0
 */
@Data
@Builder
public class FunctionCallResponse {

    /**
     * AI的直接回复内容
     * 如果AI选择直接回答而不调用函数，此字段有值
     */
    private String content;

    /**
     * AI请求调用的函数列表
     * 如果AI需要调用函数，此字段有值
     */
    private List<ToolCall> toolCalls;

    /**
     * 使用的模型名称
     */
    private String modelName;

    /**
     * 错误信息
     * 如果调用失败，此字段有值
     */
    private String error;

    /**
     * 工具调用信息
     */
    @Data
    @Builder
    public static class ToolCall {
        /**
         * 函数名称
         */
        private String functionName;

        /**
         * 函数参数（JSON字符串）
         */
        private String arguments;

        /**
         * 工具调用ID（可选，用于关联）
         */
        private String id;
    }

    /**
     * 检查是否有函数调用
     */
    public boolean hasToolCalls() {
        return toolCalls != null && !toolCalls.isEmpty();
    }

    /**
     * 检查是否有错误
     */
    public boolean hasError() {
        return error != null && !error.isEmpty();
    }

    /**
     * 检查是否成功
     */
    public boolean isSuccess() {
        return !hasError() && (content != null || hasToolCalls());
    }
}
