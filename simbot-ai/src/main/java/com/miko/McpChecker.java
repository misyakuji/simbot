//package com.miko;
//
//import io.modelcontextprotocol.client.McpClient;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import jakarta.annotation.PostConstruct;
//
//@Component
//@RequiredArgsConstructor
//public class McpChecker {
//
//    private final McpClient napcat;  // 注入 MCP Client 名称同 application.yml 定义
//
//    @PostConstruct
//    public void checkTools() {
//        System.out.println("MCP Client tools:");
//        napcat.listTools().forEach(tool ->
//                System.out.println(" - Tool: " + tool.name())
//        );
//    }
//}
