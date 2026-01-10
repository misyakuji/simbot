package com.miko.entity.napcat.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 获取贴表情详情响应实体
 * 用于接收 /fetch_emoji_like 接口的返回结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FetchEmojiLikeResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

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
    private EmojiLikeDetailData data;

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
    public static class EmojiLikeDetailData {
        /**
         * 结果
         */
        private Integer result;

        /**
         * 错误信息
         */
        private String errMsg;

        /**
         * 表情点赞列表
         */
        private List<EmojiLikeItem> emojiLikesList;

        /**
         * Cookie
         */
        private String cookie;

        /**
         * 是否最后一页
         */
        private Boolean isLastPage;

        /**
         * 是否第一页
         */
        private Boolean isFirstPage;
    }

    /**
     * 表情点赞项
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmojiLikeItem {
        /**
         * Tiny ID
         */
        private String tinyId;

        /**
         * 昵称
         */
        private String nickName;

        /**
         * 头像URL
         */
        private String headUrl;
    }
}
