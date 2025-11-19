package com.aicustomer.config;

import com.aicustomer.service.ArkChatService;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
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
     * 创建自定义ChatClient，使用豆包模型
     */
    @Bean
    public ChatClient doubaoChatClient() {
        return new DoubaoChatClient(arkChatService);
    }
    
    /**
     * 自定义ChatClient实现，封装豆包服务
     */
    private static class DoubaoChatClient implements ChatClient {
        private final ArkChatService arkChatService;
        
        public DoubaoChatClient(ArkChatService arkChatService) {
            this.arkChatService = arkChatService;
        }
        
        @Override
        public ChatResponse call(Prompt prompt) {
            String systemPrompt = null;
            String userMessage = null;
            List<Map<String, String>> history = new ArrayList<>();
            
            // 提取系统消息和用户消息
            for (Message message : prompt.getInstructions()) {
                if (message instanceof SystemMessage) {
                    systemPrompt = message.getContent();
                } else if (message instanceof UserMessage) {
                    userMessage = message.getContent();
                }
            }
            
            // 调用豆包服务
            String response;
            if (history.isEmpty()) {
                response = arkChatService.chat(userMessage, systemPrompt);
            } else {
                response = arkChatService.chatWithHistory(userMessage, history);
            }
            
            // 构建ChatResponse
            return new ChatResponse(List.of(
                new org.springframework.ai.chat.messages.AssistantMessage(response != null ? response : "")
            ));
        }
        
        @Override
        public Flux<ChatResponse> stream(Prompt prompt) {
            // 流式响应暂不支持，返回单次响应
            return Flux.just(call(prompt));
        }
    }
}

