package com.miko.napcat.entity.request;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 获取贴表情详情请求实体
 * 用于调用 /fetch_emoji_like 接口获取消息的表情详情
 */
@Data
@Accessors(chain = true)
public class FetchEmojiLikeRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     * 支持数字或字符串类型
     */
    private String messageId;

    /**
     * 表情ID
     */
    private String emojiId;

    /**
     * 表情类型
     */
    private String emojiType;

    /**
     * 数量
     * 默认值：20
     */
    private Integer count = 20;
}
