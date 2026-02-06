package com.miko.ai.util;

import com.miko.tool.BotToolMeta;
import com.miko.tool.BotToolParamMeta;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 火山方舟AI模型 JSON Schema 构建工具类
 * <p>
 * 该工具类用于将Java对象元数据转换为符合JSON Schema规范的结构，
 * 主要用于AI模型的工具调用参数定义。
 */
public class ArkSchemaBuilder {

    /**
     * 私有构造函数，防止实例化
     */
    private ArkSchemaBuilder() {
    }

    /**
     * 根据工具元数据构建JSON Schema结构
     *
     * @param meta 工具元数据，包含参数定义信息
     * @return 符合JSON Schema规范的Map结构
     */
    public static Map<String, Object> buildJsonSchema(BotToolMeta meta) {
        // 初始化属性和必填字段列表
        Map<String, Object> properties = new LinkedHashMap<>();
        List<String> required = new ArrayList<>();
        // 遍历所有参数元数据
        for (BotToolParamMeta param : meta.params()) {
            Map<String, Object> propSchema = new LinkedHashMap<>();
            // 映射Java类型到JSON Schema类型
            String type = mapJavaTypeToJsonSchema(param.type());
            propSchema.put("type", type);
            propSchema.put("description", param.name());
            // 处理枚举类型参数
            if (param.type().isEnum()) {
                Object[] enumValues = param.type().getEnumConstants();
                List<String> enumList = new ArrayList<>();
                for (Object ev : enumValues) {
                    enumList.add(ev.toString());
                }
                propSchema.put("enum", enumList);
            }
            // 处理集合和数组类型的参数
            handleCollectionType(param, propSchema);
            // 将参数添加到属性映射中
            properties.put(param.name(), propSchema);
            // 记录必填参数
            if (param.required()) {
                required.add(param.name());
            }
        }
        // 构建最终的Schema结构
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", properties);
        // 只有存在必填字段时才添加required属性
        if (!required.isEmpty()) {
            schema.put("required", required);
        }
        return schema;
    }

    /**
     * 将Java类型映射为对应的JSON Schema类型字符串
     *
     * @param type Java类型Class对象
     * @return 对应的JSON Schema类型字符串
     */
    public static String mapJavaTypeToJsonSchema(Class<?> type) {
        // 基本数据类型映射
        if (type == String.class) return "string";
        if (type == Integer.class || type == int.class) return "integer";
        if (type == Long.class || type == long.class) return "integer";
        if (type == Float.class || type == float.class || type == Double.class || type == double.class) return "number";
        if (type == Boolean.class || type == boolean.class) return "boolean";
        // 枚举类型映射为字符串
        if (type.isEnum()) return "string";
        // 集合和数组类型映射为数组
        if (Collection.class.isAssignableFrom(type) || type.isArray()) return "array";
        // Map类型映射为对象
        if (Map.class.isAssignableFrom(type)) return "object";
        // 时间相关类型映射为字符串
        if (type == LocalDate.class || type == LocalDateTime.class || java.util.Date.class.isAssignableFrom(type))
            return "string";
        // 其他复杂对象默认映射为对象类型
        return "object";
    }

    /**
     * 处理集合和数组类型的Schema定义
     *
     * @param param      参数元数据
     * @param propSchema 属性Schema映射
     */
    private static void handleCollectionType(BotToolParamMeta param, Map<String, Object> propSchema) {
        Class<?> type = param.type();
        // 处理集合类型（List、Set等）
        if (Collection.class.isAssignableFrom(type)) {
            Type genericType = param.genericType();
            // 如果是参数化类型，提取泛型参数
            if (genericType instanceof ParameterizedType pt) {
                Type[] typeArgs = pt.getActualTypeArguments();
                if (typeArgs.length == 1) {
                    Class<?> itemClass = (Class<?>) typeArgs[0];
                    propSchema.put("items", Map.of("type", mapJavaTypeToJsonSchema(itemClass)));
                }
            } else {
                // 非参数化类型默认使用字符串作为元素类型
                propSchema.put("items", Map.of("type", "string"));
            }
        } 
        // 处理数组类型
        else if (type.isArray()) {
            propSchema.put("items", Map.of("type", mapJavaTypeToJsonSchema(type.getComponentType())));
        }
    }
}