# simbot

This is robot project with Simple Robot Framework in Springboot.

## 环境准备

- 配置jdk25环境变量
- 配置 API Key
- 配置maven
-

### 参考文档

```bash
#Napcat
https://napneko.github.io/guide/boot/Shell

#火山方舟大模型服务平台
https://www.volcengine.com/docs/82379/1399008?lang=zh

#Spring AI
https://docs.spring.io/spring-ai/reference/api/chat/deepseek-chat.html

#Simple Robot Framework
https://simbot.forte.love/home.html
```

### Linux环境运行

```bash
#配置ARK_API_KEY到环境变量
export ARK_API_KEY="your_api_key_here"

#配置JASYPT加密到环境变量
export JASYPT_ENCRYPTOR_KEY="xxxxxx"

#赋予可执行权限
chmod +x mvnw

#maven打包
git pull && ./mvnw clean package -Dmaven.test.skip=true

#运行jar
java -jar simbot-starter/target/simbot-starter.jar
```

### Windows环境运行

```shell

#maven打包
./mvnw.cmd clean package '-Dmaven.test.skip=true' 

#运行jar
java -jar .\simbot-starter\target\simbot-starter.jar
```

