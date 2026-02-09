package com.miko.converter;

import com.miko.response.AiResponse;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 响应转换器类，用于将Spring AI的ChatResponse转换为自定义的AiResponse对象。
 */
@Component
public class ResponseConverter {

    /**
     * 将ChatResponse转换为AiResponse对象。
     *
     * @param response Spring AI的ChatResponse对象
     * @return 转换后的AiResponse对象
     */
    public AiResponse convert(ChatResponse response) {
        // 获取生成结果列表
        List<Generation> generations = response.getResults();

        // 提取并拼接所有生成内容
        String content = generations.stream()
                .map(Generation::getOutput)
                .map(AssistantMessage::getText) // 获取助理消息的文本内容
                .collect(Collectors.joining("\n"));

        // 测试：提取工具调用信息
        List<com.miko.tool.ToolCall> toolCalls = generations.stream()
                .map(Generation::getOutput) // 获取助理消息
                .flatMap(msg -> {
                    List<AssistantMessage.ToolCall> calls = msg.getToolCalls();
                    if (!calls.isEmpty()) {
                        return calls.stream(); // 如果有工具调用，则展开流
                    } else {
                        return List.<AssistantMessage.ToolCall>of().stream(); // 否则返回空流
                    }
                })
                .map(c -> com.miko.tool.ToolCall.builder()
                        .id(c.id())           // 工具调用ID
                        .name(c.name())       // 工具名称
                        .arguments(c.arguments()) // 工具参数
                        .build()
                )
                .collect(Collectors.toList());

        // 构建并返回AiResponse对象
        return AiResponse.builder()
                .content(content)         // 设置内容
                .toolCalls(toolCalls)     // 设置工具调用列表
                .finishReason(null)       // 完成原因（可根据需要填充）
                .build();
    }
}
