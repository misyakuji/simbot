package com.miko.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatClient chatClient;

    public void test() {
        String result = chatClient
                .prompt()
                .system("你是QQ群机器人。如果要发送消息，请调用 MCP 工具 sendGroupAt。")
                .user("在 737138270 群里@943869478")
                .call()
                .content();

        System.out.println("AI 回复：" + result);
    }

//    McpClientRegistry registry;
//
//    @PostConstruct
//    public void listTools() {
//        registry.getClients().forEach((name, client) -> {
//            System.out.println("MCP Client: " + name);
//            client.listTools().forEach(tool ->
//                    System.out.println(" - " + tool.name())
//            );
//        });
}
