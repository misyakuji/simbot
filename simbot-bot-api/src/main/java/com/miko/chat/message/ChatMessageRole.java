package com.miko.chat.message;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 聊天消息角色枚举类，定义了聊天系统中不同角色的类型。
 * 每个枚举常量代表一种特定的角色，并关联一个字符串值，
 * 该值可用于序列化和反序列化操作。
 */
public enum ChatMessageRole {
    /**
     * 系统角色，通常用于表示系统级别的消息或指令。
     */
    SYSTEM("system"),

    /**
     * 用户角色，表示普通用户的输入消息。
     */
    USER("user"),

    /**
     * 助手角色，表示AI助手的回复或响应。
     */
    ASSISTANT("assistant"),

    /**
     * 函数角色，表示函数调用相关的消息。
     */
    FUNCTION("function"),

    /**
     * 工具角色，表示工具调用相关的消息。
     */
    TOOL("tool");

    /**
     * 角色对应的字符串值，用于JSON序列化和反序列化。
     */
    @JsonValue
    private final String value;

    /**
     * 构造方法，初始化枚举常量的字符串值。
     *
     * @param value 角色对应的字符串值
     */
    private ChatMessageRole(String value) {
        this.value = value;
    }

    /**
     * 获取角色对应的字符串值。
     *
     * @return 角色的字符串值
     */
    public String value() {
        return this.value;
    }
}
