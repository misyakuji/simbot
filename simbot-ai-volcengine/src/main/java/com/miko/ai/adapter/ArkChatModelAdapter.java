package com.miko.ai.adapter;

import com.miko.ai.converter.ArkMessageConverter;
import com.miko.ai.response.ArkChatResponse;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * 火山方舟 ChatModel 适配器
 * 设计模式：Adapter(适配器模式)
 * 作用：将火山方舟私有API适配为Spring AI标准ChatModel接口，解决默认路径拼接404问题
 * 注解说明：@Primary 优先注入该实现，覆盖框架默认配置
 */
@Primary
@Component
public class ArkChatModelAdapter implements ChatModel {

    /**
     * API调用超时时间，防止block()无限阻塞线程
     */
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    /**
     * 响应式HTTP客户端，用于调用火山方舟接口
     */
    private final WebClient webClient;

    /**
     * 火山方舟接口基础地址
     */
    private final String baseUrl;

    /**
     * 火山方舟模型名称
     */
    private final String model;

    /**
     * 构造器注入配置参数，初始化WebClient
     *
     * @param apiKey  火山方舟key
     * @param baseUrl 火山方舟api
     * @param model   模型名称
     */
    public ArkChatModelAdapter(
            @Value("${spring.ai.openai.api-key}") String apiKey,
            @Value("${spring.ai.openai.base-url}") String baseUrl,
            @Value("${spring.ai.openai.chat.options.model}") String model
    ) {
        // 初始化WebClient，配置默认请求头（身份校验）
        this.webClient = WebClient.builder()
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
        this.baseUrl = baseUrl;
        this.model = model;
    }

    /**
     * 核心调用方法：接收Spring AI标准Prompt，调用火山方舟API并返回标准响应
     *
     * @param prompt Spring AI标准对话提示对象，包含上下文消息、配置等信息
     * @return Spring AI标准ChatResponse响应对象
     */
    @Override
    public @Nullable ChatResponse call(Prompt prompt) {

        //  格式转换：调用工具类，将Spring AI Prompt转为火山方舟兼容的消息格式
        List<Map<String, String>> arkMessages = ArkMessageConverter.convertToArkMessages(prompt);

        // 3. 构造请求体：封装模型名称和消息列表，匹配火山方舟接口参数规范
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", arkMessages
        );

        //  发起HTTP请求，调用火山方舟聊天补全接口，增加异常捕获和超时控制
        ArkChatResponse arkResponse;
        try {
            arkResponse = webClient.post()
                    // 拼接完整接口地址，规避框架默认/v1路径问题
                    .uri(baseUrl + "/chat/completions")
                    // 设置请求体参数
                    .bodyValue(requestBody)
                    // 解析响应结果
                    .retrieve()
                    // 将JSON响应反序列化为自定义实体类
                    .bodyToMono(ArkChatResponse.class)
                    // 设置同步阻塞超时时间
                    .block(TIMEOUT);
        } catch (WebClientResponseException e) {
            // 捕获HTTP状态码异常（4xx/5xx），抛出携带详细信息的异常
            throw new RuntimeException(
                    "火山方舟API调用失败，状态码：" + e.getStatusCode() +
                            "，响应内容：" + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            // 捕获网络超时、连接失败等通用异常
            throw new RuntimeException("火山方舟接口调用异常：" + e.getMessage(), e);
        }

        //  解析响应结果：多层空指针防护，安全提取回复文本
        Generation generation = getGeneration(arkResponse);
        // 构建最终响应对象，附加模型元数据
        return ChatResponse.builder()
                .generations(List.of(generation))
                .metadata("model", "volc-ark-" + model)
                .build();
    }

    /**
     * 从ArkChat响应中提取并返回生成结果
     *
     * @param arkResponse ArkChat响应对象，包含聊天模型的响应数据
     * @return Generation 返回处理后的生成结果对象，非空
     */
    private static @NonNull Generation getGeneration(ArkChatResponse arkResponse) {
        String replyContent = null;
        if (arkResponse != null && arkResponse.getChoices() != null && !arkResponse.getChoices().isEmpty()) {
            // 安全获取第一条回复的消息内容
            ArkChatResponse.Choice firstChoice = arkResponse.getChoices().get(0);
            if (firstChoice.getMessage() != null) {
                replyContent = firstChoice.getMessage().getContent();
            }
        }
        // 封装为Spring AI标准响应格式
        AssistantMessage assistantMessage = new AssistantMessage(replyContent);
        // 构建生成结果包装对象
        return new Generation(assistantMessage);
    }

    /**
     * 流式调用接口，继承父类默认实现，可根据业务需求扩展
     * @param prompt 对话提示对象
     * @return 流式响应流
     */
//    @Override
//    public Flux<ChatResponse> stream(Prompt prompt) {
//        return ChatModel.super.stream(prompt);
//    }
}
