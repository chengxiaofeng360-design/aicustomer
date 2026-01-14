package com.aicustomer.adapter;

import com.aicustomer.model.FunctionCallRequest;
import com.aicustomer.model.FunctionCallResponse;

/**
 * AI模型适配器接口
 * 
 * 提供统一的Function Calling接口，让任何AI模型都能以相同方式使用
 * 不同模型可以有不同的实现方式（原生API或模拟）
 * 
 * @author AI Customer Management System
 * @version 2.0.0
 */
public interface AiModelAdapter {

    /**
     * 使用Function Calling调用AI模型
     * 
     * @param request 包含消息、历史和函数定义的请求
     * @return 包含回复内容或函数调用的响应
     */
    FunctionCallResponse chat(FunctionCallRequest request);

    /**
     * 检查适配器是否可用
     * 
     * @return true if available, false otherwise
     */
    boolean isAvailable();

    /**
     * 获取适配器名称
     * 
     * @return 适配器名称，如 "DeepSeek", "Doubao", "Zhipu"
     */
    String getName();

    /**
     * 获取优先级
     * 数字越小优先级越高
     * 
     * @return 优先级值（1-999）
     */
    int getPriority();
}
