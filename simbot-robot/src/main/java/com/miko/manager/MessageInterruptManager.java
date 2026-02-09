package com.miko.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息中断管理器
 * 
 * <p>核心功能：</p>
 * <ul>
 *   <li>防止多个监听器对同一消息进行重复处理</li>
 *   <li>确保消息只被一个监听器处理</li>
 *   <li>提供线程安全的中断状态管理</li>
 * </ul>
 * 
 * <p>使用示例：</p>
 * <pre>
 * // 收到消息时检查是否已被处理
 * if (messageInterruptManager.checkAndClear(eventId)) {
 *     // 消息已被其他监听器处理，直接返回
 *     return;
 * }
 * 
 * // 处理消息逻辑...
 * 
 * // 标记消息已处理，防止其他监听器重复处理
 * messageInterruptManager.setInterrupt(eventId);
 * </pre>
 * 
 * <p>注意事项：</p>
 * <ul>
 *   <li>建议在消息处理完成后调用 {@link #forceClear()} 清除标记，避免内存泄漏</li>
 *   <li>{@link #checkOnly(String)} 方法仅用于调试，生产环境应避免使用</li>
 * </ul>
 */
@Slf4j
@Component
public class MessageInterruptManager {
    
    /** 线程安全的中断标记存储容器 */
    private final Map<String, Boolean> interruptFlags = new ConcurrentHashMap<>();

    /**
     * 私有构造函数，防止外部实例化
     * Spring容器会通过反射机制自动注入
     */
    private MessageInterruptManager() {
        // 初始化逻辑（如有需要可在此添加）
    }

    /**
     * 设置消息中断标记
     * 
     * @param eventId 消息事件唯一标识
     * @apiNote 使用 Boolean.TRUE 而非 true，确保类型一致性
     */
    public void setInterrupt(String eventId) {
        interruptFlags.put(eventId, Boolean.TRUE);
    }

    /**
     * 原子性检查并清除中断标记
     * 
     * @param eventId 消息事件唯一标识
     * @return true-消息已被处理并清除标记，false-消息未被处理
     * @implNote 使用 remove() 而非 get()，避免多线程并发问题
     */
    public boolean checkAndClear(String eventId) {
        Boolean interrupted = interruptFlags.remove(eventId);
        return Boolean.TRUE.equals(interrupted);
    }

    /**
     * 仅检查中断状态（不推荐生产环境使用）
     * 
     * @param eventId 消息事件唯一标识
     * @return 中断状态
     * @deprecated 仅用于调试目的，生产环境请使用 {@link #checkAndClear(String)}
     */
    public boolean checkOnly(String eventId) {
        return Boolean.TRUE.equals(interruptFlags.get(eventId));
    }

    /**
     * 强制清除所有中断标记
     * 
     * @apiNote 建议在应用关闭或定期维护时调用，防止内存泄漏
     */
    public void forceClear() {
        int size = interruptFlags.size();
        interruptFlags.clear();
        if (size > 0) {
            log.debug("已强制清除 {} 个中断标记", size);
        }
    }

    /**
     * 输出当前实例哈希值（调试用途）
     * 
     * @implNote 用于确认Spring容器中Bean的单例特性
     */
    public void printInstanceHash() {
        log.info("MessageInterruptManager实例哈希值: {}", this.hashCode());
    }
}
