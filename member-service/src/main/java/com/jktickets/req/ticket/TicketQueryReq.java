package com.jktickets.req.ticket;
import com.jktickets.req.PageReq;
import lombok.Data;

//该实体 是与表结构一一对应
@Data
public class TicketQueryReq extends PageReq {

//        用于用户个人查询
    private Long memberId;


}
