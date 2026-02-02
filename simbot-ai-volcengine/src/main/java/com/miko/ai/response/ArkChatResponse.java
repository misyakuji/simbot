package com.miko.ai.response;

import lombok.Data;

import java.util.List;

/**
 * ArkChatResponse 类用于封装Ark(火山方舟)聊天服务的响应数据
 * 该类提供了一个数据结构来存储和管理从Ark聊天API返回的响应信息
 */
@Data
public class ArkChatResponse {
    private String id;
    private List<Choice> choices;

    @Data
    public static class Choice {
        private int index;
        private Message message;

        @Data
        public static class Message {
            private String role;
            private String content;
        }
    }

}
