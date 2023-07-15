package com.jktickets.job.basic;



//适合单体应用不适合集群 ，没法实时更换定时任务状态

import cn.hutool.core.util.RandomUtil;
import com.jktickets.controller.JobController;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

//DisallowConcurrentExecution 静止并发执行(也就是TimeSleep 之后才执行)
@DisallowConcurrentExecution
public class DailyTrainJob implements Job {



    private static Logger LOG = LoggerFactory.getLogger(DailyTrainJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        // 增加日志流水号
        MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));
        LOG.info("生成每日车次数据开始");


        LOG.info("生成每日车次数据结束");
    }
}
