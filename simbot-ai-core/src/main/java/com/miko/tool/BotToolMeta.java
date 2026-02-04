package com.miko.tool;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 工具元数据记录类，用于存储工具的基本信息。
 * 方法级元信息
 *
 * @param name        工具名称
 * @param description 工具描述
 * @param bean        工具实例对象
 * @param method      工具对应的方法
 * @param params      工具参数列表
 */
public record BotToolMeta(String name, String description, Object bean, Method method, List<BotToolParamMeta> params) {


}
