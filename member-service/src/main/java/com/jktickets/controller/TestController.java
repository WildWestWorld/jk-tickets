package com.jktickets.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RefreshScope 加上了Nacos的配置文件改变 对应的value值也会实时更新
@RefreshScope
public class TestController {
    @Value("${test.nacos}")
    String testNacos;


//    用于获取端口属性
    @Autowired
    Environment environment;

    @GetMapping("/hello")
    public String hello() {
        String port = environment.getProperty("local.server.port");



        return String.format("Hello %s!  端口:%s",testNacos,port);
    }
}
