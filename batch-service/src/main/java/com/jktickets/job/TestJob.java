package com.jktickets.job;



//适合单体应用不适合集群 ，没法实时更换定时任务状态

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class TestJob implements Job {




    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("Test Job");
    }
}
