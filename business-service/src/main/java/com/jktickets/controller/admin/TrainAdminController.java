package com.jktickets.controller.admin;


import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
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
@RequestMapping("/admin/train")
public class TrainAdminController {
    @Resource
    private TrainService trainService;

    @Resource
    private TrainSeatService trainSeatService;

    @PostMapping("/save")
    public CommonRes<Object> saveTrain(@Valid @RequestBody TrainSaveReq req) {

        trainService.saveTrain(req);
       if(ObjectUtil.isNull(req.getId())){
           return new CommonRes<>("添加Train成功");
       }else{
           return new CommonRes<>("编辑Train成功");
       }
    }

    @GetMapping("/queryList")
    public CommonRes<PageRes<TrainQueryRes>> queryTrainList(@Valid TrainQueryReq req) {

        //       获取当前用户的MemberID
        //req.setMemberId(LoginMemberContext.getId());
        PageRes<TrainQueryRes> trainQueryResList = trainService.queryTrainList(req);
        return new CommonRes<>(trainQueryResList);
    }


    @DeleteMapping("/delete/{id}")
    public CommonRes<Object> deleteById(@PathVariable Long id) {
            trainService.deleteById(id);
        return new CommonRes<>("删除Train成功");

    }
//    trainCode火车编号
    @GetMapping("/genSeat/{trainCode}")
    public CommonRes<Object> genSeat(@PathVariable String trainCode) {
        trainSeatService.genTrainSeat(trainCode);

        return new CommonRes<>("生成Seat成功");

    }

    @GetMapping("/queryAll")
    public CommonRes<List<TrainQueryRes>> queryAllTrainList() {

        //       获取当前用户的MemberID
        //req.setMemberId(LoginMemberContext.getId());
        List<TrainQueryRes> trainQueryRes = trainService.queryAllTrainList();


        return new CommonRes<>(trainQueryRes);
    }

}
