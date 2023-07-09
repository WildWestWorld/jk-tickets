package com.jktickets.service;



import com.jktickets.req.trainCarriage.TrainCarriageQueryReq;
import com.jktickets.req.trainCarriage.TrainCarriageSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.trainCarriage.TrainCarriageQueryRes;

import java.util.List;

public interface TrainCarriageService {
   void saveTrainCarriage(TrainCarriageSaveReq req);
   PageRes<TrainCarriageQueryRes> queryTrainCarriageList(TrainCarriageQueryReq req);

   void deleteById(Long id);
}