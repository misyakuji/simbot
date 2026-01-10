package com.miko.config;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * SimBot配置工具类
 */
@Component
public class SimBotConfigUtil {

    private final SimBotConfig simBotConfig;

    // 默认值常量
    private static final String DEFAULT_API_SERVER = "http://localhost:3000";
    private static final int DEFAULT_WS_RETRY_TIMES = 2147483647; // Integer.MAX_VALUE
    private static final int DEFAULT_WS_RETRY_DELAY = 3500;

    public SimBotConfigUtil(SimBotConfig simBotConfig) {
        this.simBotConfig = simBotConfig;
    }

    /**
     * 获取有效的API服务器地址
     */
    public String getEffectiveApiServerHost() {
        return Optional.ofNullable(simBotConfig)
                .map(SimBotConfig::getAuthorization)
                .map(SimBotConfig.Authorization::getApiServerHost)
                .orElse(DEFAULT_API_SERVER);
    }

    /**
     * 获取事件服务器地址（可能为null）
     */
    public String getEventServerHost() {
        return Optional.ofNullable(simBotConfig)
                .map(SimBotConfig::getAuthorization)
                .map(SimBotConfig.Authorization::getEventServerHost)
                .orElse(null);
    }

    /**
     * 是否启用事件订阅
     */
    public boolean isEventServerEnabled() {
        return getEventServerHost() != null && !getEventServerHost().trim().isEmpty();
    }

    /**
     * 获取API访问令牌（优先级：apiAccessToken > accessToken）
     */
    public String getEffectiveApiAccessToken() {
        SimBotConfig.Authorization auth = simBotConfig.getAuthorization();
        if (auth == null) return null;

        if (auth.getApiAccessToken() != null) {
            return auth.getApiAccessToken();
        }
        return auth.getAccessToken();
    }

    /**
     * 获取事件访问令牌（优先级：eventAccessToken > accessToken）
     */
    public String getEffectiveEventAccessToken() {
        SimBotConfig.Authorization auth = simBotConfig.getAuthorization();
        if (auth == null) return null;

        if (auth.getEventAccessToken() != null) {
            return auth.getEventAccessToken();
        }
        return auth.getAccessToken();
    }

    /**
     * 获取API请求超时时间
     */
    public Integer getApiRequestTimeout() {
        return Optional.ofNullable(simBotConfig)
                .map(SimBotConfig::getConfig)
                .map(SimBotConfig.Config::getApiHttpRequestTimeoutMillis)
                .orElse(null);
    }

    /**
     * 获取API连接超时时间
     */
    public Integer getApiConnectTimeout() {
        return Optional.ofNullable(simBotConfig)
                .map(SimBotConfig::getConfig)
                .map(SimBotConfig.Config::getApiHttpConnectTimeoutMillis)
                .orElse(null);
    }

    /**
     * 获取API Socket超时时间
     */
    public Integer getApiSocketTimeout() {
        return Optional.ofNullable(simBotConfig)
                .map(SimBotConfig::getConfig)
                .map(SimBotConfig.Config::getApiHttpSocketTimeoutMillis)
                .orElse(null);
    }

    /**
     * 获取WebSocket最大重试次数
     */
    public int getWsMaxRetryTimes() {
        return Optional.ofNullable(simBotConfig)
                .map(SimBotConfig::getConfig)
                .map(SimBotConfig.Config::getWsConnectMaxRetryTimes)
                .orElse(DEFAULT_WS_RETRY_TIMES);
    }

    /**
     * 获取WebSocket重试延迟
     */
    public int getWsRetryDelay() {
        return Optional.ofNullable(simBotConfig)
                .map(SimBotConfig::getConfig)
                .map(SimBotConfig.Config::getWsConnectRetryDelayMillis)
                .orElse(DEFAULT_WS_RETRY_DELAY);
    }

    /**
     * 构建完整的API URL
     */
    public String buildApiUrl(String endpoint) {
        String baseUrl = getEffectiveApiServerHost();
        if (baseUrl.endsWith("/") && endpoint.startsWith("/")) {
            return baseUrl + endpoint.substring(1);
        } else if (!baseUrl.endsWith("/") && !endpoint.startsWith("/")) {
            return baseUrl + "/" + endpoint;
        } else {
            return baseUrl + endpoint;
        }
    }

    /**
     * 构建WebSocket连接URL
     */
    public String buildWebSocketUrl() {
        if (!isEventServerEnabled()) {
            return null;
        }

        String baseUrl = getEventServerHost();
        String token = getEffectiveEventAccessToken();

        if (token == null) {
            return baseUrl;
        }

        if (baseUrl.contains("?")) {
            return baseUrl + "&access_token=" + token;
        } else {
            return baseUrl + "?access_token=" + token;
        }
    }
}