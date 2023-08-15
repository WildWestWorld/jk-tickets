package com.jktickets.controller;


import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.jktickets.exception.BusinessException;
import com.jktickets.exception.BusinessExceptionEnum;
import com.jktickets.req.confirmOrder.ConfirmOrderDoReq;
import com.jktickets.res.CommonRes;
import com.jktickets.service.AfterConfirmOrderService;
import com.jktickets.service.ConfirmOrderService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/confirmOrder")
public class ConfirmOrderController {


    private final static Logger LOG = LoggerFactory.getLogger(ConfirmOrderController.class);


    @Resource
    ConfirmOrderService confirmOrderService;

    @Autowired
    private StringRedisTemplate redisTemplate;
    @PostMapping("/doConfirm")
    @SentinelResource(value = "/confirmOrderDoConfirm", blockHandler = "doConfirmBlock")
    public CommonRes<Object> doConfirm(@Valid @RequestBody ConfirmOrderDoReq req) {


        // 图形验证码校验
        String imageCodeToken = req.getImageCodeToken();
        String imageCode = req.getImageCode();
        String imageCodeRedis = redisTemplate.opsForValue().get(imageCodeToken);
        LOG.info("从redis中获取到的验证码：{}", imageCodeRedis);
        if (ObjectUtils.isEmpty(imageCodeRedis)) {
            return new CommonRes<>(false,400, "验证码已过期", null);
        }
        // 验证码校验，大小写忽略，提升体验，比如Oo Vv Ww容易混
        if (!imageCodeRedis.equalsIgnoreCase(imageCode)) {
            return new CommonRes<>(false, 400,"验证码不正确", null);
        } else {
            // 验证通过后，移除验证码
            redisTemplate.delete(imageCodeToken);
        }



        confirmOrderService.doConfirm(req);
        //       获取当前用户的MemberID
        //req.setMemberId(LoginMemberContext.getId());
        return new CommonRes<>();
    }


    //    返回值也得保持一致
    public CommonRes<Object> doConfirmBlock(ConfirmOrderDoReq req, BlockException e) {
        LOG.info("购票请求被限流:{}", req);
//        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
        CommonRes<Object> commonRes = new CommonRes<>();
        commonRes.setSuccess(false);
        commonRes.setMessage(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION.getDesc());
        return commonRes;
    }


//    @DeleteMapping("/delete/{id}")
//    public CommonRes<Object> deleteById(@PathVariable Long id) {
//            confirmOrderService.deleteById(id);
//        return new CommonRes<>("删除ConfirmOrder成功");
//
//    }

}
