package com.miko.ai.constant;

/**
 * 火山方舟对话相关常量定义类
 * 
 * 该类包含了火山方舟AI对话服务所需的各种常量配置，
 * 包括工具调用标识和API调用超时设置等核心参数。
 */
public class ArkChatConstants {
    
    /**
     * 自定义工具执行跟踪前缀标识
     * 用于标识AI工具调用的执行轨迹
     */
    public static final String TOOL_TRACE_PREFIX = "BOT_TOOL_EXEC";
    
    /**
     * 默认API调用超时时间（秒）
     * 当未指定超时时间时使用的默认值
     */
    public static final int DEFAULT_TIMEOUT_SECONDS = 15;

    /**
     * 私有构造函数
     * 防止该常量类被意外实例化
     */
    private ArkChatConstants() {}
}
