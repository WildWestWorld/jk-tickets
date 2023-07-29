package com.jktickets.service;



import com.jktickets.domain.DailyTrainSeat;
import com.jktickets.domain.DailyTrainTicket;
import com.jktickets.req.confirmOrder.ConfirmOrderDoReq;
import com.jktickets.req.confirmOrder.ConfirmOrderQueryReq;
import com.jktickets.req.confirmOrder.ConfirmOrderSaveReq;
import com.jktickets.req.confirmOrder.ConfirmOrderTicketReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.confirmOrder.ConfirmOrderQueryRes;

import java.util.List;

public interface AfterConfirmOrderService {
   void afterDoConfirm(DailyTrainTicket dailyTrainTicket,List<DailyTrainSeat> finalSeatList,List<ConfirmOrderTicketReq> ticketReqList);

}