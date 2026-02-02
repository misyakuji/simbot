package com.miko.ai.service;

import org.springframework.web.bind.annotation.RequestParam;

public interface ArkChatService {
    public String chat(@RequestParam String prompt);
}
