package com.jktickets.controller.feign;


import cn.hutool.core.util.ObjectUtil;
import com.jktickets.req.MemberTicketReq;
import com.jktickets.req.ticket.TicketSaveReq;
import com.jktickets.res.CommonRes;
import com.jktickets.service.TicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feign/ticket")
public class FeignTicketController {
    @Resource
    private TicketService ticketService;

    @PostMapping("/save")
    public CommonRes<Object> saveTicket(@Valid @RequestBody MemberTicketReq req) throws Exception {

        ticketService.saveTicket(req);
        return new CommonRes<>("添加Ticket成功");
    }


}
