package com.jktickets.job.basic;



//适合单体应用不适合集群 ，没法实时更换定时任务状态

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

//DisallowConcurrentExecution 静止并发执行(也就是TimeSleep 之后才执行)
@DisallowConcurrentExecution
public class TestJob implements Job {




    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("Test Job Start");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Test Job Stop");
    }
}
