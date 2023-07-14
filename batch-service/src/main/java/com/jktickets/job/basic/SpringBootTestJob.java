package com.jktickets.job.basic;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;



//适合单体应用不适合集群 ，没法实时更换定时任务状态
//@Component
//@EnableScheduling
//public class SpringBootTestJob {
////    cron = "秒 分 小时 月份中的日期 月份 星期中的日期 年份"
////    0/5  秒/5 等于时就触发
//    @Scheduled(cron = "0/5 * * * * ?")
//    private void test(){
//        System.out.println("SpringBootTestJob Test");
//    }
//}
