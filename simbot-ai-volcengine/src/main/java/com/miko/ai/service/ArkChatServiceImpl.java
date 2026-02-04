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

    public String aiCallGroupAt(@RequestParam String prompt) {
        String systemPrompt = """
                                  - 仅在用户明确要求使用工具时调用它。
                                  - 如果不需要使用工具，请直接以普通文本回答。
                """;
        ChatResponse chatResponse = chatClient
                .prompt(prompt).system(systemPrompt)
                .call().chatResponse();
        AssistantMessage message = Objects.requireNonNull(Objects.requireNonNull(chatResponse).getResult()).getOutput();
        if (message.getText() != null && message.getText().contains(TOOL_TRACE_PREFIX)) {
            chatResponse = chatClient
                    .prompt(message.getText())
                    .call().chatResponse();
            log.warn("二次回写模型成功！");
        }
        return Objects.requireNonNull(Objects.requireNonNull(chatResponse).getResult()).getOutput().getText();
    }

    @Override
    public String chat(String prompt) {
        return "";
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