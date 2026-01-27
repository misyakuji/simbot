package com.miko.service;

import com.miko.entity.napcat.enums.NapCatApiEnum;
import com.miko.entity.napcat.request.*;
import com.miko.entity.napcat.response.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * NapCat API服务类
 * <p>
 * 封装所有NapCat API的调用方法，提供便捷的业务层接口
 * </p>
 *
 * @author misyakuji
 * @since 2026-01-10
 */
@Getter
@Slf4j
@Service
@RequiredArgsConstructor
public class NapCatApiService {

    /**
     * 基础API服务
     * 获取底层BaseApiService实例
     */
    private final BaseApiService baseApiService;

    // ==================== 戳一戳相关 ====================

    /**
     * 发送戳一戳
     *
     * @param request 请求对象
     * @return 响应对象
     */
    public SendPokeResponse sendPoke(SendPokeRequest request) {
        return baseApiService.callApi(NapCatApiEnum.SEND_POKE, request, SendPokeResponse.class);
    }

    /**
     * 发送群聊戳一戳
     *
     * @param request 请求对象
     * @return 响应对象
     */
    public GroupPokeResponse groupPoke(GroupPokeRequest request) {
        return baseApiService.callApi(NapCatApiEnum.GROUP_POKE, request, GroupPokeResponse.class);
    }

    /**
     * 发送私聊戳一戳
     *
     * @param request 请求对象
     * @return 响应对象
     */
    public FriendPokeResponse friendPoke(FriendPokeRequest request) {
        return baseApiService.callApi(NapCatApiEnum.FRIEND_POKE, request, FriendPokeResponse.class);
    }

    // ==================== 消息发送相关 ====================

    /**
     * 发送群消息
     *
     * @param request 请求对象
     * @return 响应对象
     */
    public SendGroupMsgResponse sendGroupMsg(SendGroupMsgRequest request) {
        return baseApiService.callApi(NapCatApiEnum.SEND_GROUP_MSG, request, SendGroupMsgResponse.class);
    }

    /**
     * 发送私聊消息
     *
     * @param request 请求对象
     * @return 响应对象
     */
    public SendPrivateMsgResponse sendPrivateMsg(SendPrivateMsgRequest request) {
        return baseApiService.callApi(NapCatApiEnum.SEND_PRIVATE_MSG, request, SendPrivateMsgResponse.class);
    }

    /**
     * 发送群AI语音
     *
     * @param request 请求对象
     * @return 响应对象
     */
    public SendGroupAiRecordResponse sendGroupAiRecord(SendGroupAiRecordRequest request) {
        return baseApiService.callApi(NapCatApiEnum.SEND_GROUP_AI_RECORD, request, SendGroupAiRecordResponse.class);
    }

    // ==================== 消息撤回相关 ====================

    /**
     * 撤回消息
     *
     * @param request 请求对象
     * @return 响应对象
     */
    public DeleteMsgResponse deleteMsg(DeleteMsgRequest request) {
        return baseApiService.callApi(NapCatApiEnum.DELETE_MSG, request, DeleteMsgResponse.class);
    }

    // ==================== 消息获取相关 ====================

    /**
     * 获取消息详情
     *
     * @param request 请求对象
     * @return 响应对象
     */
    public GetMsgResponse getMsg(GetMsgRequest request) {
        return baseApiService.callApi(NapCatApiEnum.GET_MSG, request, GetMsgResponse.class);
    }

    /**
     * 获取群历史消息
     *
     * @param request 请求对象
     * @return 响应对象
     */
    public GetGroupMsgHistoryResponse getGroupMsgHistory(GetGroupMsgHistoryRequest request) {
        return baseApiService.callApi(NapCatApiEnum.GET_GROUP_MSG_HISTORY, request, GetGroupMsgHistoryResponse.class);
    }

    /**
     * 获取好友历史消息
     *
     * @param request 请求对象
     * @return 响应对象
     */
    public GetFriendMsgHistoryResponse getFriendMsgHistory(GetFriendMsgHistoryRequest request) {
        return baseApiService.callApi(NapCatApiEnum.GET_FRIEND_MSG_HISTORY, request, GetFriendMsgHistoryResponse.class);
    }

    /**
     * 获取合并转发消息
     *
     * @param request 请求对象
     * @return 响应对象
     */
    public GetForwardMsgResponse getForwardMsg(GetForwardMsgRequest request) {
        return baseApiService.callApi(NapCatApiEnum.GET_FORWARD_MSG, request, GetForwardMsgResponse.class);
    }

    /**
     * 获取语音消息详情
     *
     * @param request 请求对象
     * @return 响应对象
     */
    public GetRecordResponse getRecord(GetRecordRequest request) {
        return baseApiService.callApi(NapCatApiEnum.GET_RECORD, request, GetRecordResponse.class);
    }

    /**
     * 获取图片消息详情
     *
     * @param request 请求对象
     * @return 响应对象
     */
    public GetImageResponse getImage(GetImageRequest request) {
        return baseApiService.callApi(NapCatApiEnum.GET_IMAGE, request, GetImageResponse.class);
    }

    // ==================== 消息转发相关 ====================

    /**
     * 发送合并转发消息
     *
     * @param request 请求对象
     * @return 响应对象
     */
    public SendForwardMsgResponse sendForwardMsg(SendForwardMsgRequest request) {
        return baseApiService.callApi(NapCatApiEnum.SEND_FORWARD_MSG, request, SendForwardMsgResponse.class);
    }

    /**
     * 发送群合并转发消息
     *
     * @param request 请求对象
     * @return 响应对象
     */
    public SendGroupForwardMsgResponse sendGroupForwardMsg(SendGroupForwardMsgRequest request) {
        return baseApiService.callApi(NapCatApiEnum.SEND_GROUP_FORWARD_MSG, request, SendGroupForwardMsgResponse.class);
    }

    /**
     * 发送私聊合并转发消息
     *
     * @param request 请求对象
     * @return 响应对象
     */
    public SendPrivateForwardMsgResponse sendPrivateForwardMsg(SendPrivateForwardMsgRequest request) {
        return baseApiService.callApi(NapCatApiEnum.SEND_PRIVATE_FORWARD_MSG, request, SendPrivateForwardMsgResponse.class);
    }

    /**
     * 消息转发到群
     *
     * @param request 请求对象
     * @return 响应对象
     */
    public ForwardGroupSingleMsgResponse forwardGroupSingleMsg(ForwardGroupSingleMsgRequest request) {
        return baseApiService.callApi(NapCatApiEnum.FORWARD_GROUP_SINGLE_MSG, request, ForwardGroupSingleMsgResponse.class);
    }

    /**
     * 消息转发到私聊
     *
     * @param request 请求对象
     * @return 响应对象
     */
    public ForwardFriendSingleMsgResponse forwardFriendSingleMsg(ForwardFriendSingleMsgRequest request) {
        return baseApiService.callApi(NapCatApiEnum.FORWARD_FRIEND_SINGLE_MSG, request, ForwardFriendSingleMsgResponse.class);
    }

    // ==================== 表情相关 ====================

    /**
     * 贴表情
     *
     * @param request 请求对象
     * @return 响应对象
     */
    public SetMsgEmojiLikeResponse setMsgEmojiLike(SetMsgEmojiLikeRequest request) {
        return baseApiService.callApi(NapCatApiEnum.SET_MSG_EMOJI_LIKE, request, SetMsgEmojiLikeResponse.class);
    }

    /**
     * 获取贴表情详情
     *
     * @param request 请求对象
     * @return 响应对象
     */
    public FetchEmojiLikeResponse fetchEmojiLike(FetchEmojiLikeRequest request) {
        return baseApiService.callApi(NapCatApiEnum.FETCH_EMOJI_LIKE, request, FetchEmojiLikeResponse.class);
    }

    /**
     * 获取好友列表（带分类）
     *
     * @return 响应对象
     */
    public GetFriendsWithCategoryResponse getFriendsWithCategory() {
        return baseApiService.callApi(NapCatApiEnum.GET_FRIENDS_WITH_CATEGORY, null, GetFriendsWithCategoryResponse.class);
    }
}
