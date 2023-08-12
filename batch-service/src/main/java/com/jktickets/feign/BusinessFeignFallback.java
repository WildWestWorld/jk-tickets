package com.jktickets.feign;


import com.jktickets.res.CommonRes;
import org.springframework.stereotype.Component;

import java.util.Date;

//处理降级 的异常
//以及无法再Feign上使用 SentinelResource



//必须加上Component
@Component
public class BusinessFeignFallback implements BusinessFeign{
    @Override
    public String hello() {
        return "Fallback";
    }

    @Override
    public CommonRes<Object> genDailyByDate(Date date) {
        return null;
    }
}
