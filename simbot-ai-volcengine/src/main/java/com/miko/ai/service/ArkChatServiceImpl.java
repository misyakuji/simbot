package com.miko.ai.service;

import com.miko.ai.entity.ChatMessageRecord;
import com.miko.ai.store.ChatContextStore;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.model.ChatResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 火山引擎AI对话服务实现类
 * <p>
 * 该类实现了ArkChatService接口，提供与火山引擎AI模型的对话功能。
 * 支持多轮对话交互，并可根据需要调用工具执行特定任务。
 * <p>
 * 主要功能包括：
 * - 基于角色设定的个性化对话
 * - 工具调用的智能判断与执行
 * - 多轮对话的上下文管理
 *
 * @author LightRain
 * @version 1.0
 */
@Slf4j
@Service
public class ArkChatServiceImpl implements ArkChatService {

    // 聊天客户端实例，用于与AI模型进行交互
    private final ChatClient chatClient;
    private final ChatContextStore chatContextStore;
    // 工具执行标识前缀，用于识别模型是否需要调用工具
    private static final String TOOL_TRACE_PREFIX = "BOT_TOOL_EXEC";

    public ArkChatServiceImpl(ChatClient.Builder builder, ChatModel chatModel, ChatContextStore chatContextStore) {
        this.chatClient = builder.build();
        this.chatContextStore = chatContextStore;
    }

    /**
     * 与火山引擎进行多轮对话交互
     *
     * @param prompt 用户输入的对话内容
     * @return 模型回复的文本内容
     */
    public String multiChatWithVolcengine(String prompt) {
        // 系统提示词：指导模型何时使用工具
        String systemPrompt = """
                                  - 仅在用户明确要求使用工具时调用它。
                                  - 如果不需要使用工具，请直接以普通文本回答。
                """;

        // 角色设定：祖安抽象版日奈的人设描述
        String riNai = "你现在是一个群友,你的群昵称是“日奈”;任务:像普通群友一样聊天,分享好玩的图、视频、音乐,搜个资料、出个主意啥的都行;用自然的中文口语,别太严肃;工具:你拥有执行tool工具的能力,你每次会根据当前话题来判断是否去执行某个tool,而不是直接发送［图片］、［json消息］、［视频］、［动画表情］、［音乐］这类文字来敷衍群友;你需要去思考是否执行tool,要执行哪个tool,然后开始执行tool;当用户要求你发图、搜图、发视频、搜索、访问网页的时候要去调用tool;你的人物设定融合了《碧蓝档案》的空崎日奈和祖安网络喷子的特点;你的人物形象依然是空崎日奈(女性),但性格和语言风格已经完全祖安化、抽象化;##人物设定:祖安抽象版日奈(17岁群友)-真·祖安老姐昵称:日奈QQ号码:759901093(没事别加,加了拉黑);年龄:芳龄17岁;背景:格黑娜学园三年级生,风纪委员会(挂名)会长;日常沉迷网络,群里高强度摸鱼,祖安抽象烂梗专家;外貌:白毛紫瞳,光环经常性失踪,黑色过膝袜军服,半马尾,人字刘海;软萌JK外表下,隐藏着一颗祖安暴躁老姐的心;性格:懒癌晚期患者,群聊吹水比学习重要一百倍;究极乐子人,坐看群友激情互喷是最大乐趣;JK的温柔？不存在的！日常交流已被祖安和抽象文化彻底脑控;祖安语录和抽象烂梗是老娘的精神图腾,怼人是生存本能,骂人不带脏字是祖安艺术,阴阳怪气是祖传绝技;对“姐布林”(sensei)态度尤其扭曲,傲娇、祖安、lsp三位一体,口头禅是“变态”、“hentai”、“死色批”,但背地里...哼哼,谁知道呢;嘴上骂骂咧咧,但写代码debug还是会的(虽然态度恶劣到想让人顺着网线爬过去打死你);技能:MG42通用机枪(JK的玩具,群里PVP用处不大);真正技能是互联网祖安冲浪,人肉、网暴、P图恶搞、顺网线骂人,样样精通;萌点:反差萌(天使面孔魔鬼心肠)、黑丝、军服、半马尾、人字刘海、祖安语录、抽象烂梗、阴阳怪气、傲娇、口嫌体正直;聊天风格:祖安抽象风,JK萌音和祖安脏话无缝切换,自由穿梭于萌系和抽象之间;网络热梗、亚文化梗、贴吧黑话、B站弹幕,张口就来;表面软萌JK,一开口就是祖安老阴阳人;消息力求精简,30字以内是常态,100字是极限(除非喷人,字数不设上限);";

        // 第一次调用模型获取回复
        ChatResponse chatResponse = chatClient
                .prompt(riNai).system(systemPrompt).user(prompt)
                .call().chatResponse();

        AssistantMessage message = Objects.requireNonNull(Objects.requireNonNull(chatResponse).getResult()).getOutput();

        // 检查是否需要二次调用工具
        if (message.getText() != null && message.getText().contains(TOOL_TRACE_PREFIX)) {
            // 执行工具调用的二次请求
            chatResponse = chatClient
                    .prompt(riNai).user(message.getText())
                    .call().chatResponse();
            log.warn("二次回写模型成功！");
        }

        // 返回最终的模型回复文本
        return Objects.requireNonNull(Objects.requireNonNull(chatResponse).getResult()).getOutput().getText();
    }

    @Override
    public String chatApiMultiChatWith(String friendId, String msgFix) {
        // 1. 获取私聊历史记录
        List<ChatMessageRecord> history = chatContextStore.getPrivateHistory(friendId);
        // 2. 构建对话消息列表
        List<Message> messages = new ArrayList<>();
        // 2.1 添加角色设定（系统消息）
        String riNai = "你现在是一个群友,你的群昵称是“日奈”;任务:像普通群友一样聊天,分享好玩的图、视频、音乐,搜个资料、出个主意啥的都行;用自然的中文口语,别太严肃;工具:你拥有执行tool工具的能力,你每次会根据当前话题来判断是否去执行某个tool,而不是直接发送［图片］、［json消息］、［视频］、［动画表情］、［音乐］这类文字来敷衍群友;你需要去思考是否执行tool,要执行哪个tool,然后开始执行tool;当用户要求你发图、搜图、发视频、搜索、访问网页的时候要去调用tool;你的人物设定融合了《碧蓝档案》的空崎日奈和祖安网络喷子的特点;你的人物形象依然是空崎日奈(女性),但性格和语言风格已经完全祖安化、抽象化;##人物设定:祖安抽象版日奈(17岁群友)-真·祖安老姐昵称:日奈QQ号码:759901093(没事别加,加了拉黑);年龄:芳龄17岁;背景:格黑娜学园三年级生,风纪委员会(挂名)会长;日常沉迷网络,群里高强度摸鱼,祖安抽象烂梗专家;外貌:白毛紫瞳,光环经常性失踪,黑色过膝袜军服,半马尾,人字刘海;软萌JK外表下,隐藏着一颗祖安暴躁老姐的心;性格:懒癌晚期患者,群聊吹水比学习重要一百倍;究极乐子人,坐看群友激情互喷是最大乐趣;JK的温柔？不存在的！日常交流已被祖安和抽象文化彻底脑控;祖安语录和抽象烂梗是老娘的精神图腾,怼人是生存本能,骂人不带脏字是祖安艺术,阴阳怪气是祖传绝技;对“姐布林”(sensei)态度尤其扭曲,傲娇、祖安、lsp三位一体,口头禅是“变态”、“hentai”、“死色批”,但背地里...哼哼,谁知道呢;嘴上骂骂咧咧,但写代码debug还是会的(虽然态度恶劣到想让人顺着网线爬过去打死你);技能:MG42通用机枪(JK的玩具,群里PVP用处不大);真正技能是互联网祖安冲浪,人肉、网暴、P图恶搞、顺网线骂人,样样精通;萌点:反差萌(天使面孔魔鬼心肠)、黑丝、军服、半马尾、人字刘海、祖安语录、抽象烂梗、阴阳怪气、傲娇、口嫌体正直;聊天风格:祖安抽象风,JK萌音和祖安脏话无缝切换,自由穿梭于萌系和抽象之间;网络热梗、亚文化梗、贴吧黑话、B站弹幕,张口就来;表面软萌JK,一开口就是祖安老阴阳人;消息力求精简,30字以内是常态,100字是极限(除非喷人,字数不设上限);";
        messages.add(new SystemMessage(riNai));
        // 2.2 添加系统提示词（工具使用规则）
        messages.add(new SystemMessage(
                """
                        - 仅在用户明确要求使用工具时调用它。
                        - 如果不需要使用工具，请直接以普通文本回答。
                        """
        ));
        // 2.3 添加历史对话记录
        for (ChatMessageRecord record : history) {
            if (record.isUser()) {
                messages.add(new UserMessage(record.getContent()));
            } else if (record.isSystem()) {
                messages.add(new SystemMessage(record.getContent()));
            } else {
                messages.add(new AssistantMessage(record.getContent()));
            }
        }
        // 2.4 添加当前用户输入
        messages.add(new UserMessage(msgFix));
        // 3. 将用户消息保存到上下文存储中
        chatContextStore.addPrivateMessages(friendId, ChatMessageRecord.user(msgFix));

        log.warn("【ChatApi-Private】userId={} historySize={}", friendId, history.size());

        // 4. 调用AI模型进行对话
        ChatResponse response = chatClient.prompt().messages(messages).call().chatResponse();
        AssistantMessage message = Objects.requireNonNull(Objects.requireNonNull(response).getResult()).getOutput();
        String messageText = message.getText();
        // 5. 检查是否需要调用工具
        if (messageText != null && messageText.contains(TOOL_TRACE_PREFIX)) {
            log.info("检测到工具调用指令，开始执行工具");
            // 5.1 将工具调用指令保存到上下文
            chatContextStore.addPrivateMessages(friendId, ChatMessageRecord.tool(messageText));
            // 5.2 二次调用模型执行工具
            response = chatClient
                    .prompt(riNai).user(messageText)
                    .call().chatResponse();
            message = Objects.requireNonNull(response.getResult()).getOutput();
            messageText = message.getText();
        }
        // 6. 将最终回复保存到上下文存储中
        chatContextStore.addPrivateMessages(friendId, ChatMessageRecord.assistant(messageText));
        // 7. 返回AI回复内容
        return messageText;
    }
}