package com.jktickets.exception;


import cn.hutool.core.util.StrUtil;
import com.jktickets.res.CommonRes;
import io.seata.core.context.RootContext;
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
    public CommonRes exceptionHandler(Exception e) throws Exception {
        LOG.info("Seata全局事务ID:{}", RootContext.getXID());

//        如果是在一次全局事务里出了异常，就不要包装返回值，将异常抛给调用方，让调用方回滚事务
//        因为 SEATA是根据状态码来判别事务是否要回滚，如果继续走下去，只会出现 状态码200，提示系统出现异常，请联系管理员
//        那事务就不会回滚，所以我们得加上判断 如果是SEATA事务抛出的异常我们就不用自己封装的异常了
        if(StrUtil.isNotBlank(RootContext.getXID())){
            throw e;
        }



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
     *
     */


//    * BindException 引入的得是 spring的validation 这个拦截就是要拦截 validation 发出报错信息
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public CommonRes exceptionHandler(BindException e) {
        CommonRes commonRes = new CommonRes();
        LOG.error("校验异常：{}", e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        commonRes.setSuccess(false);
        commonRes.setCode(400);
        commonRes.setMessage(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return commonRes;
    }





//    拦截Sentinel异常 让他返回正常的异常
    @ExceptionHandler(value = RuntimeException.class)
    @ResponseBody
    public CommonRes exceptionHandler(RuntimeException e) {
        throw e;
    }

}
