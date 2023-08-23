package com.jktickets.service;


import com.jktickets.req.confirmOrder.ConfirmOrderDoReq;

public interface BeforeConfirmOrderService {
    Long beforeDoConfirm(ConfirmOrderDoReq req);

}