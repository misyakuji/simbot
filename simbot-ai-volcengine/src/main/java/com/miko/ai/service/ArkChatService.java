package com.miko.ai.service;

import com.miko.entity.BotChatContext;
import org.springframework.web.bind.annotation.RequestParam;

public interface ArkChatService {
    public String chat(@RequestParam String prompt);
    public String multiChatWithDoubao(String prompt, BotChatContext botChatContext);
    public String aiCallGroupAt(String userRequest) throws Exception;
}
