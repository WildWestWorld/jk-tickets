package com.jktickets.controller;


import cn.hutool.core.util.ObjectUtil;
import com.jktickets.req.station.StationQueryReq;
import com.jktickets.req.station.StationSaveReq;
import com.jktickets.res.CommonRes;
import com.jktickets.res.PageRes;
import com.jktickets.res.station.StationQueryRes;
import com.jktickets.service.StationService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/station")
public class StationController {
    @Resource
    StationService stationService;



    @GetMapping("/queryList")
    public CommonRes<PageRes<StationQueryRes>> queryStationList(@Valid StationQueryReq req) {

        //       获取当前用户的MemberID
        //req.setMemberId(LoginMemberContext.getId());
        PageRes<StationQueryRes> stationQueryResList = stationService.queryStationList(req);
        return new CommonRes<>(stationQueryResList);
    }



    @GetMapping("/queryAll")
    public CommonRes<List<StationQueryRes>> queryAllStationList() {

        //       获取当前用户的MemberID
        //req.setMemberId(LoginMemberContext.getId());
        List<StationQueryRes> trainQueryRes = stationService.queryAllStationList();


        return new CommonRes<>(trainQueryRes);
    }

}
