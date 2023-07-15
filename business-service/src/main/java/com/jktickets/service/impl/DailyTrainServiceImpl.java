package com.jktickets.service.impl;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.domain.DailyTrain;
import com.jktickets.domain.DailyTrainExample;
import com.jktickets.domain.Train;
import com.jktickets.mapper.DailyTrainMapper;

import com.jktickets.req.dailyTrain.DailyTrainQueryReq;
import com.jktickets.req.dailyTrain.DailyTrainSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.dailyTrain.DailyTrainQueryRes;

import com.jktickets.service.DailyTrainService;

import com.jktickets.service.DailyTrainStationService;
import com.jktickets.service.TrainService;
import com.jktickets.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DailyTrainServiceImpl implements DailyTrainService {

    private final static Logger LOG = LoggerFactory.getLogger(DailyTrainService.class);

    @Resource
    DailyTrainMapper dailyTrainMapper;

    @Resource
    TrainService trainService;


    @Resource
    DailyTrainStationService dailyTrainStationService;

    @Override
    public void saveDailyTrain(DailyTrainSaveReq req) {
        DailyTrain dailyTrain = BeanUtil.copyProperties(req, DailyTrain.class);


        DateTime nowTime = DateTime.now();

        if (ObjectUtil.isNull(dailyTrain.getId())) {
            //        从 线程中获取数据
//          dailyTrain.setMemberId(LoginMemberContext.getId());
            dailyTrain.setId(SnowUtil.getSnowflakeNextId());
            dailyTrain.setCreateTime(nowTime);
            dailyTrain.setUpdateTime(nowTime);

            dailyTrainMapper.insert(dailyTrain);
        } else {
            dailyTrain.setUpdateTime(nowTime);
            dailyTrainMapper.updateByPrimaryKeySelective(dailyTrain);
        }


    }

    @Override
    public PageRes<DailyTrainQueryRes> queryDailyTrainList(DailyTrainQueryReq req) {
        DailyTrainExample dailyTrainExample = new DailyTrainExample();
        dailyTrainExample.setOrderByClause("date desc,code asc");
        DailyTrainExample.Criteria criteria = dailyTrainExample.createCriteria();
        if (ObjectUtil.isNotNull(req.getDate())) {
            criteria.andDateEqualTo(req.getDate());
        }
        if (ObjectUtil.isNotEmpty(req.getCode())) {
            criteria.andCodeEqualTo(req.getCode());
        }

        // 分页处理
        PageHelper.startPage(req.getPage(), req.getSize());
        List<DailyTrain> dailyTrainList = dailyTrainMapper.selectByExample(dailyTrainExample);


        PageInfo<DailyTrain> dailyTrainPageInfo = new PageInfo<>(dailyTrainList);

        LOG.info("总行数：{}", dailyTrainPageInfo.getTotal());
        LOG.info("总页数：{}", dailyTrainPageInfo.getPages());

//  转成Controller的传输类
        List<DailyTrainQueryRes> dailyTrainQueryResList = BeanUtil.copyToList(dailyTrainList, DailyTrainQueryRes.class);

        PageRes<DailyTrainQueryRes> pageRes = new PageRes<>();
        pageRes.setList(dailyTrainQueryResList);
        pageRes.setTotal(dailyTrainPageInfo.getTotal());
        return pageRes;
    }


    @Override
    public void deleteById(Long id) {
        dailyTrainMapper.deleteByPrimaryKey(id);
    }


    //    生成某日所有车次信息
    @Override
    public void genDaily(Date date) {
        List<Train> trainList = trainService.selectAllTrainList();
//        查询是否有火车列表
        if (CollUtil.isEmpty(trainList)) {
            LOG.info("没有车次基础数据，任务结束");
            return;
        }

        for (Train train : trainList) {
//            生成每日火车
            genDailyTrain(date, train);


//        生成该车次的车站数据
            dailyTrainStationService.genDailyTrainStation(date,train.getCode());
        }

    }
    @Override
    public  void genDailyTrain(Date date, Train train) {
        //            先删除之前可能存在的 车次
        DailyTrainExample dailyTrainExample = new DailyTrainExample();
//            查询是否有相同的车次
        dailyTrainExample.createCriteria().andDateEqualTo(date).andCodeEqualTo(train.getCode());
//        有相同的就先删除
        dailyTrainMapper.deleteByExample(dailyTrainExample);


        DateTime now = DateTime.now();
//        生成该车次的数据
        DailyTrain dailyTrain = BeanUtil.copyProperties(train, DailyTrain.class);
        dailyTrain.setId(SnowUtil.getSnowflakeNextId());
        dailyTrain.setCreateTime(now);
        dailyTrain.setUpdateTime(now);

        dailyTrainMapper.insert(dailyTrain);

    }
}
