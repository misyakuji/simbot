package com.miko.entity.napcat.request;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 贴表情请求实体
 * 用于调用 /set_msg_emoji_like 接口为消息贴表情
 */
@Data
@Accessors(chain = true)
public class SetMsgEmojiLikeRequest {

    /**
     * 消息ID
     * 支持数字或字符串类型
     */
    private String messageId;

    /**
     * 表情ID
     */
    private Integer emojiId;

    /**
     * 是否贴
     */
    private Boolean set;
}
