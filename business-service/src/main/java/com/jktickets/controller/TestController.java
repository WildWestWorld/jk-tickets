package com.jktickets.controller;


import com.jktickets.feign.MemberFeign;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private static Logger LOG = LoggerFactory.getLogger(TestController.class);

    @Resource
    MemberFeign memberFeign;

    @GetMapping("/hello")
    public String hello() {


        String hello = memberFeign.hello();
        LOG.info(hello);

        return "HelloWorldBatch!"+hello;
    }
}
