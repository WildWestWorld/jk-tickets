package com.jktickets.service.impl;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.domain.*;
import com.jktickets.mapper.DailyTrainSeatMapper;

import com.jktickets.req.dailyTrainSeat.DailyTrainSeatQueryReq;
import com.jktickets.req.dailyTrainSeat.DailyTrainSeatSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.dailyTrainSeat.DailyTrainSeatQueryRes;

import com.jktickets.service.DailyTrainSeatService;

import com.jktickets.service.TrainSeatService;
import com.jktickets.service.TrainStationService;
import com.jktickets.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class DailyTrainSeatServiceImpl implements DailyTrainSeatService {

    private final static Logger LOG = LoggerFactory.getLogger(DailyTrainSeatService.class);

    @Resource
    DailyTrainSeatMapper dailyTrainSeatMapper;


    @Resource
    private TrainSeatService trainSeatService;

    @Resource
    private TrainStationService trainStationService;

    @Override
    public void saveDailyTrainSeat(DailyTrainSeatSaveReq req) {


        DateTime nowTime = DateTime.now();
        DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(req, DailyTrainSeat.class);

        if (ObjectUtil.isNull(dailyTrainSeat.getId())) {
            //        从 线程中获取数据
//          dailyTrainSeat.setMemberId(LoginMemberContext.getId());
            dailyTrainSeat.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainSeat.setCreateTime(nowTime);
            dailyTrainSeat.setUpdateTime(nowTime);

            dailyTrainSeatMapper.insert(dailyTrainSeat);
        } else {
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

    @Transactional
    @Override
    public void genDailyTrainSeat(Date date, String trainCode) {
        LOG.info("生成日期【{}】车次【{}】的座位信息开始", DateUtil.formatDate(date), trainCode);

        // 删除某日某车次的座位信息
        DailyTrainSeatExample dailyTrainSeatExample = new DailyTrainSeatExample();
        dailyTrainSeatExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode);
        dailyTrainSeatMapper.deleteByExample(dailyTrainSeatExample);

        List<TrainStation> stationList = trainStationService.selectByTrainCode(trainCode);
//    是否被售卖 是要根据车站的多少 0000=没有售卖  0010=1以前的算是被售卖
        String sell = StrUtil.fillBefore("", '0', stationList.size() - 1);

        // 查出某车次的所有的座位信息
        List<TrainSeat> seatList = trainSeatService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(seatList)) {
            LOG.info("该车次没有座位基础数据，生成该车次的座位信息结束");
            return;
        }

        for (TrainSeat trainSeat : seatList) {
            DateTime now = DateTime.now();
            DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(trainSeat, DailyTrainSeat.class);
            dailyTrainSeat.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainSeat.setCreateTime(now);
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeat.setDate(date);
            dailyTrainSeat.setSell(sell);
            dailyTrainSeatMapper.insert(dailyTrainSeat);
        }
        LOG.info("生成日期【{}】车次【{}】的座位信息结束", DateUtil.formatDate(date), trainCode);
    }





    @Override
    public int countTrainSeat(Date date, String trainCode, String seatType) {
        DailyTrainSeatExample example = new DailyTrainSeatExample();
        DailyTrainSeatExample.Criteria criteria = example.createCriteria();
//        根据 日期 火车 坐位的类型
//        也就是查询 某个坐位的类型 的数量
        criteria
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode);

        if (StrUtil.isNotBlank(seatType)) {
            criteria.andSeatTypeEqualTo(seatType);
        }


        long l = dailyTrainSeatMapper.countByExample(example);
        if (l == 0L) {
            return -1;
        }
        return (int) l;
    }


    @Override
    public List<DailyTrainSeat> selectByCarriage(Date date, String trainCode, Integer carriageIndex) {

        DailyTrainSeatExample dailyTrainSeatExample = new DailyTrainSeatExample();

//        按座位的索引排序
        dailyTrainSeatExample.setOrderByClause("carriage_seat_index asc");

        dailyTrainSeatExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode)
                .andCarriageIndexEqualTo(carriageIndex);
        List<DailyTrainSeat> dailyTrainSeats = dailyTrainSeatMapper.selectByExample(dailyTrainSeatExample);
        return dailyTrainSeats;
    }
}
