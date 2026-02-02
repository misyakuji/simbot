package com.miko.ai.service;

import com.miko.entity.BotChatContext;
import com.miko.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ArkChatServiceImpl implements ArkChatService{

    private final ChatClient chatClient;
    private final ChatModel chatModel;

    public ArkChatServiceImpl(ChatClient.Builder builder, ChatModel chatModel) {
        this.chatClient = builder.build();
        this.chatModel = chatModel;
    }

    public String chat(@RequestParam String prompt) {
        return chatClient
                .prompt(prompt)
                .call()
                .content();
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
//        messages.add(new SystemMessage(systemPrompt));

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