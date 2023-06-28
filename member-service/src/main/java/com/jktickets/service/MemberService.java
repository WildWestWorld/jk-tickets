package com.jktickets.service;


import com.jktickets.req.MemberRegisterReq;
import org.springframework.stereotype.Service;


public interface MemberService {
    int countNum();

    long registerByMobile(MemberRegisterReq req);
}
