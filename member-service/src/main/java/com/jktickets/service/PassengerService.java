package com.jktickets.service;


import com.jktickets.req.passenger.PassengerQueryReq;
import com.jktickets.req.passenger.PassengerSaveReq;
import com.jktickets.res.passenger.PassengerQueryRes;

import java.util.List;

public interface PassengerService {
         void savePassenger(PassengerSaveReq req);
         List<PassengerQueryRes> queryPassengerList(PassengerQueryReq req);
}
