package com.miko.tool;

import java.lang.annotation.*;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface BotToolParam {

    /**
     * 获取名称。
     *
     * @return 返回一个表示名称的字符串。
     */
    String name();


    /**
     * 指定某个属性或字段是否为必需项。
     *
     * @return 如果该属性或字段是必需的，则返回true；否则返回false。
     * 默认值为true，表示默认情况下该属性或字段是必需的。
     */
    boolean required() default true;

}
