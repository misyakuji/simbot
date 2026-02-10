package com.miko.napcat.tool;

import com.miko.napcat.service.message.SendGroupMsgService;
import com.miko.napcat.service.message.request.SendGroupAtRequest;
import com.miko.napcat.service.message.request.SendGroupImageRequest;
import com.miko.napcat.service.message.request.SendGroupTextRequest;
import com.miko.napcat.service.message.response.SendGroupMsgResponse;
import com.miko.tool.BotTool;
import com.miko.tool.BotToolParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * QQ群消息发送工具类
 * <p>
 * 提供多种QQ群消息发送功能，包括文本消息、@消息、图片消息等。
 * 支持通过BotTool注解定义可调用的工具方法，便于集成到AI模型中使用。
 * </p>
 *
 * @author YourName
 * @version 1.0
 * @since 2025-04-07
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SendGroupMsgTool {

    private final SendGroupMsgService sendGroupMsgService;

    /**
     * 向指定QQ群发送文本消息
     *
     * @param groupId 群号，用于指定消息发送的目标群组
     * @param text    消息内容，不能为空
     * @return 操作结果提示信息
     * @throws IllegalArgumentException 当groupId或text为空时抛出
     */
    @BotTool(name = "send_group_text", description = "向指定的QQ群中发送文本消息")
    public String sendGroupText(@BotToolParam(name = "groupId") String groupId,
                                @BotToolParam(name = "text") String text) {
        // 参数校验
        if (groupId == null || groupId.trim().isEmpty()) {
            throw new IllegalArgumentException("指定的群号不能为空");
        }
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("消息内容不能为空");
        }

        // 构建请求对象
        SendGroupTextRequest request = new SendGroupTextRequest();
        request.setGroupId(groupId);
        request.setMessage(new SendGroupTextRequest.Message("text", new SendGroupTextRequest.TextData(text)));

        // 发送消息并获取响应
        SendGroupMsgResponse resp = sendGroupMsgService.sendGroupText(request);

        // 返回操作结果
        return resp.toString();
    }

    /**
     * 在指定QQ群中@指定QQ号成员
     *
     * @param groupId 群号，用于指定消息发送的目标群组
     * @param atQq    被@的QQ号，用于指定要@的群成员
     * @return 操作结果提示信息，包含成功@的成员QQ号
     * @throws IllegalArgumentException 当groupId或atQq为空时抛出
     */
    @BotTool(name = "send_group_at", description = "向指定QQ群中@指定QQ号成员，发送群@消息")
    public String sendGroupAt(@BotToolParam(name = "groupId") String groupId,
                              @BotToolParam(name = "atQq") String atQq) {
        // 参数校验
        if (groupId == null || groupId.trim().isEmpty()) {
            throw new IllegalArgumentException("群号不能为空");
        }
        if (atQq == null || atQq.trim().isEmpty()) {
            throw new IllegalArgumentException("被@的QQ号不能为空");
        }

        log.info("🚨 sendGroupAt 工具方法被成功调用，群号：{}，被@QQ：{}", groupId, atQq);

        // 构建请求对象
        SendGroupAtRequest request = new SendGroupAtRequest();
        request.setGroupId(groupId);
        request.setMessage(new SendGroupAtRequest.Message("at", new SendGroupAtRequest.AtData(atQq, "string")));

        // 发送消息并获取响应
        SendGroupMsgResponse resp = sendGroupMsgService.sendGroupAt(request);

        // 返回操作结果
        return resp.toString();
    }

    // 两个类似的群回复方法AI模型可能会重复执行

//    /**
//     * 在指定QQ群中发送回复内容
//     *
//     * @param groupId 群号，用于指定消息发送的目标群组
//     * @param text    回复内容，不能为空
//     * @return 操作结果提示信息
//     * @throws IllegalArgumentException 当groupId或text为空时抛出
//     */
//    @BotTool(name = "send_group_reply", description = "向指定QQ群中发送回复内容")
//    public String sendGroupReply(@BotToolParam(name = "groupId") String groupId,
//                                 @BotToolParam(name = "text") String text) {
//        log.info("🚨 sendGroupReply 工具方法被成功调用，群号：{}，回复内容：{}", groupId, text);
//        // 参数校验
//        if (groupId == null || groupId.trim().isEmpty()) {
//            throw new IllegalArgumentException("群号不能为空");
//        }
//        if (text == null || text.trim().isEmpty()) {
//            throw new IllegalArgumentException("消息内容不能为空");
//        }
//
//        // 构建请求对象
//        SendGroupReplyRequest request = new SendGroupReplyRequest();
//        request.setGroupId(groupId);
//        request.setMessage(List.of(new SendGroupReplyRequest.Message("text", new SendGroupReplyRequest.TextData(text))));
//        log.info("✅ sendGroupReply 请求参数：{}", request);
//        // 发送消息并获取响应
//        SendGroupMsgResponse resp = sendGroupMsgService.sendGroupReply(request);
//        log.info("✅ sendGroupReply 响应结果：{}", resp);
//        // 返回操作结果
//        return resp.toString();
//    }

    /**
     * 向指定QQ群发送图片消息
     *
     * @param groupId 群号，用于指定消息发送的目标群组
     * @param file    图片文件路径或URL，不能为空
     * @return 操作结果提示信息
     * @throws IllegalArgumentException 当groupId或file为空时抛出
     */
    @BotTool(name = "send_group_image", description = "向指定QQ群中发送图片，支持本地图片路径或网络URL")
    public String sendGroupImage(@BotToolParam(name = "groupId") String groupId,
                                 @BotToolParam(name = "file") String file) {
        // 参数校验
        if (groupId == null || groupId.trim().isEmpty()) {
            throw new IllegalArgumentException("群号不能为空");
        }
        if (file == null || file.trim().isEmpty()) {
            throw new IllegalArgumentException("图片链接不能为空");
        }

        // 构建请求对象
        SendGroupImageRequest request = new SendGroupImageRequest();
        request.setGroupId(groupId);
        request.setMessage(new SendGroupImageRequest.Message("image", new SendGroupImageRequest.ImageData(file)));

        // 发送消息并获取响应
        SendGroupMsgResponse resp = sendGroupMsgService.sendGroupImage(request);

        // 返回操作结果
        return resp.toString();
    }

    // TODO 发送群表情

    // TODO 发送群文件

    // TODO 发送群视频

    // TODO 发送群语音

    // TODO 发送群合并转发消息

    // TODO 消息转发到群

    // TODO 发送群聊戳一戳

    // TODO 发送群聊音乐卡片
}