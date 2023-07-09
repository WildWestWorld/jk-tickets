package com.jktickets.service.impl;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.domain.TrainSeat;
import com.jktickets.domain.TrainSeatExample;
import com.jktickets.mapper.TrainSeatMapper;

import com.jktickets.req.trainSeat.TrainSeatQueryReq;
import com.jktickets.req.trainSeat.TrainSeatSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.trainSeat.TrainSeatQueryRes;

import com.jktickets.service.TrainSeatService;

import com.jktickets.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainSeatServiceImpl implements TrainSeatService {

    private final static Logger LOG = LoggerFactory.getLogger(TrainSeatService.class);

    @Resource
    TrainSeatMapper trainSeatMapper;

    @Override
    public void saveTrainSeat(TrainSeatSaveReq req) {
        TrainSeat trainSeat = BeanUtil.copyProperties(req, TrainSeat.class);


        DateTime nowTime  = DateTime.now();

        if(ObjectUtil.isNull(trainSeat.getId())){
            //        从 线程中获取数据
//          trainSeat.setMemberId(LoginMemberContext.getId());
            trainSeat.setId(SnowUtil.getSnowflakeNextId());
            trainSeat.setCreateTime(nowTime);
            trainSeat.setUpdateTime(nowTime);

            trainSeatMapper.insert(trainSeat);
        }else{
            trainSeat.setUpdateTime(nowTime);
            trainSeatMapper.updateByPrimaryKeySelective(trainSeat);
        }



    }

    @Override
    public PageRes<TrainSeatQueryRes> queryTrainSeatList(TrainSeatQueryReq req) {
        TrainSeatExample trainSeatExample = new TrainSeatExample();
        TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();
//        if(ObjectUtil.isNotNull(req.getMemberId())){
//            criteria.andMemberIdEqualTo(req.getMemberId());
//        }

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
    public void deleteById(Long id) {
        trainSeatMapper.deleteByPrimaryKey(id);
    }
}
