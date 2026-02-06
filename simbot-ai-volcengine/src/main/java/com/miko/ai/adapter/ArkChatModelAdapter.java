package com.miko.ai.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miko.ai.converter.ArkMessageConverter;
import com.miko.ai.response.ArkChatResponse;
import com.miko.tool.BotToolExecutor;
import com.miko.tool.BotToolMeta;
import com.miko.tool.BotToolParamMeta;
import com.miko.tool.BotToolRegistry;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
    private static final Duration TIMEOUT = Duration.ofSeconds(15);
    private static final Logger log = LoggerFactory.getLogger(ArkChatModelAdapter.class);

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
     * 工具注册中心，用于管理和注册自定义工具函数
     * 在构造器中注入，支持动态工具扩展
     */
    private final BotToolRegistry botToolRegistry;

    /**
     * 工具执行器，用于执行自定义工具函数
     */
    private final BotToolExecutor botToolExecutor;

    /**
     * 固定前缀：定义标准化的工具执行标识
     */
    private static final String TOOL_TRACE_PREFIX = "BOT_TOOL_EXEC";

    /**
     * JSON序列化工具，用于将对象转换为JSON字符串
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

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
            @Value("${spring.ai.openai.chat.options.model}") String model, BotToolRegistry botToolRegistry, BotToolExecutor botToolExecutor
    ) {
        // 初始化WebClient，配置默认请求头（身份校验）
        this.webClient = WebClient.builder()
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
        this.baseUrl = baseUrl;
        this.model = model;
        this.botToolRegistry = botToolRegistry;
        this.botToolExecutor = botToolExecutor;
    }

    /**
     * 核心调用方法：接收Spring AI标准Prompt，调用火山方舟API并返回标准响应
     *
     * @param prompt Spring AI标准对话提示对象，包含上下文消息、配置等信息
     * @return Spring AI标准ChatResponse响应对象
     */
    @Override
    public @Nullable ChatResponse call(@NonNull Prompt prompt) {
        //  格式转换：调用工具类，将Spring AI Prompt转为火山方舟兼容的消息格式
        List<Map<String, String>> arkMessages = ArkMessageConverter.convertToArkMessages(prompt);
        //  动态生成 tools JSON
        List<Map<String, Object>> toolsJson = botToolRegistry.getAllTools().stream()
                .map(meta -> Map.of(
                        "type", "function",
                        "function", Map.of(
                                "name", meta.name(),
                                "description", meta.description(),
                                "parameters", buildJsonSchema(meta)
                        )
                ))
                .toList();
        //  构造请求体：封装模型名称和消息列表，匹配火山方舟接口参数规范
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", arkMessages,
                "tools", toolsJson
        );
        //  发起HTTP请求，调用火山方舟聊天接口，增加异常捕获和超时控制
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
            // 新增：处理block返回null（超时/无响应）场景
            if (arkResponse == null) {
                log.error("火山方舟接口调用超时，未获取到响应");
                return buildFallbackChatResponse("请求超时，请稍后再试~");
            }
        } catch (WebClientResponseException e) {
            // 捕获HTTP状态码异常（4xx/5xx），抛出携带详细信息的异常
            throw new RuntimeException("火山方舟API调用失败，状态码：" + e.getStatusCode() + "，响应内容：" + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            // 捕获网络超时、连接失败等通用异常
            throw new RuntimeException("火山方舟接口调用异常：" + e.getMessage(), e);
        }
        //  解析响应结果：多层空指针防护，安全提取回复文本
        Generation generation = getGeneration(Objects.requireNonNull(arkResponse));

        // 构建最终响应对象，附加模型元数据
        return ChatResponse.builder()
                .generations(List.of(generation))
                .metadata("model", "volc-ark-" + model)
                .build();
    }
    /**
     * 构造降级响应，保证接口正常返回
     */
    private ChatResponse buildFallbackChatResponse(String fallbackText) {
        AssistantMessage fallbackMessage = new AssistantMessage(fallbackText);
        Generation generation = new Generation(fallbackMessage);
        return ChatResponse.builder()
                .generations(List.of(generation))
                .metadata("fallback", true)
                .build();
    }
    /**
     * 将 ArkChatResponse 转换为 Spring AI Generation 对象。
     * 该方法支持处理包含工具调用的响应，并自动执行工具调用，将结果封装到生成对象中。
     *
     * @param arkResponse 输入的 ArkChatResponse 对象，包含模型的响应信息
     * @return 返回封装后的 Generation 对象，其中包含助手消息（AssistantMessage），
     * 如果存在工具调用，则会包含工具调用的结果和相关信息
     */
    private @NonNull Generation getGeneration(ArkChatResponse arkResponse) {
        // 获取第一个选择项及其消息内容
        ArkChatResponse.Choice firstChoice = arkResponse.getChoices().getFirst();
        ArkChatResponse.Message message = firstChoice.getMessage();

        AssistantMessage assistantMessage;

        // 判断是否存在工具调用
        if (message.getToolCalls() != null && !message.getToolCalls().isEmpty()) {
            // 处理工具调用逻辑
            List<AssistantMessage.ToolCall> toolCalls = new ArrayList<>();
            List<Map<String, Object>> results = new ArrayList<>();

            // 遍历所有工具调用
            for (var tc : message.getToolCalls()) {
                String toolName = tc.getFunction().getName();
                String arguments = tc.getFunction().getArguments();
                Map<String, Object> argsMap;

                // 将工具参数从 JSON 字符串转换为 Map
                try {
                    argsMap = objectMapper.readValue(arguments, new TypeReference<>() {
                    });
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("解析工具参数失败: " + arguments, e);
                }

                // 执行工具并获取结果
                Object result = botToolExecutor.execute(toolName, argsMap);
                results.add(Map.of("toolName", toolName, "result", result));

                // 封装工具调用信息
                toolCalls.add(new AssistantMessage.ToolCall(
                        tc.getId(),
                        tc.getType(),
                        toolName,
                        arguments
                ));
            }
            log.warn("工具执行结果 results: {}", results);
            // 构造包含工具调用结果的助手消息
            assistantMessage = AssistantMessage.builder()
                    .content(TOOL_TRACE_PREFIX + ": Completed :" + results)
                    .toolCalls(toolCalls)
                    .build();
            log.warn("工具执行结果 assistantMessage1: {}", assistantMessage);

        } else {
            // 处理普通文本消息
            assistantMessage = new AssistantMessage(message.getContent());
            log.warn("工具执行结果 assistantMessage2: {}", assistantMessage);

        }
        // 返回封装后的 Generation 对象
        return new Generation(assistantMessage);
    }

    /**
     * 根据工具元数据构建JSON Schema结构
     * 用于描述工具函数的参数规范，供大模型理解和调用
     *
     * @param meta 工具元数据，包含参数定义和描述信息
     * @return 符合JSON Schema规范的参数描述对象
     */
    public static Map<String, Object> buildJsonSchema(BotToolMeta meta) {
        // 初始化属性和必填字段集合
        Map<String, Object> properties = new LinkedHashMap<>();
        List<String> required = new ArrayList<>();

        // 遍历工具参数元数据
        for (BotToolParamMeta param : meta.params()) {
            // 初始化单个参数的Schema结构
            Map<String, Object> propSchema = new LinkedHashMap<>();

            // 类型映射：将Java类型转换为JSON Schema类型
            String type = mapType(param.type());
            propSchema.put("type", type);

            // 添加参数描述信息
            propSchema.put("description", param.name());

            // 枚举类型处理：提取枚举值并添加到Schema中
            if (param.type().isEnum()) {
                Object[] enumValues = param.type().getEnumConstants();
                List<String> enumList = new ArrayList<>();
                for (Object ev : enumValues) {
                    enumList.add(ev.toString());
                }
                propSchema.put("enum", enumList);
            }

            // 集合/数组类型处理：根据泛型或组件类型生成items结构
            if (Collection.class.isAssignableFrom(param.type())) {
                // 获取泛型类型信息
                Type genericType = param.genericType();
                if (genericType instanceof ParameterizedType pt) {
                    Type[] typeArgs = pt.getActualTypeArguments();
                    if (typeArgs.length == 1) {
                        Class<?> itemClass = (Class<?>) typeArgs[0];
                        propSchema.put("items", Map.of("type", mapType(itemClass)));
                    }
                } else {
                    // 默认使用string类型
                    propSchema.put("items", Map.of("type", "string"));
                }
            } else if (param.type().isArray()) {
                // 数组类型处理
                propSchema.put("items", Map.of("type", mapType(param.type().getComponentType())));
            }

            // 将参数Schema添加到properties中
            properties.put(param.name(), propSchema);

            // 必填字段处理
            if (param.required()) {
                required.add(param.name());
            }
        }

        // 构建最终的JSON Schema对象
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", properties);
        // 仅在存在必填字段时添加required属性
        if (!required.isEmpty()) {
            schema.put("required", required);
        }

        // 记录生成的Schema日志
        log.warn("buildJsonSchema: {}", schema);

        return schema;
    }

    /**
     * 将Java类型映射为JSON Schema类型字符串
     * 支持基本数据类型的转换
     *
     * @param type Java类类型
     * @return 对应的JSON Schema类型字符串
     */
    public static String mapType(Class<?> type) {
        if (type == String.class) return "string";
        if (type == Integer.class || type == int.class) return "integer";
        if (type == Long.class || type == long.class) return "integer";
        if (type == Float.class || type == float.class) return "number";
        if (type == Double.class || type == double.class) return "number";
        if (type == Boolean.class || type == boolean.class) return "boolean";
        if (type.isEnum()) return "string";
        if (Collection.class.isAssignableFrom(type) || type.isArray()) return "array";
        if (Map.class.isAssignableFrom(type)) return "object";
        if (type == LocalDate.class || type == LocalDateTime.class || type == Date.class) return "string";
        return "object"; // 自定义对象
    }

}
