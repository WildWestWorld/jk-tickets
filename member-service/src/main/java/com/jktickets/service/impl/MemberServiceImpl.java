package com.jktickets.service.impl;

import com.jktickets.mapper.MemberMapper;
import com.jktickets.service.MemberService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService {

    @Resource
    MemberMapper memberMapper;


    @Override
    public int countNum() {
        return memberMapper.countNum();
    }
}
