package com.jktickets.service;



import com.jktickets.domain.DailyTrain;
import com.jktickets.req.dailyTrainTicket.DailyTrainTicketQueryReq;
import com.jktickets.req.dailyTrainTicket.DailyTrainTicketSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.dailyTrainTicket.DailyTrainTicketQueryRes;

import java.util.Date;
import java.util.List;

public interface DailyTrainTicketService {
   void saveDailyTrainTicket(DailyTrainTicketSaveReq req);
   PageRes<DailyTrainTicketQueryRes> queryDailyTrainTicketList(DailyTrainTicketQueryReq req);

   void deleteById(Long id);

   void genDailyTrainTicket(DailyTrain dailyTrain, Date date, String trainCode);
}