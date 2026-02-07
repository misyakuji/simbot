package com.miko.chat.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 聊天功能调用实体类
 * 用于封装聊天过程中函数调用的相关信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatFunctionCall {
    /**
     * 函数名称
     * 表示要调用的具体函数名
     */
    private String name;
    
    /**
     * 函数参数
     * 以JSON字符串格式存储函数调用所需的参数信息
     */
    private String arguments;
}
