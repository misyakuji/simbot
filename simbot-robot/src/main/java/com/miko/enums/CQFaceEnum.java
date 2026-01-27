package com.miko.enums;

import lombok.Getter;
import love.forte.simbot.common.id.ID;

/**
 * CQ表情枚举
 */
@Getter
public enum CQFaceEnum {
    FACE_0(0, "惊讶"),
    FACE_1(1, "撇嘴"),
    FACE_2(2, "色"),
    FACE_3(3, "发呆"),
    FACE_4(4, "得意"),
    FACE_5(5, "流泪"),
    FACE_6(6, "害羞"),
    FACE_7(7, "闭嘴"),
    FACE_8(8, "睡"),
    FACE_9(9, "大哭"),
    FACE_10(10, "尴尬"),
    FACE_11(11, "发怒"),
    FACE_12(12, "调皮"),
    FACE_13(13, "呲牙"),
    FACE_14(14, "微笑"),
    FACE_15(15, "难过"),
    FACE_16(16, "酷"),
    FACE_18(18, "抓狂"),
    FACE_19(19, "呕吐"),
    FACE_20(20, "偷笑"),
    FACE_21(21, "可爱"),
    FACE_22(22, "白眼"),
    FACE_23(23, "傲慢"),
    FACE_24(24, "饥饿"),
    FACE_25(25, "困"),
    FACE_26(26, "惊恐"),
    FACE_27(27, "流汗"),
    FACE_28(28, "憨笑"),
    FACE_29(29, "悠闲"),
    FACE_30(30, "奋斗"),
    FACE_31(31, "咒骂"),
    FACE_32(32, "疑问"),
    FACE_33(33, "嘘"),
    FACE_34(34, "晕"),
    FACE_36(36, "衰"),
    FACE_37(37, "骷髅"),
    FACE_38(38, "敲打"),
    FACE_39(39, "再见"),
    FACE_41(41, "发抖"),
    FACE_42(42, "爱情"),
    FACE_43(43, "跳跳"),
    FACE_46(46, "猪头"),
    FACE_49(49, "拥抱"),
    FACE_53(53, "蛋糕"),
    FACE_56(56, "刀"),
    FACE_59(59, "便便"),
    FACE_60(60, "咖啡"),
    FACE_63(63, "玫瑰"),
    FACE_64(64, "凋谢"),
    FACE_66(66, "爱心"),
    FACE_67(67, "心碎"),
    FACE_74(74, "太阳"),
    FACE_75(75, "月亮"),
    FACE_76(76, "赞"),
    FACE_77(77, "踩"),
    FACE_78(78, "握手"),
    FACE_79(79, "胜利"),
    FACE_85(85, "飞吻"),
    FACE_86(86, "怄火"),
    FACE_89(89, "西瓜"),
    FACE_96(96, "冷汗"),
    FACE_97(97, "擦汗"),
    FACE_98(98, "抠鼻"),
    FACE_99(99, "鼓掌"),
    FACE_100(100, "糗大了"),
    FACE_101(101, "坏笑"),
    FACE_102(102, "左哼哼"),
    FACE_103(103, "右哼哼"),
    FACE_104(104, "哈欠"),
    FACE_105(105, "鄙视"),
    FACE_106(106, "委屈"),
    FACE_107(107, "快哭了"),
    FACE_108(108, "阴险"),
    FACE_109(109, "亲亲"),
    FACE_110(110, "吓"),
    FACE_111(111, "可怜"),
    FACE_112(112, "菜刀"),
    FACE_114(114, "篮球"),
    FACE_116(116, "示爱"),
    FACE_118(118, "抱拳"),
    FACE_119(119, "勾引"),
    FACE_120(120, "拳头"),
    FACE_121(121, "差劲"),
    FACE_123(123, "NO"),
    FACE_124(124, "OK"),
    FACE_125(125, "转圈"),
    FACE_129(129, "挥手"),
    FACE_137(137, "鞭炮"),
    FACE_144(144, "喝彩"),
    FACE_146(146, "爆筋"),
    FACE_147(147, "棒棒糖"),
    FACE_169(169, "手枪"),
    FACE_171(171, "茶"),
    FACE_172(172, "眨眼睛"),
    FACE_173(173, "泪奔"),
    FACE_174(174, "无奈"),
    FACE_175(175, "卖萌"),
    FACE_176(176, "小纠结"),
    FACE_177(177, "喷血"),
    FACE_178(178, "斜眼笑"),
    FACE_179(179, "doge"),
    FACE_181(181, "戳一戳"),
    FACE_182(182, "笑哭"),
    FACE_183(183, "我最美"),
    FACE_185(185, "羊驼"),
    FACE_187(187, "幽灵"),
    FACE_201(201, "点赞"),
    FACE_212(212, "托腮"),
    FACE_262(262, "/脑阔疼"),
    FACE_263(263, "/沧桑"),
    FACE_264(264, "/捂脸"),
    FACE_265(265, "/辣眼睛"),
    FACE_266(266, "/哦哟"),
    FACE_267(267, "/头秃"),
    FACE_268(268, "/问号脸"),
    FACE_269(269, "/暗中观察"),
    FACE_270(270, "/emm"),
    FACE_271(271, "/吃瓜"),
    FACE_272(272, "/呵呵哒"),
    FACE_273(273, "/我酸了"),
    FACE_277(277, "/汪汪"),
    FACE_281(281, "/无眼笑"),
    FACE_282(282, "/敬礼"),
    FACE_283(283, "/狂笑"),
    FACE_284(284, "/面无表情"),
    FACE_285(285, "/摸鱼"),
    FACE_286(286, "/魔鬼笑"),
    FACE_287(287, "/哦"),
    FACE_289(289, "/睁眼"),
    FACE_293(293, "/摸锦鲤"),
    FACE_294(294, "/期待"),
    FACE_297(297, "/拜谢"),
    FACE_298(298, "/元宝"),
    FACE_299(299, "/牛啊"),
    FACE_300(300, "/胖三斤"),
    FACE_302(302, "/左拜年"),
    FACE_303(303, "/右拜年"),
    FACE_305(305, "/右亲亲"),
    FACE_306(306, "/牛气冲天"),
    FACE_307(307, "/喵喵"),
    FACE_311(311, "/打call"),
    FACE_312(312, "/变形"),
    FACE_314(314, "/仔细分析"),
    FACE_317(317, "/菜汪"),
    FACE_318(318, "/崇拜"),
    FACE_319(319, "/比心"),
    FACE_320(320, "/庆祝"),
    FACE_323(323, "/嫌弃"),
    FACE_324(324, "/吃糖"),
    FACE_325(325, "/惊吓"),
    FACE_326(326, "/生气"),
    FACE_332(332, "/举牌牌"),
    FACE_333(333, "/烟花"),
    FACE_334(334, "/虎虎生威"),
    FACE_336(336, "/豹富"),
    FACE_337(337, "/花朵脸"),
    FACE_338(338, "/我想开了"),
    FACE_339(339, "/舔屏"),
    FACE_341(341, "/打招呼"),
    FACE_342(342, "/酸Q"),
    FACE_343(343, "/我方了"),
    FACE_344(344, "/大怨种"),
    FACE_345(345, "/红包多多"),
    FACE_346(346, "/你真棒棒"),
    FACE_347(347, "/大展宏兔"),
    FACE_349(349, "/坚强"),
    FACE_350(350, "/贴贴"),
    FACE_351(351, "/敲敲"),
    FACE_352(352, "/咦"),
    FACE_353(353, "/拜托"),
    FACE_354(354, "/尊嘟假嘟"),
    FACE_355(355, "/耶"),
    FACE_356(356, "/666"),
    FACE_357(357, "/裂开"),
    FACE_424(424, "/续标识"),
    FACE_425(425, "/求放过"),
    FACE_426(426, "/玩火"),
    FACE_427(427, "/偷感"),
    FACE_428(428, "/收到"),
    FACE_429(429, "/蛇年快乐"),

    // 未知表情（作为默认返回值）
    UNKNOWN(-1, "未知表情");

    // 枚举属性（与JSON字段对应，id转为Integer类型）
    private final Integer faceId;
    private final String faceText;

    /**
     * 枚举构造方法
     *
     * @param faceId   CQ表情id
     * @param faceText 表情文本（可为null）
     */
    CQFaceEnum(Integer faceId, String faceText) {
        this.faceId = faceId;
        this.faceText = faceText;
    }

    /**
     * 根据faceId获取枚举实例
     *
     * @param faceId CQ表情id（可为null）
     * @return 对应枚举实例，无匹配则返回UNKNOWN
     */
    public static CQFaceEnum getByFaceId(Integer faceId) {
        if (faceId == null) {
            return UNKNOWN;
        }
        for (CQFaceEnum faceEnum : values()) {
            if (faceEnum.faceId.equals(faceId)) {
                return faceEnum.faceText == null ? UNKNOWN : faceEnum;
            }
        }
        return UNKNOWN;
    }

    /**
     * 新增：判断表情是否为UNKNOWN（未知）或不存在
     *
     * @param faceId CQ表情id（可为null）
     * @return true=有效表情；false=未知/不存在
     */
    public static boolean isExist(ID faceId) {
        return getByFaceId(Integer.parseInt(faceId.toString())) != UNKNOWN;
    }

    /**
     * 对外提供：根据faceId获取对应的faceText
     *
     * @param faceId CQ表情id
     * @return 表情文本，无匹配则返回"未知表情"
     */
    public static String getFaceTextById(Integer faceId) {
        return getByFaceId(faceId).faceText;
    }

    public static String getFaceTextByID(ID faceId) {
        return getFaceTextById(Integer.parseInt(faceId.toString()));
    }

}
