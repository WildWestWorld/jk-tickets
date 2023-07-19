package com.jktickets.controller.admin;


import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.req.confirmOrder.ConfirmOrderDoReq;
import com.jktickets.req.confirmOrder.ConfirmOrderQueryReq;
import com.jktickets.req.confirmOrder.ConfirmOrderSaveReq;
import com.jktickets.res.CommonRes;
import com.jktickets.res.PageRes;
import com.jktickets.res.confirmOrder.ConfirmOrderQueryRes;
import com.jktickets.service.ConfirmOrderService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/confirmOrder")
public class ConfirmOrderAdminController {
    @Resource
    ConfirmOrderService confirmOrderService;



    @GetMapping("/doConfirm")
    public CommonRes<Object> doConfirm(@Valid ConfirmOrderDoReq req) {
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
