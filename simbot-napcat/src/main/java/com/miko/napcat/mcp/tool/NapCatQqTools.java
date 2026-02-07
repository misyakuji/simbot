package com.miko.napcat.mcp.tool;


import com.miko.napcat.service.message.SendGroupMsgService;
import com.miko.napcat.service.message.request.SendGroupMsgRequest;
import com.miko.napcat.service.message.response.SendGroupMsgResponse;
import com.miko.tool.BotTool;
import com.miko.tool.BotToolParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * NapCat QQ 能力的 MCP 服务封装类，提供AI可调用的工具方法
 * 
 * 包含以下功能：
 * - 群内@指定成员
 * - 查询指定城市天气信息
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NapCatQqTools {

    private final SendGroupMsgService sendGroupMsgService;
    private final WebClient webClient = WebClient.create();

    /**
     * 在指定QQ群中@指定QQ号成员
     * 
     * @param groupId 群号
     * @param atQq 被@的QQ号
     * @return 操作结果提示信息
     */
    @BotTool(name = "send_group_at", description = "在指定QQ群中@指定QQ号成员，发送群@消息")
    public String sendGroupAt(@BotToolParam(name = "groupId") String groupId, @BotToolParam(name = "atQq") String atQq) {
        log.info("🚨 sendGroupAt 工具方法被成功调用，群号：{}，被@QQ：{}", groupId, atQq);
        SendGroupMsgRequest request = new SendGroupMsgRequest();
        request.setGroupId(groupId);
        request.setMessage(new SendGroupMsgRequest.Message("at", new SendGroupMsgRequest.AtData(atQq, "string")));

        SendGroupMsgResponse resp = sendGroupMsgService.sendGroupAt(request);
        return "已@成员：" + atQq;
    }

    /**
     * 获取指定城市的天气信息
     * 
     * @param city 城市名称
     * @return 天气信息JSON字符串或错误提示
     */
    @BotTool(name = "get_weather", description = "获取指定城市的天气信息")
    public String getWeather(@BotToolParam(name = "city") String city) {
        // 参数校验
        if (city == null || city.isBlank()) {
            return "请输入有效的城市名称！";
        }
        
        try {
            // 构建并发送天气API请求
            Mono<String> weatherMono = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("uapis.cn")
                            .path("/api/v1/misc/weather")
                            .queryParam("city", city)
                            .build())
                    // 处理响应状态码异常
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(), 
                             response -> Mono.error(new RuntimeException("天气接口请求失败，状态码：" + response.statusCode())))
                    // 提取响应体
                    .bodyToMono(String.class)
                    // 设置5秒超时
                    .timeout(Duration.ofSeconds(5));
            
            // 阻塞获取结果（适配BotTool同步返回要求）
            String result = weatherMono.block();
            log.info("城市{}天气查询结果：{}", city, result);
            return result;
        } catch (Exception e) {
            log.error("查询{}天气异常", city, e);
            return "查询" + city + "天气失败：" + e.getMessage();
        }
    }
}