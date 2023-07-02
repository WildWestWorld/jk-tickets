package com.jktickets.controller;


import com.jktickets.req.merber.MemberLoginReq;
import com.jktickets.req.merber.MemberRegisterReq;
import com.jktickets.req.merber.MemberSendCodeReq;
import com.jktickets.res.CommonRes;
import com.jktickets.res.member.MemberLoginRes;
import com.jktickets.service.MemberService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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
//    @RequestBody 用于请求 用JSON
    public CommonRes<Long> registerByMobile(@Valid  @RequestBody  MemberRegisterReq req){
        long mobile = memberService.registerByMobile(req);

        return new CommonRes<>(mobile);
    }
    @PostMapping("/sendCode")
    public CommonRes<String> sendCode(@Valid @RequestBody MemberSendCodeReq req){
         memberService.sendCode(req);

        return new CommonRes<>("短信发送成功");
    }

    @PostMapping("/login")
    public CommonRes<MemberLoginRes> sendCode(@Valid  @RequestBody MemberLoginReq req){
        MemberLoginRes memberLoginRes = memberService.login(req);

        return new CommonRes<>(memberLoginRes);
    }



}
