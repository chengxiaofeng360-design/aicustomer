package com.aicustomer.config;

import com.aicustomer.service.ArkChatService;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spring AI配置类
 * 提供对豆包（Doubao）模型的支持
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Configuration
public class SpringAiConfig {
    
    private final ArkChatService arkChatService;
    
    public SpringAiConfig(ArkChatService arkChatService) {
        this.arkChatService = arkChatService;
    }
    
    /**
     * 创建自定义ChatModel，使用豆包模型
     * 这个Bean可以在Controller中注入使用
     */
    @Bean(name = "doubaoChatModel")
    public ChatModel doubaoChatModel() {
        return new DoubaoChatModel(arkChatService);
    }
    
    /**
     * 自定义ChatModel实现，封装豆包服务
     * 实现Spring AI的ChatModel接口，提供统一的AI调用方式
     */
    private static class DoubaoChatModel implements ChatModel {
        private final ArkChatService arkChatService;
        
        public DoubaoChatModel(ArkChatService arkChatService) {
            this.arkChatService = arkChatService;
        }
        
        @Override
        public ChatResponse call(Prompt prompt) {
            String systemPrompt = null;
            String userMessage = null;
            List<Map<String, String>> history = new ArrayList<>();
            
            // 提取系统消息、用户消息和对话历史
            for (Message message : prompt.getInstructions()) {
                if (message instanceof SystemMessage) {
                    systemPrompt = message.getContent();
                } else if (message instanceof UserMessage) {
                    userMessage = message.getContent();
                } else if (message instanceof org.springframework.ai.chat.messages.AssistantMessage) {
                    // 处理助手消息（用于构建对话历史）
                    Map<String, String> assistantMsg = new HashMap<>();
                    assistantMsg.put("role", "assistant");
                    assistantMsg.put("content", message.getContent());
                    history.add(assistantMsg);
                }
            }
            
            // 如果有对话历史，需要重新构建历史列表（包含用户和助手消息）
            // 注意：Spring AI的Prompt可能已经包含了历史消息，我们需要正确提取
            List<Map<String, String>> fullHistory = new ArrayList<>();
            boolean hasHistory = false;
            for (Message message : prompt.getInstructions()) {
                if (message instanceof UserMessage && !message.getContent().equals(userMessage)) {
                    // 这是历史中的用户消息
                    Map<String, String> userMsg = new HashMap<>();
                    userMsg.put("role", "user");
                    userMsg.put("content", message.getContent());
                    fullHistory.add(userMsg);
                    hasHistory = true;
                } else if (message instanceof org.springframework.ai.chat.messages.AssistantMessage) {
                    Map<String, String> assistantMsg = new HashMap<>();
                    assistantMsg.put("role", "assistant");
                    assistantMsg.put("content", message.getContent());
                    fullHistory.add(assistantMsg);
                    hasHistory = true;
                }
            }
            
            // 调用豆包服务
            String response;
            if (userMessage == null) {
                response = "错误：未找到用户消息";
            } else if (hasHistory && !fullHistory.isEmpty()) {
                // 使用对话历史
                response = arkChatService.chatWithHistory(userMessage, fullHistory);
            } else {
                // 单轮对话
                response = arkChatService.chat(userMessage, systemPrompt);
            }
            
            // 构建ChatResponse
            if (response == null) {
                response = "抱歉，AI服务暂时无法响应";
            }
            
            // 创建Generation和ChatResponse
            org.springframework.ai.chat.messages.AssistantMessage assistantMessage = 
                new org.springframework.ai.chat.messages.AssistantMessage(response);
            Generation generation = new Generation(assistantMessage);
            
            return ChatResponse.builder()
                .withGenerations(List.of(generation))
                .build();
        }
        
        @Override
        public Flux<ChatResponse> stream(Prompt prompt) {
            // 流式响应暂不支持，返回单次响应
            return Flux.just(call(prompt));
        }
        
        @Override
        public org.springframework.ai.chat.prompt.ChatOptions getDefaultOptions() {
            // 返回默认选项（可以为null或空选项）
            return null;
        }
    }
}

