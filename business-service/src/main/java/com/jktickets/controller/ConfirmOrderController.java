package com.jktickets.controller;


import com.jktickets.req.confirmOrder.ConfirmOrderDoReq;
import com.jktickets.res.CommonRes;
import com.jktickets.service.ConfirmOrderService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/confirmOrder")
public class ConfirmOrderController {
    @Resource
    ConfirmOrderService confirmOrderService;



    @PostMapping("/doConfirm")
    public CommonRes<Object> doConfirm(@Valid @RequestBody  ConfirmOrderDoReq req) {
        confirmOrderService.doConfirm(req);
        //       获取当前用户的MemberID
        //req.setMemberId(LoginMemberContext.getId());
        return new CommonRes<>();
    }


//    @DeleteMapping("/delete/{id}")
//    public CommonRes<Object> deleteById(@PathVariable Long id) {
//            confirmOrderService.deleteById(id);
//        return new CommonRes<>("删除ConfirmOrder成功");
//
//    }

}
