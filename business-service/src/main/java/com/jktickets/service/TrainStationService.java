package com.jktickets.service;



import com.jktickets.req.trainStation.TrainStationQueryReq;
import com.jktickets.req.trainStation.TrainStationSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.trainStation.TrainStationQueryRes;

import java.util.List;

public interface TrainStationService {
   void saveTrainStation(TrainStationSaveReq req);
   PageRes<TrainStationQueryRes> queryTrainStationList(TrainStationQueryReq req);

   void deleteById(Long id);
}