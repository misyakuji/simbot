package com.miko.napcat.service;

import com.miko.config.BotConfig; // 依赖抽象接口
import com.miko.napcat.enums.request.SendGroupMsgRequest;
import com.miko.napcat.enums.response.SendGroupMsgResponse;
import com.miko.service.BaseApiService;
import com.miko.napcat.enums.NapCatApiEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NapCatApiService {
    private final BaseApiService baseApiService;
    // 注入robot模块的BotConfig实现类（SimBotConfigAdapter）
    private final BotConfig botConfig;


//    public <T> Mono<T> callNapCatApiReactive(
//            NapCatApiEnum api,
//            Object request,
//            Class<T> responseType) {
//
//        return webClient.post()
//                .uri(api.getUrl())
//                .bodyValue(request)
//                .retrieve()
//                .bodyToMono(responseType)
//
//                // ⭐⭐⭐ 强烈推荐
//                .timeout(Duration.ofSeconds(10))
//
//                .onErrorResume(e ->
//                        Mono.error(new RuntimeException(
//                                "NapCat API 调用失败: " + e.getMessage()
//                        ))
//                );
//    }

//    public Mono<SendGroupMsgResponse> sendGroupAtReactive(
//            SendGroupMsgRequest request) {
//
//        return baseApiService.callApiReactive(
//                NapCatApiEnum.SEND_GROUP_AT,
//                request,
//                SendGroupMsgResponse.class
//        );
//    }

    /*
        2026.2.6 19:30 新增：测试异步调用
         */
    public <T, R> Mono<R> callNapCatApiReactive(
            NapCatApiEnum apiEnum,
            T request,
            Class<R> responseClass) {

        return baseApiService.callApiReactive(
                botConfig,
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