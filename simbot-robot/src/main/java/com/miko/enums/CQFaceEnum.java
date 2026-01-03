package com.miko.enums;

import lombok.Getter;
import love.forte.simbot.common.id.ID;

/**
 * CQ表情枚举
 */
@Getter
public enum CQFaceEnum {
    // 以下是从JSON中提取并去重后的枚举项（按id升序排列，方便查阅）
    FACE_0(0, null),
    FACE_1(1, null),
    FACE_2(2, null),
    FACE_3(3, null),
    FACE_4(4, null),
    FACE_5(5, null),
    FACE_6(6, null),
    FACE_7(7, null),
    FACE_8(8, null),
    FACE_9(9, null),
    FACE_10(10, null),
    FACE_11(11, null),
    FACE_12(12, null),
    FACE_13(13, null),
    FACE_14(14, null),
    FACE_15(15, null),
    FACE_18(18, null),
    FACE_19(19, null),
    FACE_20(20, null),
    FACE_21(21, null),
    FACE_22(22, null),
    FACE_23(23, null),
    FACE_24(24, null),
    FACE_25(25, null),
    FACE_26(26, null),
    FACE_27(27, null),
    FACE_28(28, null),
    FACE_29(29, null),
    FACE_30(30, null),
    FACE_31(31, null),
    FACE_32(32, null),
    FACE_33(33, null),
    FACE_34(34, null),
    FACE_36(36, null),
    FACE_37(37, null),
    FACE_38(38, null),
    FACE_39(39, null),
    FACE_41(41, null),
    FACE_42(42, null),
    FACE_43(43, null),
    FACE_46(46, null),
    FACE_49(49, null),
    FACE_53(53, null),
    FACE_56(56, null),
    FACE_59(59, null),
    FACE_60(60, null),
    FACE_63(63, null),
    FACE_64(64, null),
    FACE_66(66, null),
    FACE_67(67, null),
    FACE_74(74, null),
    FACE_75(75, null),
    FACE_76(76, null),
    FACE_77(77, null),
    FACE_78(78, null),
    FACE_79(79, null),
    FACE_85(85, null),
    FACE_86(86, null),
    FACE_89(89, null),
    FACE_96(96, null),
    FACE_97(97, null),
    FACE_98(98, null),
    FACE_99(99, null),
    FACE_100(100, null),
    FACE_101(101, null),
    FACE_102(102, null),
    FACE_103(103, null),
    FACE_104(104, null),
    FACE_105(105, null),
    FACE_106(106, null),
    FACE_107(107, null),
    FACE_108(108, null),
    FACE_109(109, null),
    FACE_110(110, null),
    FACE_111(111, null),
    FACE_112(112, null),
    FACE_114(114, null),
    FACE_116(116, null),
    FACE_118(118, null),
    FACE_119(119, null),
    FACE_120(120, null),
    FACE_121(121, null),
    FACE_123(123, null),
    FACE_124(124, null),
    FACE_125(125, null),
    FACE_129(129, null),
    FACE_137(137, null),
    FACE_144(144, null),
    FACE_146(146, null),
    FACE_147(147, null),
    FACE_169(169, null),
    FACE_171(171, null),
    FACE_172(172, null),
    FACE_173(173, null),
    FACE_174(174, null),
    FACE_175(175, null),
    FACE_176(176, null),
    FACE_177(177, null),
    FACE_178(178, null),
    FACE_179(179, null),
    FACE_181(181, null),
    FACE_182(182, null),
    FACE_183(183, null),
    FACE_185(185, null),
    FACE_187(187, null),
    FACE_201(201, null),
    FACE_212(212, null),
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
    FACE_349(349, "/坚强"),
    FACE_350(350, null),
    FACE_351(351, "/敲敲"),
    FACE_352(352, "/咦"),
    FACE_353(353, "/拜托"),
    FACE_354(354, "/尊嘟假嘟"),
    FACE_355(355, "/耶"),
    FACE_356(356, "/666"),
    FACE_357(357, "/裂开"),
    FACE_424(424, "/续标识"),
    FACE_425(425, null),
    FACE_426(426, null),
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
        // 处理null值，补充默认名称
        this.faceText = (faceText == null) ? "未知表情" : faceText;
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
