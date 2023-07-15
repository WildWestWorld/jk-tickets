package com.jktickets.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

//开启外键
@FeignClient(name = "business-service",url = "http://127.0.0.1:8002/business")
public interface BusinessFeign {
    @GetMapping("/hello")
    String hello();
}
