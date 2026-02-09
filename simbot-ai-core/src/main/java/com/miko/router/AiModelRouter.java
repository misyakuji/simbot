package com.miko.router;

import com.miko.model.AiModel;

/**
 * AI模型路由接口
 * 用于根据模型名称获取对应的AI模型实例
 */
public interface AiModelRouter {
    /**
     * 根据模型名称获取AI模型实例
     * @param model 模型名称
     * @return 对应的AI模型实例
     */
    AiModel getModel(String model);
}
