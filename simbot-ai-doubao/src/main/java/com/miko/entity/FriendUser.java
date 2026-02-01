package com.miko.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class FriendUser {
    private int id; // 排序id
    private Long userId;         // QQ号 / 用户唯一ID
    private Integer favorability; // 好感度
    private Integer intimacyLevel; // 亲密等级
    private Integer talkCount;    // 聊天次数
    private LocalDateTime lastTalkTime;    // 最后一次聊天时间
    private Integer mood;         // 当前情绪状态
    private String remark;        // Bot给好友的备注
    private String aiPersona;    //AI人格模板
    private String aiModel;      //使用的AI模型
    private BigDecimal aiTemperature;      //AI随机度
    private String aiMemorySummary; //AI记忆摘要
    private LocalDateTime createTime;      // 首次见面时间
    private LocalDateTime updateTime;      // 更新时间
}
