package com.miko.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
//@PropertySource(value = {"classpath:webInfo.properties"})
public class TestController {


    @Value("${web.msg}")
    private String webMsg;

    @GetMapping
    String test() {
        return webMsg;
    }
}
