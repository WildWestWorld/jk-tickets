package com.jktickets.service;



import com.jktickets.dto.ConfirmOrderMQDto;
import com.jktickets.req.confirmOrder.ConfirmOrderDoReq;
import com.jktickets.req.confirmOrder.ConfirmOrderQueryReq;
import com.jktickets.req.confirmOrder.ConfirmOrderSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.confirmOrder.ConfirmOrderQueryRes;

import java.util.List;

public interface ConfirmOrderService {
   void saveConfirmOrder(ConfirmOrderSaveReq req);
   PageRes<ConfirmOrderQueryRes> queryConfirmOrderList(ConfirmOrderQueryReq req);

   void deleteById(Long id);

//   void doConfirm(ConfirmOrderDoReq req);
   void doConfirm(ConfirmOrderMQDto dto);
   Integer queryLineCount(Long id);

   public Integer cancel(Long id);
}