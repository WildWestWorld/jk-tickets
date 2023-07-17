package com.jktickets.controller;


import cn.hutool.core.util.ObjectUtil;
import com.jktickets.req.dailyTrainTicket.DailyTrainTicketQueryReq;
import com.jktickets.req.dailyTrainTicket.DailyTrainTicketSaveReq;
import com.jktickets.res.CommonRes;
import com.jktickets.res.PageRes;
import com.jktickets.res.dailyTrainTicket.DailyTrainTicketQueryRes;
import com.jktickets.service.DailyTrainTicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dailyTrainTicket")
public class DailyTrainTicketController {
    @Resource
    DailyTrainTicketService dailyTrainTicketService;



    @GetMapping("/queryList")
    public CommonRes<PageRes<DailyTrainTicketQueryRes>> queryDailyTrainTicketList(@Valid DailyTrainTicketQueryReq req) {

        //       获取当前用户的MemberID
        //req.setMemberId(LoginMemberContext.getId());
        PageRes<DailyTrainTicketQueryRes> dailyTrainTicketQueryResList = dailyTrainTicketService.queryDailyTrainTicketList(req);
        return new CommonRes<>(dailyTrainTicketQueryResList);
    }




}
