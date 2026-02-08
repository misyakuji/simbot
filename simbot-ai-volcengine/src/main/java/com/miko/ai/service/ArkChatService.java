package com.miko.ai.service;

/**
 * 火山引擎AI对话服务接口
 * 定义了与火山引擎AI模型进行智能对话交互的核心方法
 */
public interface ArkChatService {
    /**
     * 执行多轮对话交互，向火山引擎AI模型发送用户输入并获取响应
     * responsesApi 接口请求时调用
     *
     * @param prompt 用户输入的对话内容，将作为AI模型的输入提示词
     * @return AI模型生成的自然语言回复内容
     */
    String responsesApiMultiChatWith(String prompt);

    /**
     * 针对指定好友执行个性化多轮对话交互
     * chatApi j接口请求时调用
     *
     * @param friendId 好友的唯一标识符，用于区分不同用户上下文
     * @param msgFix   好友发送的消息内容，作为对话的输入
     * @return AI模型基于好友身份和对话历史生成的个性化回复
     */
    String chatApiMultiChatWith(String friendId, String msgFix);
}
