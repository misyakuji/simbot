package com.miko.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class BotTaskModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // 消息内容
    private String content;
    // 发送目标
    private String targetType;
    // 发送目标
    private String targetId;
    // 类型
    private String type;
}
