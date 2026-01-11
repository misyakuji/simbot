package com.miko.service;

import com.miko.config.SimBotConfig;
import com.miko.entity.napcat.enums.NapCatApiEnum;
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

/**
 * 基础API服务类
 * <p>
 * 封装WebClient的API调用逻辑，提供统一的请求/响应处理、错误处理机制和可配置的请求参数
 * </p>
 *
 * @author misyakuji
 * @since 2026-01-10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BaseApiService {

    /**
     * WebClient实例
     */
    private final WebClient webClient;

    private final SimBotConfig simBotConfig;

    /**
     * 默认超时时间（秒）
     */
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;

    /**
     * 调用API（根据API枚举）
     *
     * @param apiEnum API枚举
     * @param request 请求对象
     * @param <T> 请求类型
     * @param <R> 响应类型
     * @return 响应对象
     */
    public <T, R> R callApi(NapCatApiEnum apiEnum, T request, Class<R> responseClass) {
        return callApi(
            apiEnum.getMethod(),
            apiEnum.getPath(),
            request,
            responseClass,
            null,
            null,
            DEFAULT_TIMEOUT_SECONDS
        );
    }

    /**
     * 调用API（根据API枚举，带超时配置）
     *
     * @param apiEnum API枚举
     * @param request 请求对象
     * @param responseClass 响应类
     * @param timeoutSeconds 超时时间（秒）
     * @param <T> 请求类型
     * @param <R> 响应类型
     * @return 响应对象
     */
    public <T, R> R callApi(NapCatApiEnum apiEnum, T request, Class<R> responseClass, int timeoutSeconds) {
        return callApi(
            apiEnum.getMethod(),
            apiEnum.getPath(),
            request,
            responseClass,
            null,
            null,
            timeoutSeconds
        );
    }

    /**
     * 调用API（根据API枚举，带请求头）
     *
     * @param apiEnum API枚举
     * @param request 请求对象
     * @param responseClass 响应类
     * @param headers 请求头
     * @param <T> 请求类型
     * @param <R> 响应类型
     * @return 响应对象
     */
    public <T, R> R callApiWithHeaders(NapCatApiEnum apiEnum, T request, Class<R> responseClass,
                                       Map<String, String> headers) {
        return callApi(
            apiEnum.getMethod(),
            apiEnum.getPath(),
            request,
            responseClass,
            headers,
            null,
            DEFAULT_TIMEOUT_SECONDS
        );
    }

    /**
     * 调用API（根据API枚举，带查询参数）
     *
     * @param apiEnum API枚举
     * @param request 请求对象
     * @param responseClass 响应类
     * @param queryParams 查询参数
     * @param <T> 请求类型
     * @param <R> 响应类型
     * @return 响应对象
     */
    public <T, R> R callApiWithQueryParams(NapCatApiEnum apiEnum, T request, Class<R> responseClass,
                                           Map<String, String> queryParams) {
        return callApi(
            apiEnum.getMethod(),
            apiEnum.getPath(),
            request,
            responseClass,
            null,
            queryParams,
            DEFAULT_TIMEOUT_SECONDS
        );
    }

    /**
     * 调用API（通用方法）
     *
     * @param method HTTP方法
     * @param path API路径
     * @param request 请求对象
     * @param responseClass 响应类
     * @param headers 自定义头信息
     * @param queryParams 查询参数
     * @param timeoutSeconds 超时时间（秒）
     * @param <T> 请求类型
     * @param <R> 响应类型
     * @return 响应对象
     */
    public <T, R> R callApi(HttpMethod method, String path, T request, Class<R> responseClass,
                          Map<String, String> headers, Map<String, String> queryParams, int timeoutSeconds) {
        try {
            log.info("调用API - 方法: {}, 路径: {}, 请求: {}", method, path, JsonUtils.toJson(request));

            // 构建WebClient请求
            WebClient.RequestBodySpec requestSpec = webClient
                .method(method)
                .uri(uriBuilder -> {
                    String fullPath = simBotConfig.getAuthorization().getApiServerHost() + path;
                    uriBuilder.path(fullPath);
                    if (queryParams != null && !queryParams.isEmpty()) {
                        queryParams.forEach(uriBuilder::queryParam);
                    }
                    return uriBuilder.build();
                })
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

            // 添加自定义头信息
            if (headers != null && !headers.isEmpty()) {
                headers.forEach(requestSpec::header);
            }

            // 根据HTTP方法设置请求体
            if (method != HttpMethod.GET && request != null) {
                requestSpec.body(BodyInserters.fromValue(request));
            }

            // 执行请求并设置超时
            Mono<String> responseMono = requestSpec.retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(timeoutSeconds));

            // 获取响应
            String responseBody = responseMono.block();

            log.info("API响应 - 方法: {}, 路径: {}, 响应: {}", method, path, responseBody);

            // 解析响应
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

    /**
     * 异步调用API
     *
     * @param method HTTP方法
     * @param path API路径
     * @param request 请求对象
     * @param responseClass 响应类
     * @param <T> 请求类型
     * @param <R> 响应类型
     * @return 响应对象Mono
     */
    public <T, R> Mono<R> callApiAsync(HttpMethod method, String path, T request, Class<R> responseClass) {
        return callApiAsync(method, path, request, responseClass, null, null, DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * 异步调用API（带完整配置）
     *
     * @param method HTTP方法
     * @param path API路径
     * @param request 请求对象
     * @param responseClass 响应类
     * @param headers 自定义头信息
     * @param queryParams 查询参数
     * @param timeoutSeconds 超时时间（秒）
     * @param <T> 请求类型
     * @param <R> 响应类型
     * @return 响应对象Mono
     */
    public <T, R> Mono<R> callApiAsync(HttpMethod method, String path, T request, Class<R> responseClass,
                                     Map<String, String> headers, Map<String, String> queryParams, int timeoutSeconds) {
        try {
            log.info("异步调用API - 方法: {}, 路径: {}, 请求: {}", method, path, JsonUtils.toJson(request));

            // 构建WebClient请求
            WebClient.RequestBodySpec requestSpec = webClient
                .method(method)
                .uri(uriBuilder -> {
                    String fullPath = simBotConfig.getAuthorization().getApiServerHost() + path;
                    uriBuilder.path(fullPath);
                    if (queryParams != null && !queryParams.isEmpty()) {
                        queryParams.forEach(uriBuilder::queryParam);
                    }
                    return uriBuilder.build();
                })
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

            // 添加自定义头信息
            if (headers != null && !headers.isEmpty()) {
                headers.forEach(requestSpec::header);
            }

            // 根据HTTP方法设置请求体
            if (method != HttpMethod.GET && request != null) {
                requestSpec.body(BodyInserters.fromValue(request));
            }

            // 执行请求并设置超时
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

    /**
     * GET请求
     *
     * @param path API路径
     * @param responseClass 响应类
     * @param <R> 响应类型
     * @return 响应对象
     */
    public <R> R get(String path, Class<R> responseClass) {
        return callApi(HttpMethod.GET, path, null, responseClass, null, null, DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * GET请求（带查询参数）
     *
     * @param path API路径
     * @param queryParams 查询参数
     * @param responseClass 响应类
     * @param <R> 响应类型
     * @return 响应对象
     */
    public <R> R get(String path, Map<String, String> queryParams, Class<R> responseClass) {
        return callApi(HttpMethod.GET, path, null, responseClass, null, queryParams, DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * POST请求
     *
     * @param path API路径
     * @param request 请求对象
     * @param responseClass 响应类
     * @param <T> 请求类型
     * @param <R> 响应类型
     * @return 响应对象
     */
    public <T, R> R post(String path, T request, Class<R> responseClass) {
        return callApi(HttpMethod.POST, path, request, responseClass, null, null, DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * PUT请求
     *
     * @param path API路径
     * @param request 请求对象
     * @param responseClass 响应类
     * @param <T> 请求类型
     * @param <R> 响应类型
     * @return 响应对象
     */
    public <T, R> R put(String path, T request, Class<R> responseClass) {
        return callApi(HttpMethod.PUT, path, request, responseClass, null, null, DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * DELETE请求
     *
     * @param path API路径
     * @param request 请求对象
     * @param responseClass 响应类
     * @param <T> 请求类型
     * @param <R> 响应类型
     * @return 响应对象
     */
    public <T, R> R delete(String path, T request, Class<R> responseClass) {
        return callApi(HttpMethod.DELETE, path, request, responseClass, null, null, DEFAULT_TIMEOUT_SECONDS);
    }
}
