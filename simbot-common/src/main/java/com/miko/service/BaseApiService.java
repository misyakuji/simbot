package com.miko.service;

import com.miko.config.BotConfig;
import com.miko.exception.ApiException;
import com.miko.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BaseApiService {
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;
    private final WebClient webClient; // 仍通过Spring注入

    public <T, R> R callApi(BotConfig botConfig, HttpMethod method, String path, T request, Class<R> responseClass,
                            Map<String, String> headers, Map<String, String> queryParams, int timeoutSeconds) {
        try {
            log.info("调用API - 方法: {}, 路径: {}, 请求: {}", method, path, JsonUtils.toJson(request));
            // 使用抽象接口的方法获取配置，而非具体类
            WebClient webClient1 = webClient.mutate().baseUrl(botConfig.getApiServerHost()).build();

            WebClient.RequestBodySpec requestSpec = webClient1
                    .method(method)
                    .uri(uriBuilder -> {
                        String fullPath = botConfig.getApiServerHost() + path;
                        uriBuilder.path(path);
                        if (queryParams != null && !queryParams.isEmpty()) {
                            queryParams.forEach(uriBuilder::queryParam);
                        }
                        return uriBuilder.build();
                    })
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON);

            if (headers != null && !headers.isEmpty()) {
                headers.forEach(requestSpec::header);
            }

            if (method != HttpMethod.GET && request != null) {
                requestSpec.body(BodyInserters.fromValue(request));
            }

            Mono<String> responseMono = requestSpec.retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds));

            String responseBody = responseMono.block();
            log.info("API响应 - 方法: {}, 路径: {}, 响应: {}", method, path, responseBody);

            return JsonUtils.fromJson(responseBody, responseClass);

        } catch (WebClientResponseException e) {
            log.error("API调用失败 - 方法: {}, 路径: {}, 状态码: {}, 响应: {}",
                    method, path, e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new ApiException(
                    e.getStatusCode().value(),
                    "API调用失败: " + e.getMessage(),
                    e
            );
        } catch (Exception e) {
            log.error("API调用异常 - 方法: {}, 路径: {}, 异常: {}", method, path, e.getMessage(), e);
            throw new ApiException(
                    "API调用异常: " + e.getMessage(),
                    e
            );
        }
    }

    public <T, R> Mono<R> callApiAsync(BotConfig botConfig, HttpMethod method, String path, T request, Class<R> responseClass) {
        return callApiAsync(botConfig, method, path, request, responseClass, null, null, DEFAULT_TIMEOUT_SECONDS);
    }

    public <T, R> Mono<R> callApiAsync(BotConfig botConfig, HttpMethod method, String path, T request, Class<R> responseClass,
                                       Map<String, String> headers, Map<String, String> queryParams, int timeoutSeconds) {
        try {
            log.info("异步调用API - 方法: {}, 路径: {}, 请求: {}", method, path, JsonUtils.toJson(request));

            WebClient.RequestBodySpec requestSpec = webClient
                    .method(method)
                    .uri(uriBuilder -> {
                        String fullPath = botConfig.getApiServerHost() + path;
                        uriBuilder.path(fullPath);
                        if (queryParams != null && !queryParams.isEmpty()) {
                            queryParams.forEach(uriBuilder::queryParam);
                        }
                        return uriBuilder.build();
                    })
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON);

            if (headers != null && !headers.isEmpty()) {
                headers.forEach(requestSpec::header);
            }

            if (method != HttpMethod.GET && request != null) {
                requestSpec.body(BodyInserters.fromValue(request));
            }

            return requestSpec.retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .map(responseBody -> {
                        log.info("异步API响应 - 方法: {}, 路径: {}, 响应: {}", method, path, responseBody);
                        return JsonUtils.fromJson(responseBody, responseClass);
                    })
                    .onErrorMap(WebClientResponseException.class, e -> {
                        log.error("异步API调用失败 - 方法: {}, 路径: {}, 状态码: {}, 响应: {}",
                                method, path, e.getStatusCode(), e.getResponseBodyAsString(), e);
                        return new ApiException(
                                e.getStatusCode().value(),
                                "API调用失败: " + e.getMessage(),
                                e
                        );
                    })
                    .onErrorMap(Exception.class, e -> {
                        log.error("异步API调用异常 - 方法: {}, 路径: {}, 异常: {}", method, path, e.getMessage(), e);
                        return new ApiException(
                                "API调用异常: " + e.getMessage(),
                                e
                        );
                    });

        } catch (Exception e) {
            log.error("异步API调用配置错误 - 方法: {}, 路径: {}, 异常: {}", method, path, e.getMessage(), e);
            return Mono.error(new ApiException(
                    "异步API调用配置错误: " + e.getMessage(),
                    e
            ));
        }
    }

    public <R> R get(BotConfig botConfig, String path, Class<R> responseClass) {
        return callApi(botConfig, HttpMethod.GET, path, null, responseClass, null, null, DEFAULT_TIMEOUT_SECONDS);
    }

    public <R> R get(BotConfig botConfig, String path, Map<String, String> queryParams, Class<R> responseClass) {
        return callApi(botConfig, HttpMethod.GET, path, null, responseClass, null, queryParams, DEFAULT_TIMEOUT_SECONDS);
    }

    public <T, R> R post(BotConfig botConfig, String path, T request, Class<R> responseClass) {
        return callApi(botConfig, HttpMethod.POST, path, request, responseClass, null, null, DEFAULT_TIMEOUT_SECONDS);
    }

    public <T, R> R put(BotConfig botConfig, String path, T request, Class<R> responseClass) {
        return callApi(botConfig, HttpMethod.PUT, path, request, responseClass, null, null, DEFAULT_TIMEOUT_SECONDS);
    }

    public <T, R> R delete(BotConfig botConfig, String path, T request, Class<R> responseClass) {
        return callApi(botConfig, HttpMethod.DELETE, path, request, responseClass, null, null, DEFAULT_TIMEOUT_SECONDS);
    }

    public <T, R> R callApi(BotConfig botConfig, HttpMethod method, String path, T request, Class<R> responseClass) {
        return callApi(botConfig, method, path, request, responseClass, null, null, DEFAULT_TIMEOUT_SECONDS);
    }
}