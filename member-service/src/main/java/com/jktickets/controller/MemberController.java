package com.jktickets.controller;


import com.jktickets.req.MemberRegisterReq;
import com.jktickets.res.CommonRes;
import com.jktickets.service.MemberService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
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
    public CommonRes<Integer>  count() {
        int countNum = memberService.countNum();


        return new CommonRes<>(countNum);
    }


    @PostMapping("/register")
    public CommonRes<Long> registerByMobile(@Valid MemberRegisterReq req){
        long mobile = memberService.registerByMobile(req);

        return new CommonRes<>(mobile);
    }

}
