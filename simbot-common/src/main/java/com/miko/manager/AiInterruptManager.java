package com.miko.manager;


import lombok.Data;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * AI请求中断管理器
 * 管理全局请求中断标记，支持ai/robot等多模块共用
 */
@Getter
@Component
public class AiInterruptManager {

    /**
     * 线程安全的中断标记存储容器
     * key: 请求唯一ID  value: 是否中断(true=中断)
     */
    private final ConcurrentHashMap<String, Boolean> interruptFlag = new ConcurrentHashMap<>();

    /**
     * 设置请求中断标记
     * @param requestId 请求唯一标识
     */
    public void setInterruptFlag(String requestId) {
        if (requestId != null) {
            interruptFlag.put(requestId, Boolean.TRUE);
        }
    }

    /**
     * 判断当前请求是否需要中断
     * @param requestId 请求唯一标识
     * @return true=已标记中断，false=未中断/无标记
     */
    public boolean isInterrupted(String requestId) {
        return Boolean.TRUE.equals(interruptFlag.get(requestId));
    }

    /**
     * 主动清理中断标记，防止内存溢出
     * @param requestId 请求唯一标识
     */
    public void clearFlag(String requestId) {
        interruptFlag.remove(requestId);
    }

    /**
     * 异步延迟清理标记，兜底处理异常场景
     * @param requestId 请求唯一标识
     */
    public void clearFlagAsync(String requestId) {
        CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(10);
                clearFlag(requestId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}