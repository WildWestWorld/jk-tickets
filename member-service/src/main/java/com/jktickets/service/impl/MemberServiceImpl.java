package com.jktickets.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.jktickets.domain.Member;
import com.jktickets.domain.MemberExample;
import com.jktickets.exception.BusinessException;
import com.jktickets.exception.BusinessExceptionEnum;
import com.jktickets.mapper.MemberMapper;
import com.jktickets.req.MemberRegisterReq;
import com.jktickets.service.MemberService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public long registerByMobile(MemberRegisterReq req) {
        String mobile = req.getMobile();


//        创建ORM条件查询
        MemberExample memberExample = new MemberExample();
//        memberExample.createCriteria() = where
//        .andMobileEqualTo(mobile) = 找数据库中和传入的mobile一样的数据
        memberExample.createCriteria().andMobileEqualTo(mobile);
//        查询是否已经有这个手机号
        List<Member> memberList = memberMapper.selectByExample(memberExample);
//      如果已经有了这个手机号
        if (CollUtil.isNotEmpty(memberList)){
//            return memberList.get(0).getId();
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }

        Member member = new Member();
        member.setId(System.currentTimeMillis());
        member.setMobile(mobile);

        memberMapper.insert(member);
        return member.getId();
    }
}
