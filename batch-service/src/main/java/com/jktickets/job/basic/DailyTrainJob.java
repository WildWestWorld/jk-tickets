package com.jktickets.job.basic;



//适合单体应用不适合集群 ，没法实时更换定时任务状态

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.jktickets.controller.JobController;
import com.jktickets.feign.BusinessFeign;
import com.jktickets.res.CommonRes;
import jakarta.annotation.Resource;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Date;

//DisallowConcurrentExecution 静止并发执行(也就是TimeSleep 之后才执行)
@DisallowConcurrentExecution
public class DailyTrainJob implements Job {

    @Resource
    BusinessFeign businessFeign;

    private static Logger LOG = LoggerFactory.getLogger(DailyTrainJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        // 增加日志流水号
        MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));
        LOG.info("生成 15天后的每日车次数据开始");
        Date date =  new Date();
        DateTime dateTime = DateUtil.offsetDay(date, 15);
        Date offsetDate = dateTime.toJdkDate();


        CommonRes<Object> objectCommonRes = businessFeign.genDailyByDate(offsetDate);
        LOG.info("生成 15天后的 每日车次数据结果:{}",objectCommonRes);

        LOG.info("生成 15天后的 每日车次数据结束");
    }
}
