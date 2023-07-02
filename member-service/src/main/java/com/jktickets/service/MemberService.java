package com.jktickets.service;


import com.jktickets.req.merber.MemberLoginReq;
import com.jktickets.req.merber.MemberRegisterReq;
import com.jktickets.req.merber.MemberSendCodeReq;
import com.jktickets.res.MemberLoginRes;


public interface MemberService {
    int countNum();

    long registerByMobile(MemberRegisterReq req);
    void sendCode(MemberSendCodeReq req);
    MemberLoginRes login(MemberLoginReq req);
}
