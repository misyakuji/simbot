package com.miko.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class ArkChatServiceImpl implements ArkChatService{

    private final ChatClient chatClient;

    public ArkChatServiceImpl(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String chat(@RequestParam String prompt) {
        return chatClient
                .prompt(prompt)
                .call()
                .content();
    }
}