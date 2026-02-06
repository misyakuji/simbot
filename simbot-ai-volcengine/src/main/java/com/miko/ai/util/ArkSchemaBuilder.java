package com.miko.ai.util;

import com.miko.tool.BotToolMeta;
import com.miko.tool.BotToolParamMeta;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 方舟模型Schema构建工具类
 * 提供将BotToolMeta转换为JSON Schema的功能
 */
public final class ArkSchemaBuilder {

    private ArkSchemaBuilder() {}

    /**
     * 根据BotToolMeta构建JSON Schema
     * 
     * @param meta Bot工具元数据
     * @return JSON Schema Map对象
     */
    public static Map<String, Object> buildJsonSchema(BotToolMeta meta) {
        Map<String, Object> properties = new LinkedHashMap<>();
        List<String> required = new ArrayList<>();

        // 遍历所有参数构建属性Schema
        for (BotToolParamMeta param : meta.params()) {
            Map<String, Object> propSchema = new LinkedHashMap<>();
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

            // 处理集合/数组类型参数
            handleCollectionType(param, propSchema);

            properties.put(param.name(), propSchema);
            if (param.required()) {
                required.add(param.name());
            }
        }

        // 构建最终的Schema对象
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", properties);
        if (!required.isEmpty()) {
            schema.put("required", required);
        }
        return schema;
    }

    /**
     * 将Java类型映射为JSON Schema类型字符串
     * 
     * @param type Java类型Class对象
     * @return 对应的JSON Schema类型字符串
     */
    public static String mapJavaTypeToJsonSchema(Class<?> type) {
        if (type == String.class) return "string";
        if (type == Integer.class || type == int.class) return "integer";
        if (type == Long.class || type == long.class) return "integer";
        if (type == Float.class || type == float.class || type == Double.class || type == double.class) return "number";
        if (type == Boolean.class || type == boolean.class) return "boolean";
        if (type.isEnum()) return "string";
        if (Collection.class.isAssignableFrom(type) || type.isArray()) return "array";
        if (Map.class.isAssignableFrom(type)) return "object";
        if (type == LocalDate.class || type == LocalDateTime.class || java.util.Date.class.isAssignableFrom(type)) return "string";
        return "object";
    }

    /**
     * 处理集合/数组类型的Schema构建
     * 
     * @param param 参数元数据
     * @param propSchema 属性Schema Map
     */
    private static void handleCollectionType(BotToolParamMeta param, Map<String, Object> propSchema) {
        Class<?> type = param.type();
        if (Collection.class.isAssignableFrom(type)) {
            Type genericType = param.genericType();
            if (genericType instanceof ParameterizedType pt) {
                Type[] typeArgs = pt.getActualTypeArguments();
                if (typeArgs.length == 1) {
                    Class<?> itemClass = (Class<?>) typeArgs[0];
                    propSchema.put("items", Map.of("type", mapJavaTypeToJsonSchema(itemClass)));
                }
            } else {
                // 无法确定泛型类型时默认使用string
                propSchema.put("items", Map.of("type", "string"));
            }
        } else if (type.isArray()) {
            propSchema.put("items", Map.of("type", mapJavaTypeToJsonSchema(type.getComponentType())));
        }
    }
}