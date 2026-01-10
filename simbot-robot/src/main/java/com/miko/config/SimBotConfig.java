package com.miko.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.miko.factory.JsonPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


/**
 * SimBot OneBot v11 配置类
 * 对应 simbot-bots/default-bot.json 文件
 */
@Data
@Configuration
@PropertySource(
        value = "classpath:simbot-bots/default.bot.json",
        factory = JsonPropertySourceFactory.class
)
@ConfigurationProperties()
public class SimBotConfig {

    /**
     * 固定值: simbot.onebot11
     */
    private String component;

    /**
     * 授权配置
     */
    private Authorization authorization;

    /**
     * 额外的可选配置
     */
    private Config config;

    @Data
    public static class Authorization {
        /**
         * 唯一ID，作为组件内 Bot 的 id，用于组件内去重
         * 可以随便编，但建议是bot的qq号
         */
        @JsonProperty("botUniqueId")
        private String botUniqueId;

        /**
         * api地址，是个http/https服务器的路径，默认localhost:3000
         */
        @JsonProperty("apiServerHost")
        private String apiServerHost;

        /**
         * 订阅事件的服务器地址，是个ws/wss路径，默认 `null`
         * 如果为 `null` 则不会连接 ws 和订阅事件
         */
        @JsonProperty("eventServerHost")
        private String eventServerHost;

        /**
         * 配置的 token，可以是null，代表同时配置 apiAccessToken 和 eventAccessToken
         */
        @JsonProperty("accessToken")
        private String accessToken;

        /**
         * 用于API请求时用的 token，默认 null
         */
        @JsonProperty("apiAccessToken")
        private String apiAccessToken;

        /**
         * 用于连接事件订阅ws时用的 token，默认 null
         */
        @JsonProperty("eventAccessToken")
        private String eventAccessToken;
    }

    @Data
    public static class Config {
        /**
         * API请求中的超时请求配置。整数数字，单位毫秒，默认为 `null`
         */
        @JsonProperty("apiHttpRequestTimeoutMillis")
        private Integer apiHttpRequestTimeoutMillis;

        /**
         * API请求中的连接超时配置。整数数字，单位毫秒，默认为 `null`
         */
        @JsonProperty("apiHttpConnectTimeoutMillis")
        private Integer apiHttpConnectTimeoutMillis;

        /**
         * API请求中的Socket超时配置。整数数字，单位毫秒，默认为 `null`
         */
        @JsonProperty("apiHttpSocketTimeoutMillis")
        private Integer apiHttpSocketTimeoutMillis;

        /**
         * 每次尝试连接到 ws 服务时的最大重试次数，大于等于0的整数，默认为 2147483647
         */
        @JsonProperty("wsConnectMaxRetryTimes")
        private Integer wsConnectMaxRetryTimes;

        /**
         * 每次尝试连接到 ws 服务时，如果需要重新尝试，则每次尝试之间的等待时长
         * 整数数字，单位毫秒，默认为 3500
         */
        @JsonProperty("wsConnectRetryDelayMillis")
        private Integer wsConnectRetryDelayMillis;

        /**
         * 默认图片附加参数
         */
        @JsonProperty("defaultImageAdditionalParams")
        private DefaultImageAdditionalParams defaultImageAdditionalParams;
    }

    @Data
    public static class DefaultImageAdditionalParams {
        /**
         * 本地文件转Base64
         */
        @JsonProperty("localFileToBase64")
        private Boolean localFileToBase64;

        /**
         * 图片类型
         */
        @JsonProperty("type")
        private String type;

        /**
         * 是否缓存
         */
        @JsonProperty("cache")
        private Boolean cache;

        /**
         * 是否使用代理
         */
        @JsonProperty("proxy")
        private Boolean proxy;

        /**
         * 超时时间
         */
        @JsonProperty("timeout")
        private Integer timeout;
    }
}