package com.miko.tool;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;


/**
 * BotToolParser 是一个用于解析带有 @BotTool 注解的方法的工具类。
 * 它能够扫描指定对象中的方法，提取符合要求的工具方法信息，
 * 并将其封装为 BotToolMeta 对象列表，便于后续调用和管理。
 * 扫描 + 解析
 * <p>主要功能包括：
 * <ul>
 *   <li>解析对象中带有 @BotTool 注解的方法</li>
 *   <li>提取方法名称、描述、参数等元数据信息</li>
 *   <li>验证方法参数是否正确标注 @BotToolParam 注解</li>
 *   <li>生成结构化的工具元数据供外部使用</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * BotToolParser parser = new BotToolParser();
 * List<BotToolMeta> tools = parser.parse(myToolInstance);
 * }</pre>
 *
 * @author LightRain
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BotToolParser {
    /**
     * BotToolRegistry，用于存储工具元数据
     */
    private final BotToolRegistry registry;
    /**
     * 工具实例列表
     */
    private final List<Object> toolBeans;

    @PostConstruct
    public void init() {
        // 启动时扫描所有工具类 Bean
        for (Object bean : toolBeans) {
            List<BotToolMeta> metas = parse(bean);
            for (BotToolMeta meta : metas) {
                // 注册到 BotToolRegistry
                registry.register(meta);
                log.info("(๑•̀ㅂ•́)و✧ Registered BotTool (≧∀≦)ゞ | FunctionName：{} Description：{} ,", meta.name(),meta.description());
            }
        }
    }

    /**
     * 解析给定对象中的方法，提取带有 @BotTool 注解的方法信息，并将其封装为 BotToolMeta 对象列表返回。
     *
     * @param bean 需要解析的对象实例，该对象中可能包含带有 @BotTool 注解的方法
     * @return 包含所有匹配方法信息的 BotToolMeta 对象列表
     */
    public List<BotToolMeta> parse(Object bean) {
        // 初始化用于存储解析结果的列表
        List<BotToolMeta> tools = new ArrayList<>();

        // 获取对象的类信息及其声明的所有方法
        Class<?> clazz = bean.getClass();
        Method[] methods = clazz.getDeclaredMethods();

        // 遍历所有方法，筛选出带有 @BotTool 注解的方法并进行解析
        for (Method method : methods) {
            // 跳过没有 @BotTool 注解的方法
            if (!method.isAnnotationPresent(BotTool.class)) {
                continue;
            }

            // 解析当前方法并生成对应的 BotToolMeta 对象
            BotToolMeta meta = parseMethod(bean, method);
            tools.add(meta);
            // 将BotTool工具注册到 BotToolRegistry
            registry.register(meta);
        }

        // 返回解析后的工具元数据列表
        return tools;
    }


    /**
     * 解析单个 @BotTool 方法，提取方法元数据并封装为 BotToolMeta 对象。
     *
     * @param bean   方法所属的 Bean 实例对象
     * @param method 需要解析的 Method 对象
     * @return 返回封装了工具名称、描述、Bean 实例、方法对象及参数信息的 BotToolMeta 对象
     */
    private BotToolMeta parseMethod(Object bean, Method method) {
        // 获取方法上的 @BotTool 注解
        BotTool tool = method.getAnnotation(BotTool.class);

        // 确定工具名称：如果注解中未指定名称，则使用方法名作为默认名称
        String toolName = tool.name().isEmpty()
                ? method.getName()
                : tool.name();

        // 获取工具描述信息
        String description = tool.description();

        // 解析方法参数，生成参数元数据列表
        List<BotToolParamMeta> params = parseParams(method);

        // 设置方法可访问性，确保可以调用私有或受保护的方法
        method.setAccessible(true);

        // 构造并返回 BotToolMeta 对象
        return new BotToolMeta(
                toolName,
                description,
                bean,
                method,
                params
        );
    }


    /**
     * 解析方法参数，提取每个参数的元信息并封装为 BotToolParamMeta 对象列表。
     *
     * @param method 需要解析的方法对象
     * @return 包含所有参数元信息的列表，每个元素为 BotToolParamMeta 类型
     * @throws IllegalStateException 如果方法参数未标注 @BotToolParam 注解，则抛出异常
     */
    private List<BotToolParamMeta> parseParams(Method method) {
        // 初始化参数元信息列表
        List<BotToolParamMeta> params = new ArrayList<>();

        // 获取方法的所有参数
        Parameter[] parameters = method.getParameters();

        // 遍历每个参数，解析其注解和类型信息
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            // 检查参数是否标注了 @BotToolParam 注解
            BotToolParam paramAnn = parameter.getAnnotation(BotToolParam.class);
            if (paramAnn == null) {
                // 如果未标注注解，则抛出异常，提示开发者必须添加注解
                throw new IllegalStateException(
                        "BotTool 方法参数必须标注 @BotToolParam: "
                                + method.getDeclaringClass().getName()
                                + "#" + method.getName()
                );
            }

            // 创建参数元信息对象并添加到列表中
            BotToolParamMeta meta = new BotToolParamMeta(
                    paramAnn.name(),       // 参数名称
                    parameter.getType(),   // 参数类型
                    parameter.getParameterizedType(),   // 泛型类型
                    paramAnn.required(),   // 是否为必填参数
                    i                      // 参数在方法中的索引位置
            );

            params.add(meta);
        }

        // 返回解析后的参数元信息列表
        return params;
    }


}
