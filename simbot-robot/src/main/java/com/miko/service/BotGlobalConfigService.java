package com.miko.service;

import com.miko.entity.BotGlobalConfig;
import com.miko.mapper.BotGlobalConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotGlobalConfigService {

    private final BotGlobalConfigMapper botGlobalConfigMapper;

    /**
     * 获取启用的全局配置
     * @return 全局配置信息
     */
    public BotGlobalConfig getEnabledConfig() {
        return botGlobalConfigMapper.getEnabledConfig();
    }

    /**
     * 根据BotId获取全局配置
     * @param botId Bot ID
     * @return 全局配置信息
     */
    public BotGlobalConfig getByBotId(String botId) {
        return botGlobalConfigMapper.getByBotId(botId);
    }

    /**
     * 根据配置ID和BotId获取全局配置
     * @param configId 配置ID
     * @param botId Bot ID
     * @return 全局配置信息
     */
    public BotGlobalConfig getById(Long configId, String botId) {
        return botGlobalConfigMapper.getById(configId, botId);
    }

    /**
     * 创建全局配置
     * @param config 全局配置信息
     */
    public void createConfig(BotGlobalConfig config) {
        LocalDateTime now = LocalDateTime.now();
        config.setCreateTime(now);
        config.setUpdateTime(now);
        botGlobalConfigMapper.insert(config);
        log.info("创建全局配置成功: botId={}", config.getBotId());
    }

    /**
     * 更新全局配置
     * @param config 全局配置信息
     */
    public void updateConfig(BotGlobalConfig config) {
        config.setUpdateTime(LocalDateTime.now());
        botGlobalConfigMapper.update(config);
        log.info("更新全局配置成功: configId={}, botId={}", config.getConfigId(), config.getBotId());
    }

    /**
     * 部分更新全局配置
     * @param config 全局配置信息，包含要更新的字段和必要的ID信息
     */
    public void patchConfig(BotGlobalConfig config) {
        config.setUpdateTime(LocalDateTime.now());
        botGlobalConfigMapper.patch(config);
        log.info("部分更新全局配置成功: configId={}, botId={}", config.getConfigId(), config.getBotId());
    }

    /**
     * 更新配置启用状态
     * @param configId 配置ID
     * @param botId Bot ID
     * @param enabled 启用状态（1-启用，0-禁用）
     */
    public void updateEnabled(Long configId, String botId, String enabled) {
        botGlobalConfigMapper.updateEnabled(configId, botId, enabled, LocalDateTime.now());
        log.info("更新配置启用状态成功: configId={}, botId={}, enabled={}", configId, botId, enabled);
    }

    /**
     * 删除全局配置
     * @param configId 配置ID
     * @param botId Bot ID
     */
    public void deleteConfig(Long configId, String botId) {
        botGlobalConfigMapper.delete(configId, botId);
        log.info("删除全局配置成功: configId={}, botId={}", configId, botId);
    }

}