package com.miko.tool;

import java.lang.annotation.*;

/**
 * BotTool 注解用于标记机器人工具方法。
 * 该注解可以应用于方法上，定义工具的名称、描述以及是否直接返回结果。
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BotTool {
    /**
     * 模型的工具名
     * 默认为空字符串，表示使用方法名作为工具名
     */
    String name() default "";

    /**
     * 工具功能描述
     * 必须提供，用于描述该工具的功能和用途
     */
    String description();

    /**
     * 是否直接将结果作为最终回复
     * 默认为 false，表示由调度器决定如何处理结果
     * 如果设置为 true，则直接将工具执行结果作为最终回复返回
     */
    boolean returnDirect() default false;
}
