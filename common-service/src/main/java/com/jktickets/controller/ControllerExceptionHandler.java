package com.jktickets.controller;


import com.jktickets.exception.BusinessException;
import com.jktickets.res.CommonRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常处理、数据预处理等
 */

//@ControllerAdvice  Controller 异常处理 弹出异常会被这里拦截
@ControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    /**
     * 所有异常统一处理
     * 针对位置异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public CommonRes exceptionHandler(Exception e) {
        CommonRes commonRes = new CommonRes();
        LOG.error("系统异常：", e);
        commonRes.setSuccess(false);
        commonRes.setCode(400);
        commonRes.setMessage("系统出现异常，请联系管理员");
//        commonRes.setMessage(e.getMessage());
        return commonRes;
    }

    /**
     * 业务异常统一处理
     * @param e
     * @return
     */
    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public CommonRes exceptionHandler(BusinessException e) {
        CommonRes commonRes = new CommonRes();
        LOG.error("业务异常:{}", e.getBusinessExceptionEnum().getDesc());
        commonRes.setSuccess(false);
        commonRes.setCode(400);
        commonRes.setMessage(e.getBusinessExceptionEnum().getDesc());
        return commonRes;
    }

    /**
     * 校验异常统一处理
     * @param e
     * @return
     */
//    @ExceptionHandler(value = BindException.class)
//    @ResponseBody
//    public CommonRes exceptionHandler(BindException e) {
//        CommonRes commonRes = new CommonRes();
//        LOG.error("校验异常：{}", e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
//        commonRes.setSuccess(false);
//        commonRes.setMessage(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
//        return commonRes;
//    }

}
