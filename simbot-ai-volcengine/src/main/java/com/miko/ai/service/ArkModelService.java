package com.miko.ai.service;

import java.util.List;

/**
 * ArkModelService 接口定义了与模型相关的服务方法。
 * 该接口提供了获取模型列表和当前模型的功能。
 */
public interface ArkModelService {
    /**
     * 获取所有可用的模型名称列表。
     *
     * @return 包含模型名称的字符串列表
     */
    List<String> getModels();

    /**
     * 获取当前正在使用的模型名称。
     *
     * @return 当前模型的名称
     */
    String getCurrentModel();
}
