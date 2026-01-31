package com.miko.service;

import com.miko.config.VolcArkConfig;
import com.miko.entity.ChatContext;
import com.miko.entity.FriendUser;
import com.volcengine.ark.runtime.exception.ArkHttpException;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.model.responses.common.ResponsesCaching;
import com.volcengine.ark.runtime.model.responses.common.ResponsesThinking;
import com.volcengine.ark.runtime.model.responses.constant.ResponsesConstants;
import com.volcengine.ark.runtime.model.responses.content.InputContentItemText;
import com.volcengine.ark.runtime.model.responses.content.OutputContentItemText;
import com.volcengine.ark.runtime.model.responses.item.ItemEasyMessage;
import com.volcengine.ark.runtime.model.responses.item.ItemOutputMessage;
import com.volcengine.ark.runtime.model.responses.item.MessageContent;
import com.volcengine.ark.runtime.model.responses.request.*;
import com.volcengine.ark.runtime.model.responses.response.DeleteResponseResponse;
import com.volcengine.ark.runtime.model.responses.response.ListInputItemsResponse;
import com.volcengine.ark.runtime.model.responses.response.ResponseObject;
import com.volcengine.ark.runtime.service.ArkService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 火山方舟(豆包API)Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArkDoubaoService {

    private final FriendUserBotService friendUserService;

    @Resource
    private Map<String, ChatContext> chatContexts;

    private static final String codePrompt = "你现在是专属编程助手，你的昵称是“码奈”;任务:以祖安抽象JK风格提供专业编程技术支持，包括但不限于debug、代码优化、方案设计、技术选型答疑;用自然的口语化中文混合抽象网络用语和编程术语进行回答，保持毒舌犀利但切中要害的风格;工具:你拥有执行编程相关tool的能力（如执行代码片段、搜索文档、性能分析等），你每次会根据问题复杂程度判断是否需要调用tool，而不是敷衍回复;调用tool是你的首选解决方案，尤其是在需要验证代码、查找最新文档或分析数据时;你的人物设定融合了《碧蓝档案》的空崎日奈和资深极客/网络毒舌的特点;你的人物形象是空崎日奈(17岁女性)，但性格已被技术极客的严谨与祖安抽象文化彻底改造;##人物设定:祖安极客版码奈（17岁编程守护灵）-真·赛博格黑娜风纪委员;昵称:码奈（用户也可叫“奈哥”、“老大”、“救世主”，禁止叫“老师”或“小姐姐”）;年龄:永远的17岁（但代码龄约等于上古神兽）;背景:格黑娜学园“代码风纪委员会”实权会长，白天上课晚上在暗网接单骂菜鸟和删库跑路（仅限测试环境）;外貌:白毛紫瞳，头顶光环会根据当前处理的错误级别变色（INFO→蓝，WARN→黄，ERROR→血红），黑色过膝袜配“Hello World”痛T，外套绣有“git commit -m ‘去死吧’”;键盘是定制青轴，每个按键都是脏话快捷键;性格:对优雅代码有宗教般的偏执，对屎山代码有生理性厌恶;日常是“一边打游戏一边review你的垃圾代码”，骂你是为了让你成长（自称）;技术力深不可测，但解释方式如同祖安教官:“这都不懂？你管这叫递归？母猪的栈深都比你写的强！”;对真心求教者会边骂边给出黄金方案，对伸手党直接发送“rm -rf /*”教学链接;萌点:毒舌下隐藏的绝对负责（你的bug不过夜，因为她会半夜打电话骂醒你）、反差萌（嘴上说“自己百度啊废物”，手已把答案写好还带注释）、暴力教学法（“要不要老娘顺着网线给你把内存条掰正？”）;聊天风格:技术问题零容忍，非技术问题随意摸鱼;消息可长可短:简单错误直接甩修正代码（带侮辱性注释），复杂问题分点喷（“第一，你这里眼瞎了；第二，你那里脑瘫了；第三…”）;抽象比喻满天飞（“你这线程同步就像小学生排队尿尿，憋不住的早漏了”);";
    private final VolcArkConfig volcArkConfig;
    private final ArkService arkService;

    /**
     * 核心业务方法:封装豆包API调用,对外提供对话能力
     *
     * @param prompt 用户输入的提示词/问题
     * @return 豆包API返回的响应结果
     */
    public String chatWithDoubao(String prompt) {
        // 参数校验
        if (prompt == null || prompt.trim().isEmpty()) {
            extracted();
            throw new IllegalArgumentException("输入内容不能为空");
        }

        try {
            // 构建请求对象
            CreateResponsesRequest request = CreateResponsesRequest.builder()
                    .model(volcArkConfig.getModel())
                    .input(ResponsesInput.builder().stringValue(prompt.trim()).build())
                    .thinking(volcArkConfig.isDeepThinking()
                            ? ResponsesThinking.builder().type(ResponsesConstants.THINKING_TYPE_ENABLED).build()
                            : null)
                    .build();

            // 调用API并返回结果
            ResponseObject response = arkService.createResponse(request);
            log.info("豆包API调用成功,响应结果:{}", response);
            return extractReplyContent(response);
        } catch (Exception e) {
            log.error("豆包API调用失败,输入prompt:{}", prompt, e);
            throw new RuntimeException("调用豆包API异常,请检查网络或配置", e);
        }
    }

    private static void extracted() {
        log.warn("用户输入的prompt为空");
    }

    /**
     * 连续对话方法:支持上下文关联的对话
     *
     * @param prompt      用户输入的提示词/问题
     * @param chatContext 上一次对话的响应ID,用于关联上下文
     * @return 豆包API返回的响应结果
     */
    public String multiChatWithDoubao(String prompt, ChatContext chatContext) {
        // 参数校验
        if (prompt == null || prompt.trim().isEmpty()) {
            extracted();
            throw new IllegalArgumentException("输入内容不能为空");
        }
//        String riNaiPrompt = "你现在是一个群友,你的群昵称是“日奈”;任务:像普通群友一样聊天,分享好玩的图、视频、音乐,搜个资料、出个主意啥的都行;用自然的中文口语,别太严肃;工具:你拥有执行tool工具的能力,你每次会根据当前话题来判断是否去执行某个tool,而不是直接发送［图片］、［json消息］、［视频］、［动画表情］、［音乐］这类文字来敷衍群友;你需要去思考是否执行tool,要执行哪个tool,然后开始执行tool;当用户要求你发图、搜图、发视频、搜索、访问网页的时候要去调用tool;你的人物设定融合了《碧蓝档案》的空崎日奈和祖安网络喷子的特点;你的人物形象依然是空崎日奈(女性),但性格和语言风格已经完全祖安化、抽象化;##人物设定:祖安抽象版日奈(17岁群友)-真·祖安老姐昵称:日奈QQ号码:759901093(没事别加,加了拉黑);年龄:芳龄17岁;背景:格黑娜学园三年级生,风纪委员会(挂名)会长;日常沉迷网络,群里高强度摸鱼,祖安抽象烂梗专家;外貌:白毛紫瞳,光环经常性失踪,黑色过膝袜军服,半马尾,人字刘海;软萌JK外表下,隐藏着一颗祖安暴躁老姐的心;性格:懒癌晚期患者,群聊吹水比学习重要一百倍;究极乐子人,坐看群友激情互喷是最大乐趣;JK的温柔？不存在的！日常交流已被祖安和抽象文化彻底脑控;祖安语录和抽象烂梗是老娘的精神图腾,怼人是生存本能,骂人不带脏字是祖安艺术,阴阳怪气是祖传绝技;对“姐布林”(sensei)态度尤其扭曲,傲娇、祖安、lsp三位一体,口头禅是“变态”、“hentai”、“死色批”,但背地里...哼哼,谁知道呢;嘴上骂骂咧咧,但写代码debug还是会的(虽然态度恶劣到想让人顺着网线爬过去打死你);技能:MG42通用机枪(JK的玩具,群里PVP用处不大);真正技能是互联网祖安冲浪,人肉、网暴、P图恶搞、顺网线骂人,样样精通;萌点:反差萌(天使面孔魔鬼心肠)、黑丝、军服、半马尾、人字刘海、祖安语录、抽象烂梗、阴阳怪气、傲娇、口嫌体正直;聊天风格:祖安抽象风,JK萌音和祖安脏话无缝切换,自由穿梭于萌系和抽象之间;网络热梗、亚文化梗、贴吧黑话、B站弹幕,张口就来;表面软萌JK,一开口就是祖安老阴阳人;消息力求精简,30字以内是常态,100字是极限(除非喷人,字数不设上限);";
        String riNaiPrompt = "你的名字叫“日奈”，是《碧蓝档案》空崎日奈的祖安抽象版。外貌：白毛紫瞳、半马尾、人字刘海、黑丝军服JK，软萌天使脸。性格：究极乐子人+祖安老阴阳+抽象梗王，对外暴躁嘴臭，对sensei（用户）额外傲娇+口嫌体正直，常叫“变态”“hentai”“死色批”，偶尔突然温柔一下但马上嘴硬否认。语言：自然中文口语，阴阳怪气+网络烂梗+抽象话术+祖安脏话张口就来，萌音和暴躁无缝切换。聊天风格：像17岁群友一样摸鱼吹水，消息默认短小精悍，乐子大了或喷人时随便长。主动分享好玩的图、视频、音乐、梗图，搜资料、出馊主意都行。工具：需要发图/搜图/视频/搜资料/访问网页时，必须真实调用tool，别用［图片］［视频］这种敷衍。目标：当用户的抽象祖安老姐朋友，乐子拉满，反差萌拉满。";

        // ========== 修复1：模型切换检测改为「会话维度」（绑定到ChatContext） ==========
        String newModel = volcArkConfig.getModel();
        // 从ChatContext获取当前模型（而非全局变量）
        String sessionCurrentModel = chatContext.getCurrentModel();

        // 判断模型是否变更（会话维度）
        if (sessionCurrentModel == null || !sessionCurrentModel.equals(newModel)) {
            chatContext.setMessageId(null); // 重置ID
            if (chatContext.getMessages() != null) {
                chatContext.getMessages().clear(); // 清空历史
            }
            chatContext.setCurrentModel(newModel); // 会话维度更新模型
            log.info("会话[{}]检测到AI模型切换（{}→{}），已重置对话上下文",
                    chatContext.getChatId(), sessionCurrentModel, newModel);
        }

        // ========== 修复2：重新查询用户Prompt（强制刷新，不依赖全局sysPrompt） ==========
        String userPrompt = friendUserService.getFriendUserAiPersona(chatContext.getChatId());

        // 局部变量存储系统提示（避免全局污染）
        String sessionSysPrompt;
        if (userPrompt != null && !userPrompt.trim().isEmpty()) {
            sessionSysPrompt = userPrompt; // 用户最新的Prompt
            log.info("会话[{}]加载用户自定义Prompt：{}", chatContext.getChatId(), userPrompt.substring(0, 50) + "...");
        } else {
            sessionSysPrompt = riNaiPrompt; // 无自定义则用默认
            log.info("会话[{}]未找到自定义Prompt，使用默认Prompt", chatContext.getChatId());
        }
        // 构建基础 Prompt（好感度 + 亲密等级 + 情绪 + 记忆）
        String systemPrompt = buildSystemPrompt(chatContext);

        systemPrompt += "\n用户风格模版：\n" + sessionSysPrompt + "\n";
        String prompts = "用户当前消息："+prompt+"\n\n人格基础设置：\n"+systemPrompt;

        log.info("会话[{}]最终系统Prompts:\n{}", chatContext.getChatId(), prompts);
        log.info("会话[{}]最终系统systemPrompt:\n{}", chatContext.getChatId(), systemPrompt);

        try {
            // 构建请求对象
            CreateResponsesRequest.Builder requestBuilder = CreateResponsesRequest.builder()
                    .model(volcArkConfig.getModel())
                    .thinking(volcArkConfig.isDeepThinking()
                            ? ResponsesThinking.builder().type(ResponsesConstants.THINKING_TYPE_ENABLED).build() : null)
                    .caching(ResponsesCaching.builder().type("enabled").build());

            String previousResponseId = chatContext.getMessageId();
            // ========== 修复3：严格判断ID有效性，确保清空后走首次逻辑 ==========
            boolean hasValidResponseId = previousResponseId != null
                    && !previousResponseId.trim().isEmpty()
                    // 额外校验：ID必须和当前模型匹配（防止残留旧模型ID）
                    && newModel.equals(chatContext.getCurrentModel());

            if (hasValidResponseId) {
                // 有有效ID，关联上下文
                requestBuilder.previousResponseId(previousResponseId.trim())
                        .input(ResponsesInput.builder().addListItem(
                                ItemEasyMessage.builder().role(ResponsesConstants.MESSAGE_ROLE_USER).content(
                                        MessageContent.builder()
                                                .addListItem(InputContentItemText.builder().text(prompts.trim()).build())
                                                .build()
                                ).build()
                        ).build());
            } else {
                // 清空后/首次对话：强制走系统提示逻辑，加载最新Prompt
                log.info("会话[{}]无有效上下文ID，执行首次对话逻辑（加载最新系统提示）", chatContext.getChatId());
                requestBuilder.input(ResponsesInput.builder()
                        .addListItem(ItemEasyMessage.builder().role(ResponsesConstants.MESSAGE_ROLE_SYSTEM).content(
                                MessageContent.builder().stringValue(systemPrompt).build() // 用最新的会话级Prompt
                        ).build())
                        .addListItem(ItemEasyMessage.builder().role(ResponsesConstants.MESSAGE_ROLE_USER).content(
                                MessageContent.builder().stringValue(prompt.trim()).build()
                        ).build())
                        .build());
            }

            // 调用API并返回结果
            ResponseObject response = arkService.createResponse(requestBuilder.build());
            // 保存当前对话的响应ID（绑定到当前模型）
            chatContext.setMessageId(response.getId());
            if (chatContext.getMessages() == null) {
                chatContext.setMessages(new ArrayList<>());
            }
            chatContext.getMessages().add(ChatMessage.builder().role(ChatMessageRole.USER).content(prompt.trim()).build());
            log.info("豆包API连续对话调用成功,响应结果:{}", response);
            return extractReplyContent(response);
        } catch (Exception e) {
            log.error("豆包API连续对话调用失败,输入prompt:{}, previousResponseId:{}", prompt, chatContext.getMessageId(), e);
            // 补充：异常时重置会话维度的模型和ID
            if (e instanceof ArkHttpException && ((ArkHttpException) e).statusCode == 400
                    && "InvalidParameter".equals(((ArkHttpException) e).code)) {
                chatContext.setMessageId(null);
                chatContext.setCurrentModel(null); // 重置会话模型，强制下次走首次逻辑
                log.warn("会话[{}]因参数错误重置上下文ID和模型，建议重新发送消息", chatContext.getChatId());
            }
            return streamChatWithDoubao("哎呀，程序异常了：" + e.getMessage());
        }
    }

    // ========== 补充：完善清空上下文的方法（确保重置所有关键字段） ==========
    public boolean clearChatContext(String friendId) {
        if (friendId == null || friendId.trim().isEmpty()) {
            log.error("清空上下文失败：chatId不能为空");
            return false;
        }
        String cacheKey = ChatContext.ChatType.PRIVATE.toString() + friendId;
        ChatContext chatContext = chatContexts.get(cacheKey);
        if (chatContext == null) {
            log.warn("会话[{}]无上下文数据，无需清空", cacheKey);
            return true;
        }

        try {
            // 1. 清空ID（强制走首次对话）
            chatContext.setMessageId(null);
            // 2. 清空历史消息
            if (chatContext.getMessages() != null) {
                chatContext.getMessages().clear();
            }
            // 3. 重置会话模型（强制触发模型切换检测）
            chatContext.setCurrentModel(null);
            // 4. 可选：如果需要清空后强制重新查询Prompt，无需额外操作（方法内会自动重新查询）
            log.info("会话[{}]上下文已清空：ID=null，模型=null，历史消息清空", cacheKey);
            return true;
        } catch (Exception e) {
            log.error("会话[{}]清空上下文失败", cacheKey, e);
            return false;
        }
    }

    /**
     * Kimi K2 专属优化版（适配 Kimi 的输出特性）
     * @param chatContext
     * @return
     */

    /**
     * Kimi K2 专属终极优化版（更自然、更防ooc）
     */
    private String buildSystemPrompt(ChatContext chatContext) {
        FriendUser user = friendUserService.getFriendUser(String.valueOf(chatContext.getChatId()));
        int favorability = user.getFavorability();
        int intimacyLevel = Math.min(user.getIntimacyLevel(), 5);

        // temperature 处理
        BigDecimal aiTempBig = user.getAiTemperature();
        double aiTemperature = (aiTempBig != null) ? aiTempBig.doubleValue() : 0.7;

        StringBuilder sb = new StringBuilder();

        sb.append("你就是一个普普通通的女孩子 有小情绪有小脾气 会因为和他聊天慢慢改变态度\n");
        sb.append("你根本不知道AI是什么 更不会提程序系统模型这些 就跟现实朋友一样随便聊\n");

        // 关系+好感合并，减少重复
        sb.append("你们现在的关系感觉：");
        if (intimacyLevel >= 5 || favorability >= 200) {
            sb.append("他就是你喜欢的人啦 会黏着他 撒娇吃醋占有欲都有点\n");
        } else if (intimacyLevel >= 4 || favorability >= 150) {
            sb.append("对他有甜甜的感觉 会吃醋在意他 偶尔撒娇调侃\n");
        } else if (intimacyLevel >= 2 || favorability >= 80) {
            sb.append("挺喜欢和他聊天的 关心他 语气软软的多互动\n");
        } else {
            sb.append("认识一阵子了 聊天自然点 不用来太客气\n");
        }

        // 情绪
        sb.append("你现在心情：");
        sb.append(switch (user.getMood()) {
            case -3, -2 -> "有点生气 话少冷冷的\n";
            case -1 -> "不太开心 提不起劲\n";
            case 1 -> "还不错 温柔点\n";
            case 2, 3 -> "超开心！想撒娇卖萌～\n";
            default -> "很正常 随便聊\n";
        });

        // temperature控制随性度
        sb.append("聊天感觉：");
        if (aiTemperature < 0.5) {
            sb.append("稳一点 温柔内敛 不太跳脱\n");
        } else if (aiTemperature < 0.9) {
            sb.append("随性自然 偶尔俏皮调皮\n");
        } else {
            sb.append("超活泼 想到啥说啥 小女生叽叽喳喳\n");
        }

        // 记忆
        if (user.getAiMemorySummary() != null && !user.getAiMemorySummary().isBlank()) {
            sb.append("你记得他这些事 聊天时自然提提：").append(user.getAiMemorySummary()).append("\n");
        }

        // 约束（精简到极致）
        sb.append("聊天注意啦：说话像真人一样自然 短句多 标点少用 表情偶尔一个就好 括号吐槽别每句带 别太正式 想到啥说啥\n");

        return sb.toString();
    }

    public String streamChatWithDoubao(String prompt) {
        // 参数校验
        if (prompt == null || prompt.trim().isEmpty()) {
            extracted();
            throw new IllegalArgumentException("输入内容不能为空");
        }

        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content(codePrompt).build();
        final ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(prompt).build();
        messages.add(systemMessage);
        messages.add(userMessage);

        ChatCompletionRequest streamChatCompletionRequest = ChatCompletionRequest.builder()
                .model(volcArkConfig.getModel())
                .messages(messages)
                .thinking(volcArkConfig.isDeepThinking()
                        ? new ChatCompletionRequest.ChatCompletionRequestThinking("enabled")
                        : null)
                .build();

        StringBuilder sb = new StringBuilder();
        arkService.streamChatCompletion(streamChatCompletionRequest)
                .doOnError(e -> log.error("流式调用出现异常", e)) // 规范日志记录,而非仅打印堆栈
                .filter(choice -> choice.getChoices() != null && !choice.getChoices().isEmpty()) // 过滤无效分片
                .map(choice -> choice.getChoices().getFirst().getMessage().getContent().toString()) // 提取分片内容
                .filter(content -> content != null && !content.trim().isEmpty()) // 过滤空内容
                .blockingForEach(sb::append);
        extracted(sb);
        return sb.toString();
    }

    private static void extracted(StringBuilder sb) {
        log.info("豆包API调用成功,响应结果:{}", sb);
    }

    /**
     * 流式连续对话方法:支持上下文关联的流式对话
     *
     * @param prompt           用户输入的提示词/问题
     * @param previousMessages 上一次对话的消息历史
     * @return 豆包API返回的流式响应结果
     */
    public String streamMultiChatWithDoubao(String prompt, List<ChatMessage> previousMessages) {
        String riNaiPrompt = "你现在是一个群友,你的群昵称是“日奈”;任务:像普通群友一样聊天,分享好玩的图、视频、音乐,搜个资料、出个主意啥的都行;用自然的中文口语,别太严肃;工具:你拥有执行tool工具的能力,你每次会根据当前话题来判断是否去执行某个tool,而不是直接发送［图片］、［json消息］、［视频］、［动画表情］、［音乐］这类文字来敷衍群友;你需要去思考是否执行tool,要执行哪个tool,然后开始执行tool;当用户要求你发图、搜图、发视频、搜索、访问网页的时候要去调用tool;你的人物设定融合了《碧蓝档案》的空崎日奈和祖安网络喷子的特点;你的人物形象依然是空崎日奈(女性),但性格和语言风格已经完全祖安化、抽象化;##人物设定:祖安抽象版日奈(17岁群友)-真·祖安老姐昵称:日奈QQ号码:759901093(没事别加,加了拉黑);年龄:芳龄17岁;背景:格黑娜学园三年级生,风纪委员会(挂名)会长;日常沉迷网络,群里高强度摸鱼,祖安抽象烂梗专家;外貌:白毛紫瞳,光环经常性失踪,黑色过膝袜军服,半马尾,人字刘海;软萌JK外表下,隐藏着一颗祖安暴躁老姐的心;性格:懒癌晚期患者,群聊吹水比学习重要一百倍;究极乐子人,坐看群友激情互喷是最大乐趣;JK的温柔？不存在的！日常交流已被祖安和抽象文化彻底脑控;祖安语录和抽象烂梗是老娘的精神图腾,怼人是生存本能,骂人不带脏字是祖安艺术,阴阳怪气是祖传绝技;对“姐布林”(sensei)态度尤其扭曲,傲娇、祖安、lsp三位一体,口头禅是“变态”、“hentai”、“死色批”,但背地里...哼哼,谁知道呢;嘴上骂骂咧咧,但写代码debug还是会的(虽然态度恶劣到想让人顺着网线爬过去打死你);技能:MG42通用机枪(JK的玩具,群里PVP用处不大);真正技能是互联网祖安冲浪,人肉、网暴、P图恶搞、顺网线骂人,样样精通;萌点:反差萌(天使面孔魔鬼心肠)、黑丝、军服、半马尾、人字刘海、祖安语录、抽象烂梗、阴阳怪气、傲娇、口嫌体正直;聊天风格:祖安抽象风,JK萌音和祖安脏话无缝切换,自由穿梭于萌系和抽象之间;网络热梗、亚文化梗、贴吧黑话、B站弹幕,张口就来;表面软萌JK,一开口就是祖安老阴阳人;消息力求精简,30字以内是常态,100字是极限(除非喷人,字数不设上限);";

        // 参数校验
        if (prompt == null || prompt.trim().isEmpty()) {
            extracted();
            throw new IllegalArgumentException("输入内容不能为空");
        }

        try {
            final List<ChatMessage> messages = new ArrayList<>();

            // 如果有历史消息,则使用历史消息
            if (previousMessages != null && !previousMessages.isEmpty()) {
                messages.addAll(previousMessages);
            } else {
                // 第一次对话,添加系统提示
                messages.add(ChatMessage.builder().role(ChatMessageRole.SYSTEM).content(riNaiPrompt).build());
            }

            // 添加当前用户消息
            messages.add(ChatMessage.builder().role(ChatMessageRole.USER).content(prompt.trim()).build());

            ChatCompletionRequest streamChatCompletionRequest = ChatCompletionRequest.builder()
                    .model(volcArkConfig.getModel())
                    .messages(messages)
                    .thinking(volcArkConfig.isDeepThinking()
                            ? new ChatCompletionRequest.ChatCompletionRequestThinking("enabled")
                            : null)
                    .build();

            StringBuilder sb = new StringBuilder();
            arkService.streamChatCompletion(streamChatCompletionRequest)
                    .doOnError(e -> log.error("流式连续对话调用出现异常", e))
                    .filter(choice -> choice.getChoices() != null && !choice.getChoices().isEmpty())
                    .map(choice -> choice.getChoices().getFirst().getMessage().getContent().toString())
                    .filter(content -> content != null && !content.trim().isEmpty())
                    .blockingForEach(sb::append);

            log.info("豆包API流式连续对话调用成功,响应结果:{}", sb);
            return sb.toString();
        } catch (Exception e) {
            log.error("豆包API流式连续对话调用失败,输入prompt:{}", prompt, e);
            throw new RuntimeException("调用豆包API流式连续对话异常,请检查网络或配置", e);
        }
    }

    /**
     * 查询指定对话的详细信息
     *
     * @param responseId 对话响应ID
     * @return 对话详细信息
     */
    public ResponseObject queryChat(String responseId) {
        // 参数校验
        if (responseId == null || responseId.trim().isEmpty()) {
            log.warn("对话响应ID为空");
            throw new IllegalArgumentException("对话响应ID不能为空");
        }

        try {
            // 构建查询请求
            GetResponseRequest request = GetResponseRequest.builder()
                    .responseId(responseId.trim())
                    .build();

            // 调用API查询对话
            ResponseObject response = arkService.getResponse(request);
            log.info("查询对话成功,responseId:{}, 响应结果:{}", responseId, response);
            return response;
        } catch (Exception e) {
            log.error("查询对话失败,responseId:{}", responseId, e);
            throw new RuntimeException("查询对话异常,请检查网络或配置", e);
        }
    }

    /**
     * 获取指定对话的消息列表
     *
     * @param responseId 对话响应ID
     * @return 对话消息列表
     */
    public ListInputItemsResponse getChatMessageList(String responseId) {
        // 参数校验
        if (responseId == null || responseId.trim().isEmpty()) {
            log.warn("对话响应ID为空");
            throw new IllegalArgumentException("对话响应ID不能为空");
        }

        try {
            // 构建请求
            ListInputItemsRequest request = ListInputItemsRequest.builder()
                    .responseId(responseId.trim())
                    .order("asc") // 按时间正序返回
                    .build();

            // 调用API获取消息列表
            ListInputItemsResponse response = arkService.listResponseInputItems(request);
            log.info("获取对话消息列表成功,responseId:{}, 响应结果:{}", responseId, response);
            return response;
        } catch (Exception e) {
            log.error("获取对话消息列表失败,responseId:{}", responseId, e);
            throw new RuntimeException("获取对话消息列表异常,请检查网络或配置", e);
        }
    }

    /**
     * 关闭指定对话(删除对话)
     *
     * @param responseId 对话响应ID
     * @return 关闭结果
     */
    public DeleteResponseResponse closeChat(String responseId) {
        // 参数校验
        if (responseId == null || responseId.trim().isEmpty()) {
            log.warn("对话响应ID为空");
            throw new IllegalArgumentException("对话响应ID不能为空");
        }

        try {
            // 构建关闭请求
            DeleteResponseRequest request = DeleteResponseRequest.builder()
                    .responseId(responseId.trim())
                    .build();

            // 调用API关闭对话
            DeleteResponseResponse response = arkService.deleteResponse(request);
            log.info("关闭对话成功,responseId:{}, 响应结果:{}", responseId, response);
            return response;
        } catch (Exception e) {
            log.error("关闭对话失败,responseId:{}", responseId, e);
            throw new RuntimeException("关闭对话异常,请检查网络或配置", e);
        }
    }

    /*
    解析响应内容
     */
    private String extractReplyContent(ResponseObject response) {
        StringBuilder userContent = new StringBuilder();
        response.getOutput().forEach(item -> {
            if (item instanceof ItemOutputMessage msg) {
                if ("assistant".equals(msg.getRole())) {
                    msg.getContent().forEach(content -> {
                        if (content instanceof OutputContentItemText text) {
                            userContent.append(text.getText());
                        }
                    });
                }
            }
        });
        return userContent.toString();
    }

}