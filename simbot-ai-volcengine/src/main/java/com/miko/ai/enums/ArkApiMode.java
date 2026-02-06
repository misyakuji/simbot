package com.miko.ai.enums;

import lombok.Data;

/**
 * 火山引擎AI服务API模式枚举类
 * 
 * 定义了不同类型的API调用模式，用于区分聊天完成和响应处理等不同功能接口
 */
public enum ArkApiMode {
    /**
     * 聊天完成API模式 - 用于处理对话交互请求
     * 对应API端点: /chat/completions
     */
    CHAT_API("/chat/completions"),
    
    /**
     * 响应API模式 - 用于处理通用响应请求
     * 对应API端点: /responses
     */
    RESPONSES_API("/responses");

    private final String value;

    /**
     * 枚举构造函数
     * @param value API路径值，用于标识不同的API端点
     */
    ArkApiMode(String value) {
        this.value = value;
    }

    /**
     * 根据字符串值获取对应的枚举实例
     * @param value 要匹配的API路径字符串值
     * @return 匹配的枚举实例，如果未找到匹配项则返回CHAT_API作为默认值
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
