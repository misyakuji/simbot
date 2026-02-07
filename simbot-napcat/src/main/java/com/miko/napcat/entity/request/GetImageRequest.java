package com.miko.napcat.entity.request;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 获取图片消息详情请求实体
 * 用于调用 /get_image 接口获取图片消息详情
 */
@Data
@Accessors(chain = true)
public class GetImageRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件ID（可选，二选一）
     */
    private String fileId;

    /**
     * 文件（可选，二选一）
     */
    private String file;
}
