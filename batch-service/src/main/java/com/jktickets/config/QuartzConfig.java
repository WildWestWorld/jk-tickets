package com.jktickets.config;// package com.jiawa.train.batch.config;

import com.jktickets.job.TestJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//
// import com.jiawa.train.batch.job.TestJob;
// import org.quartz.*;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
//
@Configuration
public class QuartzConfig {

    /**
     * 声明一个任务
     *
     * @return
     */
    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob(TestJob.class)
//                 编写定时任务的名字
                .withIdentity("TestJob", "test")
                .storeDurably()
                .build();
    }

    /**
     * 声明一个触发器，什么时候触发这个任务
     *
     * @return
     */
    @Bean
    public Trigger trigger() {
        return TriggerBuilder.newTrigger()
//                为哪个定时任务
                .forJob(jobDetail())
 //                 编写触发器的名字
                .withIdentity("trigger", "trigger")
//                开始启动
                .startNow()
                //    cron = "秒 分 小时 月份中的日期 月份 星期中的日期 年份"
//    0/5  秒/5 等于时就触发
                .withSchedule(CronScheduleBuilder.cronSchedule("*/2 * * * * ?"))
                .build();
    }
}
