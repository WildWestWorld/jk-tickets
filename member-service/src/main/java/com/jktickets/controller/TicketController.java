package com.jktickets.controller;


import com.jktickets.context.LoginMemberContext;
import com.jktickets.req.ticket.TicketQueryReq;
import com.jktickets.res.CommonRes;
import com.jktickets.res.PageRes;
import com.jktickets.res.ticket.TicketQueryRes;
import com.jktickets.service.TicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ticket")
public class TicketController {
    @Resource
    TicketService ticketService;

//    @PostMapping("/save")
//    public CommonRes<Object> saveTicket(@Valid @RequestBody MemberTicketReq req) {
//
//        ticketService.saveTicket(req);
//           return new CommonRes<>("添加Ticket成功");
//
//    }

    @GetMapping("/queryList")
    public CommonRes<PageRes<TicketQueryRes>> queryTicketList(@Valid TicketQueryReq req) {

        //       获取当前用户的MemberID
        req.setMemberId(LoginMemberContext.getId());
        PageRes<TicketQueryRes> ticketQueryResList = ticketService.queryTicketList(req);
        return new CommonRes<>(ticketQueryResList);
    }


//    @DeleteMapping("/delete/{id}")
//    public CommonRes<Object> deleteById(@PathVariable Long id) {
//            ticketService.deleteById(id);
//        return new CommonRes<>("删除Ticket成功");
//
//    }

}
