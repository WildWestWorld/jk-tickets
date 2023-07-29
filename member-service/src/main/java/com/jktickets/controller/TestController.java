package com.jktickets.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Value("${test.nacos}")
    String testNacos;

    @GetMapping("/hello")
    public String hello() {
        return String.format("Hello %s!",testNacos);
    }
}
