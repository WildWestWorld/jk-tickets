package com.jktickets.controller.admin;


import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.req.dailyTrainStation.DailyTrainStationQueryReq;
import com.jktickets.req.dailyTrainStation.DailyTrainStationSaveReq;
import com.jktickets.res.CommonRes;
import com.jktickets.res.PageRes;
import com.jktickets.res.dailyTrainStation.DailyTrainStationQueryRes;
import com.jktickets.service.DailyTrainStationService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dailyTrainStation")
public class DailyTrainStationAdminController {
    @Resource
    DailyTrainStationService dailyTrainStationService;

    @PostMapping("/save")
    public CommonRes<Object> saveDailyTrainStation(@Valid @RequestBody DailyTrainStationSaveReq req) {

        dailyTrainStationService.saveDailyTrainStation(req);
       if(ObjectUtil.isNull(req.getId())){
           return new CommonRes<>("添加DailyTrainStation成功");
       }else{
           return new CommonRes<>("编辑DailyTrainStation成功");
       }
    }

    @GetMapping("/queryList")
    public CommonRes<PageRes<DailyTrainStationQueryRes>> queryDailyTrainStationList(@Valid DailyTrainStationQueryReq req) {

        //       获取当前用户的MemberID
        //req.setMemberId(LoginMemberContext.getId());
        PageRes<DailyTrainStationQueryRes> dailyTrainStationQueryResList = dailyTrainStationService.queryDailyTrainStationList(req);
        return new CommonRes<>(dailyTrainStationQueryResList);
    }


    @DeleteMapping("/delete/{id}")
    public CommonRes<Object> deleteById(@PathVariable Long id) {
            dailyTrainStationService.deleteById(id);
        return new CommonRes<>("删除DailyTrainStation成功");

    }

}
