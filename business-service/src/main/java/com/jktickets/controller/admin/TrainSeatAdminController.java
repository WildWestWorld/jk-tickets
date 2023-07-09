package com.jktickets.controller.admin;


import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.req.trainSeat.TrainSeatQueryReq;
import com.jktickets.req.trainSeat.TrainSeatSaveReq;
import com.jktickets.res.CommonRes;
import com.jktickets.res.PageRes;
import com.jktickets.res.trainSeat.TrainSeatQueryRes;
import com.jktickets.service.TrainSeatService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/trainSeat")
public class TrainSeatAdminController {
    @Resource
    TrainSeatService trainSeatService;

    @PostMapping("/save")
    public CommonRes<Object> saveTrainSeat(@Valid @RequestBody TrainSeatSaveReq req) {

        trainSeatService.saveTrainSeat(req);
       if(ObjectUtil.isNull(req.getId())){
           return new CommonRes<>("添加TrainSeat成功");
       }else{
           return new CommonRes<>("编辑TrainSeat成功");
       }
    }

    @GetMapping("/queryList")
    public CommonRes<PageRes<TrainSeatQueryRes>> queryTrainSeatList(@Valid TrainSeatQueryReq req) {

        //       获取当前用户的MemberID
        //req.setMemberId(LoginMemberContext.getId());
        PageRes<TrainSeatQueryRes> trainSeatQueryResList = trainSeatService.queryTrainSeatList(req);
        return new CommonRes<>(trainSeatQueryResList);
    }


    @DeleteMapping("/delete/{id}")
    public CommonRes<Object> deleteById(@PathVariable Long id) {
            trainSeatService.deleteById(id);
        return new CommonRes<>("删除TrainSeat成功");

    }

}
