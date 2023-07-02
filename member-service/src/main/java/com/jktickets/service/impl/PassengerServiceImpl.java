package com.jktickets.service.impl;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.domain.Passenger;
import com.jktickets.domain.PassengerExample;
import com.jktickets.mapper.PassengerMapper;

import com.jktickets.req.passenger.PassengerQueryReq;
import com.jktickets.req.passenger.PassengerSaveReq;
import com.jktickets.res.passenger.PassengerQueryRes;
import com.jktickets.service.MemberService;
import com.jktickets.service.PassengerService;

import com.jktickets.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PassengerServiceImpl implements PassengerService {

    private final static Logger LOG = LoggerFactory.getLogger(MemberService.class);

    @Resource
    PassengerMapper passengerMapper;

    @Override
    public void savePassenger(PassengerSaveReq req) {
        Passenger passenger = BeanUtil.copyProperties(req, Passenger.class);
        DateTime nowTime  = DateTime.now();

//        从 线程中获取数据
        passenger.setMemberId(LoginMemberContext.getId());
        passenger.setId(SnowUtil.getSnowflakeNextId());
        passenger.setCreateTime(nowTime);
        passenger.setUpdateTime(nowTime);

        passengerMapper.insert(passenger);

    }

    @Override
    public List<PassengerQueryRes> queryPassengerList(PassengerQueryReq req) {
        PassengerExample passengerExample = new PassengerExample();
        PassengerExample.Criteria criteria = passengerExample.createCriteria();
        if(ObjectUtil.isNotNull(req.getMemberId())){
            criteria.andMemberIdEqualTo(req.getMemberId());
        }
        List<Passenger> passengers = passengerMapper.selectByExample(passengerExample);
//  转成Controller的传输类
        List<PassengerQueryRes> passengerQueryRes = BeanUtil.copyToList(passengers, PassengerQueryRes.class);
        return passengerQueryRes;
    }
}
