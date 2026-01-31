package com.miko.engine;

import com.miko.entity.FriendUser;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 计算好感度
 */
public class FavorabilityEngine {
    private static final Set<String> NEGATIVE_KEYWORDS = Set.of(
            "烦", "滚", "傻", "蠢", "闭嘴", "恶心", "讨厌",
            "无聊", "没意思", "不想聊", "别烦我",
            "你是谁", "有病", "智障"
    );

    /**
     * 好感度引擎核心处理逻辑
     * <p>
     * 根据一次用户发言上下文，计算：
     * - 好感度变化
     * - 亲密等级变化
     * - 情绪变化
     */
    public FavorabilityResult process(FavorabilityContext ctx) {

        // 本次消息对好感度的变化值（正负都可能）
        int delta = 0;

        // ===============================
        // 1️⃣ 基础互动奖励
        // ===============================
        // 只要你和她说话，本身就会 +3
        // 防止“冷处理”状态下完全无增长
        delta += 3;

        // ===============================
        // 2️⃣ 时间奖励（每天一次）
        // ===============================
        // 今天第一次主动找她聊天，会有额外加成
        // 用来鼓励“持续关系”，而不是刷屏
        if (ctx.isFirstMessageToday()) {
            delta += 8;
        }

        // ===============================
        // 3️⃣ 当前情绪对好感度的影响
        // ===============================
        // 情绪只允许影响 -1 ~ +1
        // 防止情绪值过大导致好感度异常波动
        int moodImpact = Math.max(-1, Math.min(1, ctx.getCurrentMood()));
        delta += moodImpact;

        // ===============================
        // 4️⃣ 消极消息惩罚
        // ===============================
        // 比如：辱骂、命令式、低情商内容
        // 直接扣 4 点好感
        if (ctx.isNegativeMessage()) {
            delta -= 4;
        }

        // ===============================
        // 🚫 防刷屏机制（重点）
        // ===============================
        // 如果距离上一次聊天不足 5 分钟
        // 本次消息不产生任何好感度变化
        if (ctx.getLastTalkTime() != null) {

            long minutes = Duration.between(
                    ctx.getLastTalkTime(),
                    ctx.getNow()
            ).toMinutes();

            if (minutes < 1) {
                delta = 0;
            } else if (minutes < 5) {
                delta = Math.max(0, delta - 1);
            }

        }

        // ===============================
        // 好感度结算（不能小于 0）
        // ===============================
        int newFavorability = Math.max(
                ctx.getCurrentFavorability() + delta,
                0
        );

        // ===============================
        // 亲密等级重新计算
        // ===============================
        int newIntimacy = IntimacyCalculator.calc(
                newFavorability,
                ctx.getTalkCount() + 1
        );

        // 判断亲密等级是否发生变化
        boolean intimacyChanged =
                newIntimacy != ctx.getCurrentIntimacyLevel();

        // ===============================
        // 情绪变化计算
        // ===============================
        // delta > 0 → 情绪略微变好
        // delta < 0 → 情绪略微变差
        int moodDelta = Integer.compare(delta, 0);

        // 情绪范围限制在 -3 ~ +3
        int newMood = Math.max(
                -3,
                Math.min(3, ctx.getCurrentMood() + moodDelta)
        );

        // ===============================
        // 构建结果对象
        // ===============================
        return FavorabilityResult.builder()
                .oldFavorability(ctx.getCurrentFavorability())
                .newFavorability(newFavorability)

                .oldIntimacyLevel(ctx.getCurrentIntimacyLevel())
                .newIntimacyLevel(newIntimacy)
                .intimacyLevelChanged(intimacyChanged)

                .oldMood(ctx.getCurrentMood())
                .newMood(newMood)
                .build();
    }


    public boolean isFirstToday(FriendUser user) {
        // 从未聊过天，一定是今天第一次
        LocalDateTime lastTalkTime = user.getLastTalkTime();
        if (lastTalkTime == null) {
            return true;
        }

        // 上次聊天的日期
        LocalDate lastDate = lastTalkTime.toLocalDate();

        // 当前日期
        LocalDate today = LocalDate.now();

        // 只要不是同一天，就是今天第一次
        return !lastDate.isEqual(today);
    }

    public boolean checkNegative(String message) {
//        String msg = event.getMessageContent().getPlainText();
        String msg = message;

        if (msg == null || msg.isBlank()) {
            return false;
        }

        // 统一处理
        msg = msg.trim().toLowerCase();

        // 命中关键词
        for (String keyword : NEGATIVE_KEYWORDS) {
            if (msg.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

}

