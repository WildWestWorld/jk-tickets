package com.jktickets.service.impl;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.domain.DailyTrainSeat;
import com.jktickets.domain.DailyTrainSeatExample;
import com.jktickets.mapper.DailyTrainSeatMapper;

import com.jktickets.req.dailyTrainSeat.DailyTrainSeatQueryReq;
import com.jktickets.req.dailyTrainSeat.DailyTrainSeatSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.dailyTrainSeat.DailyTrainSeatQueryRes;

import com.jktickets.service.DailyTrainSeatService;

import com.jktickets.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyTrainSeatServiceImpl implements DailyTrainSeatService {

    private final static Logger LOG = LoggerFactory.getLogger(DailyTrainSeatService.class);

    @Resource
    DailyTrainSeatMapper dailyTrainSeatMapper;

    @Override
    public void saveDailyTrainSeat(DailyTrainSeatSaveReq req) {
        DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(req, DailyTrainSeat.class);


        DateTime nowTime  = DateTime.now();

        if(ObjectUtil.isNull(dailyTrainSeat.getId())){
            //        从 线程中获取数据
//          dailyTrainSeat.setMemberId(LoginMemberContext.getId());
            dailyTrainSeat.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainSeat.setCreateTime(nowTime);
            dailyTrainSeat.setUpdateTime(nowTime);

            dailyTrainSeatMapper.insert(dailyTrainSeat);
        }else{
            dailyTrainSeat.setUpdateTime(nowTime);
            dailyTrainSeatMapper.updateByPrimaryKeySelective(dailyTrainSeat);
        }



    }

    @Override
    public PageRes<DailyTrainSeatQueryRes> queryDailyTrainSeatList(DailyTrainSeatQueryReq req) {
        DailyTrainSeatExample dailyTrainSeatExample = new DailyTrainSeatExample();
        dailyTrainSeatExample.setOrderByClause("date desc, train_code asc, carriage_index asc, carriage_seat_index asc");
        DailyTrainSeatExample.Criteria criteria = dailyTrainSeatExample.createCriteria();


        if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }

        // 分页处理
        PageHelper.startPage(req.getPage(), req.getSize());
        List<DailyTrainSeat> dailyTrainSeatList = dailyTrainSeatMapper.selectByExample(dailyTrainSeatExample);


        PageInfo<DailyTrainSeat> dailyTrainSeatPageInfo = new PageInfo<>(dailyTrainSeatList);

        LOG.info("总行数：{}", dailyTrainSeatPageInfo.getTotal());
        LOG.info("总页数：{}", dailyTrainSeatPageInfo.getPages());

//  转成Controller的传输类
        List<DailyTrainSeatQueryRes> dailyTrainSeatQueryResList = BeanUtil.copyToList(dailyTrainSeatList, DailyTrainSeatQueryRes.class);

        PageRes<DailyTrainSeatQueryRes> pageRes = new PageRes<>();
        pageRes.setList(dailyTrainSeatQueryResList);
        pageRes.setTotal(dailyTrainSeatPageInfo.getTotal());
        return pageRes;
    }


    @Override
    public void deleteById(Long id) {
        dailyTrainSeatMapper.deleteByPrimaryKey(id);
    }



}
