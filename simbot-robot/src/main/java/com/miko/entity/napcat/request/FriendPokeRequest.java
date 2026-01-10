package com.miko.entity.napcat.request;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 发送私聊戳一戳请求实体
 * 用于调用 /friend_poke 接口发送私聊戳一戳
 */
@Data
@Accessors(chain = true)
public class FriendPokeRequest {

    /**
     * 用户ID（必填）
     * 私聊对象
     * 支持数字或字符串类型
     */
    private String userId;

    /**
     * 目标ID（可选）
     * 戳一戳对象，可不填
     * 支持数字或字符串类型
     */
    private String targetId;
}
