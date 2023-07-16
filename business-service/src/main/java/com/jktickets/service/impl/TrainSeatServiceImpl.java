package com.jktickets.service.impl;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jktickets.domain.TrainCarriage;
import com.jktickets.domain.TrainSeat;
import com.jktickets.domain.TrainSeatExample;
import com.jktickets.enums.SeatColEnum;
import com.jktickets.mapper.TrainSeatMapper;

import com.jktickets.req.trainSeat.TrainSeatQueryReq;
import com.jktickets.req.trainSeat.TrainSeatSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.trainSeat.TrainSeatQueryRes;

import com.jktickets.service.TrainCarriageService;
import com.jktickets.service.TrainSeatService;

import com.jktickets.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainSeatServiceImpl implements TrainSeatService {

    private final static Logger LOG = LoggerFactory.getLogger(TrainSeatService.class);

    @Resource
    TrainSeatMapper trainSeatMapper;

    @Resource
    TrainCarriageService trainCarriageService;

    @Override
    public void saveTrainSeat(TrainSeatSaveReq req) {


        DateTime nowTime = DateTime.now();
        TrainSeat trainSeat = BeanUtil.copyProperties(req, TrainSeat.class);

        if (ObjectUtil.isNull(trainSeat.getId())) {
            //        从 线程中获取数据
//          trainSeat.setMemberId(LoginMemberContext.getId());
            trainSeat.setId(SnowUtil.getSnowflakeNextId());
            trainSeat.setCreateTime(nowTime);
            trainSeat.setUpdateTime(nowTime);

            trainSeatMapper.insert(trainSeat);
        } else {
            trainSeat.setUpdateTime(nowTime);
            trainSeatMapper.updateByPrimaryKeySelective(trainSeat);
        }


    }

    @Override
    public PageRes<TrainSeatQueryRes> queryTrainSeatList(TrainSeatQueryReq req) {
        TrainSeatExample trainSeatExample = new TrainSeatExample();
        trainSeatExample.setOrderByClause("train_code asc, carriage_index asc, carriage_seat_index asc");

        TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();
//        if(ObjectUtil.isNotNull(req.getMemberId())){
//            criteria.andMemberIdEqualTo(req.getMemberId());
//        }

//        根据 列车号 查询 座位
        if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }

        // 分页处理
        PageHelper.startPage(req.getPage(), req.getSize());
        List<TrainSeat> trainSeatList = trainSeatMapper.selectByExample(trainSeatExample);


        PageInfo<TrainSeat> trainSeatPageInfo = new PageInfo<>(trainSeatList);

        LOG.info("总行数：{}", trainSeatPageInfo.getTotal());
        LOG.info("总页数：{}", trainSeatPageInfo.getPages());

//  转成Controller的传输类
        List<TrainSeatQueryRes> trainSeatQueryResList = BeanUtil.copyToList(trainSeatList, TrainSeatQueryRes.class);

        PageRes<TrainSeatQueryRes> pageRes = new PageRes<>();
        pageRes.setList(trainSeatQueryResList);
        pageRes.setTotal(trainSeatPageInfo.getTotal());
        return pageRes;
    }


    @Override
    @Transactional
    public void genTrainSeat(String trainCode) {

        DateTime now = DateTime.now();
//        清空当前车次下的所有座位记录
        TrainSeatExample trainSeatExample = new TrainSeatExample();
        trainSeatExample.setOrderByClause("train_code asc, carriage_index asc, carriage_seat_index asc");

        TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();
        criteria.andTrainCodeEqualTo(trainCode);
        trainSeatMapper.deleteByExample(trainSeatExample);


        List<TrainCarriage> carriageList = trainCarriageService.selectByTrainCode(trainCode);

        LOG.info("当前车次下的车厢数：{}", carriageList.size());
        //        查找当前车次下的所有车厢
        for (TrainCarriage trainCarriage : carriageList) {

            Integer rowCount = trainCarriage.getRowCount();
            String seatType = trainCarriage.getSeatType();
            int seatIndex = 1;

            //        拿到车厢数据行数   座位类型=》列数
//        根据车厢的座位类型，筛选出所有列 比如 ！车厢类型!是一等座 则列的座位类型是ACDF之一，用座位类型判断列数
            List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(seatType);

            LOG.info("根据车厢的座位类型，筛选出所有的列：{}", colEnumList);

            //        循环行数
            for (int row = 1; row <= rowCount; row++) {

//        循环列数
                for (SeatColEnum seatColEnum : colEnumList) {
                    // 构造座位数据并保存数据库
                    TrainSeat trainSeat = new TrainSeat();
                    trainSeat.setId(SnowUtil.getSnowflakeNextId());
                    trainSeat.setTrainCode(trainCode);
                    trainSeat.setCarriageIndex(trainCarriage.getIndex());
//                    StrUtil.fillBefore(String.valueOf(row), '0', 2) 2=>02
                    trainSeat.setRow(StrUtil.fillBefore(String.valueOf(row), '0', 2));
                    trainSeat.setCol(seatColEnum.getCode());
                    trainSeat.setSeatType(seatType);
                    trainSeat.setCarriageSeatIndex(seatIndex++);
                    trainSeat.setCreateTime(now);
                    trainSeat.setUpdateTime(now);
                    trainSeatMapper.insert(trainSeat);
                }
            }


        }
    }


    @Override
    public void deleteById(Long id) {
        trainSeatMapper.deleteByPrimaryKey(id);
    }


    @Override
    public List<TrainSeat> selectByTrainCode(String trainCode) {
        TrainSeatExample trainSeatExample = new TrainSeatExample();
        trainSeatExample.setOrderByClause("`id` asc");
        TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();
        criteria.andTrainCodeEqualTo(trainCode);
        return trainSeatMapper.selectByExample(trainSeatExample);
    }


}
