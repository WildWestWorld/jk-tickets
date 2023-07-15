package com.jktickets.req.dailyTrainSeat;
import com.jktickets.req.PageReq;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

//该实体 是与表结构一一对应
@Data
public class DailyTrainSeatQueryReq extends PageReq {

    /**
     * 日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    /**
     * 车次编号
     */
    private String trainCode;
}
