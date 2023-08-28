package com.jktickets.controller;


import com.jktickets.req.dailyTrainStation.DailyTrainStationQueryAllReq;
import com.jktickets.res.CommonRes;
import com.jktickets.res.dailyTrainStation.DailyTrainStationQueryRes;
import com.jktickets.service.DailyTrainStationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dailyTrainStation")
public class DailyTrainStationController {

    @Autowired
    private DailyTrainStationService dailyTrainStationService;

    @GetMapping("/queryByTrainCode")
    public CommonRes<List<DailyTrainStationQueryRes>> queryByTrain(@Valid DailyTrainStationQueryAllReq req) {
        List<DailyTrainStationQueryRes> list = dailyTrainStationService.queryByTrain(req.getDate(), req.getTrainCode());
        return new CommonRes<>(list);
    }

}
