package com.miko.ai.service;

import org.springframework.web.bind.annotation.RequestParam;

/**
 * 火山引擎AI对话服务接口
 * 提供与火山引擎AI模型进行多轮对话的功能
 */
public interface ArkChatService {
    /**
     * 与火山引擎AI模型进行多轮对话
     * 
     * @param prompt 用户输入的对话内容
     * @return AI模型的回复内容
     */
    public String multiChatWithVolcengine(@RequestParam String prompt);
}
