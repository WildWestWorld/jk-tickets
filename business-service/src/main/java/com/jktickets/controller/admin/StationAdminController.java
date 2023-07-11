package com.jktickets.controller.admin;


import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.req.station.StationQueryReq;
import com.jktickets.req.station.StationSaveReq;
import com.jktickets.req.train.TrainQueryReq;
import com.jktickets.res.CommonRes;
import com.jktickets.res.PageRes;
import com.jktickets.res.station.StationQueryRes;
import com.jktickets.res.train.TrainQueryRes;
import com.jktickets.service.StationService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/station")
public class StationAdminController {
    @Resource
    StationService stationService;

    @PostMapping("/save")
    public CommonRes<Object> saveStation(@Valid @RequestBody StationSaveReq req) {

        stationService.saveStation(req);
       if(ObjectUtil.isNull(req.getId())){
           return new CommonRes<>("添加Station成功");
       }else{
           return new CommonRes<>("编辑Station成功");
       }
    }

    @GetMapping("/queryList")
    public CommonRes<PageRes<StationQueryRes>> queryStationList(@Valid StationQueryReq req) {

        //       获取当前用户的MemberID
        //req.setMemberId(LoginMemberContext.getId());
        PageRes<StationQueryRes> stationQueryResList = stationService.queryStationList(req);
        return new CommonRes<>(stationQueryResList);
    }


    @DeleteMapping("/delete/{id}")
    public CommonRes<Object> deleteById(@PathVariable Long id) {
            stationService.deleteById(id);
        return new CommonRes<>("删除Station成功");

    }



    @GetMapping("/queryAll")
    public CommonRes<List<StationQueryRes>> queryAllStationList(@Valid TrainQueryReq req) {

        //       获取当前用户的MemberID
        //req.setMemberId(LoginMemberContext.getId());
        List<StationQueryRes> trainQueryRes = stationService.queryAllStationList(req);


        return new CommonRes<>(trainQueryRes);
    }

}
