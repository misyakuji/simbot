package com.miko.service;

import com.miko.affection.FavorabilityContext;
import com.miko.affection.FavorabilityEngine;
import com.miko.affection.FavorabilityResult;
import com.miko.entity.FriendUser;
import com.miko.mapper.BotContactMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotContactService {

    private final BotContactMapper botContactMapper;

    /**
     * 查询好感度
     * 不存在返回 null
     */
    
    public Integer getFavorability(String authorId) {
        return botContactMapper.selectFavorabilityByQqId(authorId);
    }

    /**
     * 初始化好友列表（首次见面）
     */
    
    public void insertFriendUser(String authorId, String remark) {
        FriendUser user = new FriendUser();
        // 设置Uid
        user.setUserId(Long.valueOf(authorId));
        // 默认初始好感度
        user.setFavorability(0);
        // 默认初始亲密等级
        user.setIntimacyLevel(0);
        // 默认初始聊天次数
        user.setTalkCount(0);
        // 最后一次聊天设置为当前时间
        user.setLastTalkTime(null); // 首次见面未聊天，不阻止首次增量
        // 默认初始情绪状态
        user.setMood(0);
        // 默认初始备注
        user.setRemark(remark);
        // 默认初始AI人格模版
        user.setAiPersona("");
        // 当前使用的AI模型
        user.setAiModel("");
        // 默认初始AI随机度
        user.setAiTemperature(BigDecimal.valueOf(0.7));// 默认AI温度
        // AI记忆摘要
        user.setAiMemorySummary("");
        // 首次见面时间
        user.setCreateTime(LocalDateTime.now());
        // 更新时间
        user.setUpdateTime(LocalDateTime.now());

        botContactMapper.insertFriendUser(user);
    }

    
    public void updateAiPrompt(String authorId, String prompt) {
        botContactMapper.updateAiPrompt(authorId, prompt);
    }

    
    public String getFriendUserAiPersona(String chatId) {
        return botContactMapper.getFriendUserAiPersona(chatId);
    }

    
    public FriendUser getFriendUser(String authorId) {
        return botContactMapper.getFriendUser(authorId);
    }

    
    public void updateFriendUser(FriendUser user, String msgFix) {
        // 4️⃣ 构造好感度上下文
        FavorabilityContext ctx = FavorabilityContext.builder()
                .currentFavorability(user.getFavorability())
                .currentIntimacyLevel(user.getIntimacyLevel())
                .currentMood(user.getMood())
                .talkCount(user.getTalkCount())
                .lastTalkTime(user.getLastTalkTime())
                .now(LocalDateTime.now())
                .isFirstMessageToday(new FavorabilityEngine().isFirstToday(user))
                .isNegativeMessage(new FavorabilityEngine().checkNegative(msgFix))
                .build();

        // 5️⃣ 计算好感度变化
        FavorabilityResult result = new FavorabilityEngine().process(ctx);

        // 6️⃣ 更新好友记录
        user.setFavorability(result.getNewFavorability());
        user.setIntimacyLevel(result.getNewIntimacyLevel());
        user.setMood(result.getNewMood());
        user.setTalkCount(user.getTalkCount() + 1);

        // 仅在有好感度增加时更新最后聊天时间，防刷
        if (result.getNewFavorability() != ctx.getCurrentFavorability()) {
            user.setLastTalkTime(ctx.getNow());
        }

        botContactMapper.updateGoodFeeling(user);
    }

    
    public void updateAiModel(String authorId, String targetModel) {
        botContactMapper.updateAiModel(String.valueOf(authorId), targetModel);
        log.info("用户 {} AI模型更新为 {}", authorId, targetModel);
    }
}
