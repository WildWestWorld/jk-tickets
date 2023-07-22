package com.jktickets.service;



import com.jktickets.domain.TrainSeat;
import com.jktickets.req.trainSeat.TrainSeatQueryReq;
import com.jktickets.req.trainSeat.TrainSeatSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.trainSeat.TrainSeatQueryRes;

import java.util.List;

public interface TrainSeatService {
   void saveTrainSeat(TrainSeatSaveReq req);
   PageRes<TrainSeatQueryRes> queryTrainSeatList(TrainSeatQueryReq req);

   void deleteById(Long id);

   void genTrainSeat(String trainCode);
   List<TrainSeat> selectByTrainCode(String trainCode);



}