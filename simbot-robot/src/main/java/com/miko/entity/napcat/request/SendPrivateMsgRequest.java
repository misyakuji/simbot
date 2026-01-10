package com.miko.entity.napcat.request;

import com.miko.entity.napcat.model.MessageData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 发送私聊消息请求实体
 * 用于调用 /send_private_msg 接口发送私聊消息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SendPrivateMsgRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     * 支持数字或字符串类型
     */
    private String userId;

    /**
     * 消息内容
     */
    private List<MessageData> message;
}
