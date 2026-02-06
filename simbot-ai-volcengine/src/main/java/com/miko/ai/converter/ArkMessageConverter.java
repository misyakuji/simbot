package com.miko.ai.converter;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 火山方舟消息格式转换器
 * 设计原则：遵循单一职责原则，专门负责将Spring AI标准对象转换为火山方舟API所需的格式
 */
public class ArkMessageConverter {

    /**
     * 私有构造函数，防止该工具类被实例化
     */
    private ArkMessageConverter() {
        // 工具类不应被实例化
    }

    /**
     * 将Spring AI的Prompt对象转换为火山方舟API兼容的消息列表
     *
     * @param prompt Spring AI对话提示对象，包含系统消息、用户消息等
     * @return 符合火山方舟接口规范的消息结构列表
     */
    public static List<Map<String, String>> convertToArkMessages(Prompt prompt) {
        // 初始化用于存储转换后消息的列表
        List<Map<String, String>> messageList = new ArrayList<>();

        // 输入参数空值检查，防止后续操作出现空指针异常
        if (prompt == null) {
            return messageList;
        }

        // 处理系统消息：遍历所有系统消息，过滤掉内容为空的消息并添加到结果列表
        prompt.getSystemMessages().forEach(sysMsg -> {
            addValidMessage(messageList, "system", sysMsg);
        });

        // 处理用户消息：遍历所有用户消息，过滤掉内容为空的消息并添加到结果列表
        prompt.getUserMessages().forEach(userMsg -> {
            addValidMessage(messageList, "user", userMsg);
        });

        // 处理最后一条用户或工具响应消息
        Message lastMsg = prompt.getLastUserOrToolResponseMessage();
        if (lastMsg != null && lastMsg.getText() != null) {
            messageList.add(Map.of(
                    // 保留原始消息的角色类型，避免强制指定为assistant导致上下文不准确
                    "role", lastMsg.getMessageType().getValue(),
                    "content", lastMsg.getText()
            ));
        }
        return messageList;
    }

    /**
     * 通用辅助方法：验证消息内容是否有效，若有效则将其添加至目标列表
     *
     * @param list    存储消息的目标列表
     * @param role    消息角色（如 system、user、assistant）
     * @param message Spring AI标准消息对象
     */
    private static void addValidMessage(List<Map<String, String>> list, String role, Message message) {
        // 检查消息对象及其文本内容是否为空
        if (message != null && message.getText() != null) {
            list.add(Map.of("role", role, "content", message.getText()));
        }
    }
}