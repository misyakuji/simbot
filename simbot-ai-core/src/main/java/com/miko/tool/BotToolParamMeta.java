package com.miko.tool;

import java.lang.reflect.Type;

/**
 * BotToolParser解析@BotTool注解的元数据信息。
 * 参数级元信息
 *
 * @param name     参数名称
 * @param type     参数类型
 * @param required 是否为必需参数
 * @param index    参数在方法中的索引位置
 */
public record BotToolParamMeta(String name,
                               Class<?> type,
                               Type genericType,
                               boolean required,
                               int index) {
}
