package com.miko.tool;

import lombok.Builder;
import lombok.Data;

/**
 * 工具执行结果封装类
 * 用于统一返回工具调用的结果信息
 */
@Data
@Builder
public class BotToolResult {
    /**
     * 执行结果数据，供系统使用
     */
    private Object data;
    
    /**
     * 执行结果消息，供AI理解和处理
     */
    private String message;
    
    /**
     * 执行是否成功
     */
    private boolean success;
    
    /**
     * 错误码，用于标识具体的错误类型
     */
    private String errorCode;

    /**
     * 创建成功的执行结果
     * @param data 执行结果数据
     * @return 成功的BotToolResult实例
     */
    public static BotToolResult success(Object data){
        return BotToolResult.builder()
                .success(true)
                .data(data)
                .message(data != null ? data.toString() : "执行成功")
                .build();
    }

    /**
     * 创建失败的执行结果
     * @param msg 失败消息
     * @return 失败的BotToolResult实例
     */
    public static BotToolResult failure(String msg){
        return BotToolResult.builder()
                .success(false)
                .message(msg)
                .build();
    }
}
