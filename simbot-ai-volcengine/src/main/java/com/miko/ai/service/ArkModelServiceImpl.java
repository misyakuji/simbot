package com.miko.ai.service;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Ark模型服务实现类
 * 用于管理Ark相关的模型配置和服务
 */
@Service
@ConfigurationProperties(prefix = "spring.ai.ark")  // 注意前缀，用于绑定配置属性
public class ArkModelServiceImpl implements ArkModelService{
    
    /**
     * 可用的模型列表
     */
    @Setter
    private List<String> models;

    /**
     * 当前使用的模型名称
     * 从配置文件中读取 spring.ai.openai.chat.options.model 属性
     */
    @Value("${spring.ai.openai.chat.options.model}")
    private String model;

    /**
     * 获取所有可用的模型列表
     * @return 模型名称列表
     */
    @Override
    public List<String> getModels() {
        return models;
    }

    /**
     * 获取当前使用的模型名称
     * @return 当前模型名称
     */
    @Override
    public String getCurrentModel() {
        return model;
    }

}
