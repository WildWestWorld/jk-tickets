package com.jktickets.controller;


import com.jktickets.req.passenger.PassengerSaveReq;
import com.jktickets.res.CommonRes;
import com.jktickets.service.PassengerService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/passenger")
public class PassengerController {
    @Resource
    PassengerService passengerService;

    @PostMapping("/save")
    public CommonRes<Object> save(@Valid @RequestBody PassengerSaveReq req) {
       passengerService.savePassenger(req);
        return new CommonRes<>("添加乘客成功");
    }
}
