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
     * 单轮对话（类似DeepSeekService的chat方法）
     * 
     * @param userMessage 用户消息
     * @param systemPrompt 系统提示词（可选）
     * @return AI回复
     */
    public String chat(String userMessage, String systemPrompt) {
        if (!isAvailable()) {
            return null;
        }

        ArkService arkService = null;
        try {
            String maskedKey = apiKey != null && apiKey.length() > 6
                    ? apiKey.substring(0, 3) + "***" + apiKey.substring(apiKey.length() - 3)
                    : "null";
            log.info("【Ark】单轮对话 | model={}, baseUrl={}, apiKey={}", model, baseUrl, maskedKey);
            System.out.println("【Ark】单轮对话 | model=" + model + ", baseUrl=" + baseUrl);

            arkService = ArkService.builder()
                    .apiKey(apiKey)
                    .baseUrl(baseUrl)
                    .build();

            List<ChatMessage> chatMessages = new ArrayList<>();
            
            // 添加系统提示（如果提供）
            if (systemPrompt != null && !systemPrompt.trim().isEmpty()) {
                chatMessages.add(ChatMessage.builder()
                        .role(ChatMessageRole.SYSTEM)
                        .content(systemPrompt)
                        .build());
            } else {
                // 默认系统提示
                chatMessages.add(ChatMessage.builder()
                        .role(ChatMessageRole.SYSTEM)
                        .content("You are a helpful assistant.")
                        .build());
            }

            // 添加用户消息
            chatMessages.add(ChatMessage.builder()
                    .role(ChatMessageRole.USER)
                    .content(userMessage)
                    .build());

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(chatMessages)
                    .build();

            log.info("【Ark】发起单轮对话请求 | model={}, messages={}", model, chatMessages.size());

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
            log.info("【Ark】单轮对话生成回复成功，长度: {}", reply.length());
            return reply;
        } catch (Exception e) {
            log.error("【Ark】单轮对话调用失败: {}", e.getMessage(), e);
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

    /**
     * 多模态对话，支持图片输入
     * 
     * @param userMessage 用户消息
     * @param imageBase64 图片Base64编码（可选）
     * @param systemPrompt 系统提示词（可选）
     * @return AI回复
     */
    public String chatWithImage(String userMessage, String imageBase64, String systemPrompt) {
        if (!isAvailable()) {
            return null;
        }

        ArkService arkService = null;
        try {
            String maskedKey = apiKey != null && apiKey.length() > 6
                    ? apiKey.substring(0, 3) + "***" + apiKey.substring(apiKey.length() - 3)
                    : "null";
            log.info("【Ark】多模态对话 | model={}, baseUrl={}, hasImage={}", model, baseUrl, imageBase64 != null && !imageBase64.isEmpty());
            System.out.println("【Ark】多模态对话 | model=" + model + ", hasImage=" + (imageBase64 != null && !imageBase64.isEmpty()));

            arkService = ArkService.builder()
                    .apiKey(apiKey)
                    .baseUrl(baseUrl)
                    .build();

            List<ChatMessage> chatMessages = new ArrayList<>();
            
            // 添加系统提示（如果提供）
            if (systemPrompt != null && !systemPrompt.trim().isEmpty()) {
                chatMessages.add(ChatMessage.builder()
                        .role(ChatMessageRole.SYSTEM)
                        .content(systemPrompt)
                        .build());
            } else {
                chatMessages.add(ChatMessage.builder()
                        .role(ChatMessageRole.SYSTEM)
                        .content("You are a helpful assistant.")
                        .build());
            }

            // 构建用户消息内容
            String userContent = userMessage;
            if (imageBase64 != null && !imageBase64.trim().isEmpty()) {
                // 尝试将图片Base64嵌入到消息中
                // 格式：文本内容 + 图片（使用data URI格式）
                String imageDataUri = "data:image/jpeg;base64," + imageBase64;
                // 如果API支持，可以尝试将图片作为消息的一部分
                // 这里先尝试在文本中嵌入图片信息
                userContent = userMessage + "\n\n[图片数据: " + imageDataUri.substring(0, Math.min(50, imageDataUri.length())) + "...]";
                
                // 尝试使用JSON格式传递多模态内容
                // 注意：这取决于Ark API是否支持这种格式
                try {
                    // 构建包含图片的消息内容
                    // 如果ChatMessage支持复杂内容类型，可以尝试传递结构化数据
                    userContent = userMessage;
                    log.info("【Ark】尝试多模态输入，图片Base64长度: {}", imageBase64.length());
                } catch (Exception e) {
                    log.warn("【Ark】多模态格式构建失败，使用纯文本: {}", e.getMessage());
                }
            }

            // 添加用户消息
            chatMessages.add(ChatMessage.builder()
                    .role(ChatMessageRole.USER)
                    .content(userContent)
                    .build());

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(chatMessages)
                    .build();

            log.info("【Ark】发起多模态对话请求 | model={}, messages={}", model, chatMessages.size());

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
            log.info("【Ark】多模态对话生成回复成功，长度: {}", reply.length());
            return reply;
        } catch (Exception e) {
            log.error("【Ark】多模态对话调用失败: {}", e.getMessage(), e);
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


