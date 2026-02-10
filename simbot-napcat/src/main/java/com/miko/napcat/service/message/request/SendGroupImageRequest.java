package com.miko.napcat.service.message.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 发送群组图片请求类
 * 用于构建发送到指定群组的图片消息请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SendGroupImageRequest {
    /**
     * 目标群号
     * 用于指定消息发送的目标群组
     */
    @JsonProperty("group_id")
    private String groupId;

    /**
     * 消息内容
     * 包含要发送的具体消息信息
     */
    private Message message;

    /**
     * 消息对象
     * 包含消息类型和具体数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        /**
         * 消息类型
         * 例如："image" 表示图片消息
         */
        private String type;

        /**
         * 消息数据
         * 包含具体的消息内容数据
         */
        private ImageData data;
    }

    /**
     * 图片数据
     * 用于图片类型消息，包含图片的URL或本地路径
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageData {
        /**
         * 图片URL
         * 网络图片的完整URL地址
         */
        private String url;

        /**
         * 图片路径（本地）
         * 本地图片文件的路径
         */
        private String file;

        /**
         * 构造函数
         * @param file 本地图片文件路径
         */
        public ImageData(String file) {
            this.file = file;
        }
    }
}