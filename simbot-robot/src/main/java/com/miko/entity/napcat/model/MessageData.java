package com.miko.entity.napcat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 消息数据基类
 * 用于表示不同类型的消息内容
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MessageData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消息类型
     * 可能的值: text, face, image, record, at, reply, video, forward, music, node, json, dice, rps, markdown, contact, poke, mface, file, xml 等
     */
    private String type;

    /**
     * 文本消息数据
     */
    private TextData text;

    /**
     * 表情消息数据
     */
    private FaceData face;

    /**
     * 图片消息数据
     */
    private ImageData image;

    /**
     * 语音消息数据
     */
    private RecordData record;

    /**
     * @消息数据
     */
    private AtData at;

    /**
     * 回复消息数据
     */
    private ReplyData reply;

    /**
     * 视频消息数据
     */
    private VideoData video;

    /**
     * 文件消息数据
     */
    private FileData file;

    /**
     * 转发消息数据
     */
    private ForwardData forward;

    /**
     * 音乐消息数据
     */
    private MusicData music;

    /**
     * 转发节点数据
     */
    private NodeData node;

    /**
     * JSON消息数据
     */
    private JsonData json;

    /**
     * 骰子消息数据
     */
    private DiceData dice;

    /**
     * 猜拳消息数据
     */
    private RpsData rps;

    /**
     * Markdown消息数据
     */
    private MarkdownData markdown;

    /**
     * 联系人消息数据
     */
    private ContactData contact;

    /**
     * 戳一戳消息数据
     */
    private PokeData poke;

    /**
     * 商城表情数据
     */
    private MfaceData mface;

    /**
     * XML消息数据
     */
    private String xml;

    /**
     * 其他扩展数据（用于未明确定义的消息类型）
     */
    private Map<String, Object> extra;

    /**
     * 文本数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class TextData {
        private String text;
    }

    /**
     * 表情数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class FaceData {
        private String id;
        private String resultId;
        private Integer chainCount;
    }

    /**
     * 图片数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class ImageData {
        private String file;
        private String path;
        private String url;
        private String thumb;
        private String name;
        private String summary;
        private Integer subType;
    }

    /**
     * 语音数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class RecordData {
        private String file;
        private String path;
        private String url;
        private String thumb;
        private String name;
    }

    /**
     * @数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class AtData {
        private String qq;
        private String name;
    }

    /**
     * 回复数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class ReplyData {
        private String id;
    }

    /**
     * 视频数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class VideoData {
        private String file;
        private String path;
        private String url;
        private String thumb;
        private String name;
    }

    /**
     * 文件数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class FileData {
        private String file;
        private String path;
        private String url;
        private String thumb;
        private String name;
    }

    /**
     * 转发数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class ForwardData {
        private String id;
    }

    /**
     * 音乐数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class MusicData {
        private String type;  // qq, 163, kugou, migu, kuwo, custom
        private Object id;
        private String url;
        private String audio;
        private String title;
        private String image;
        private String content;
    }

    /**
     * 节点数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class NodeData {
        private String id;
        private String userId;
        private String uin;
        private String nickname;
        private String name;
        private List<MessageData> content;
        private Object otherContent;
        private String source;
        private List<NewsItem> news;
        private String text;
        private String summary;
        private String prompt;
        private String time;
    }

    /**
     * JSON数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class JsonData {
        private Object data;
        private Map<String, Object> config;
    }

    /**
     * 骰子数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class DiceData {
        private Object result;
    }

    /**
     * 猜拳数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class RpsData {
        private Object result;
    }

    /**
     * Markdown数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class MarkdownData {
        private String content;
    }

    /**
     * 联系人数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class ContactData {
        private String type;
        private String id;
    }

    /**
     * 戳一戳数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class PokeData {
        private String type;
        private String id;
    }

    /**
     * 商城表情数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class MfaceData {
        private Integer emojiPackageId;
        private String emojiId;
        private String key;
        private String summary;
    }

    /**
     * 新闻项（用于转发消息）
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class NewsItem {
        private String title;
        private String url;
        private String image;
        private String summary;
        private String source;
    }
}
