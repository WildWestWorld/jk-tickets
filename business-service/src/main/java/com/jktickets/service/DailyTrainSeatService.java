package com.jktickets.service;



import com.jktickets.req.dailyTrainSeat.DailyTrainSeatQueryReq;
import com.jktickets.req.dailyTrainSeat.DailyTrainSeatSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.dailyTrainSeat.DailyTrainSeatQueryRes;

import java.util.List;

public interface DailyTrainSeatService {
   void saveDailyTrainSeat(DailyTrainSeatSaveReq req);
   PageRes<DailyTrainSeatQueryRes> queryDailyTrainSeatList(DailyTrainSeatQueryReq req);

   void deleteById(Long id);
}