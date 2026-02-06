package com.miko.napcat.service;

import com.miko.config.BotConfig; // 依赖抽象接口
import com.miko.napcat.enums.NapCatApiEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NapCatApiService {
    private final BaseApiService baseApiService;
    private final BotConfig botConfig;

    /**
     * 调用 NapCat API 的核心方法
     * 使用注入的 BotConfig，避免手动传递配置参数
     *
     * @param apiEnum       API 枚举，包含请求方法和路径
     * @param request       请求体对象
     * @param responseClass 响应结果的类型
     * @param <T>           请求体泛型
     * @param <R>           响应体泛型
     * @return 解析后的响应结果
     */
    public <T, R> R callNapCatApi(NapCatApiEnum apiEnum, T request, Class<R> responseClass) {
        return baseApiService.callApi(
                botConfig, // 使用注入的抽象配置
                apiEnum.getMethod(),
                apiEnum.getPath(),
                request,
                responseClass,
                null,
                null,
                30
        );
    }

    /**
     * 调用 NapCat API 并指定超时时间
     *
     * @param apiEnum         API 枚举
     * @param request         请求体对象
     * @param responseClass   响应结果类型
     * @param timeoutSeconds  超时时间（秒）
     * @param <T>             请求体泛型
     * @param <R>             响应体泛型
     * @return 解析后的响应结果
     */
    public <T, R> R callNapCatApi(NapCatApiEnum apiEnum, T request, Class<R> responseClass, int timeoutSeconds) {
        return baseApiService.callApi(
                botConfig,
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
     * 调用 NapCat API 并携带自定义请求头
     *
     * @param apiEnum       API 枚举
     * @param request       请求体对象
     * @param responseClass 响应结果类型
     * @param headers       自定义请求头
     * @param <T>           请求体泛型
     * @param <R>           响应体泛型
     * @return 解析后的响应结果
     */
    public <T, R> R callNapCatApiWithHeaders(NapCatApiEnum apiEnum, T request, Class<R> responseClass,
                                             Map<String, String> headers) {
        return baseApiService.callApi(
                botConfig,
                apiEnum.getMethod(),
                apiEnum.getPath(),
                request,
                responseClass,
                headers,
                null,
                30
        );
    }

    /**
     * 调用 NapCat API 并携带查询参数
     *
     * @param apiEnum       API 枚举
     * @param request       请求体对象
     * @param responseClass 响应结果类型
     * @param queryParams   查询参数
     * @param <T>           请求体泛型
     * @param <R>           响应体泛型
     * @return 解析后的响应结果
     */
    public <T, R> R callNapCatApiWithQueryParams(NapCatApiEnum apiEnum, T request, Class<R> responseClass,
                                                 Map<String, String> queryParams) {
        return baseApiService.callApi(
                botConfig,
                apiEnum.getMethod(),
                apiEnum.getPath(),
                request,
                responseClass,
                null,
                queryParams,
                30
        );
    }
}