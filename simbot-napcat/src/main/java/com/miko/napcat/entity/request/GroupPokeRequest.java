package com.miko.napcat.entity.request;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 发送群聊戳一戳请求实体
 * 用于调用 /group_poke 接口在群聊中发送戳一戳
 */
@Data
@Accessors(chain = true)
public class GroupPokeRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 群ID
     * 支持数字或字符串类型
     */
    private String groupId;

    /**
     * 用户ID
     * 支持数字或字符串类型
     */
    private String userId;
}
