package com.miko.ai.store;

import com.miko.ai.entity.ChatMessageRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 对话上下文存储器（内存版，生产环境建议替换为 Redis 实现）
 * 功能说明：
 * - 支持群聊和私聊两种对话上下文管理
 * - 自动维护消息历史记录，防止请求体过大
 * - 提供上下文的增删查清功能
 * 存储结构：
 * - key = chatType:groupId:userId & key = chatType:userId
 * - value：消息记录列表
 */
@Slf4j
@Component
public class ChatContextStore {
    
    /** 核心存储结构：key = chatType:groupId:userId & key = chatType:userId -> 消息列表 */
    private final Map<String, List<ChatMessageRecord>> contextMap = new ConcurrentHashMap<>();

    /** 最大历史消息保留数量，避免请求体过大的阈值 */
    private static final int MAX_HISTORY_SIZE = 20;

    /**
     * 构建群聊上下文存储键
     * 
     * @param groupId 群号
     * @param userId 用户ID
     * @return 存储键，格式为 group{groupId}:{userId}
     */
    private String buildGroupKey(String groupId, String userId) {
        return "group" + groupId + ":" + userId;
    }

    /**
     * 构建私聊上下文存储键
     * 
     * @param userId 用户ID
     * @return 存储键，格式为 private:{userId}
     */
    private String buildPrivateKey(String userId) {
        return "private" + ":" + userId;
    }

    /**
     * 获取指定群聊的对话历史记录
     * 
     * @param groupId 群号
     * @param userId 用户ID
     * @return 对话历史记录列表（不存在则创建新的空列表）
     */
    public List<ChatMessageRecord> getGroupHistory(String groupId, String userId) {
        return contextMap.computeIfAbsent(buildGroupKey(groupId, userId), k -> new CopyOnWriteArrayList<>());
    }

    /**
     * 获取指定用户的私聊对话历史记录
     * 
     * @param userId 用户ID
     * @return 对话历史记录列表（不存在则创建新的空列表）
     */
    public List<ChatMessageRecord> getPrivateHistory(String userId) {
        return contextMap.computeIfAbsent(buildPrivateKey(userId), k -> new CopyOnWriteArrayList<>());
    }

    /**
     * 向群聊上下文追加消息记录，并自动截断超长历史
     * 
     * @param groupId 群号
     * @param userId 用户ID
     * @param records 要添加的消息记录数组
     */
    public void addGroupMessages(String groupId, String userId, ChatMessageRecord... records) {
        String key = buildGroupKey(groupId, userId);
        List<ChatMessageRecord> history = contextMap.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>());
        
        // 追加新消息
        history.addAll(Arrays.asList(records));
        
        // 记录调试日志
        log.info("[ChatContextStore] 当前群聊上下文 key={} records={} size={}", key, Arrays.toString(records), history.size());
        
        // 若历史消息超出最大限制，则截断只保留最新的 MAX_HISTORY_SIZE 条
        if (history.size() > MAX_HISTORY_SIZE) {
            List<ChatMessageRecord> trimmed = new ArrayList<>(
                history.subList(history.size() - MAX_HISTORY_SIZE, history.size())
            );
            history.clear();
            history.addAll(trimmed);
        }
    }

    /**
     * 向私聊上下文追加消息记录，并自动截断超长历史
     * 
     * @param userId 用户ID
     * @param records 要添加的消息记录数组
     */
    public void addPrivateMessages(String userId, ChatMessageRecord... records) {
        String key = buildPrivateKey(userId);
        List<ChatMessageRecord> history = contextMap.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>());
        
        // 追加新消息
        history.addAll(Arrays.asList(records));
        
        // 记录警告日志（私聊使用 warn 级别）
        log.info("[ChatContextStore] 当前私聊上下文 key={} records={} size={}", key, Arrays.toString(records), history.size());
        
        // 若历史消息超出最大限制，则截断只保留最新的 MAX_HISTORY_SIZE 条
        if (history.size() > MAX_HISTORY_SIZE) {
            List<ChatMessageRecord> trimmed = new ArrayList<>(
                history.subList(history.size() - MAX_HISTORY_SIZE, history.size())
            );
            history.clear();
            history.addAll(trimmed);
        }
    }

    /**
     * 清空指定群聊的对话上下文
     * 
     * @param groupId 群号
     * @param userId 用户ID
     */
    public void clearGroupContext(String groupId, String userId) {
        contextMap.remove(buildGroupKey(groupId, userId));
    }

    /**
     * 清空指定用户的私聊对话上下文
     * 
     * @param userId 用户ID
     */
    public void clearPrivateContext(String userId) {
        contextMap.remove(buildPrivateKey(userId));
    }

    /**
     * 清空所有对话上下文（慎用）
     */
    public void clearAll() {
        contextMap.clear();
    }
}