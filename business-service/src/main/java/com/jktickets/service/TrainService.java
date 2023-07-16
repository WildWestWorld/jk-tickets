package com.jktickets.service;



import com.jktickets.domain.Train;
import com.jktickets.req.train.TrainQueryReq;
import com.jktickets.req.train.TrainSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.train.TrainQueryRes;

import java.util.List;

public interface TrainService {
   void saveTrain(TrainSaveReq req);
   PageRes<TrainQueryRes> queryTrainList(TrainQueryReq req);
   List<TrainQueryRes> queryAllTrainList();


   Train selectByUnique(String code);

   void deleteById(Long id);
   List<Train> selectAllTrainList();


}