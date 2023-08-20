package com.jktickets.service;


import com.jktickets.req.confirmOrder.ConfirmOrderDoReq;

public interface BeforeConfirmOrderService {
    void beforeDoConfirm(ConfirmOrderDoReq req);

}