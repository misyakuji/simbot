package com.miko.ai.strategy.impl;

import com.miko.ai.enums.ArkApiMode;
import com.miko.ai.strategy.ArkApiStrategy;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Mono;

public class ResponsesApiStrategy implements ArkApiStrategy {
    @Override
    public ChatResponse call(Prompt prompt) {
        return null;
    }

    @Override
    public Mono<ChatResponse> reactiveCall(Prompt prompt) {
        return null;
    }

    @Override
    public ArkApiMode getSupportMode() {
        return null;
    }
}
