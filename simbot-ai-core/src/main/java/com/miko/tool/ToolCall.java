package com.miko.tool;

import lombok.Builder;
import lombok.Data;

/**
 * 工具调用类，用于封装工具调用的相关信息。
 * 测试：暂未使用
 */
@Data
@Builder
public class ToolCall {

    /**
     * 工具调用的唯一标识符。
     * 很重要！！用于tool结果回传。
     */
    private String id;

    /**
     * 工具的名称。
     */
    private String name;

    /**
     * 工具调用的参数。
     * 建议直接存 JSON 字符串。
     */
    private String arguments;
}
