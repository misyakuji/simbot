package com.miko.entity.napcat.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * 用户状态枚举
 * <p>
 * 定义了所有可用的用户状态及其对应的JSON数据结构。
 * 每个状态包含status、ext_status和battery_status三个字段。
 * </p>
 *
 * @author misyakuji
 * @since 2026-01-10
 */
@Getter
public enum UserStatusEnum {

    /**
     * 在线
     */
    ONLINE(
            "在线",
            StatusData.builder()
                    .status(10)
                    .extStatus(0)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * Q我吧
     */
    CALL_ME(
            "Q我吧",
            StatusData.builder()
                    .status(60)
                    .extStatus(0)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 离开
     */
    AWAY(
            "离开",
            StatusData.builder()
                    .status(30)
                    .extStatus(0)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 忙碌
     */
    BUSY(
            "忙碌",
            StatusData.builder()
                    .status(50)
                    .extStatus(0)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 请勿打扰
     */
    DO_NOT_DISTURB(
            "请勿打扰",
            StatusData.builder()
                    .status(70)
                    .extStatus(0)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 隐身
     */
    INVISIBLE(
            "隐身",
            StatusData.builder()
                    .status(40)
                    .extStatus(0)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 听歌中
     */
    LISTENING_MUSIC(
            "听歌中",
            StatusData.builder()
                    .status(10)
                    .extStatus(1028)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 春日限定
     */
    SPRING_LIMITED(
            "春日限定",
            StatusData.builder()
                    .status(10)
                    .extStatus(2037)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 一起元梦
     */
    TOGETHER_YUANMENG(
            "一起元梦",
            StatusData.builder()
                    .status(10)
                    .extStatus(2025)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 求星搭子
     */
    LOOKING_FOR_STAR_PARTNER(
            "求星搭子",
            StatusData.builder()
                    .status(10)
                    .extStatus(2026)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 被掏空
     */
    EMPTIED(
            "被掏空",
            StatusData.builder()
                    .status(10)
                    .extStatus(2014)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 今日天气
     */
    TODAY_WEATHER(
            "今日天气",
            StatusData.builder()
                    .status(10)
                    .extStatus(1030)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 我crash了
     */
    CRASHED(
            "我crash了",
            StatusData.builder()
                    .status(10)
                    .extStatus(2019)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 爱你
     */
    LOVE_YOU(
            "爱你",
            StatusData.builder()
                    .status(10)
                    .extStatus(2006)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 恋爱中
     */
    IN_LOVE(
            "恋爱中",
            StatusData.builder()
                    .status(10)
                    .extStatus(1051)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 好运锦鲤
     */
    LUCKY_KOI(
            "好运锦鲤",
            StatusData.builder()
                    .status(10)
                    .extStatus(1071)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 水逆退散
     */
    BAD_LUCK_GO_AWAY(
            "水逆退散",
            StatusData.builder()
                    .status(10)
                    .extStatus(1201)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 嗨到飞起
     */
    SUPER_HIGH(
            "嗨到飞起",
            StatusData.builder()
                    .status(10)
                    .extStatus(1056)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 元气满满
     */
    FULL_OF_ENERGY(
            "元气满满",
            StatusData.builder()
                    .status(10)
                    .extStatus(1058)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 宝宝认证
     */
    BABY_CERTIFIED(
            "宝宝认证",
            StatusData.builder()
                    .status(10)
                    .extStatus(1070)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 一言难尽
     */
    HARD_TO_EXPLAIN(
            "一言难尽",
            StatusData.builder()
                    .status(10)
                    .extStatus(1063)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 难得糊涂
     */
    HARD_TO_BE_WISE(
            "难得糊涂",
            StatusData.builder()
                    .status(10)
                    .extStatus(2001)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * emo中
     */
    EMO(
            "emo中",
            StatusData.builder()
                    .status(10)
                    .extStatus(1401)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 我太难了
     */
    SO_HARD(
            "我太难了",
            StatusData.builder()
                    .status(10)
                    .extStatus(1062)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 我想开了
     */
    LET_IT_GO(
            "我想开了",
            StatusData.builder()
                    .status(10)
                    .extStatus(2013)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 我没事
     */
    I_AM_FINE(
            "我没事",
            StatusData.builder()
                    .status(10)
                    .extStatus(1052)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 想静静
     */
    WANT_QUIET(
            "想静静",
            StatusData.builder()
                    .status(10)
                    .extStatus(1061)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 悠哉哉
     */
    RELAXED(
            "悠哉哉",
            StatusData.builder()
                    .status(10)
                    .extStatus(1059)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 去旅行
     */
    TRAVELING(
            "去旅行",
            StatusData.builder()
                    .status(10)
                    .extStatus(2015)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 信号弱
     */
    WEAK_SIGNAL(
            "信号弱",
            StatusData.builder()
                    .status(10)
                    .extStatus(1011)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 出去浪
     */
    HANG_OUT(
            "出去浪",
            StatusData.builder()
                    .status(10)
                    .extStatus(2003)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 肝作业
     */
    DOING_HOMEWORK(
            "肝作业",
            StatusData.builder()
                    .status(10)
                    .extStatus(2012)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 学习中
     */
    STUDYING(
            "学习中",
            StatusData.builder()
                    .status(10)
                    .extStatus(1018)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 搬砖中
     */
    WORKING(
            "搬砖中",
            StatusData.builder()
                    .status(10)
                    .extStatus(2023)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 摸鱼中
     */
    SLACKING(
            "摸鱼中",
            StatusData.builder()
                    .status(10)
                    .extStatus(1300)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 无聊中
     */
    BORED(
            "无聊中",
            StatusData.builder()
                    .status(10)
                    .extStatus(1060)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * timi中
     */
    GAMING(
            "timi中",
            StatusData.builder()
                    .status(10)
                    .extStatus(1027)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 睡觉中
     */
    SLEEPING(
            "睡觉中",
            StatusData.builder()
                    .status(10)
                    .extStatus(1016)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 熬夜中
     */
    STAYING_UP_LATE(
            "熬夜中",
            StatusData.builder()
                    .status(10)
                    .extStatus(1032)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 追剧中
     */
    BINGE_WATCHING(
            "追剧中",
            StatusData.builder()
                    .status(10)
                    .extStatus(1021)
                    .batteryStatus(0)
                    .build()
    ),

    /**
     * 我的电量
     */
    BATTERY_STATUS(
            "我的电量",
            StatusData.builder()
                    .status(10)
                    .extStatus(1000)
                    .batteryStatus(0)
                    .build()
    );

    /**
     * 状态名称
     */
    private final String name;

    /**
     * 状态数据
     */
    private final StatusData data;

    /**
     * 构造函数
     *
     * @param name 状态名称
     * @param data 状态数据
     */
    UserStatusEnum(String name, StatusData data) {
        this.name = name;
        this.data = data;
    }

    /**
     * 获取status值
     *
     * @return status值
     */
    public int getStatus() {
        return data.getStatus();
    }

    /**
     * 获取ext_status值
     *
     * @return ext_status值
     */
    public int getExtStatus() {
        return data.getExtStatus();
    }

    /**
     * 获取battery_status值
     *
     * @return battery_status值
     */
    public int getBatteryStatus() {
        return data.getBatteryStatus();
    }

    /**
     * 根据status和ext_status查找对应的状态枚举
     *
     * @param status    状态值
     * @param extStatus 扩展状态值
     * @return 对应的状态枚举，如果未找到则返回null
     */
    public static UserStatusEnum findByStatus(int status, int extStatus) {
        for (UserStatusEnum userStatus : values()) {
            if (userStatus.data.getStatus() == status && userStatus.data.getExtStatus() == extStatus) {
                return userStatus;
            }
        }
        return null;
    }

    /**
     * 根据状态名称查找对应的状态枚举
     *
     * @param name 状态名称
     * @return 对应的状态枚举，如果未找到则返回null
     */
    public static UserStatusEnum findByName(String name) {
        if (name == null) {
            return null;
        }
        for (UserStatusEnum userStatus : values()) {
            if (userStatus.name.equals(name)) {
                return userStatus;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("UserStatusEnum{name='%s', status=%d, extStatus=%d, batteryStatus=%d}",
                name, data.getStatus(), data.getExtStatus(), data.getBatteryStatus());
    }

    /**
     * 状态数据类
     * <p>
     * 用于序列化为JSON格式的状态数据
     * </p>
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusData {
        /**
         * 状态值
         */
        @JsonProperty("status")
        private Integer status;

        /**
         * 扩展状态值
         */
        @JsonProperty("ext_status")
        private Integer extStatus;

        /**
         * 电池状态值
         */
        @JsonProperty("battery_status")
        private Integer batteryStatus;
    }
}
