package com.miko.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * 天气工具类，用于获取指定城市的天气信息
 */
@Slf4j
@Component
public class WeatherTool {
    private final WebClient webClient = WebClient.create();

    /**
     * 根据城市名称获取天气信息
     *
     * @param city 城市名称，不能为空或空白字符
     * @return 天气信息的JSON字符串；若查询失败则返回错误提示信息
     */
    @BotTool(name = "get_weather", description = "获取指定城市的天气信息")
    public String getWeather(@BotToolParam(name = "city") String city) {
        // 参数有效性检查
        if (city == null || city.isBlank()) {
            return "请输入有效的城市名称！";
        }

        try {
            // 构造天气API请求URL并发送GET请求
            Mono<String> weatherMono = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("uapis.cn")
                            .path("/api/v1/misc/weather")
                            .queryParam("city", city)
                            .build())
                    // 处理非2xx状态码的异常情况
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(),
                            response -> Mono.error(new RuntimeException("天气接口请求失败，状态码：" + response.statusCode())))
                    // 将响应体转换为字符串
                    .bodyToMono(String.class)
                    // 设置请求超时时间为5秒
                    .timeout(Duration.ofSeconds(5));

            // 同步阻塞获取API响应结果
            String result = weatherMono.block();
            log.info("成功查询城市[{}]的天气信息: {}", city, result);
            return result;
        } catch (Exception e) {
            // 记录异常日志并返回友好的错误信息
            log.error("查询城市[{}]天气时发生异常", city, e);
            return "查询" + city + "天气失败：" + e.getMessage();
        }
    }
}
