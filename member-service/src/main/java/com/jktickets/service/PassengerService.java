package com.jktickets.service;



import com.jktickets.req.passenger.PassengerQueryReq;
import com.jktickets.req.passenger.PassengerSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.passenger.PassengerQueryRes;

import java.util.List;

public interface PassengerService {
   void savePassenger(PassengerSaveReq req);
   PageRes<PassengerQueryRes> queryPassengerList(PassengerQueryReq req);

   void deleteById(Long id);

   List<PassengerQueryRes> queryMine();
}