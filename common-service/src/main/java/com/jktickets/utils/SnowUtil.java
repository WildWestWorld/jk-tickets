package com.jktickets.utils;

import cn.hutool.core.util.IdUtil;

/**
 * 封装hutool雪花算法
 */
public class SnowUtil {

    private static long dataCenterId = 1;  //数据中心
    private static long workerId = 1;     //机器标识
    //        huTool生成雪花算法 IdUtil.getSnowflake('机器码','地区码')
    public static long getSnowflakeNextId() {
        return IdUtil.getSnowflake(workerId, dataCenterId).nextId();
    }

    public static String getSnowflakeNextIdStr() {
        return IdUtil.getSnowflake(workerId, dataCenterId).nextIdStr();
    }
}
