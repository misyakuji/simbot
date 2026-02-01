package com.miko.quartz;

import com.miko.config.SimBotConfig;
import com.miko.service.BotTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.application.Application;
import love.forte.simbot.common.id.Identifies;
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBot;
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBotManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

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
    @Scheduled(cron = "0 0 8 * * ?")
    public void goodMorning() {
        try {
            final OneBotBot bot = getBot();
            botTaskService.getAllActive().forEach(task -> {
                if ("0".equals(task.getTargetType())) {
                    // https://simbot.forte.love/component-onebot-v11-onebotbot.html#onebotbotgrouprelation
                    bot.getGroupRelation().getGroups().collectAsync(
                            bot, group -> {
                                if (group.getId().equals(Identifies.of(task.getTargetId()))) {
                                    group.sendAsync(task.getContent());
                                    log.info("发送定时任务,group={}, msg={}", task.getTargetId(), task.getContent());
                                }
                            }
                    );
                } else if ("1".equals(task.getTargetType())) {
                    // https://simbot.forte.love/component-onebot-v11-onebotbot.html#onebotbotfriendrelation
                    bot.getContactRelation().getContacts().collectAsync(
                            bot, friend -> {
                                if (friend.getId().equals(Identifies.of(task.getTargetId()))) {
                                    friend.sendAsync(task.getContent());
                                    log.info("发送定时任务,friend={}, msg={}", friend.getId(), task.getContent());
                                }
                            }
                    );
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
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        // 只在早上8点到晚上22点发送消息
        if (hour < 8 || hour > 22) {
            return;
        }

        try {
            final OneBotBot bot = getBot();
            final String msg = String.format("现在是时间%s，要记得多喝热水哦！",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年M月d日H时")));
//            bot.getGroupRelation().getGroups().collectAsync(
//                    bot, group -> {
//                        group.sendAsync(msg);
//                        log.info("发送定时任务,group={}, msg={}", group.getId(), msg);
//                    }
//            );
        } catch (Exception e) {
            log.error("定时任务发送异常!", e);
        }
    }

    public OneBotBot getBot() {
        return application.getBotManagers()
                .stream()
                .filter(it -> it instanceof OneBotBotManager)
                .map(it -> (OneBotBotManager) it)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("OneBotBotManager not found!"))
                .get(Identifies.of(simBotConfig.getAuthorization().getBotUniqueId()));
    }
}