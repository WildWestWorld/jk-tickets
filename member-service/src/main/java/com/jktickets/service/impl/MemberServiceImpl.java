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

        long count = memberMapper.countByExample(null);
        int countFormat = Math.toIntExact(count);
        return countFormat;
    }
}
