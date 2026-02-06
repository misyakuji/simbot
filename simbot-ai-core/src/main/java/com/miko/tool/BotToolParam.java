package com.miko.tool;

import java.lang.annotation.*;

/**
 * BotToolParam 注解用于标记方法参数，以便在工具调用时提供额外的元数据。
 * 该注解可以指定参数的名称和是否为必需项。
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface BotToolParam {

    /**
     * 获取参数的名称。
     *
     * @return 返回一个表示参数名称的字符串。
     */
    String name();


    /**
     * 指定该参数是否为必需项。
     *
     * @return 如果该参数是必需的，则返回true；否则返回false。
     * 默认值为true，表示默认情况下该参数是必需的。
     */
    boolean required() default true;

}
