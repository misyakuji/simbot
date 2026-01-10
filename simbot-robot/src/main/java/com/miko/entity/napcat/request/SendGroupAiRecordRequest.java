package com.miko.entity.napcat.request;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 发送群AI语音请求实体
 * 用于调用 /send_group_ai_record 接口发送群聊AI语音
 */
@Data
@Accessors(chain = true)
public class SendGroupAiRecordRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 群ID
     * 支持数字或字符串类型
     */
    private String groupId;

    /**
     * 角色
     * 对应 character_id
     */
    private String character;

    /**
     * 文本
     */
    private String text;
}
