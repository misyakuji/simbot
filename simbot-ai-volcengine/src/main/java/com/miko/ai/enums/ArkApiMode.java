package com.miko.ai.enums;

/**
 * 枚举类，定义了火山引擎AI服务的不同API模式
 */
public enum ArkApiMode {
    /**
     * 聊天完成API模式
     */
    CHAT_API("/chat/completions"),
    
    /**
     * 响应API模式
     */
    RESPONSES_API("/responses");

    private final String value;

    /**
     * 构造函数
     * @param value API路径值
     */
    ArkApiMode(String value) {
        this.value = value;
    }

    /**
     * 根据字符串匹配枚举
     * @param value 要匹配的字符串值
     * @return 匹配的枚举实例，如果未找到则返回CHAT_API作为默认值
     */
    public static ArkApiMode getByValue(String value) {
        for (ArkApiMode mode : values()) {
            if (mode.value.equals(value)) {
                return mode;
            }
        }
        // 默认使用旧版接口
        return CHAT_API;
    }
}
