package com.jktickets.controller;


import com.jktickets.req.seatSell.SeatSellReq;
import com.jktickets.res.CommonRes;
import com.jktickets.res.seatSell.SeatSellRes;
import com.jktickets.service.DailyTrainSeatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

;

@RestController
@RequestMapping("/seatSell")
public class SeatSellController {

    @Autowired
    private DailyTrainSeatService dailyTrainSeatService;

    @GetMapping("/query")
    public CommonRes<List<SeatSellRes>> query(@Valid SeatSellReq req) {
        List<SeatSellRes> seatList = dailyTrainSeatService.querySeatSell(req);
        return new CommonRes<>(seatList);
    }

}
