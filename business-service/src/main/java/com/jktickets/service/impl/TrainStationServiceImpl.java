package com.jktickets.service.impl;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.domain.TrainStation;
import com.jktickets.domain.TrainStationExample;
import com.jktickets.mapper.TrainStationMapper;

import com.jktickets.req.trainStation.TrainStationQueryReq;
import com.jktickets.req.trainStation.TrainStationSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.trainStation.TrainStationQueryRes;

import com.jktickets.service.TrainStationService;

import com.jktickets.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainStationServiceImpl implements TrainStationService {

    private final static Logger LOG = LoggerFactory.getLogger(TrainStationService.class);

    @Resource
    TrainStationMapper trainStationMapper;

    @Override
    public void saveTrainStation(TrainStationSaveReq req) {
        TrainStation trainStation = BeanUtil.copyProperties(req, TrainStation.class);


        DateTime nowTime  = DateTime.now();

        if(ObjectUtil.isNull(trainStation.getId())){
            //        从 线程中获取数据
//          trainStation.setMemberId(LoginMemberContext.getId());
            trainStation.setId(SnowUtil.getSnowflakeNextId());
            trainStation.setCreateTime(nowTime);
            trainStation.setUpdateTime(nowTime);

            trainStationMapper.insert(trainStation);
        }else{
            trainStation.setUpdateTime(nowTime);
            trainStationMapper.updateByPrimaryKeySelective(trainStation);
        }



    }

    @Override
    public PageRes<TrainStationQueryRes> queryTrainStationList(TrainStationQueryReq req) {
        TrainStationExample trainStationExample = new TrainStationExample();
        TrainStationExample.Criteria criteria = trainStationExample.createCriteria();
//        if(ObjectUtil.isNotNull(req.getMemberId())){
//            criteria.andMemberIdEqualTo(req.getMemberId());
//        }

        // 分页处理
        PageHelper.startPage(req.getPage(), req.getSize());
        List<TrainStation> trainStationList = trainStationMapper.selectByExample(trainStationExample);


        PageInfo<TrainStation> trainStationPageInfo = new PageInfo<>(trainStationList);

        LOG.info("总行数：{}", trainStationPageInfo.getTotal());
        LOG.info("总页数：{}", trainStationPageInfo.getPages());

//  转成Controller的传输类
        List<TrainStationQueryRes> trainStationQueryResList = BeanUtil.copyToList(trainStationList, TrainStationQueryRes.class);

        PageRes<TrainStationQueryRes> pageRes = new PageRes<>();
        pageRes.setList(trainStationQueryResList);
        pageRes.setTotal(trainStationPageInfo.getTotal());
        return pageRes;
    }


    @Override
    public void deleteById(Long id) {
        trainStationMapper.deleteByPrimaryKey(id);
    }
}
