package com.jktickets.service.impl;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.domain.Passenger;
import com.jktickets.domain.PassengerExample;
import com.jktickets.mapper.PassengerMapper;

import com.jktickets.req.passenger.PassengerQueryReq;
import com.jktickets.req.passenger.PassengerSaveReq;
import com.jktickets.res.PageRes;
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

    private final static Logger LOG = LoggerFactory.getLogger(PassengerService.class);

    @Resource
    PassengerMapper passengerMapper;

    @Override
    public void savePassenger(PassengerSaveReq req) {
        Passenger passenger = BeanUtil.copyProperties(req, Passenger.class);


        DateTime nowTime  = DateTime.now();

        if(ObjectUtil.isNull(passenger.getId())){
            //        从 线程中获取数据
            passenger.setMemberId(LoginMemberContext.getId());
            passenger.setId(SnowUtil.getSnowflakeNextId());
            passenger.setCreateTime(nowTime);
            passenger.setUpdateTime(nowTime);

            passengerMapper.insert(passenger);
        }else{
            passenger.setUpdateTime(nowTime);
            passengerMapper.updateByPrimaryKeySelective(passenger);
        }



    }

    @Override
    public PageRes<PassengerQueryRes> queryPassengerList(PassengerQueryReq req) {
        PassengerExample passengerExample = new PassengerExample();
        PassengerExample.Criteria criteria = passengerExample.createCriteria();
        if(ObjectUtil.isNotNull(req.getMemberId())){
            criteria.andMemberIdEqualTo(req.getMemberId());
        }

        // 分页处理
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Passenger> passengerList = passengerMapper.selectByExample(passengerExample);


        PageInfo<Passenger> passengerPageInfo = new PageInfo<>(passengerList);

        LOG.info("总行数：{}", passengerPageInfo.getTotal());
        LOG.info("总页数：{}", passengerPageInfo.getPages());

//  转成Controller的传输类
        List<PassengerQueryRes> passengerQueryResList = BeanUtil.copyToList(passengerList, PassengerQueryRes.class);

        PageRes<PassengerQueryRes> pageRes = new PageRes<>();
        pageRes.setList(passengerQueryResList);
        pageRes.setTotal(passengerPageInfo.getTotal());
        return pageRes;
    }


    @Override
    public void deleteById(Long id) {
        passengerMapper.deleteByPrimaryKey(id);
    }
}
