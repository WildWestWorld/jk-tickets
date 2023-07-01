package com.jktickets.service;


import com.jktickets.req.MemberLoginReq;
import com.jktickets.req.MemberRegisterReq;
import com.jktickets.req.MemberSendCodeReq;
import com.jktickets.res.MemberLoginRes;


public interface MemberService {
    int countNum();

    long registerByMobile(MemberRegisterReq req);
    void sendCode(MemberSendCodeReq req);
    MemberLoginRes login(MemberLoginReq req);
}
