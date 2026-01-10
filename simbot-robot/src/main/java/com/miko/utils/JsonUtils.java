package com.miko.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * JSON工具类
 * <p>
 * 提供JSON序列化和反序列化的工具方法
 * </p>
 *
 * @author misyakuji
 * @since 2026-01-10
 */
@Slf4j
public class JsonUtils {

    /**
     * ObjectMapper实例
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        // 配置ObjectMapper
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    /**
     * 将对象序列化为JSON字符串
     *
     * @param obj 对象
     * @return JSON字符串
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败: {}", e.getMessage(), e);
            throw new RuntimeException("JSON序列化失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将对象序列化为格式化的JSON字符串
     *
     * @param obj 对象
     * @return 格式化的JSON字符串
     */
    public static String toPrettyJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败: {}", e.getMessage(), e);
            throw new RuntimeException("JSON序列化失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将JSON字符串反序列化为对象
     *
     * @param json JSON字符串
     * @param clazz 目标类
     * @param <T> 目标类型
     * @return 目标对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("JSON反序列化失败: {}, JSON: {}", e.getMessage(), json, e);
            throw new RuntimeException("JSON反序列化失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("JSON反序列化失败: {}, JSON: {}", e.getMessage(), json, e);
            throw new RuntimeException("JSON反序列化失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将JSON字符串反序列化为对象（带类型引用）
     *
     * @param json JSON字符串
     * @param typeReference 类型引用
     * @param <T> 目标类型
     * @return 目标对象
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            log.error("JSON反序列化失败: {}, JSON: {}", e.getMessage(), json, e);
            throw new RuntimeException("JSON反序列化失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("JSON反序列化失败: {}, JSON: {}", e.getMessage(), json, e);
            throw new RuntimeException("JSON反序列化失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将JSON字符串反序列化为Map
     *
     * @param json JSON字符串
     * @return Map对象
     */
    public static Map<String, Object> toMap(String json) {
        return fromJson(json, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * 将对象转换为另一个对象
     *
     * @param source 源对象
     * @param targetClass 目标类
     * @param <S> 源类型
     * @param <T> 目标类型
     * @return 目标对象
     */
    public static <S, T> T convertValue(S source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.convertValue(source, targetClass);
        } catch (IllegalArgumentException e) {
            log.error("对象转换失败: {}", e.getMessage(), e);
            throw new RuntimeException("对象转换失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取ObjectMapper实例
     *
     * @return ObjectMapper实例
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
}
