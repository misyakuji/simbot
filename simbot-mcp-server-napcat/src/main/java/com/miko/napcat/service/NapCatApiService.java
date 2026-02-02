package com.miko.napcat.service;

import com.miko.config.BotConfig; // 依赖抽象接口
import com.miko.service.BaseApiService;
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
    // 注入robot模块的BotConfig实现类（SimBotConfigAdapter）
    private final BotConfig botConfig;

    /**
     * 核心改造：使用注入的BotConfig，无需手动传参
     */
    public <T, R> R callNapCatApi(NapCatApiEnum apiEnum, T request, Class<R> responseClass) {
        return baseApiService.callApi(
                botConfig, // 传入抽象配置，而非具体的SimBotConfig
                apiEnum.getMethod(),
                apiEnum.getPath(),
                request,
                responseClass,
                null,
                null,
                30
        );
    }

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