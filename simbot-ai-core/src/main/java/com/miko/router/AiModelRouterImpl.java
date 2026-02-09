package com.miko.router;

import com.miko.model.AiModel;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * AI模型路由器实现类
 * 
 * 该类负责管理所有可用的AI模型实例，并提供根据模型名称获取对应实例的功能。
 * 通过Spring的依赖注入机制自动收集所有实现了AiModel接口的Bean实例。
 */
@Slf4j
@Component
public class AiModelRouterImpl implements AiModelRouter {

    /**
     * 存储所有可用的AI模型实例
     * 
     * Key: 模型名称（Bean名称）
     * Value: 对应的AiModel实例
     */
    private final Map<String, AiModel> aiModels;

    /**
     * 初始化方法，在Bean创建完成后执行
     * 
     * 该方法会检查是否有AI模型被加载，并记录相关信息到日志中。
     * 如果没有加载任何模型，会输出警告日志；
     * 如果有模型被加载，则会输出每个模型的名称和实现类信息。
     */
    @PostConstruct
    public void init() {
        if (aiModels == null || aiModels.isEmpty()) {
            log.warn("当前没有加载任何 AI 模型！");
        } else {
            aiModels.forEach((name, model) -> 
                log.info("(๑•̀ㅂ•́)و✧ 已加载的 AI 模型列表 - 模型名: {} - 实现类: {}  (≧∀≦)ゞ",
                        name, model.getClass().getSimpleName()));
        }
    }

    /**
     * 构造函数
     * 
     * @param aiModels Spring容器自动注入的所有AiModel类型的Bean集合
     *                 Key为Bean名称，Value为对应的AiModel实例
     */
    public AiModelRouterImpl(Map<String, AiModel> aiModels) {
        this.aiModels = aiModels;
    }

    /**
     * 根据模型名称获取对应的AI模型实例
     * 
     * @param modelName 要获取的AI模型名称
     * @return 对应的AiModel实例
     * @throws RuntimeException 当找不到指定名称的模型时抛出此异常
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
