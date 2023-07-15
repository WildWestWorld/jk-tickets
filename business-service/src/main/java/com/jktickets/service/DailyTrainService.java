package com.jktickets.service;



import com.jktickets.domain.Train;
import com.jktickets.req.dailyTrain.DailyTrainQueryReq;
import com.jktickets.req.dailyTrain.DailyTrainSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.dailyTrain.DailyTrainQueryRes;

import java.util.Date;
import java.util.List;

public interface DailyTrainService {
   void saveDailyTrain(DailyTrainSaveReq req);
   PageRes<DailyTrainQueryRes> queryDailyTrainList(DailyTrainQueryReq req);

   void deleteById(Long id);

   void genDaily(Date date);

}