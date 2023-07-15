package com.jktickets.controller.admin;


import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.req.dailyTrainSeat.DailyTrainSeatQueryReq;
import com.jktickets.req.dailyTrainSeat.DailyTrainSeatSaveReq;
import com.jktickets.res.CommonRes;
import com.jktickets.res.PageRes;
import com.jktickets.res.dailyTrainSeat.DailyTrainSeatQueryRes;
import com.jktickets.service.DailyTrainSeatService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dailyTrainSeat")
public class DailyTrainSeatAdminController {
    @Resource
    DailyTrainSeatService dailyTrainSeatService;

    @PostMapping("/save")
    public CommonRes<Object> saveDailyTrainSeat(@Valid @RequestBody DailyTrainSeatSaveReq req) {

        dailyTrainSeatService.saveDailyTrainSeat(req);
       if(ObjectUtil.isNull(req.getId())){
           return new CommonRes<>("添加DailyTrainSeat成功");
       }else{
           return new CommonRes<>("编辑DailyTrainSeat成功");
       }
    }

    @GetMapping("/queryList")
    public CommonRes<PageRes<DailyTrainSeatQueryRes>> queryDailyTrainSeatList(@Valid DailyTrainSeatQueryReq req) {

        //       获取当前用户的MemberID
        //req.setMemberId(LoginMemberContext.getId());
        PageRes<DailyTrainSeatQueryRes> dailyTrainSeatQueryResList = dailyTrainSeatService.queryDailyTrainSeatList(req);
        return new CommonRes<>(dailyTrainSeatQueryResList);
    }


    @DeleteMapping("/delete/{id}")
    public CommonRes<Object> deleteById(@PathVariable Long id) {
            dailyTrainSeatService.deleteById(id);
        return new CommonRes<>("删除DailyTrainSeat成功");

    }

}
