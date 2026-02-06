package com.miko.napcat.mcp.tool;


import com.miko.napcat.service.message.ext.SendGroupMsgService;
import com.miko.service.SendGroupMsgRequest;
import com.miko.service.SendGroupMsgResponse;
import com.miko.tool.BotTool;
import com.miko.tool.BotToolParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * NapCat QQ 能力的 MCP 服务封装（供AI调用）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NapCatQqTools {

    private final SendGroupMsgService sendGroupMsgService;
    WebClient webClient = WebClient.create();

    /*
    2026.2.6 19:30 新增：测试异步调用
     */
    @BotTool(name = "send_group_at", description = "在指定QQ群中@成员")
    public Mono<String> sendGroupAt(
            @BotToolParam(name = "groupId") String groupId,
            @BotToolParam(name = "atQq") String atQq) {

        SendGroupMsgRequest request = new SendGroupMsgRequest();

        request.setGroupId(groupId);
        request.setMessage(
                new SendGroupMsgRequest.Message(
                        "at",
                        new SendGroupMsgRequest.AtData(atQq, "string")
                )
        );

        return sendGroupMsgService.sendGroupAt(request)
                .map(resp -> "已@成员：" + atQq)
                .timeout(Duration.ofSeconds(5)).onErrorResume(e ->
                        Mono.just("发送失败：" + e.getMessage())
                );
    }

    /*
        2026.2.6 19:30 新增：测试异步调用
         */
    @BotTool(name = "get_weather", description = "获取指定城市的天气信息")
    public Mono<String> getWeather(
            @BotToolParam(name = "city") String city) {

        if (city == null || city.isBlank()) {
            return Mono.just("请输入有效的城市名称！");
        }
        log.info("开始查询城市{}天气...", city);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("uapis.cn")
                        .path("/api/v1/misc/weather")
                        .queryParam("city", city)
                        .build()
                )
                .retrieve()
                .bodyToMono(String.class)

                // ⭐ 强烈推荐加！
                .timeout(Duration.ofSeconds(5))

                // ⭐ 防炸
                .onErrorResume(e ->
                        Mono.just("查询天气失败：" + e.getMessage())
                )

                .doOnNext(res ->
                        log.info("城市{}天气查询结果：{}", city, res)
                );
    }

}