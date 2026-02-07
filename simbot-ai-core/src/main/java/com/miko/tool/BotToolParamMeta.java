package com.miko.tool;

import java.lang.reflect.Type;

/**
 * 用于存储 BotTool 注解解析后的参数元数据信息。
 * <p>
 * 该记录类封装了方法参数的相关信息，包括参数名称、类型、泛型类型、是否为必需参数以及参数在方法中的索引位置。
 *
 * @param name        参数名称
 * @param type        参数的原始类型（Class）
 * @param genericType 参数的泛型类型（Type），可用于处理泛型参数
 * @param required    标识该参数是否为必需参数
 * @param index       参数在方法参数列表中的索引位置（从0开始）
 */
public record BotToolParamMeta(String name,
                               Class<?> type,
                               Type genericType,
                               boolean required,
                               int index) {
}
