package com.jktickets.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.jwt.JWTUtil;
import com.jktickets.domain.Member;
import com.jktickets.domain.MemberExample;
import com.jktickets.exception.BusinessException;
import com.jktickets.exception.BusinessExceptionEnum;
import com.jktickets.mapper.MemberMapper;
import com.jktickets.req.MemberLoginReq;
import com.jktickets.req.MemberRegisterReq;
import com.jktickets.req.MemberSendCodeReq;
import com.jktickets.res.MemberLoginRes;
import com.jktickets.service.MemberService;
import com.jktickets.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MemberServiceImpl implements MemberService {

    private final static Logger LOG = LoggerFactory.getLogger(MemberService.class);

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
        if (CollUtil.isNotEmpty(memberList)) {
//            return memberList.get(0).getId();
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }

        Member member = new Member();
//        member.setId(System.currentTimeMillis());
//        设置雪花算法 为id
        member.setId(SnowUtil.getSnowflakeNextId());
        member.setMobile(mobile);

        memberMapper.insert(member);
        return member.getId();
    }


    @Override
    public void sendCode(MemberSendCodeReq req) {
        String mobile = req.getMobile();


//        创建ORM条件查询
        Member memberDB = SelectMemberByMobile(mobile);
//        如果手机号不存在 则插入一条数据
        if (ObjectUtil.isNull(memberDB)) {
            LOG.info("手机号不存在,则插入数据");
            Member member = new Member();
//        member.setId(System.currentTimeMillis());
//        设置雪花算法 为id
            member.setId(SnowUtil.getSnowflakeNextId());
            member.setMobile(mobile);

            memberMapper.insert(member);
        } else {
            LOG.info("手机号存在,不插入数据");
        }

//        生成验证码
//        随机四位数的字符串
        String code = RandomUtil.randomString(4);
//        String code = "8888";
        LOG.info("生成短信验证码:{}", code);

//        保存短信记录表:手机号，短信验证码，有效期，是否已使用，业务类型，发送时间，使用时间
        LOG.info("保存短信记录表");

//        对接短信通道，发送短信
        LOG.info("对接短信通道");


    }

    @Override
    public MemberLoginRes login(MemberLoginReq req) {
        String mobile = req.getMobile();
        String code = req.getCode();


        Member memberDB = SelectMemberByMobile(mobile);
        if (ObjectUtil.isNull(memberDB)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_NOT_EXIST);
        }

        //校验短信验证码
        if (!"8888".equals(code)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_CODE_ERROR);

        }

 //        利用HuTool进行复制
//        copyProperties(要复制的数据,要转化成的类)
        MemberLoginRes memberLoginRes = BeanUtil.copyProperties(memberDB, MemberLoginRes.class);


//        利用huTool生成JWT
//        1.拿到Payload   用户信息转成Map
        Map<String, Object> map = BeanUtil.beanToMap(memberLoginRes);
//        2.设置秘钥
        String key = "JK12306";
//        3.huTool生成Token createToken(Map<String, Object> payload, byte[] key)
        String token = JWTUtil.createToken(map, key.getBytes());
        memberLoginRes.setToken(token);
        return memberLoginRes;


    }

    //    子函数
    private Member SelectMemberByMobile(String mobile) {
        //        创建ORM条件查询
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
//        查询是否已经有这个手机号
        List<Member> memberList = memberMapper.selectByExample(memberExample);

//        没有查询到数据
        if (CollUtil.isEmpty(memberList)) {
            return null;
        } else {
            return memberList.get(0);
        }

    }
}
