package com.miko.ai.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 火山方舟 Responses API 响应封装
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArkResponsesApiResponse {

    private String id;
    private String model;
    private String previous_response_id;
    private List<Message> output;

    @Data
    public static class Message {
        private String id;
        private List<Summary> summary;
        private List<Content> content;

        @JsonProperty("call_id")
        private String callId;
        private String type;
        private String name;
        private String arguments;

    }

    @Data
    public static class Content {
        private String text;
        private String type;
    }

    @Data
    public static class Summary {
        private String text;
        private String type;
    }
}
