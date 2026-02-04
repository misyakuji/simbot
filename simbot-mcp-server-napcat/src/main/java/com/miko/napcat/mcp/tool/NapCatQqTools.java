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
public class NapCatQqTools{

    private final SendGroupMsgService sendGroupMsgService;
    WebClient webClient = WebClient.create();

    @BotTool(name = "send_group_at", description = "在指定QQ群中@指定QQ号成员，发送群@消息")
    public String sendGroupAt(@BotToolParam(name = "groupId") String groupId, @BotToolParam(name = "atQq") String atQq) {
        log.info("🚨 sendGroupAt 工具方法被成功调用，群号：{}，被@QQ：{}", groupId, atQq);
        SendGroupMsgRequest request = new SendGroupMsgRequest();
        request.setGroupId(groupId);
        request.setMessage(
                new SendGroupMsgRequest.Message(
                        "at",
                        new SendGroupMsgRequest.AtData(atQq, "string")
                )
        );

        SendGroupMsgResponse resp = sendGroupMsgService.sendGroupAt(request);
        return "已@成员：" + atQq;
    }
    @BotTool(name = "get_weather", description = "获取指定城市的天气信息")
    public String getWeather(@BotToolParam(name = "city") String city) {
        if (city == null || city.isBlank()) {
            return "请输入有效的城市名称！";
        }
        try {
            Mono<String> weatherMono = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("uapis.cn")
                            .path("/api/v1/misc/weather")
                            .queryParam("city", city)
                            .build()
                    )
                    // 第一步：执行请求获取响应规格
                    .retrieve()
                    // 第二步：在retrieve之后调用onStatus处理异常状态码
                    .onStatus(
                            status -> !status.is2xxSuccessful(),
                            response -> Mono.just(new RuntimeException(
                                    "天气接口请求失败，状态码：" + response.statusCode()
                            ))
                    )
                    // 第三步：转换响应体
                    .bodyToMono(String.class)
                    // 超时保护
                    .timeout(Duration.ofSeconds(5));
            // 阻塞获取结果（适配BotTool需要返回字符串的场景）
            String result = weatherMono.block();
            log.info("城市{}天气查询结果：{}", city, result);
            return result;
        } catch (Exception e) {
            log.error("查询{}天气异常", city, e);
            return "查询" + city + "天气失败：" + e.getMessage();
        }
    }

}