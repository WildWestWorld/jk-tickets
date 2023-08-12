package com.jktickets.feign;


import com.jktickets.res.CommonRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;

//开启外键
//@FeignClient(name = "business-service",url = "http://127.0.0.1:8002/business")
@FeignClient(value = "business-service",fallback = BusinessFeignFallback.class)
public interface BusinessFeign {
    @GetMapping("/business/hello")
    String hello();

    @GetMapping("/business/admin/dailyTrain/genDaily/{date}")
    CommonRes<Object> genDailyByDate(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date);
}
