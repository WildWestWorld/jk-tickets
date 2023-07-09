package com.jktickets.controller.admin;


import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.req.trainCarriage.TrainCarriageQueryReq;
import com.jktickets.req.trainCarriage.TrainCarriageSaveReq;
import com.jktickets.res.CommonRes;
import com.jktickets.res.PageRes;
import com.jktickets.res.trainCarriage.TrainCarriageQueryRes;
import com.jktickets.service.TrainCarriageService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/trainCarriage")
public class TrainCarriageAdminController {
    @Resource
    TrainCarriageService trainCarriageService;

    @PostMapping("/save")
    public CommonRes<Object> saveTrainCarriage(@Valid @RequestBody TrainCarriageSaveReq req) {

        trainCarriageService.saveTrainCarriage(req);
       if(ObjectUtil.isNull(req.getId())){
           return new CommonRes<>("添加TrainCarriage成功");
       }else{
           return new CommonRes<>("编辑TrainCarriage成功");
       }
    }

    @GetMapping("/queryList")
    public CommonRes<PageRes<TrainCarriageQueryRes>> queryTrainCarriageList(@Valid TrainCarriageQueryReq req) {

        //       获取当前用户的MemberID
        //req.setMemberId(LoginMemberContext.getId());
        PageRes<TrainCarriageQueryRes> trainCarriageQueryResList = trainCarriageService.queryTrainCarriageList(req);
        return new CommonRes<>(trainCarriageQueryResList);
    }


    @DeleteMapping("/delete/{id}")
    public CommonRes<Object> deleteById(@PathVariable Long id) {
            trainCarriageService.deleteById(id);
        return new CommonRes<>("删除TrainCarriage成功");

    }

}
