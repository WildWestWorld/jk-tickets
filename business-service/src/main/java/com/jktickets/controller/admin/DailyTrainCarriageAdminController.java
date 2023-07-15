package com.jktickets.controller.admin;


import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.req.dailyTrainCarriage.DailyTrainCarriageQueryReq;
import com.jktickets.req.dailyTrainCarriage.DailyTrainCarriageSaveReq;
import com.jktickets.res.CommonRes;
import com.jktickets.res.PageRes;
import com.jktickets.res.dailyTrainCarriage.DailyTrainCarriageQueryRes;
import com.jktickets.service.DailyTrainCarriageService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dailyTrainCarriage")
public class DailyTrainCarriageAdminController {
    @Resource
    DailyTrainCarriageService dailyTrainCarriageService;

    @PostMapping("/save")
    public CommonRes<Object> saveDailyTrainCarriage(@Valid @RequestBody DailyTrainCarriageSaveReq req) {

        dailyTrainCarriageService.saveDailyTrainCarriage(req);
       if(ObjectUtil.isNull(req.getId())){
           return new CommonRes<>("添加DailyTrainCarriage成功");
       }else{
           return new CommonRes<>("编辑DailyTrainCarriage成功");
       }
    }

    @GetMapping("/queryList")
    public CommonRes<PageRes<DailyTrainCarriageQueryRes>> queryDailyTrainCarriageList(@Valid DailyTrainCarriageQueryReq req) {

        //       获取当前用户的MemberID
        //req.setMemberId(LoginMemberContext.getId());
        PageRes<DailyTrainCarriageQueryRes> dailyTrainCarriageQueryResList = dailyTrainCarriageService.queryDailyTrainCarriageList(req);
        return new CommonRes<>(dailyTrainCarriageQueryResList);
    }


    @DeleteMapping("/delete/{id}")
    public CommonRes<Object> deleteById(@PathVariable Long id) {
            dailyTrainCarriageService.deleteById(id);
        return new CommonRes<>("删除DailyTrainCarriage成功");

    }

}
