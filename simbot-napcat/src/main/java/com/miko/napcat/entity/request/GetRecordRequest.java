package com.miko.napcat.entity.request;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 获取语音消息详情请求实体
 * 用于调用 /get_record 接口获取语音消息详情
 */
@Data
@Accessors(chain = true)
public class GetRecordRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件（可选，二选一）
     */
    private String file;

    /**
     * 文件ID（可选，二选一）
     */
    private String fileId;

    /**
     * 输出格式（必填）
     * 可选值：mp3, amr, wma, m4a, spx, ogg, wav, flac
     */
    private String outFormat;
}
