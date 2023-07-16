package com.jktickets.controller.admin;


import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.req.dailyTrainTicket.DailyTrainTicketQueryReq;
import com.jktickets.req.dailyTrainTicket.DailyTrainTicketSaveReq;
import com.jktickets.res.CommonRes;
import com.jktickets.res.PageRes;
import com.jktickets.res.dailyTrainTicket.DailyTrainTicketQueryRes;
import com.jktickets.service.DailyTrainTicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dailyTrainTicket")
public class DailyTrainTicketAdminController {
    @Resource
    DailyTrainTicketService dailyTrainTicketService;

    @PostMapping("/save")
    public CommonRes<Object> saveDailyTrainTicket(@Valid @RequestBody DailyTrainTicketSaveReq req) {

        dailyTrainTicketService.saveDailyTrainTicket(req);
       if(ObjectUtil.isNull(req.getId())){
           return new CommonRes<>("添加DailyTrainTicket成功");
       }else{
           return new CommonRes<>("编辑DailyTrainTicket成功");
       }
    }

    @GetMapping("/queryList")
    public CommonRes<PageRes<DailyTrainTicketQueryRes>> queryDailyTrainTicketList(@Valid DailyTrainTicketQueryReq req) {

        //       获取当前用户的MemberID
        //req.setMemberId(LoginMemberContext.getId());
        PageRes<DailyTrainTicketQueryRes> dailyTrainTicketQueryResList = dailyTrainTicketService.queryDailyTrainTicketList(req);
        return new CommonRes<>(dailyTrainTicketQueryResList);
    }


    @DeleteMapping("/delete/{id}")
    public CommonRes<Object> deleteById(@PathVariable Long id) {
            dailyTrainTicketService.deleteById(id);
        return new CommonRes<>("删除DailyTrainTicket成功");

    }

}
