package com.miko.adapter;

import com.miko.config.BotConfig;
import com.miko.config.SimBotConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * SimBotConfig适配器
 * 适配SimBotConfig到common模块的BotConfig接口，解耦common和robot
 */
@Component
@RequiredArgsConstructor
public class SimBotConfigAdapter implements BotConfig {
    /**
     * 注入robot模块的SimBotConfig（原配置类）
     */
    private final SimBotConfig simBotConfig;

    @Override
    public String getApiServerHost() {
        // 适配SimBotConfig的具体字段
        return simBotConfig.getAuthorization().getApiServerHost();
    }

    @Override
    public String getApiAccessToken() {
        return simBotConfig.getAuthorization().getApiAccessToken();
    }
}
