package com.jktickets.service;



import com.jktickets.req.MemberTicketReq;
import com.jktickets.req.ticket.TicketQueryReq;
import com.jktickets.req.ticket.TicketSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.ticket.TicketQueryRes;

import java.util.List;

public interface TicketService {
   void saveTicket(MemberTicketReq req) throws Exception;
   PageRes<TicketQueryRes> queryTicketList(TicketQueryReq req);

   void deleteById(Long id);
}