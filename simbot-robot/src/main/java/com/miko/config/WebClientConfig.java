package com.miko.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    /**
     * 默认超时时间（毫秒）
     */
    private static final int DEFAULT_TIMEOUT_MILLIS = 30000;
    /**
     * 默认读写超时时间（秒）
     */
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;


    @Bean
    public WebClient webClient() {
        // 配置 HttpClient 连接池和超时
        HttpClient httpClient = HttpClient.create()
                // 连接超时
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, DEFAULT_TIMEOUT_MILLIS)
                // 读写超时
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS))
                );
        // 配置ExchangeStrategies以支持大文件传输
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();

        return WebClient.builder()
                .exchangeStrategies(strategies)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                // 全局请求头
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                // 绑定 HttpClient 配置
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}