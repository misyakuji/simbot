package com.miko.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jspecify.annotations.NonNull;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;

/**
 * 自定义JSON属性源工厂
 * 用于解析JSON配置文件
 */
public class JsonPropertySourceFactory implements PropertySourceFactory {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public @NonNull PropertySource<?> createPropertySource(String name, EncodedResource resource)
            throws IOException {

        String sourceName = StringUtils.hasText(name) ? name :
                resource.getResource().getFilename();

        try {
            // 读取JSON文件
            Map<String, Object> jsonMap = objectMapper.readValue(
                    resource.getResource().getInputStream(),
                    Map.class
            );

            // 扁平化处理（将嵌套结构转换为点分隔格式）
            Map<String, Object> flattenedMap = flattenMap("", jsonMap);

            return new MapPropertySource(sourceName, flattenedMap);

        } catch (IOException e) {
            throw new IOException("Failed to parse JSON configuration from " + sourceName, e);
        }
    }

    /**
     * 递归扁平化嵌套的Map结构
     */
    private Map<String, Object> flattenMap(String prefix, Map<String, Object> sourceMap) {
        Map<String, Object> result = new java.util.HashMap<>();

        for (Map.Entry<String, Object> entry : sourceMap.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();

            Object value = entry.getValue();

            if (value instanceof Map) {
                // 递归处理嵌套的Map
                Map<String, Object> nestedMap = (Map<String, Object>) value;
                result.putAll(flattenMap(key, nestedMap));
            } else {
                // 直接放入结果
                result.put(key, value);
            }
        }

        return result;
    }
}