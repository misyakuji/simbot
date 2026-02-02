package com.miko.ai.converter;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 火山方舟消息格式转换器
 * 设计原则：单一职责原则，仅负责Spring AI标准对象与火山方舟API格式的转换
 */
public class ArkMessageConverter {

    /**
     * 私有构造方法，工具类禁止实例化
     */
    private ArkMessageConverter() {
    }

    /**
     * 将Spring AI标准Prompt转换为火山方舟接口兼容的消息列表
     * @param prompt Spring AI对话提示对象
     * @return 火山方舟要求的消息结构列表
     */
    public static List<Map<String, String>> convertToArkMessages(Prompt prompt) {
        // 初始化消息集合
        List<Map<String, String>> messageList = new ArrayList<>();
        // 空值防护，避免空指针异常
        if (prompt == null) {
            return messageList;
        }

        // 遍历并转换系统消息，仅添加非空文本的消息
        prompt.getSystemMessages().forEach(sysMsg -> {
            addValidMessage(messageList, "system", sysMsg);
        });

        // 遍历并转换用户消息，仅添加非空文本的消息
        prompt.getUserMessages().forEach(userMsg -> {
            addValidMessage(messageList, "user", userMsg);
        });

        // 获取最后一条用户/工具响应消息
        Message lastMsg = prompt.getLastUserOrToolResponseMessage();
        if (lastMsg != null && lastMsg.getText() != null) {
            messageList.add(Map.of(
                    // 使用消息原生角色，不强制赋值assistant，保证上下文准确性
                    "role", lastMsg.getMessageType().getValue(),
                    "content", lastMsg.getText()
            ));
        }

        return messageList;
    }

    /**
     * 通用方法：校验消息文本非空后，添加到消息列表
     * @param list 目标消息集合
     * @param role 消息角色（system/user/assistant）
     * @param message Spring AI标准消息对象
     */
    private static void addValidMessage(List<Map<String, String>> list, String role, Message message) {
        if (message != null && message.getText() != null) {
            list.add(Map.of("role", role, "content", message.getText()));
        }
    }
}