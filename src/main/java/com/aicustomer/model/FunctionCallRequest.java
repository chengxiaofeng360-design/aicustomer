package com.aicustomer.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Function Calling请求模型
 * 
 * 封装所有AI模型调用所需的参数
 * 
 * @author AI Customer Management System
 * @version 2.0.0
 */
@Data
@Builder
public class FunctionCallRequest {

    /**
     * 系统提示词
     */
    private String systemPrompt;

    /**
     * 用户消息
     */
    private String userMessage;

    /**
     * 对话历史
     * 格式：[{"role": "user", "content": "..."}, {"role": "assistant", "content":
     * "..."}]
     */
    private List<Map<String, String>> history;

    /**
     * 可用的函数定义列表
     */
    private List<FunctionDefinition> functions;
}
