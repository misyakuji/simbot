package com.miko;

import love.forte.simbot.spring.EnableSimbot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableSimbot
@EnableScheduling
@SpringBootApplication
public class StartApplication {
    static void main(String[] args) {
        SpringApplication.run(StartApplication.class, args);
    }
}
