package com.miko.config;


/**
 * 机器人配置抽象接口（common模块）
 * 仅暴露BaseApiService需要的配置能力，解耦具体实现（SimBotConfig）
 */
public interface BotConfig {
    /**
     * 获取API服务地址（BaseApiService核心需要）
     */
    String getApiServerHost();

    /**
     * （可选）扩展其他需要的配置项，如token、超时时间等
     */
    String getApiAccessToken();
}