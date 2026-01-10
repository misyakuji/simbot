package com.miko.entity.napcat.enums;

import com.miko.entity.napcat.request.*;
import com.miko.entity.napcat.response.*;
import org.springframework.http.HttpMethod;

/**
 * NapCat API 枚举类
 * <p>
 * 集中管理所有NapCat API的元数据信息，包括API路径、请求方法、功能描述、请求参数类和响应结果类。
 * 提供类型安全的API访问方式，便于API调用和参数验证。
 * </p>
 *
 * @author misyakuji
 * @since 2026-01-10
 */
public enum NapCatApiEnum {

    /**
     * 发送戳一戳
     * <p>向指定用户发送戳一戳操作</p>
     */
    SEND_POKE(
        "/send_poke",
        HttpMethod.POST,
        "发送戳一戳操作",
        SendPokeRequest.class,
        SendPokeResponse.class
    ),

    /**
     * 撤回消息
     * <p>撤回指定的消息</p>
     */
    DELETE_MSG(
        "/delete_msg",
        HttpMethod.POST,
        "撤回指定消息",
        DeleteMsgRequest.class,
        DeleteMsgResponse.class
    ),

    /**
     * 获取群历史消息
     * <p>获取指定群组的历史消息记录</p>
     */
    GET_GROUP_MSG_HISTORY(
        "/get_group_msg_history",
        HttpMethod.GET,
        "获取群组历史消息",
        GetGroupMsgHistoryRequest.class,
        GetGroupMsgHistoryResponse.class
    ),

    /**
     * 获取消息详情
     * <p>根据消息ID获取消息的详细信息</p>
     */
    GET_MSG(
        "/get_msg",
        HttpMethod.GET,
        "获取消息详情",
        GetMsgRequest.class,
        GetMsgResponse.class
    ),

    /**
     * 获取合并转发消息
     * <p>获取合并转发消息的详细内容</p>
     */
    GET_FORWARD_MSG(
        "/get_forward_msg",
        HttpMethod.GET,
        "获取合并转发消息",
        GetForwardMsgRequest.class,
        GetForwardMsgResponse.class
    ),

    /**
     * 贴表情
     * <p>为消息添加表情反应</p>
     */
    SET_MSG_EMOJI_LIKE(
        "/set_msg_emoji_like",
        HttpMethod.POST,
        "为消息贴表情",
        SetMsgEmojiLikeRequest.class,
        SetMsgEmojiLikeResponse.class
    ),

    /**
     * 获取好友历史消息
     * <p>获取与指定好友的历史消息记录</p>
     */
    GET_FRIEND_MSG_HISTORY(
        "/get_friend_msg_history",
        HttpMethod.GET,
        "获取好友历史消息",
        GetFriendMsgHistoryRequest.class,
        GetFriendMsgHistoryResponse.class
    ),

    /**
     * 获取贴表情详情
     * <p>获取消息的表情反应详情</p>
     */
    FETCH_EMOJI_LIKE(
        "/fetch_emoji_like",
        HttpMethod.GET,
        "获取消息表情详情",
        FetchEmojiLikeRequest.class,
        FetchEmojiLikeResponse.class
    ),

    /**
     * 发送合并转发消息
     * <p>发送合并转发的消息</p>
     */
    SEND_FORWARD_MSG(
        "/send_forward_msg",
        HttpMethod.POST,
        "发送合并转发消息",
        SendForwardMsgRequest.class,
        SendForwardMsgResponse.class
    ),

    /**
     * 获取语音消息详情
     * <p>获取语音消息的详细信息</p>
     */
    GET_RECORD(
        "/get_record",
        HttpMethod.GET,
        "获取语音消息详情",
        GetRecordRequest.class,
        GetRecordResponse.class
    ),

    /**
     * 获取图片消息详情
     * <p>获取图片消息的详细信息</p>
     */
    GET_IMAGE(
        "/get_image",
        HttpMethod.GET,
        "获取图片消息详情",
        GetImageRequest.class,
        GetImageResponse.class
    ),

    /**
     * 发送群AI语音
     * <p>向群组发送AI生成的语音消息</p>
     */
    SEND_GROUP_AI_RECORD(
        "/send_group_ai_record",
        HttpMethod.POST,
        "发送群AI语音",
        SendGroupAiRecordRequest.class,
        SendGroupAiRecordResponse.class
    ),

    /**
     * 发送群合并转发消息
     * <p>向群组发送合并转发的消息</p>
     */
    SEND_GROUP_FORWARD_MSG(
        "/send_group_forward_msg",
        HttpMethod.POST,
        "发送群合并转发消息",
        SendGroupForwardMsgRequest.class,
        SendGroupForwardMsgResponse.class
    ),

    /**
     * 消息转发到群
     * <p>将消息转发到指定群组</p>
     */
    FORWARD_GROUP_SINGLE_MSG(
        "/forward_group_single_msg",
        HttpMethod.POST,
        "消息转发到群",
        ForwardGroupSingleMsgRequest.class,
        ForwardGroupSingleMsgResponse.class
    ),

    /**
     * 发送群聊戳一戳
     * <p>在群聊中发送戳一戳操作</p>
     */
    GROUP_POKE(
        "/group_poke",
        HttpMethod.POST,
        "发送群聊戳一戳",
        GroupPokeRequest.class,
        GroupPokeResponse.class
    ),

    /**
     * 发送私聊合并转发消息
     * <p>向私聊发送合并转发的消息</p>
     */
    SEND_PRIVATE_FORWARD_MSG(
        "/send_private_forward_msg",
        HttpMethod.POST,
        "发送私聊合并转发消息",
        SendPrivateForwardMsgRequest.class,
        SendPrivateForwardMsgResponse.class
    ),

    /**
     * 消息转发到私聊
     * <p>将消息转发到私聊</p>
     */
    FORWARD_FRIEND_SINGLE_MSG(
        "/forward_friend_single_msg",
        HttpMethod.POST,
        "消息转发到私聊",
        ForwardFriendSingleMsgRequest.class,
        ForwardFriendSingleMsgResponse.class
    ),

    /**
     * 发送私聊戳一戳
     * <p>在私聊中发送戳一戳操作</p>
     */
    FRIEND_POKE(
        "/friend_poke",
        HttpMethod.POST,
        "发送私聊戳一戳",
        FriendPokeRequest.class,
        FriendPokeResponse.class
    ),

    /**
     * 发送群消息
     * <p>向指定群组发送消息</p>
     */
    SEND_GROUP_MSG(
        "/send_group_msg",
        HttpMethod.POST,
        "发送群消息",
        SendGroupMsgRequest.class,
        SendGroupMsgResponse.class
    ),

    /**
     * 发送私聊消息
     * <p>向指定好友发送私聊消息</p>
     */
    SEND_PRIVATE_MSG(
        "/send_private_msg",
        HttpMethod.POST,
        "发送私聊消息",
        SendPrivateMsgRequest.class,
        SendPrivateMsgResponse.class
    );

    /**
     * API路径
     */
    private final String path;

    /**
     * HTTP请求方法
     */
    private final HttpMethod method;

    /**
     * 功能描述
     */
    private final String description;

    /**
     * 请求参数类
     */
    private final Class<?> requestClass;

    /**
     * 响应结果类
     */
    private final Class<?> responseClass;

    /**
     * 构造函数
     *
     * @param path API路径
     * @param method HTTP请求方法
     * @param description 功能描述
     * @param requestClass 请求参数类
     * @param responseClass 响应结果类
     */
    NapCatApiEnum(String path, HttpMethod method, String description, 
                   Class<?> requestClass, Class<?> responseClass) {
        this.path = path;
        this.method = method;
        this.description = description;
        this.requestClass = requestClass;
        this.responseClass = responseClass;
    }

    /**
     * 获取API路径
     *
     * @return API路径
     */
    public String getPath() {
        return path;
    }

    /**
     * 获取HTTP请求方法
     *
     * @return HTTP请求方法
     */
    public HttpMethod getMethod() {
        return method;
    }

    /**
     * 获取功能描述
     *
     * @return 功能描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 获取请求参数类
     *
     * @return 请求参数类
     */
    public Class<?> getRequestClass() {
        return requestClass;
    }

    /**
     * 获取响应结果类
     *
     * @return 响应结果类
     */
    public Class<?> getResponseClass() {
        return responseClass;
    }

    /**
     * 根据API路径查找对应的枚举值
     *
     * @param path API路径
     * @return 对应的枚举值，如果未找到则返回null
     */
    public static NapCatApiEnum findByPath(String path) {
        if (path == null) {
            return null;
        }
        for (NapCatApiEnum api : values()) {
            if (api.path.equals(path)) {
                return api;
            }
        }
        return null;
    }

    /**
     * 获取所有GET请求的API
     *
     * @return GET请求的API列表
     */
    public static NapCatApiEnum[] getGetApis() {
        return java.util.Arrays.stream(values())
            .filter(api -> api.method == HttpMethod.GET)
            .toArray(NapCatApiEnum[]::new);
    }

    /**
     * 获取所有POST请求的API
     *
     * @return POST请求的API列表
     */
    public static NapCatApiEnum[] getPostApis() {
        return java.util.Arrays.stream(values())
            .filter(api -> api.method == HttpMethod.POST)
            .toArray(NapCatApiEnum[]::new);
    }

    @Override
    public String toString() {
        return String.format("NapCatApiEnum{path='%s', method=%s, description='%s', requestClass=%s, responseClass=%s}",
            path, method, description, requestClass.getSimpleName(), responseClass.getSimpleName());
    }

//    /**
//     * HTTP请求方法枚举
//     */
//    public enum HttpMethod {
//        /**
//         * GET请求
//         */
//        GET,
//
//        /**
//         * POST请求
//         */
//        POST,
//
//        /**
//         * PUT请求
//         */
//        PUT,
//
//        /**
//         * DELETE请求
//         */
//        DELETE;
//
//        /**
//         * 获取HTTP方法的字符串表示
//         *
//         * @return HTTP方法名称
//         */
//        public String value() {
//            return this.name();
//        }
//    }
}
