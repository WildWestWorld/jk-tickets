package com.jktickets.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RefreshScope 加上了Nacos的配置文件改变 对应的value值也会实时更新
@RefreshScope
public class TestController {
    @Value("${test.nacos}")
    String testNacos;

    @GetMapping("/hello")
    public String hello() {
        return String.format("Hello %s!",testNacos);
    }
}
