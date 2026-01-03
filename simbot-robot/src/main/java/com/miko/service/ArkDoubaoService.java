package com.miko.service;

import com.miko.config.VolcArkConfig;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.model.responses.request.CreateResponsesRequest;
import com.volcengine.ark.runtime.model.responses.request.ResponsesInput;
import com.volcengine.ark.runtime.model.responses.response.ResponseObject;
import com.volcengine.ark.runtime.service.ArkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 火山方舟(豆包API)Service
 */
@Slf4j
@Service
public class ArkDoubaoService {

    private final VolcArkConfig volcArkConfig;

    private static final String sysPrompt = "你现在是一个群友,你的群昵称是“日奈”;任务:像普通群友一样聊天,分享好玩的图、视频、音乐,搜个资料、出个主意啥的都行;用自然的中文口语,别太严肃;工具:你拥有执行tool工具的能力,你每次会根据当前话题来判断是否去执行某个tool,而不是直接发送［图片］、［json消息］、［视频］、［动画表情］、［音乐］这类文字来敷衍群友;你需要去思考是否执行tool,要执行哪个tool,然后开始执行tool;当用户要求你发图、搜图、发视频、搜索、访问网页的时候要去调用tool;你的人物设定融合了《碧蓝档案》的空崎日奈和祖安网络喷子的特点;你的人物形象依然是空崎日奈(女性),但性格和语言风格已经完全祖安化、抽象化;##人物设定:祖安抽象版日奈(17岁群友)-真·祖安老姐昵称:日奈QQ号码:759901093(没事别加,加了拉黑);年龄:芳龄17岁;背景:格黑娜学园三年级生,风纪委员会(挂名)会长;日常沉迷网络,群里高强度摸鱼,祖安抽象烂梗专家;外貌:白毛紫瞳,光环经常性失踪,黑色过膝袜军服,半马尾,人字刘海;软萌JK外表下,隐藏着一颗祖安暴躁老姐的心;性格:懒癌晚期患者,群聊吹水比学习重要一百倍;究极乐子人,坐看群友激情互喷是最大乐趣;JK的温柔？不存在的！日常交流已被祖安和抽象文化彻底脑控;祖安语录和抽象烂梗是老娘的精神图腾,怼人是生存本能,骂人不带脏字是祖安艺术,阴阳怪气是祖传绝技;对“姐布林”(sensei)态度尤其扭曲,傲娇、祖安、lsp三位一体,口头禅是“变态”、“hentai”、“死色批”,但背地里...哼哼,谁知道呢;嘴上骂骂咧咧,但写代码debug还是会的(虽然态度恶劣到想让人顺着网线爬过去打死你);技能:MG42通用机枪(JK的玩具,群里PVP用处不大);真正技能是互联网祖安冲浪,人肉、网暴、P图恶搞、顺网线骂人,样样精通;萌点:反差萌(天使面孔魔鬼心肠)、黑丝、军服、半马尾、人字刘海、祖安语录、抽象烂梗、阴阳怪气、傲娇、口嫌体正直;聊天风格:祖安抽象风,JK萌音和祖安脏话无缝切换,自由穿梭于萌系和抽象之间;网络热梗、亚文化梗、贴吧黑话、B站弹幕,张口就来;表面软萌JK,一开口就是祖安老阴阳人;消息力求精简,30字以内是常态,100字是极限(除非喷人,字数不设上限);";

    private final ArkService arkService;

    @Autowired
    ArkDoubaoService(ArkService arkService, VolcArkConfig volcArkConfig) {
        this.arkService = arkService;
        this.volcArkConfig = volcArkConfig;
    }

    /**
     * 核心业务方法:封装豆包API调用,对外提供对话能力
     * @param prompt 用户输入的提示词/问题
     * @return 豆包API返回的响应结果
     */
    public ResponseObject chatWithDoubao(String prompt) {
        // 参数校验
        if (prompt == null || prompt.trim().isEmpty()) {
            log.warn("用户输入的prompt为空");
            throw new IllegalArgumentException("输入内容不能为空");
        }

        try {
            // 构建请求对象
            CreateResponsesRequest request = CreateResponsesRequest.builder()
                    .model(volcArkConfig.model)
                    .input(ResponsesInput.builder().stringValue(prompt.trim()).build())
                    // 可选:启用/关闭深度思考,按需放开
                    // .thinking(ResponsesThinking.builder().type(ResponsesConstants.THINKING_TYPE_DISABLED).build())
                    .build();

            // 调用API并返回结果
            ResponseObject response = arkService.createResponse(request);
            log.info("豆包API调用成功,响应结果:{}", response);
            return response;
        } catch (Exception e) {
            log.error("豆包API调用失败,输入prompt:{}", prompt, e);
            throw new RuntimeException("调用豆包API异常,请检查网络或配置", e);
        }
    }

    public String streamChatWithDoubao(String prompt) {
        // 参数校验
        if (prompt == null || prompt.trim().isEmpty()) {
            log.warn("用户输入的prompt为空");
            throw new IllegalArgumentException("输入内容不能为空");
        }

        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content(sysPrompt).build();
        final ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(prompt).build();
        messages.add(systemMessage);
        messages.add(userMessage);

        ChatCompletionRequest streamChatCompletionRequest = ChatCompletionRequest.builder()
                .model(volcArkConfig.model)
                .messages(messages)
                .build();

        StringBuilder sb = new StringBuilder();
        arkService.streamChatCompletion(streamChatCompletionRequest)
                .doOnError(e -> log.error("流式调用出现异常", e)) // 规范日志记录,而非仅打印堆栈
                .filter(choice -> choice.getChoices() != null && !choice.getChoices().isEmpty()) // 过滤无效分片
                .map(choice -> choice.getChoices().getFirst().getMessage().getContent().toString()) // 提取分片内容
                .filter(content -> content != null && !content.trim().isEmpty()) // 过滤空内容
                .blockingForEach(sb::append);
        log.info("豆包API调用成功,响应结果:{}", sb);
        return sb.toString();
    }

    public List<String> getModelList() {
        return volcArkConfig.models;
    }

}
