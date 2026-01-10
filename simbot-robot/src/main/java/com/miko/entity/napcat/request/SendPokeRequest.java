package com.miko.entity.napcat.request;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 发送戳一戳请求实体
 * 用于调用 /send_poke 接口发送戳一戳消息
 */
@Data
@Accessors(chain = true)
public class SendPokeRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（必填）
     * 支持数字或字符串类型
     */
    private String userId;

    /**
     * 群ID（可选）
     * 不填则为私聊戳
     * 支持数字或字符串类型
     */
    private String groupId;

    /**
     * 戳一戳对象（可选）
     * 支持数字或字符串类型
     */
    private String targetId;
}
