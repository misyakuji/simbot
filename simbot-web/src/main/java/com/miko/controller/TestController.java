package com.miko.controller;

import com.miko.ai.service.ChatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
//@PropertySource(value = {"classpath:webInfo.properties"})
public class TestController {

    private final ChatService chatService;

    @Value("${web.msg}")
    private String webMsg;

    public TestController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    String test() {
        return webMsg;
    }

    @GetMapping("/chat")
    public String chat(String prompt) {
        return chatService.chat(prompt);
    }

}
