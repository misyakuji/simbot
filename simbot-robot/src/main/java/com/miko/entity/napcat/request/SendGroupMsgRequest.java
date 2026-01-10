package com.miko.entity.napcat.request;

import com.miko.entity.napcat.model.MessageData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 发送群消息请求实体
 * 用于调用 /send_group_msg 接口发送群聊消息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SendGroupMsgRequest {

    /**
     * 群ID
     * 支持数字或字符串类型
     */
    private String groupId;

    /**
     * 消息内容
     */
    private List<MessageData> message;
}
