package com.miko.entity.napcat.response;

import com.miko.entity.napcat.model.MessageData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 获取群历史消息响应实体
 * 用于接收 /get_group_msg_history 接口的返回结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class GetGroupMsgHistoryResponse {

    /**
     * 状态
     */
    private String status;

    /**
     * 返回码
     */
    private Integer retcode;

    /**
     * 数据
     */
    private GroupMsgHistoryData data;

    /**
     * 消息
     */
    private String message;

    /**
     * 回声（ws调用api才有此字段）
     */
    private String echo;

    /**
     * 错误信息（go-cqhttp字段）
     */
    private String wording;

    /**
     * 流式返回标记
     */
    private String stream;

    /**
     * 数据内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupMsgHistoryData {
        /**
         * 消息列表
         */
        private List<MessageDetail> messages;
    }

    /**
     * 消息详情
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageDetail {
        private Long selfId;
        private Long userId;
        private Long time;
        private Long messageId;
        private Long messageSeq;
        private Long realId;
        private String realSeq;
        private String messageType;
        private Sender sender;
        private String rawMessage;
        private Integer font;
        private String subType;
        private List<MessageData> message;
        private String messageFormat;
        private String postType;
        private Long groupId;
    }

    /**
     * 发送者信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sender {
        private Long userId;
        private String nickname;
        private String sex;
        private Integer age;
        private String card;
        private String level;
        private String role;
    }
}
