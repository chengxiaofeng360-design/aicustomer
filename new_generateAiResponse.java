/**
 * 使用统一的AI适配器生成回复
 * 
 * 新架构：使用适配器模式，任何AI模型都能平等使用Function Calling
 * 
 * @param userMessage 用户消息
 * @param history     对话历史（可选，用于多轮对话）
 * @return AI回复
 */
private String generateAiResponse(String userMessage, List<Map<String, String>> history) {
    log.info("【AI聊天服务 - 统一架构】开始处理查询: {}", userMessage);

    // 1. FAQ快速响应层
    try {
        List<FaqQa> faqs = faqQaService.searchFaq(userMessage, 1);
        if (faqs != null && !faqs.isEmpty()) {
            FaqQa faq = faqs.get(0);
            log.info("【AI聊天服务】命中FAQ: {}", faq.getQuestion());
            faqQaService.incrementHitCount(faq.getId());
            return faq.getAnswer() + "\n\n(来源: 常见问题库)";
        }
    } catch (Exception e) {
        log.error("【AI聊天服务】FAQ匹配异常: {}", e.getMessage());
    }

    // 2. 构建统一的Function Calling请求
    FunctionCallRequest request = FunctionCallRequest.builder()
            .systemPrompt(SYSTEM_PROMPT)
            .userMessage(userMessage)
            .history(history)
            .functions(functionCallingService.getAvailableFunctions())
            .build();

    // 3. 调用适配器管理器（自动选择模型和回退）
    FunctionCallResponse response = adapterManager.chatWithFallback(request);

    // 4. 处理响应
    if (response.hasError()) {
        log.error("【AI聊天服务】所有AI模型都失败: {}", response.getError());
        return generateFallbackResponse(userMessage);
    }

    // 5. 处理函数调用
    if (response.hasToolCalls()) {
        log.info("【AI聊天服务】AI请求调用{}个函数", response.getToolCalls().size());
        return handleToolCalls(response, request);
    }

    // 6. 直接回复
    if (response.getContent() != null && !response.getContent().trim().isEmpty()) {
        log.info("【AI聊天服务】使用模型: {}", response.getModelName());
        return response.getContent();
    }

    // 7. 未知情况，使用fallback
    log.warn("【AI聊天服务】AI返回了空响应，使用fallback");
    return generateFallbackResponse(userMessage);
}

/**
 * 处理函数调用
 * 执行函数并让AI整合结果
 */
private String handleToolCalls(FunctionCallResponse response, FunctionCallRequest originalRequest) {
    log.info("【AI聊天服务】开始执行函数调用");

    try {
        // 1. 执行所有函数
        List<String> results = new ArrayList<>();
        for (FunctionCallResponse.ToolCall toolCall : response.getToolCalls()) {
            log.info("【AI聊天服务】执行函数: {}", toolCall.getFunctionName());

            // 执行函数（权限由FunctionCallingService内部处理）
            String result = functionCallingService.executeFunction(
                    toolCall.getFunctionName(),
                    toolCall.getArguments(),
                    null);

            results.add(result);
            log.info("【AI聊天服务】函数执行完成，结果长度: {}", result.length());
        }

        // 2. 合并结果
        String combinedResult = String.join("\n\n", results);

        // 3. 让AI整合结果生成用户友好的回复
        String followUpMessage = originalRequest.getUserMessage() +
                "\n\n【系统查询结果】\n" + combinedResult +
                "\n\n请根据上述查询结果，用自然语言回答用户的问题。";

        FunctionCallRequest followUpRequest = FunctionCallRequest.builder()
                .systemPrompt(originalRequest.getSystemPrompt())
                .userMessage(followUpMessage)
                .history(originalRequest.getHistory())
                .functions(List.of()) // 不再需要函数调用
                .build();

        FunctionCallResponse finalResponse = adapterManager.chatWithFallback(followUpRequest);

        if (finalResponse.isSuccess() && finalResponse.getContent() != null) {
            return finalResponse.getContent();
        }

        // 降级：直接返回查询结果
        log.warn("【AI聊天服务】AI整合失败，直接返回查询结果");
        return combinedResult;

    } catch (Exception e) {
        log.error("【AI聊天服务】处理函数调用异常", e);
        return "抱歉，处理您的请求时出现错误: " + e.getMessage();
    }
}
