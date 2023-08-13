package com.jktickets.service;



import com.jktickets.domain.Train;
import com.jktickets.req.dailyTrainStation.DailyTrainStationQueryReq;
import com.jktickets.req.dailyTrainStation.DailyTrainStationSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.dailyTrainStation.DailyTrainStationQueryRes;

import java.util.Date;
import java.util.List;

public interface DailyTrainStationService {
   void saveDailyTrainStation(DailyTrainStationSaveReq req);
   PageRes<DailyTrainStationQueryRes> queryDailyTrainStationList(DailyTrainStationQueryReq req);

   void deleteById(Long id);

   void genDailyTrainStation(Date date, String trainCode);

   long countByTrainCode(String trainCode);

}