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
    FACE_18(18, "酷"), // 新增：根据常见对应表，ID 18 常对应“酷”表情
    FACE_19(19, "抓狂"), // 新增：常见对应
    FACE_20(20, "呕吐"), // 新增：常见对应
    FACE_21(21, "可爱"), // 新增
    FACE_22(22, "可怜"), // 新增
    FACE_23(23, "傲慢"), // 新增
    FACE_24(24, "饥饿"), // 新增
    FACE_25(25, "困"), // 新增
    FACE_26(26, "惊恐"), // 新增
    FACE_27(27, "流汗"), // 新增
    FACE_28(28, "憨笑"), // 新增
    FACE_29(29, "悠闲"), // 新增：常见对应“悠闲”或“大兵”
    FACE_30(30, "奋斗"), // 新增
    FACE_31(31, "咒骂"), // 新增
    FACE_32(32, "疑问"), // 新增
    FACE_33(33, "嘘"), // 新增
    FACE_34(34, "晕"), // 新增
    FACE_36(36, "衰"), // 新增
    FACE_37(37, "骷髅"), // 新增
    FACE_38(38, "敲打"), // 新增
    FACE_39(39, "再见"), // 新增
    FACE_41(41, "发抖"), // 新增
    FACE_42(42, "爱情"), // 新增：或对应“爱心”
    FACE_43(43, "跳跳"), // 新增
    FACE_46(46, "猪头"), // 新增
    FACE_49(49, "拥抱"), // 新增
    FACE_53(53, "蛋糕"), // 新增
    FACE_56(56, "礼物"), // 新增
    FACE_59(59, "咖啡"), // 新增
    FACE_60(60, "饭"), // 新增
    FACE_63(63, "闪电"), // 新增
    FACE_64(64, "炸弹"), // 新增
    FACE_66(66, "爱心"), // 新增
    FACE_67(67, "心碎"), // 新增
    FACE_74(74, "太阳"), // 新增
    FACE_75(75, "月亮"), // 新增
    FACE_76(76, "赞"), // 新增
    FACE_77(77, "踩"), // 新增
    FACE_78(78, "握手"), // 新增
    FACE_79(79, "胜利"), // 新增
    FACE_85(85, "飞吻"), // 新增
    FACE_86(86, "怄火"), // 新增
    FACE_89(89, "西瓜"), // 新增
    FACE_96(96, "冷汗"),
    FACE_97(97, "擦汗"), // 新增
    FACE_98(98, "抠鼻"), // 新增
    FACE_99(99, "鼓掌"), // 新增
    FACE_100(100, "糗大了"), // 新增
    FACE_101(101, "坏笑"), // 新增
    FACE_102(102, "左哼哼"), // 新增
    FACE_103(103, "右哼哼"), // 新增
    FACE_104(104, "哈欠"), // 新增
    FACE_105(105, "鄙视"), // 新增
    FACE_106(106, "委屈"), // 新增
    FACE_107(107, "快哭了"), // 新增
    FACE_108(108, "阴险"), // 新增
    FACE_109(109, "亲亲"), // 新增
    FACE_110(110, "吓"), // 新增
    FACE_111(111, "可怜"), // 新增：可能与22重复，但ID不同
    FACE_112(112, "菜刀"), // 新增
    FACE_114(114, "啤酒"), // 新增
    FACE_116(116, "篮球"), // 新增
    FACE_118(118, "乒乓"), // 新增
    FACE_119(119, "示爱"), // 新增
    FACE_120(120, "瓢虫"), // 新增
    FACE_121(121, "抱拳"), // 新增
    FACE_123(123, "NO"), // 新增：即“不行”
    FACE_124(124, "OK"), // 新增
    FACE_125(125, "转圈"), // 新增
    FACE_129(129, "挥手"), // 新增
    FACE_137(137, "磕头"), // 新增
    FACE_144(144, "鞭炮"), // 新增
    FACE_146(146, "喝彩"), // 新增
    FACE_147(147, "祈祷"), // 新增
    FACE_169(169, "翻白眼"), // 新增
    FACE_171(171, "灵机一动"), // 新增
    FACE_172(172, "皱眉"), // 新增
    FACE_173(173, "擦汗"), // 新增：可能与97重复
    FACE_174(174, "偷笑"), // 新增
    FACE_175(175, "无语"), // 新增
    FACE_176(176, "翻白眼"), // 新增：可能与169重复
    FACE_177(177, "嘿哈"), // 新增
    FACE_178(178, "捂脸"), // 新增：与264“/捂脸”类似，但ID不同
    FACE_179(179, "doge"), // 新增
    FACE_181(181, "加油"), // 新增
    FACE_182(182, "笑哭"), // 新增
    FACE_183(183, "我最美"), // 新增
    FACE_185(185, "托腮"), // 新增
    FACE_187(187, "拍手"), // 新增
    FACE_201(201, "点赞"), // 新增：虽然超出200，但在常见列表中
    FACE_212(212, "拒绝"), // 新增
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
     * @param faceId CQ表情id
     * @param faceText 表情文本（可为null）
     */
    CQFaceEnum(Integer faceId, String faceText) {
        this.faceId = faceId;
        this.faceText = faceText;
    }

    /**
     * 根据faceId获取枚举实例
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
     * @param faceId CQ表情id（可为null）
     * @return true=有效表情；false=未知/不存在
     */
    public static boolean isExist(ID faceId) {
        return getByFaceId(Integer.parseInt(faceId.toString())) != UNKNOWN;
    }

    /**
     * 对外提供：根据faceId获取对应的faceText
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
