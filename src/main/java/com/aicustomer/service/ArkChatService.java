package com.aicustomer.service;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ArkChatService {

    @Value("${ai-customer.ai.ark.api-key:}")
    private String apiKey;
    @Value("${ai-customer.ai.ark.base-url:https://ark.cn-beijing.volces.com/api/v3}")
    private String baseUrl;
    @Value("${ai-customer.ai.ark.model:doubao-seed-1-6-251015}")
    private String model;

    public boolean isAvailable() {
        return apiKey != null && !apiKey.trim().isEmpty();
    }

    /**
     * 使用火山方舟 Ark 对话模型，支持带历史消息
     */
    public String chatWithHistory(String userMessage, List<Map<String, String>> history) {
        if (!isAvailable()) {
            return null;
        }

        ArkService arkService = null;
        try {
            // 启动时打印当前使用的模型与端点（脱敏API Key）
            String maskedKey = apiKey != null && apiKey.length() > 6
                    ? apiKey.substring(0, 3) + "***" + apiKey.substring(apiKey.length() - 3)
                    : "null";
            log.info("【Ark】准备调用 | model={}, baseUrl={}, apiKey={}", model, baseUrl, maskedKey);
            System.out.println("【Ark】准备调用 | model=" + model + ", baseUrl=" + baseUrl);

            arkService = ArkService.builder()
                    .apiKey(apiKey)
                    .baseUrl(baseUrl)
                    .build();

            List<ChatMessage> chatMessages = new ArrayList<>();
            // 系统提示，保证对话风格
            chatMessages.add(ChatMessage.builder()
                    .role(ChatMessageRole.SYSTEM)
                    .content("You are a helpful assistant.")
                    .build());

            // 将历史消息转换为Ark消息
            if (history != null) {
                for (Map<String, String> h : history) {
                    String role = h.getOrDefault("role", "user");
                    String content = h.getOrDefault("content", "");
                    ChatMessageRole messageRole = "assistant".equalsIgnoreCase(role)
                            ? ChatMessageRole.ASSISTANT
                            : ChatMessageRole.USER;
                    chatMessages.add(ChatMessage.builder()
                            .role(messageRole)
                            .content(content)
                            .build());
                }
            }

            // 追加当前用户消息
            chatMessages.add(ChatMessage.builder()
                    .role(ChatMessageRole.USER)
                    .content(userMessage)
                    .build());

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(chatMessages)
                    .build();

            log.info("【Ark】发起请求 | model={}, messages={}", model, chatMessages.size());

            StringBuilder replyBuilder = new StringBuilder();
            arkService.createChatCompletion(request)
                    .getChoices()
                    .forEach(choice -> {
                        if (choice != null && choice.getMessage() != null) {
                            Object contentObj = choice.getMessage().getContent();
                            String content = contentObj != null ? String.valueOf(contentObj) : null;
                            if (content != null && !content.isEmpty()) {
                                replyBuilder.append(content);
                            }
                        }
                    });

            String reply = replyBuilder.toString();
            if (reply.trim().isEmpty()) {
                reply = "抱歉，Ark服务返回了空内容。";
            }
            log.info("【Ark】生成回复成功，长度: {}", reply.length());
            return reply;
        } catch (Exception e) {
            log.error("【Ark】调用失败: {}", e.getMessage(), e);
            return "⚠️ Ark服务调用异常: " + e.getMessage();
        } finally {
            if (arkService != null) {
                try {
                    arkService.shutdownExecutor();
                } catch (Exception ignore) {
                }
            }
        }
    }
}


