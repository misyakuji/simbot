package com.miko.config;

import com.volcengine.ark.runtime.service.ArkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;

/**
 * 火山方舟 ArkService 配置类
 * 统一管理 ArkService 的初始化、单例和销毁
 */
@Slf4j
@Configuration
public class ArkServiceConfig {

    @Value("${volc.ark.api-key}")
    private String apiKey;

    @Value("${volc.ark.base-url}")
    private String baseUrl;

    private ArkService arkService;

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