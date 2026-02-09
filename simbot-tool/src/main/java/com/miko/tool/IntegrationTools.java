package com.miko.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
/**
 * 集成工具类，提供多种实用功能的API调用封装
 * 包括天气查询、Epic免费游戏获取、网络连通性测试等功能
 * 所有方法均通过WebClient调用外部API接口实现
 */
@Slf4j
@Component
public class IntegrationTools {
    private final WebClient webClient = WebClient.create();

    /**
     * 获取指定城市的天气信息
     *
     * @param city 城市名称，不能为空或空白字符
     * @return 天气信息的JSON字符串；若查询失败则返回错误提示信息
     */
    @BotTool(name = "get_weather", description = "获取指定城市的天气信息")
    public String getWeather(@BotToolParam(name = "city") String city) {
        // 验证输入参数是否有效
        if (city == null || city.isBlank()) {
            return "请输入有效的城市名称！";
        }

        try {
            // 构建天气API请求并发送GET请求
            Mono<String> weatherMono = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("uapis.cn")
                            .path("/api/v1/misc/weather")
                            .queryParam("city", city)
                            .build())
                    // 处理HTTP非成功状态码
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(),
                            response -> Mono.error(new RuntimeException("天气接口请求失败，状态码：" + response.statusCode())))
                    // 解析响应体为字符串
                    .bodyToMono(String.class)
                    // 设置请求超时时间
                    .timeout(Duration.ofSeconds(5));

            // 阻塞等待并获取API响应结果
            String result = weatherMono.block();
            log.info("成功查询城市[{}]的天气信息: {}", city, result);
            return result;
        } catch (Exception e) {
            // 记录错误日志并返回友好提示
            log.error("查询城市[{}]天气时发生异常", city, e);
            return "查询" + city + "天气失败：" + e.getMessage();
        }
    }

    /**
     * 获取Epic商店本周免费游戏列表
     *
     * @return 免费游戏信息的JSON字符串；若获取失败则返回错误提示信息
     */
    @BotTool(name = "get_epic_free_games", description = "获取Epic商店本周免费游戏列表")
    public String getEpicFreeGames() {
        try {
            // 构建Epic免费游戏API请求并发送GET请求
            Mono<String> epicMono = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("uapis.cn")
                            .path("/api/v1/game/epic-free")
                            .build())
                    // 处理HTTP非成功状态码
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(),
                            response -> Mono.error(new RuntimeException("Epic免费游戏接口请求失败，状态码：" + response.statusCode())))
                    // 解析响应体为字符串
                    .bodyToMono(String.class)
                    // 设置请求超时时间
                    .timeout(Duration.ofSeconds(5));

            // 阻塞等待并获取API响应结果
            String result = epicMono.block();
            log.info("成功获取本周Epic免费游戏信息: {}", result);
            return result;
        } catch (Exception e) {
            // 记录错误日志并返回友好提示
            log.error("获取Epic免费游戏时发生异常", e);
            return "获取本周Epic免费游戏失败：" + e.getMessage();
        }
    }

    /**
     * 快速测试指定主机的网络连通性
     *
     * @param host 目标主机的IP地址或域名，不能为空或空白字符
     * @return Ping结果的JSON字符串；若测试失败则返回错误提示信息
     */
    @BotTool(name = "ping_host", description = "输入IP或域名，快速返回Ping结果，包括延迟和连通状态")
    public String pingHost(@BotToolParam(name = "host") String host) {
        // 验证输入参数是否有效
        if (host == null || host.isBlank()) {
            return "请输入有效的IP或域名！";
        }

        try {
            // 构建Ping API请求并发送GET请求
            Mono<String> pingMono = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("uapis.cn")
                            .path("/api/v1/network/ping")
                            .queryParam("host", host)
                            .build())
                    // 处理HTTP非成功状态码
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(),
                            response -> Mono.error(new RuntimeException("Ping接口请求失败，状态码：" + response.statusCode())))
                    // 解析响应体为字符串
                    .bodyToMono(String.class)
                    // 设置请求超时时间
                    .timeout(Duration.ofSeconds(5));

            // 阻塞等待并获取API响应结果
            String result = pingMono.block();
            log.info("成功Ping [{}]，结果: {}", host, result);
            return result;
        } catch (Exception e) {
            // 记录错误日志并返回友好提示
            log.error("Ping [{}]时发生异常", host, e);
            return "Ping " + host + " 失败：" + e.getMessage();
        }
    }
}
