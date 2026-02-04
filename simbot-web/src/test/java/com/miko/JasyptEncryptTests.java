package com.miko;

import com.miko.tool.*;
import org.jasypt.util.text.AES256TextEncryptor;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

/**
 * 生成加密数据库密码的测试类
 * 该类用于测试 Jasypt 加密功能以及 BotTool 相关功能
 */
class JasyptEncryptTests {

    /**
     * 测试方法：生成加密密码（直接运行该方法即可输出结果）
     * 该方法演示了如何使用 Jasypt 对数据库密码进行 AES-256 加密
     */
    @Test
    void generateEncryptedDbPassword() {
        // 1. 配置项：可根据需要修改
        String encryptKey = System.getenv("JASYPT_ENCRYPTOR_KEY"); // 从环境变量中读取加密密钥
        assert encryptKey != null : "请配置环境变量 JASYPT_ENCRYPTOR_KEY";
        String plainDbPassword = "xxxxx"; // 你的数据库明文密码（替换为实际密码）

        // 2. 创建 AES 加密器（与Spring Boot中Jasypt默认算法兼容）
        AES256TextEncryptor encryptor = new AES256TextEncryptor();
        encryptor.setPassword(encryptKey); // 设置加密密钥

        // 3. 生成加密密文
        String encryptedPassword = encryptor.encrypt(plainDbPassword);
        // 4. 解密验证（可选，用于确认密文有效性）
        String decryptedPassword = encryptor.decrypt(encryptedPassword);

        // 5. 打印结果（复制 ENC(加密密文) 到配置文件即可）
        System.out.println("=========== 数据库密码加密结果 ===========");
        System.out.println("加密密钥（后续需保留，用于解密）：" + encryptKey);
        System.out.println("数据库明文密码：" + plainDbPassword);
        System.out.println("加密后的密文：" + encryptedPassword);
        System.out.println("配置文件中需填写：ENC(" + encryptedPassword + ")");
        System.out.println("解密验证结果：" + decryptedPassword);
        assert decryptedPassword.equals(plainDbPassword);
        System.out.println("========================================");
    }

    class BotToolText {
        @BotTool(name = "send_group_at", description = "在指定QQ群中@指定QQ号成员，发送群@消息")
        public String sendGroupAt(@BotToolParam(name = "groupId") String groupId, @BotToolParam(name = "atQq") String atQq) {
            // 实际调用你的QQ逻辑
            System.out.printf("执行 sendGroupAt -> groupId=%s, atQq=%s%n", groupId, atQq);
            return "已@成员：" + atQq;
        }
    }

    /**
     * 测试 BotToolText 类的功能
     * 该方法演示了如何解析和调用 BotTool 注解标记的方法
     */
    @Test
    void BotToolText() throws InvocationTargetException, IllegalAccessException {
        BotToolText toolBean = new BotToolText();

        com.miko.tool.BotToolRegistry botToolRegistry = new com.miko.tool.BotToolRegistry();
        List<Object> toolBeans = new ArrayList<>();
        BotToolParser parser = new BotToolParser(botToolRegistry, toolBeans);

        List<BotToolMeta> metas = parser.parse(toolBean);

        for (BotToolMeta meta : metas) {
            System.out.println("工具名: " + meta.name());
            System.out.println("描述: " + meta.description());
            System.out.println("方法名: " + meta.method().getName());
            System.out.println("参数: ");
            meta.params().forEach(p ->
                    System.out.println("  " + p.name() + " : " + p.type().getSimpleName() + " , required=" + p.required())
            );

            // 演示调用
            Object result;
            if (!meta.params().isEmpty()) {
                result = meta.method().invoke(meta.bean(), "GID", "QQID"); // 示例参数
            } else {
                result = meta.method().invoke(meta.bean());
            }
            System.out.println("调用结果: " + result);
            System.out.println("--------------");
        }
    }

    /**
     * 测试 BotToolParser 扫描 Bean 并注册到 Registry
     * 该方法验证了 BotToolParser 的解析功能以及 Registry 的注册和调用机制
     */
    @Test
    void testBotToolParsingAndRegistry() throws Exception {
        // 1️⃣ 模拟一个工具 Bean
        class SampleTool {
            @BotTool(name = "send_group_at", description = "在QQ群中@某个成员")
            public String sendGroupAt(
                    @BotToolParam(name = "groupId") String groupId,
                    @BotToolParam(name = "atQq") String atQq
            ) {
                return "已@成员：" + atQq;
            }

            @BotTool(description = "返回问候语")
            public String greet(
                    @BotToolParam(name = "name") String name
            ) {
                return "Hello, " + name;
            }
        }

        SampleTool bean = new SampleTool();
        com.miko.tool.BotToolRegistry botToolRegistry = new com.miko.tool.BotToolRegistry();
        List<Object> toolBeans = new ArrayList<>();
        BotToolParser parser = new BotToolParser(botToolRegistry, toolBeans);
        // 2️⃣ 使用 BotToolParser 解析
        List<BotToolMeta> metas = parser.parse(bean);

        assertEquals(2, metas.size(), "应该解析出两个工具");

        // 3️⃣ 注册到 Registry
        BotToolRegistry registry = new BotToolRegistry();
        metas.forEach(registry::register);

        assertNotNull(String.valueOf(registry.get("send_group_at")), "send_group_at 工具应注册成功");
        assertNotNull(String.valueOf(registry.get("greet")), "greet 工具应注册成功");

        // 4️⃣ 模拟调用工具
        BotToolMeta sendAtMeta = registry.get("send_group_at");

        Object result = sendAtMeta.method().invoke(sendAtMeta.bean(), "GID", "QQID");
        System.out.println("调用 send_group_at 结果: " + result);
        assertEquals("已@成员：QQID", result);

        BotToolMeta greetMeta = registry.get("greet");
        Object greetResult = greetMeta.method().invoke(greetMeta.bean(), "小雨宝贝");
        System.out.println("调用 greet 结果: " + greetResult);
        assertEquals("Hello, 小雨宝贝", greetResult);
    }

    /**
     * BotTool 工具注册表
     * 用于存储和管理解析后的 BotTool 元数据
     */
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
        public BotToolMeta get(String name) {
            return toolMap.get(name);
        }

        /**
         * 获取所有工具
         *
         * @return 工具元数据列表
         */
        public List<BotToolMeta> getAll() {
            return new ArrayList<>(toolMap.values());
        }
    }

    /**
     * 测试 BotTool 转换为 Tools JSON 格式
     * 该方法验证了如何将 BotTool 元数据转换为符合 OpenAI Tools 格式的 JSON
     */
    @Test
    void testBotToolToToolsJson() {
        // 1️⃣ 模拟一个工具 Bean
        class SampleTool {
            @BotTool(name = "send_group_at", description = "在QQ群中@某个成员")
            public String sendGroupAt(
                    @BotToolParam(name = "groupId", required = true) String groupId,
                    @BotToolParam(name = "atQq", required = true) String atQq
            ) {
                return "已@成员：" + atQq;
            }

            @BotTool(name = "greet", description = "返回问候语")
            public String greet(
                    @BotToolParam(name = "name", required = true) String name
            ) {
                return "Hello, " + name;
            }
        }

        SampleTool bean = new SampleTool();

        // 2️⃣ 使用 BotToolParser 解析
        com.miko.tool.BotToolRegistry botToolRegistry = new com.miko.tool.BotToolRegistry();
        List<Object> toolBeans = new ArrayList<>();
        BotToolParser parser = new BotToolParser(botToolRegistry, toolBeans);
        List<BotToolMeta> metas = parser.parse(bean);

        // 3️⃣ 转换为 Tools JSON
        List<Map<String, Object>> toolsJson = metas.stream()
                .map(meta -> Map.of(
                        "type", "function",
                        "function", Map.of(
                                "name", meta.name(),
                                "description", meta.description(),
                                "parameters", buildJsonSchema(meta)
                        )
                ))
                .toList();

        // 4️⃣ 打印查看
        toolsJson.forEach(System.out::println);

        // 5️⃣ 简单校验
        assertEquals(2, toolsJson.size(), "应该生成两个 tools JSON");

        // ✅ 改成按名称检查，不依赖顺序
        Set<String> toolNames = toolsJson.stream()
                .map(tool -> ((Map<?, ?>) tool.get("function")).get("name").toString())
                .collect(Collectors.toSet());

        assertTrue(toolNames.contains("send_group_at"), "send_group_at 工具应存在");
        assertTrue(toolNames.contains("greet"), "greet 工具应存在");
    }

    /**
     * 辅助方法：生成参数 JSON Schema
     *
     * @param meta 工具元数据
     * @return 参数的 JSON Schema
     */
    private Map<String, Object> buildJsonSchema(BotToolMeta meta) {
        Map<String, Object> properties = meta.params().stream()
                .collect(Collectors.toMap(
                        BotToolParamMeta::name,
                        p -> Map.of("type", mapType(p.type()), "description", p.name())
                ));

        List<String> required = meta.params().stream()
                .filter(BotToolParamMeta::required)
                .map(BotToolParamMeta::name)
                .toList();

        return Map.of(
                "type", "object",
                "properties", properties,
                "required", required
        );
    }

    /**
     * 辅助方法：类型映射
     * 将 Java 类型映射为 JSON Schema 类型
     *
     * @param type Java 类型
     * @return 对应的 JSON Schema 类型
     */
    private String mapType(Class<?> type) {
        if (type == String.class) return "string";
        if (type == Integer.class || type == int.class) return "integer";
        if (type == Boolean.class || type == boolean.class) return "boolean";
        return "string"; // 默认当作字符串
    }
}