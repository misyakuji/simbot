package com.miko.chat.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表示一次工具调用的信息。
 * <p>
 * 该类封装了工具调用的相关属性，包括唯一标识符、调用类型、函数调用详情以及索引信息。
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatToolCall {

    /**
     * 工具调用的唯一标识符。
     */
    private String id;

    /**
     * 工具调用的类型，用于区分不同的调用场景或类别。
     */
    private String type;

    /**
     * 函数调用的具体信息，包含函数名称和参数等细节。
     */
    private ChatFunctionCall function;

    /**
     * 工具调用在序列中的索引位置，用于排序或定位。
     */
    private Integer index;
}
