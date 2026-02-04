package com.miko.tool;

import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * BotTool执行器，负责根据工具名称和参数执行对应的工具方法。
 */
@Component
public class BotToolExecutor {

    // 工具注册表，用于存储和查找已注册的工具
    private final BotToolRegistry registry;

    /**
     * 构造函数，初始化工具执行器
     *
     * @param registry 工具注册表实例
     */
    public BotToolExecutor(BotToolRegistry registry) {
        this.registry = registry;
    }

    /**
     * 执行指定工具
     *
     * @param toolName 工具名
     * @param args     参数 Map，键为参数名，值为参数值
     * @return 方法返回值
     * @throws RuntimeException 当工具未找到或执行失败时抛出
     */
    public Object execute(String toolName, Map<String, Object> args) {
        // 从注册表中获取指定名称的工具元数据，如果不存在则抛出异常
        BotToolMeta meta = registry.getTool(toolName)
                .orElseThrow(() -> new RuntimeException("未找到工具: " + toolName));
        try {
            // 获取工具方法和实例对象
            Method method = meta.method();
            Object bean = meta.bean();
            // 根据参数元数据按顺序准备方法调用参数
            Object[] params = new Object[meta.params().size()];
            for (BotToolParamMeta p : meta.params()) {
                // 按参数索引位置设置参数值
                params[p.index()] = args.get(p.name());
            }
            // 执行工具方法并返回结果
            return method.invoke(bean, params);
        } catch (Exception e) {
            // 捕获执行过程中的所有异常并包装为运行时异常
            throw new RuntimeException("工具调用失败: " + toolName, e);
        }
    }
}
