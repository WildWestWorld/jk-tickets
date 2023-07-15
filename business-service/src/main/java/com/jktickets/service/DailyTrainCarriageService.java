package com.jktickets.service;



import com.jktickets.req.dailyTrainCarriage.DailyTrainCarriageQueryReq;
import com.jktickets.req.dailyTrainCarriage.DailyTrainCarriageSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.dailyTrainCarriage.DailyTrainCarriageQueryRes;

import java.util.List;

public interface DailyTrainCarriageService {
   void saveDailyTrainCarriage(DailyTrainCarriageSaveReq req);
   PageRes<DailyTrainCarriageQueryRes> queryDailyTrainCarriageList(DailyTrainCarriageQueryReq req);

   void deleteById(Long id);
}