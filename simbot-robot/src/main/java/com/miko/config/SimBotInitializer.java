package com.miko.config;

import com.miko.util.SimBotConfigUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * SimBot启动加载器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SimBotInitializer implements CommandLineRunner {

    private final SimBotConfig simBotConfig;
    private final SimBotConfigUtil configUtil;

    @Override
    public void run(String... args) {
        log.info("=== SimBot OneBot v11 启动 ===");
        log.info("组件类型: {}", simBotConfig.getComponent());
        log.info("Bot唯一ID: {}", simBotConfig.getAuthorization().getBotUniqueId());
        log.info("API服务器: {}", configUtil.getEffectiveApiServerHost());

        if (configUtil.isEventServerEnabled()) {
            log.info("事件服务器: {}", configUtil.getEventServerHost());
            log.info("WebSocket连接URL: {}", configUtil.buildWebSocketUrl());
        } else {
            log.warn("事件服务器未配置，将不会订阅事件");
        }

        // 检查API访问令牌
        if (configUtil.getEffectiveApiAccessToken() != null) {
            log.info("API访问令牌: 已配置");
        } else {
            log.info("API访问令牌: 未配置");
        }

        // 检查事件访问令牌
        if (configUtil.getEffectiveEventAccessToken() != null) {
            log.info("事件访问令牌: 已配置");
        } else {
            log.info("事件访问令牌: 未配置");
        }

        log.info("=== 启动完成 ===\n");
    }
}