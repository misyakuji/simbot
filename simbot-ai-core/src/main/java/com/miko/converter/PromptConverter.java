package com.miko.converter;

import com.miko.message.AiMessage;
import com.miko.request.AiRequest;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Prompt转换器，用于将AiRequest转换为Spring AI的Prompt对象
 */
@Component
public class PromptConverter {

    /**
     * 将AiRequest转换为Spring AI的Prompt对象
     *
     * @param request 包含消息列表的AiRequest对象
     * @return 转换后的Prompt对象
     */
    public Prompt convert(AiRequest request) {
        // 将请求中的消息列表转换为Spring AI的消息列表
        List<Message> messages = request.getMessages()
                .stream()
                .map(this::convertMessage)
                .toList();

        // 创建并返回新的Prompt对象
        return new Prompt(messages);
    }

    /**
     * 将单个AiMessage转换为对应的Spring AI Message对象
     *
     * @param msg 需要转换的AiMessage对象
     * @return 转换后的Spring AI Message对象
     */
    private Message convertMessage(AiMessage msg) {
        // 根据消息角色进行不同的转换处理
        return switch (msg.getRole()) {
            // 用户消息转换
            case USER ->
                    new UserMessage(msg.getContent().toString());

            // 系统消息转换
            case SYSTEM ->
                    new SystemMessage(msg.getContent().toString());

            // 助手消息转换
            case ASSISTANT ->
                    new AssistantMessage(msg.getContent().toString());

            // 工具响应消息转换
            case TOOL -> {
                // 先创建 ToolResponse 对象
                ToolResponseMessage.ToolResponse response = new ToolResponseMessage.ToolResponse(
                        msg.getId(),      // 对应 AiMessage 的 id 或 ToolCall id
                        msg.getName(),    // 工具名称
                        msg.getContent().toString()  // 响应内容
                );
                // 使用 builder 模式创建 ToolResponseMessage
                yield ToolResponseMessage.builder()
                        .responses(List.of(response))   // 设置响应列表
                        .metadata(Map.of())             // 设置元数据（空）
                        .build();                       // 构建最终对象
            }
        };
    }
}
