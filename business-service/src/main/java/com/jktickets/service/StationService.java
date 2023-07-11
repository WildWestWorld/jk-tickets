package com.jktickets.service;



import com.jktickets.req.station.StationQueryReq;
import com.jktickets.req.station.StationSaveReq;
import com.jktickets.req.train.TrainQueryReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.station.StationQueryRes;
import com.jktickets.res.train.TrainQueryRes;

import java.util.List;

public interface StationService {
   void saveStation(StationSaveReq req);
   PageRes<StationQueryRes> queryStationList(StationQueryReq req);
   List<StationQueryRes> queryAllStationList(TrainQueryReq req);

   void deleteById(Long id);
}