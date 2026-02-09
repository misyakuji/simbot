package com.miko.message;

/**
 * AI角色枚举类，定义了AI交互中的不同角色类型。
 * 
 * SYSTEM: 系统角色，通常用于设置AI的行为和上下文。
 * USER: 用户角色，代表与AI交互的用户。
 * ASSISTANT: 助手角色，代表AI本身。
 * TOOL: 工具角色，表示AI调用的外部工具或功能。
 */
public enum AiRole {

    /**
     * 系统角色，用于定义AI的基础行为和上下文。
     */
    SYSTEM,

    /**
     * 用户角色，代表与AI进行交互的用户。
     */
    USER,

    /**
     * 助手角色，代表AI本身，用于生成回复和执行任务。
     */
    ASSISTANT,

    /**
     * 工具角色，表示AI调用的外部工具或功能。
     */
    TOOL

}
