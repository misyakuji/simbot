package com.miko.engine;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * 好感度上下文
 */
@Data
@Builder
public class FavorabilityContext {

    // 用户当前状态
    private int currentFavorability; // 当前的好感度
    private int currentIntimacyLevel; // 当前亲密程度
    private int currentMood; // 当前情绪
    private int talkCount; // 谈话计数

    // 行为上下文
    private LocalDateTime lastTalkTime; // 上次对话时间
    private LocalDateTime now; // 现在对话时间

    // 本次消息特征
    private boolean isFirstMessageToday; // 今天的第一个消息
    private boolean isCommand;
    private boolean isPositiveMessage; // 积极的信息
    private boolean isNegativeMessage; // 负面消息
}

