package com.jktickets.controller;


import cn.hutool.core.util.ObjectUtil;
import com.jktickets.req.train.TrainQueryReq;
import com.jktickets.req.train.TrainSaveReq;
import com.jktickets.res.CommonRes;
import com.jktickets.res.PageRes;
import com.jktickets.res.train.TrainQueryRes;
import com.jktickets.service.TrainSeatService;
import com.jktickets.service.TrainService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/train")
public class TrainController {
    @Resource
    private TrainService trainService;




    @GetMapping("/queryList")
    public CommonRes<PageRes<TrainQueryRes>> queryTrainList(@Valid TrainQueryReq req) {

        //       获取当前用户的MemberID
        //req.setMemberId(LoginMemberContext.getId());
        PageRes<TrainQueryRes> trainQueryResList = trainService.queryTrainList(req);
        return new CommonRes<>(trainQueryResList);
    }


    @GetMapping("/queryAll")
    public CommonRes<List<TrainQueryRes>> queryAllTrainList() {

        //       获取当前用户的MemberID
        //req.setMemberId(LoginMemberContext.getId());
        List<TrainQueryRes> trainQueryRes = trainService.queryAllTrainList();


        return new CommonRes<>(trainQueryRes);
    }

}
