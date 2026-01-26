package com.miko.entity;

import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import love.forte.simbot.common.id.ID;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatContext {
    private ID chatId;
    private ChatType chatType;
    private String messageId;
    private List<ChatMessage> messages;

    public enum ChatType {
        GROUP,
        PRIVATE
    }
}
