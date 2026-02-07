package com.miko.ai.service;

/**
 * 火山引擎AI对话服务接口
 * 提供与火山引擎AI模型进行多轮对话的核心功能
 */
public interface ArkChatService {
    /**
     * 与火山引擎AI模型进行多轮对话交互
     *
     * @param prompt 用户输入的对话内容，作为AI模型的输入提示
     * @return AI模型生成的回复内容字符串
     */
    public String multiChatWithVolcengine(String prompt);

    /**
     * 通过聊天API与指定好友进行多轮对话
     *
     * @param friendId 好友唯一标识符
     * @param msgFix   好友消息
     * @return AI模型针对该好友的个性化回复内容
     */
    String chatApiMultiChatWith(String friendId, String msgFix);
}
