package com.jktickets.controller;


import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.req.passenger.PassengerQueryReq;
import com.jktickets.req.passenger.PassengerSaveReq;
import com.jktickets.res.CommonRes;
import com.jktickets.res.PageRes;
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
       if(ObjectUtil.isNull(req.getId())){
           return new CommonRes<>("添加乘客成功");
       }else{
           return new CommonRes<>("编辑乘客成功");
       }
    }

    @GetMapping("/queryList")
    public CommonRes<PageRes<PassengerQueryRes>> queryPassengerList(@Valid PassengerQueryReq req) {

        //       获取当前用户的MemberID
        req.setMemberId(LoginMemberContext.getId());
        PageRes<PassengerQueryRes> passengerQueryResList = passengerService.queryPassengerList(req);
        return new CommonRes<>(passengerQueryResList);
    }


    @DeleteMapping("/delete/{id}")
    public CommonRes<Object> deleteById(@PathVariable Long id) {
            passengerService.deleteById(id);
        return new CommonRes<>("删除乘客成功");

    }

}
