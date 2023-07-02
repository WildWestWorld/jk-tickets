package com.jktickets.controller;


import com.jktickets.context.LoginMemberContext;
import com.jktickets.req.passenger.PassengerQueryReq;
import com.jktickets.req.passenger.PassengerSaveReq;
import com.jktickets.res.CommonRes;
import com.jktickets.res.passenger.PassengerQueryRes;
import com.jktickets.service.PassengerService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/passenger")
public class PassengerController {
    @Resource
    PassengerService passengerService;

    @PostMapping("/save")
    public CommonRes<Object> savePassenger(@Valid @RequestBody PassengerSaveReq req) {
       passengerService.savePassenger(req);
        return new CommonRes<>("添加乘客成功");
    }

    @GetMapping("/queryList")
    public CommonRes<Object> queryPassengerList(@Valid  PassengerQueryReq req) {
//       获取当前用户的MemberID
        req.setMemberId(LoginMemberContext.getId());
        List<PassengerQueryRes> passengerQueryResList = passengerService.queryPassengerList(req);
        return new CommonRes<>(passengerQueryResList);
    }

}
