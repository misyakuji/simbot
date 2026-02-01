package com.miko.config;

import com.volcengine.ark.runtime.service.ArkService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 火山方舟 ArkService 配置类
 * 统一管理 ArkService 的初始化、单例和销毁
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "volc.ark")
public class VolcArkConfig {

    // 全局线程安全标记：key=事件唯一ID，value=是否中断后续监听
    private final ConcurrentHashMap<String, Boolean> interruptFlag = new ConcurrentHashMap<>();
    private String apiKey;
    private String baseUrl;
    private ArkService arkService;
    private String model;
    private List<String> models;
    private boolean isDeepThinking;

    @Bean(name = "arkService")
    public ArkService arkService() {
        log.info("开始初始化 ArkService...");
        try {
            arkService = ArkService.builder()
                    .apiKey(apiKey)
                    .baseUrl(baseUrl)
                    .build();
            log.info("ArkService 初始化成功");
            return arkService;
        } catch (Exception e) {
            log.error("ArkService 初始化失败", e);
            throw new RuntimeException("ArkService 初始化异常，请检查配置参数", e);
        }
    }

    @PreDestroy
    public void destroyArkService() {
        if (arkService != null) {
            arkService.shutdownExecutor();
            log.info("ArkService 已优雅关闭，Executor 资源释放完成");
        }
    }
}