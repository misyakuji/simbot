package com.miko.tool;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BotTool {
    /**
     * 模型的工具名
     */
    String name() default "";

    /**
     * 工具功能描述
     */
    String description();

    /**
     * 是否直接将结果作为最终回复
     * （默认由调度器决定）
     */
    boolean returnDirect() default false;
}
