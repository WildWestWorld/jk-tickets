package com.jktickets.controller;


import com.jktickets.service.MemberService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
public class MemberController {
    @Resource
    MemberService memberService;

    @GetMapping("/count")
    public Integer count() {
        return memberService.countNum();
    }


    @PostMapping("/register")
    public long registerByMobile(String mobile) {
        return memberService.registerByMobile(mobile);
    }

}
