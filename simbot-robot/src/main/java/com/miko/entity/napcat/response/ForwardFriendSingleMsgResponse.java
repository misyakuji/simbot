package com.miko.entity.napcat.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 消息转发到私聊响应实体
 * 用于接收 /forward_friend_single_msg 接口的返回结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ForwardFriendSingleMsgResponse {

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
    private Object data;

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

//    /**
//     * 数据内部类
//     */
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class ForwardFriendData {
//        // 此接口返回null，暂时保留空结构
//    }
}
