package com.miko.ai.enums;

import lombok.Getter;

/**
 * 火山引擎AI服务API模式枚举类
 * <p>
 * 定义了不同类型的API调用模式，用于区分聊天完成和响应处理等不同功能接口。
 * 每个枚举值对应一个具体的API端点路径。
 * </p>
 */
@Getter
public enum ArkApiMode {

    /**
     * 聊天完成API模式
     * <p>
     * 用于处理对话交互请求，支持多轮对话和上下文理解。
     * 对应API端点: {@code /chat/completions}
     * </p>
     */
    CHAT_API("/chat/completions"),

    /**
     * 响应API模式
     * <p>
     * 用于处理通用响应请求，适用于单次问答场景。
     * 对应API端点: {@code /responses}
     * </p>
     */
    RESPONSES_API("/responses");

    private final String value;

    /**
     * 枚举构造函数
     *
     * @param value API路径值，用于唯一标识不同的API端点
     */
    ArkApiMode(String value) {
        this.value = value;
    }

    /**
     * 根据字符串值获取对应的枚举实例
     * <p>
     * 该方法通过遍历所有枚举值来查找匹配项。如果未找到匹配的枚举实例，
     * 将默认返回{@link #CHAT_API}以保证向后兼容性。
     * </p>
     *
     * @param value 要匹配的API路径字符串值，不能为空
     * @return 匹配的枚举实例，如果未找到匹配项则返回{@link #CHAT_API}作为默认值
     * @throws NullPointerException 如果传入的value为null
     */
    public static ArkApiMode getByValue(String value) {
        if (value == null) {
            throw new NullPointerException("API模式值不能为null");
        }

        for (ArkApiMode mode : values()) {
            if (mode.value.equals(value)) {
                return mode;
            }
        }

        // 默认回退到聊天完成API模式（兼容旧版本接口）
        return CHAT_API;
    }
}
