package com.miko.router;

import com.miko.model.AiModel;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * AI模型路由器实现类
 * 负责根据模型名称获取对应的AI模型实例
 */
@Component
public class AiModelRouterImpl implements AiModelRouter {

    /**
     * 存储所有可用的AI模型，key为模型名称，value为对应的AiModel实例
     */
    private final Map<String, AiModel> aiModels;

    /**
     * 构造函数
     * @param aiModels Spring自动注入所有AiModel Bean集合
     *                 key为Bean名称，value为对应的AiModel实例
     */
    public AiModelRouterImpl(Map<String, AiModel> aiModels) {
        this.aiModels = aiModels;
    }

    /**
     * 根据模型名称获取对应的AI模型
     * @param modelName 模型名称
     * @return 对应的AiModel实例
     * @throws RuntimeException 当找不到指定名称的模型时抛出异常
     */
    @Override
    public AiModel getModel(String modelName) {
        AiModel model = aiModels.get(modelName);
        if (model == null) {
            throw new RuntimeException("找不到 AI 模型: " + modelName);
        }
        return model;
    }
}
