package com.miko.engine;

import lombok.Builder;
import lombok.Data;

/**
 * 好感度结果
 */
@Data
@Builder
public class FavorabilityResult {

    private int oldFavorability; // 旧的好感度
    private int newFavorability; // 新的好感度

    private int oldIntimacyLevel; // 旧的亲密程度
    private int newIntimacyLevel; // 新的亲密程度

    private int oldMood; // 旧心情
    private int newMood; // 新心情

    private boolean intimacyLevelChanged; // 亲密程度改变
}
