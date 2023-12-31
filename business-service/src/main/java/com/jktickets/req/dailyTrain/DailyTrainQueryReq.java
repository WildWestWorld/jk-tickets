package com.jktickets.req.dailyTrain;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jktickets.req.PageReq;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

//该实体 是与表结构一一对应
@Data
public class DailyTrainQueryReq extends PageReq {
//    Gei请求 是时间必须要加这个DataTimeFormat
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
    private String code;

}
