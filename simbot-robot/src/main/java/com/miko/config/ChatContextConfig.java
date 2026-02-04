package com.miko.config;

import com.miko.entity.BotChatContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 定义全局的ChatContext缓存Bean（线程安全）
 */
@Configuration
public class ChatContextConfig {
    // 定义全局的ChatContext缓存Bean（线程安全）
    @Bean
    public Map<String, BotChatContext> chatContextCache() {
        return new ConcurrentHashMap<>();
    }
}