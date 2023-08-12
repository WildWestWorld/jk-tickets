package com.jktickets.controller;


import cn.hutool.core.util.RandomUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
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


    @SentinelResource("hello")
    @GetMapping("/hello")
    public String hello() {
        int i = RandomUtil.randomInt(1, 10);
        if(i<=3){
            throw new RuntimeException("测试异常");
        }


//        String hello = memberFeign.hello();
//        LOG.info(hello);
//        return "HelloWorldBatch!"+hello;

        return "HelloWorldBatch!";
    }
}
