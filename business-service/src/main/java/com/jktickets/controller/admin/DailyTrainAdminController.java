package com.jktickets.controller.admin;


import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.req.dailyTrain.DailyTrainQueryReq;
import com.jktickets.req.dailyTrain.DailyTrainSaveReq;
import com.jktickets.res.CommonRes;
import com.jktickets.res.PageRes;
import com.jktickets.res.dailyTrain.DailyTrainQueryRes;
import com.jktickets.service.DailyTrainService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dailyTrain")
public class DailyTrainAdminController {
    @Resource
    DailyTrainService dailyTrainService;

    @PostMapping("/save")
    public CommonRes<Object> saveDailyTrain(@Valid @RequestBody DailyTrainSaveReq req) {

        dailyTrainService.saveDailyTrain(req);
       if(ObjectUtil.isNull(req.getId())){
           return new CommonRes<>("添加DailyTrain成功");
       }else{
           return new CommonRes<>("编辑DailyTrain成功");
       }
    }

    @GetMapping("/queryList")
    public CommonRes<PageRes<DailyTrainQueryRes>> queryDailyTrainList(@Valid DailyTrainQueryReq req) {

        //       获取当前用户的MemberID
        //req.setMemberId(LoginMemberContext.getId());
        PageRes<DailyTrainQueryRes> dailyTrainQueryResList = dailyTrainService.queryDailyTrainList(req);
        return new CommonRes<>(dailyTrainQueryResList);
    }


    @DeleteMapping("/delete/{id}")
    public CommonRes<Object> deleteById(@PathVariable Long id) {
            dailyTrainService.deleteById(id);
        return new CommonRes<>("删除DailyTrain成功");

    }

}
