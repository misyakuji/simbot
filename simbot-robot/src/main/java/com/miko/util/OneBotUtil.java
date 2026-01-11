package com.miko.util;

import com.miko.enums.CQFaceEnum;
import love.forte.simbot.common.id.ID;
import love.forte.simbot.common.id.Identifies;
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotGroupMessageEvent;
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotMessageEvent;
import love.forte.simbot.component.onebot.v11.message.segment.*;

import java.util.Objects;

public class OneBotUtil {

    /**
     * 消息整合
     * @param event OneBot消息事件对象
     * @return 整合后的消息字符串
     */
    public static String fixMessage(OneBotMessageEvent event) {

        // 消息内容
        StringBuilder msgfix = new StringBuilder();
        event.getSourceEvent().getMessage().forEach(msg -> {
            if (msg instanceof OneBotReply reply) {
                // TODO
                ID id = reply.getId();
//                GetMsgApi.create(id).getApiResultDeserializer().deserialize().getData().getMessage()
            }

            if (msg instanceof OneBotText text) {
                msgfix.append(text.getData().getText());
            } else if (msg instanceof OneBotImage image) {
                msgfix.append("img[").append(image.getData().getUrl()).append("]");
            } else if (msg instanceof OneBotFace face) {
                if (CQFaceEnum.isExist(face.getData().getId())){
                    msgfix.append("face[").append(CQFaceEnum.getFaceTextByID(face.getData().getId())).append("]");
                }
            } else if (msg instanceof OneBotJson json) {
                msgfix.append("[Json]").append(json.getData().getData());
            } else if (msg instanceof OneBotAt at) {
                if (event instanceof OneBotGroupMessageEvent groupEvent) {
                    String nick = Objects.requireNonNull(groupEvent.getContent().getMember(Identifies.of(at.getData().getQq()))).getNick();
                    if (nick == null || nick.isEmpty()) {
                        nick = Objects.requireNonNull(groupEvent.getContent().getMember(Identifies.of(at.getData().getQq()))).getName();
                    }
                    msgfix.append("@").append(nick);
                }
            }
        });
        return msgfix.toString();
    }
}
