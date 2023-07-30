package com.jktickets.feign;


import com.jktickets.req.MemberTicketReq;
import com.jktickets.res.CommonRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;

//开启外键
//@FeignClient(name = "member-service",url = "http://127.0.0.1:8001")
@FeignClient(name = "member-service")
public interface MemberFeign {
    @GetMapping("/member/hello")
    String hello();

    @GetMapping("/member/feign/ticket/save")
    CommonRes<Object> save(@RequestBody MemberTicketReq req);
}
