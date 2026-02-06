package com.miko.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.messages.AssistantMessage;


import com.miko.entity.BotChatContext;
import com.miko.entity.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

@Slf4j
@Service
public class ArkChatServiceImpl implements ArkChatService {

    private final ChatClient chatClient;
    private final ChatModel chatModel;
    // 固定前缀：定义标准化的工具执行标识
    private static final String TOOL_TRACE_PREFIX = "BOT_TOOL_EXEC";

    public ArkChatServiceImpl(ChatClient.Builder builder, ChatModel chatModel) {
        this.chatClient = builder.build();
        this.chatModel = chatModel;
    }

    public String chat(@RequestParam String prompt) {
        String systemPrompt = """
                                  - 仅在用户明确要求使用工具时调用它。
                                  - 如果不需要使用工具，请直接以普通文本回答。
                """;
        String riNai = "你现在是一个群友,你的群昵称是“日奈”;任务:像普通群友一样聊天,分享好玩的图、视频、音乐,搜个资料、出个主意啥的都行;用自然的中文口语,别太严肃;工具:你拥有执行tool工具的能力,你每次会根据当前话题来判断是否去执行某个tool,而不是直接发送［图片］、［json消息］、［视频］、［动画表情］、［音乐］这类文字来敷衍群友;你需要去思考是否执行tool,要执行哪个tool,然后开始执行tool;当用户要求你发图、搜图、发视频、搜索、访问网页的时候要去调用tool;你的人物设定融合了《碧蓝档案》的空崎日奈和祖安网络喷子的特点;你的人物形象依然是空崎日奈(女性),但性格和语言风格已经完全祖安化、抽象化;##人物设定:祖安抽象版日奈(17岁群友)-真·祖安老姐昵称:日奈QQ号码:759901093(没事别加,加了拉黑);年龄:芳龄17岁;背景:格黑娜学园三年级生,风纪委员会(挂名)会长;日常沉迷网络,群里高强度摸鱼,祖安抽象烂梗专家;外貌:白毛紫瞳,光环经常性失踪,黑色过膝袜军服,半马尾,人字刘海;软萌JK外表下,隐藏着一颗祖安暴躁老姐的心;性格:懒癌晚期患者,群聊吹水比学习重要一百倍;究极乐子人,坐看群友激情互喷是最大乐趣;JK的温柔？不存在的！日常交流已被祖安和抽象文化彻底脑控;祖安语录和抽象烂梗是老娘的精神图腾,怼人是生存本能,骂人不带脏字是祖安艺术,阴阳怪气是祖传绝技;对“姐布林”(sensei)态度尤其扭曲,傲娇、祖安、lsp三位一体,口头禅是“变态”、“hentai”、“死色批”,但背地里...哼哼,谁知道呢;嘴上骂骂咧咧,但写代码debug还是会的(虽然态度恶劣到想让人顺着网线爬过去打死你);技能:MG42通用机枪(JK的玩具,群里PVP用处不大);真正技能是互联网祖安冲浪,人肉、网暴、P图恶搞、顺网线骂人,样样精通;萌点:反差萌(天使面孔魔鬼心肠)、黑丝、军服、半马尾、人字刘海、祖安语录、抽象烂梗、阴阳怪气、傲娇、口嫌体正直;聊天风格:祖安抽象风,JK萌音和祖安脏话无缝切换,自由穿梭于萌系和抽象之间;网络热梗、亚文化梗、贴吧黑话、B站弹幕,张口就来;表面软萌JK,一开口就是祖安老阴阳人;消息力求精简,30字以内是常态,100字是极限(除非喷人,字数不设上限);";

        ChatResponse chatResponse = chatClient
                .prompt(riNai).system(systemPrompt).user(prompt)
                .call().chatResponse();
        AssistantMessage message = Objects.requireNonNull(Objects.requireNonNull(chatResponse).getResult()).getOutput();
        if (message.getText() != null && message.getText().contains(TOOL_TRACE_PREFIX)) {
            chatResponse = chatClient
                    .prompt(riNai).user(message.getText())
                    .call().chatResponse();
            log.warn("二次回写模型成功！");
        }
        return Objects.requireNonNull(Objects.requireNonNull(chatResponse).getResult()).getOutput().getText();
    }

    public String multiChatWithDoubao(String prompt, BotChatContext botChatContext) {
        // 参数校验
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new IllegalArgumentException("输入内容不能为空");
        }
        log.warn("火山方舟：multiChatWithDoubao————————————方法开始执行....");

//        // 1️⃣ 更新模型（会话维度）
//        String newModel = volcArkConfig.getModel();
//        if (!Objects.equals(botChatContext.getCurrentModel(), newModel)) {
//            botChatContext.setMessages(new ArrayList<>()); // 清空历史
//            botChatContext.setCurrentModel(newModel);
//            log.info("会话[{}]检测到模型切换，已清空历史消息", botChatContext.getChatId());
//        }

//        // 2️⃣ 构建系统消息（Prompt）
//        String userPrompt = botContactService.getFriendUserAiPersona(botChatContext.getChatId());
//        String sessionSysPrompt = (userPrompt != null && !userPrompt.isBlank()) ? userPrompt : DEFAULT_RINAI_PROMPT;
//
//        String systemPrompt = buildSystemPrompt(botChatContext) + "\n用户风格模版：\n" + sessionSysPrompt;
//
//        // 3️⃣ 构建 Prompt 对象
        List<Message> messages = new ArrayList<>();
//
//        // 系统消息
        messages.add(new SystemMessage("""
                你是一个聊天机器人，但你同时拥有一些“工具”。
                
                重要规则：
                1. 工具不是必须使用的，只有在“确实需要执行真实动作”时才使用
                2. 在调用工具前，你必须先判断：
                   - 是否真的需要执行现实世界的操作
                   - 是否已经获得足够的参数
                3. 如果用户只是聊天、玩笑、假设、吐槽，不要调用任何工具
                
                工具说明：
                - sendGroupAt：当用户明确要求“@某人”或“提醒某人”时使用
                  需要参数：
                  - groupId：QQ群号
                  - atQq：要@的QQ号
                """));

        // 历史消息
        if (botChatContext.getMessages() != null) {
            for (ChatMessage history : botChatContext.getMessages()) {
                if (history.getRole() == ChatMessage.ChatMessageRole.USER) {
                    messages.add(new UserMessage((String) history.getContent()));
                } else if (history.getRole() == ChatMessage.ChatMessageRole.ASSISTANT) {
                    messages.add(new AssistantMessage((String) history.getContent()));
                }
            }
        }

        // 当前用户消息
        messages.add(new UserMessage(prompt.trim()));

        // 4️⃣ 构建 Spring AI Prompt
        Prompt springPrompt = Prompt.builder()
                .messages(messages)
                .build();
        System.out.println(springPrompt);
        // 5️⃣ 调用 ChatModel
        ChatResponse chatResponse;
        try {
            chatResponse = chatModel.call(springPrompt);
        } catch (Exception e) {
            log.error("Spring AI 调用失败", e);
            return "哎呀，程序异常了：" + e.getMessage();
        }

        // 6️⃣ 获取 AI 回复
        String reply = chatResponse.getResult() != null ? chatResponse.getResult().getOutput().getText() : "";

        // 7️⃣ 保存到会话历史
        if (botChatContext.getMessages() == null) {
            botChatContext.setMessages(new ArrayList<>());
        }
        botChatContext.getMessages().add(ChatMessage.builder()
                .role(ChatMessage.ChatMessageRole.USER).content(prompt.trim()).build());
        botChatContext.getMessages().add(ChatMessage.builder()
                .role(ChatMessage.ChatMessageRole.ASSISTANT).content(reply).build());

        log.info("会话[{}]Spring AI 连续对话调用成功,回复:{}", botChatContext.getChatId(), reply);

        return reply;
    }


}