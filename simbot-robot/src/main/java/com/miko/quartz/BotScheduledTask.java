//package com.miko.quartz;
//
//import com.miko.entity.BotTaskModel;
//import com.miko.service.BotTaskService;
//import com.miko.util.ApiUtil;
//import com.miko.util.JsonUtil;
//import jakarta.annotation.Resource;
//import lombok.val;
//import love.forte.simbot.ID;
//import love.forte.simbot.Identifies;
//import love.forte.simbot.application.BotManagers;
//import love.forte.simbot.bot.BotManager;
//import love.forte.simbot.component.mirai.MiraiFriend;
//import love.forte.simbot.component.mirai.bot.MiraiBot;
//import love.forte.simbot.component.mirai.bot.MiraiBotManager;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.HashMap;
//import java.util.List;
//
///**
// * Bot定时任务
// */
//@Component
//public class BotScheduledTask {
//    private static final Logger LOGGER = LoggerFactory.getLogger(BotScheduledTask.class);
//    @Autowired
//    private BotManagers botManagers;
//    @Resource
//    private BotTaskService botTaskService;
//
//    @Value(value = "${simbot.default-master-qq}")
//    private String defaultMaster;
//
//    @Value("${simbot.default-notice-groups}")
//    private List<String> defaultGroups;
//
//
//
//    /**
//     * 每一小时发送一次: 0 0 0/1 * * ?
//     * 每五分钟发送一次: 0 0/5 * * * ?
//     * 每天晚上8点: 0 0 20 * * ?
//     */
//    @Scheduled(cron = "0 0 8 * * ?")
//    public void goodMorning() {
//        try {
//            MiraiBot bot = getBot();
//            assert bot != null;
//            List<BotTaskModel> allTask = botTaskService.getAllActive();
//            LOGGER.info("正在发送定时任务 List={}", allTask);
//            allTask.forEach(task -> {
//                if ("0".equals(task.getTypes())) {
//                    Arrays.stream(task.getTargets().split(",")).forEach(target -> {
//                        val group = bot.getGroup(ID.$(target));
//                        if (group != null) {
//                            group.sendAsync(task.getContents());
//                        } else {
//                            LOGGER.info("消息发送失败，群组{}不存在", target);
//                        }
//
//                    });
//                } else {
//                    Arrays.stream(task.getTargets().split(",")).forEach(target -> {
//                        MiraiFriend friend = bot.getFriend(Identifies.ID(target));
//                        if (friend != null) {
//                            friend.sendAsync(task.getContents());
//                        } else {
//                            LOGGER.info("消息发送失败，好友{}不存在", target);
//                        }
//
//                    });
//                }
//            });
//        } catch (Exception e) {
//            LOGGER.error("定时任务发送异常, e={}", e);
//        }
//    }
//
//    @Scheduled(cron = "0 0/1 * * * ?")
//    public void loveGreeting() {
//        Calendar calendar = Calendar.getInstance();
//        // 获取当前小时
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        // 只在早上8点到晚上22点发送消息
////        if (hour < 8 || hour > 22) {
////            return;
////        }
//
//        try {
//            MiraiBot bot = getBot();
//            assert bot != null;
//            final String msg = ApiUtil.callGet("https://api.mcloc.cn/love/");
//
//            defaultGroups.forEach(id -> {
//                val group = bot.getGroup(ID.$(id));
//                LOGGER.info("正在发送定时任务,group={}, msg={}", id, msg);
//                if (group != null) {
//                    group.sendAsync(msg.trim());
//                } else {
//                    LOGGER.info("消息发送失败，group={}, msg={}", id, msg);
//                }
//            });
//
////            MiraiFriend friend = bot.getFriend(Identifies.ID(defaultMaster));
////            if (friend != null) {
////                friend.sendAsync(msg);
////            } else {
////                LOGGER.info("消息发送失败，friend={}, msg={}", defaultMaster, msg);
////            }
//        } catch (Exception e) {
//            LOGGER.error("定时任务发送异常, e={}", e);
//        }
//    }
//
//    public MiraiBot getBot() throws IOException {
//        HashMap result = JsonUtil.parseJson("simbot-bots/default.bot.json");
//        val botId = result.get("code");
//        // 获取所有的bot
//        for (BotManager<?> manager : botManagers) {
//            if (manager instanceof MiraiBotManager miraiBotManager) {
//                MiraiBot bot = miraiBotManager.get(Identifies.ID((Long) botId));
//                if (bot != null) {
//                    return bot;
//                }
//            }
//        }
//        return null;
//    }
//}