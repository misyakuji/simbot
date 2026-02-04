package com.miko.tool;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BotToolRegistry {

    private final Map<String, BotToolMeta> toolMap = new HashMap<>();

    /**
     * 注册工具
     *
     * @param meta 工具元数据
     */
    public void register(BotToolMeta meta) {
        toolMap.put(meta.name(), meta);
    }

    /**
     * 根据名字获取工具
     *
     * @param name 工具名称
     * @return 工具元数据
     */
    public Optional<BotToolMeta> getTool(String name) {
        return Optional.ofNullable(toolMap.get(name));
    }

    /**
     * 获取所有工具
     *
     * @return 工具元数据列表
     */
    public List<BotToolMeta> getAllTools() {
        return new ArrayList<>(toolMap.values());
    }
}
