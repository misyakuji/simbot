package com.miko.affection;

/**
 * 亲密度计算器
 */
public class IntimacyCalculator {
    /**
     * 亲密等级计算规则
     * <p>
     * 每一行规则表示：
     * favorability >= X  且  talkCount >= Y
     * 就可以达到该亲密等级
     * <p>
     * 下标从 0 开始：
     * RULES[0] -> 1级
     * RULES[1] -> 2级
     * ...
     */
    private static final int[][] RULES = {

            // 达到 1级亲密度的条件：
            // 好感度 >= 20 且 聊天次数 >= 5
            {20, 5},

            // 达到 2级亲密度的条件：
            // 好感度 >= 50 且 聊天次数 >= 15
            {50, 15},

            // 达到 3级亲密度的条件：
            // 好感度 >= 100 且 聊天次数 >= 30
            {100, 30},

            // 达到 4级亲密度的条件：
            // 好感度 >= 150 且 聊天次数 >= 45
            {150, 60},

            // 达到 5级亲密度的条件：
            // 好感度 >= 200 且 聊天次数 >= 60
            {200, 60}
    };

    /**
     * 根据好感度和聊天次数计算亲密等级
     *
     * @param favorability 当前好感度
     * @param talkCount    累计聊天次数
     * @return 亲密等级（从 0 开始，最大为 RULES.length）
     */
    public static int calc(int favorability, int talkCount) {

        // 默认亲密等级为 0（陌生人）
        int level = 0;

        // 从最低等级开始，逐级判断是否满足条件
        for (int i = 0; i < RULES.length; i++) {

            // 如果 当前好感度 >= 该等级要求的好感度
            // 且 当前聊天次数 >= 该等级要求的聊天次数
            if (favorability >= RULES[i][0] && talkCount >= RULES[i][1]) {

                // 满足条件，亲密等级提升到 i + 1
                level = i + 1;

            } else {
                // 一旦某一级不满足，后面的等级一定也不满足
                // 所以直接跳出循环
                break;
            }
        }

        // 返回最终计算出的亲密等级
        return level;
    }

}

