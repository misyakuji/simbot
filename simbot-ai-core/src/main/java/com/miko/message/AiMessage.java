package com.miko.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI消息类，用于封装AI相关的消息信息。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiMessage {
    /**
     * 消息唯一标识符
     */
    private String id;

    /**
     * AI角色，表示发送消息的AI身份
     */
    private AiRole role;

    /**
     * 消息内容，可以是文本、图像或其他数据类型
     */
    private Object content;

    /**
     * 发送者名称（可选）
     */
    private String name;
}

