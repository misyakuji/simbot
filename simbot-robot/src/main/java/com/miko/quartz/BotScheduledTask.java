package com.miko.quartz;

import com.miko.config.SimBotConfig;
import com.miko.entity.BotTaskModel;
import com.miko.service.BotTaskService;
import kotlin.sequences.SequencesKt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import love.forte.simbot.application.Application;
import love.forte.simbot.common.id.Identifies;
import love.forte.simbot.component.onebot.v11.core.api.OneBotMessageOutgoing;
import love.forte.simbot.component.onebot.v11.core.api.SendGroupMsgApi;
import love.forte.simbot.component.onebot.v11.core.api.SendPrivateMsgApi;
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBot;
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBotManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Bot定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BotScheduledTask {

    private final SimBotConfig simBotConfig;

    private final BotTaskService botTaskService;

    private final Application application;


    /**
     * 每一小时发送一次: 0 0 0/1 * * ?
     * 每五分钟发送一次: 0 0/5 * * * ?
     * 每天晚上8点: 0 0 20 * * ?
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void goodMorning() {
        try {
            List<BotTaskModel> allTask = botTaskService.getAllActive();
            log.info("正在发送定时任务 List={}", allTask);
            allTask.forEach(task -> {
                if ("0".equals(task.getTargetType())) {
                    getBot().executeAsync(SendGroupMsgApi.create(Identifies.of(task.getTargetId()),
                            OneBotMessageOutgoing.create(task.getContent())));

                } else if ("1".equals(task.getTargetType())) {
                    getBot().executeAsync(SendPrivateMsgApi.create(Identifies.of(task.getTargetId()),
                            OneBotMessageOutgoing.create(task.getContent())));

                }
            });
        } catch (Exception e) {
            log.error("定时任务发送异常!", e);
        }
    }

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void loveGreeting() {
        Calendar calendar = Calendar.getInstance();
        // 获取当前小时
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        // 只在早上8点到晚上22点发送消息
//        if (hour < 8 || hour > 22) {
//            return;
//        }

        try {
            OneBotBot bot = getBot();
            assert bot != null;
            String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            final String msg = currentTime + " hello";
            var defaultGroups = new String[]{"710117186", "710117186"};
            Arrays.stream(defaultGroups).forEach(id -> {
                val group = Identifies.of(id);
                log.info("正在发送定时任务,group={}, msg={}", id, msg);
                bot.executeAsync(SendGroupMsgApi.create(group, OneBotMessageOutgoing.create(msg)));
            });
        } catch (Exception e) {
            log.error("定时任务发送异常!", e);
        }
    }

    public OneBotBot getBot() {
        val botId = simBotConfig.getAuthorization().getBotUniqueId();
        var obManager = application.getBotManagers()
                .stream()
                .filter(it -> it instanceof OneBotBotManager)
                .map(it -> (OneBotBotManager) it)
                .findFirst()
                .orElseThrow();

        // 遍历bot
        obManager.all().iterator().forEachRemaining(bot -> {
            // ...
        });

        // 也可以转成List
        final var list = SequencesKt.toList(obManager.all());

        // 通过你配置的 uniqueBotId 获取
        return obManager.get(Identifies.of(botId));
    }
}