package com.miko.tool;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 工具定义类，用于描述一个工具的基本信息。
 * 测试：暂未使用
 */
@Data
@Builder
public class ToolDefinition {

    /**
     * 工具的名称。
     */
    private String name;

    /**
     * 工具的描述信息。
     */
    private String description;

    /**
     * 工具的参数定义，键为参数名，值为参数的相关信息。
     */
    private Map<String, Object> parameters;
}
