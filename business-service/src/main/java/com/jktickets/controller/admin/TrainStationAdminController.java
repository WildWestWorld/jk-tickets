package com.jktickets.controller.admin;


import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.req.trainStation.TrainStationQueryReq;
import com.jktickets.req.trainStation.TrainStationSaveReq;
import com.jktickets.res.CommonRes;
import com.jktickets.res.PageRes;
import com.jktickets.res.trainStation.TrainStationQueryRes;
import com.jktickets.service.TrainStationService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/trainStation")
public class TrainStationAdminController {
    @Resource
    TrainStationService trainStationService;

    @PostMapping("/save")
    public CommonRes<Object> saveTrainStation(@Valid @RequestBody TrainStationSaveReq req) {

        trainStationService.saveTrainStation(req);
       if(ObjectUtil.isNull(req.getId())){
           return new CommonRes<>("添加TrainStation成功");
       }else{
           return new CommonRes<>("编辑TrainStation成功");
       }
    }

    @GetMapping("/queryList")
    public CommonRes<PageRes<TrainStationQueryRes>> queryTrainStationList(@Valid TrainStationQueryReq req) {

        //       获取当前用户的MemberID
        //req.setMemberId(LoginMemberContext.getId());
        PageRes<TrainStationQueryRes> trainStationQueryResList = trainStationService.queryTrainStationList(req);
        return new CommonRes<>(trainStationQueryResList);
    }


    @DeleteMapping("/delete/{id}")
    public CommonRes<Object> deleteById(@PathVariable Long id) {
            trainStationService.deleteById(id);
        return new CommonRes<>("删除TrainStation成功");

    }

}
