package com.jktickets.controller.admin;
import java.util.Date;


import cn.hutool.core.util.ObjectUtil;
import com.jktickets.dto.ConfirmOrderMQDto;
import com.jktickets.req.confirmOrder.ConfirmOrderDoReq;
import com.jktickets.req.confirmOrder.ConfirmOrderQueryReq;
import com.jktickets.req.confirmOrder.ConfirmOrderSaveReq;
import com.jktickets.req.dailyTrain.DailyTrainQueryReq;
import com.jktickets.req.dailyTrain.DailyTrainSaveReq;
import com.jktickets.res.CommonRes;
import com.jktickets.res.PageRes;
import com.jktickets.res.confirmOrder.ConfirmOrderQueryRes;
import com.jktickets.res.dailyTrain.DailyTrainQueryRes;
import com.jktickets.service.BeforeConfirmOrderService;
import com.jktickets.service.ConfirmOrderService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/confirmOrder")
public class ConfirmOrderAdminController {
    @Resource
    ConfirmOrderService confirmOrderService;

    @Resource
    BeforeConfirmOrderService beforeConfirmOrderService;



    @PostMapping("/save")
    public CommonRes<Object> saveConfirmOrder(@Valid @RequestBody ConfirmOrderSaveReq req) {

        confirmOrderService.saveConfirmOrder(req);
        if(ObjectUtil.isNull(req.getId())){
            return new CommonRes<>("添加ConfirmOrder成功");
        }else{
            return new CommonRes<>("编辑ConfirmOrder成功");
        }
    }


    @GetMapping("/queryList")
    public CommonRes<PageRes<ConfirmOrderQueryRes>> queryDailyTrainList(@Valid ConfirmOrderQueryReq req) {

        //       获取当前用户的MemberID
        //req.setMemberId(LoginMemberContext.getId());
        PageRes<ConfirmOrderQueryRes> confirmOrderQueryResPageRes = confirmOrderService.queryConfirmOrderList(req);
        return new CommonRes<>(confirmOrderQueryResPageRes);
    }


    @PostMapping("/doConfirm")
    public CommonRes<Object> doConfirm(@Valid @RequestBody  ConfirmOrderDoReq req) {

        ConfirmOrderMQDto confirmOrderMQDto = new ConfirmOrderMQDto();
        confirmOrderMQDto.setLogId(req.getLogId());
        confirmOrderMQDto.setDate(req.getDate());
        confirmOrderMQDto.setTrainCode(req.getTrainCode());



        confirmOrderService.doConfirm(confirmOrderMQDto);


        //       获取当前用户的MemberID
        //req.setMemberId(LoginMemberContext.getId());
        return new CommonRes<>();
    }


    @DeleteMapping("/delete/{id}")
    public CommonRes<Object> deleteById(@PathVariable Long id) {
            confirmOrderService.deleteById(id);
        return new CommonRes<>("删除ConfirmOrder成功");

    }

}
