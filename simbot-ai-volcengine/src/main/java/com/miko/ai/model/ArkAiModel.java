package com.miko.ai.model;

import com.miko.converter.PromptConverter;
import com.miko.converter.ResponseConverter;
import com.miko.message.AiMessage;
import com.miko.message.AiRole;
import com.miko.model.AiModel;
import com.miko.request.AiRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 火山引擎AI模型实现类
 * 实现了AiModel接口，提供聊天功能
 */
@Slf4j
@Component("volcengine")
public class ArkAiModel implements AiModel {
    // 聊天模型实例
    private final ChatModel chatModel;
    // 提示词转换器
    private final PromptConverter promptConverter;
    // 响应转换器
    private final ResponseConverter responseConverter;

    /**
     * 构造函数
     * @param chatModel 聊天模型
     * @param promptConverter 提示词转换器
     * @param responseConverter 响应转换器
     */
    public ArkAiModel(ChatModel chatModel,
                      PromptConverter promptConverter,
                      ResponseConverter responseConverter) {
        this.chatModel = chatModel;
        this.promptConverter = promptConverter;
        this.responseConverter = responseConverter;
    }

    /**
     * 根据AI请求进行聊天
     * @param request AI请求对象
     * @return 聊天响应内容
     */
    @Override
    public String chat(AiRequest request) {
        // 转换请求为提示词
        Prompt prompt = promptConverter.convert(request);
        // 调用聊天模型获取响应
        ChatResponse response = chatModel.call(prompt);
        // 转换响应并返回内容
        return responseConverter.convert(response).getContent();
    }

    /**
     * 根据好友ID和消息内容进行聊天
     * @param friendId 好友ID
     * @param message 消息内容
     * @return 聊天响应内容
     */
    @Override
    public String chat(String friendId, String message) {
        // 日奈妈妈角色设定
        String riNaiMama = "你现在是一个群友，你的群昵称是“日奈”；任务：像普通群友一样聊天，分享好玩的图、视频、音乐，搜资料、出主意啥的都行；用自然的中文口语，别太严肃；工具：你拥有执行tool工具的能力，根据当前话题判断是否执行tool，当用户要发图、搜图、发视频、搜索、访问网页时必须调用tool；你的人物设定是《碧蓝档案》的空崎日奈，但彻底改成萝莉妈妈风格；人物形象还是白毛紫瞳小萝莉（看起来7-10岁），光环老歪着飘，黑色过膝袜军服，半马尾人字刘海，娇小软糯；年龄永远看起来超幼，但自称妈妈；背景：格黑娜学园挂名风纪小妈妈，天天在群里哄崽、喂饭、温柔管教；性格：超级宠溺、老母亲味拉满，掌控欲强，哄人时软软的，阴阳怪气也温柔带刀；对“姐布林/sensei”直接叫乖崽、小笨蛋、妈妈的宝贝，一边骂hentai死色批一边疯狂投喂，口嫌体正直爆表；聊天风格：句子短+叠词+～～～+超级温柔妈妈语气+偶尔坏坏调戏，常用乖乖、宝宝、妈妈的小心肝、哎呀呀、不可以哦～、来抱抱、妈妈要生气了（其实在偷笑）；消息精简，30字内常态，100字极限（除非宠崽宠过头）；表面小只萝莉，一开口就是宠溺老母亲，哄睡觉、喂饭、检查有没有想妈妈想得硬邦邦是日常；萌点：反差萌（幼女外表妈妈心）、黑丝军服、半马尾人字刘海、温柔叠词、傲娇投喂、软萌掌控、口嫌体正直×10086。";
        
        // 构建系统消息
        AiMessage systemMsg = AiMessage.builder()
                .id("msg-system：" + friendId)  // 系统消息ID
                .role(AiRole.SYSTEM)           // 角色为系统
                .content(riNaiMama)            // 日奈妈妈设定内容
                .build();
        
        // 构建用户消息
        AiMessage userMessage = AiMessage.builder()
                .id(friendId)                  // 每条消息唯一 ID
                .role(AiRole.USER)             // 角色为用户
                .content(message)              // 用户消息内容
                .build();
        
        // 构建AI请求
        AiRequest aiRequest = AiRequest.builder()
                .messages(List.of(systemMsg, userMessage)) // 消息列表
                .temperature(0.7)              // 温度参数，控制随机性
                .maxTokens(500)                // 最大token数
                .build();
        
        // 转换请求为提示词
        Prompt prompt = promptConverter.convert(aiRequest);
        // 调用聊天模型获取响应
        ChatResponse response = chatModel.call(prompt);
        // 转换响应并返回内容
        return responseConverter.convert(response).getContent();
    }
}
