package com.jktickets.service.impl;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.domain.TrainCarriage;
import com.jktickets.domain.TrainCarriageExample;
import com.jktickets.mapper.TrainCarriageMapper;

import com.jktickets.req.trainCarriage.TrainCarriageQueryReq;
import com.jktickets.req.trainCarriage.TrainCarriageSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.trainCarriage.TrainCarriageQueryRes;

import com.jktickets.service.TrainCarriageService;

import com.jktickets.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainCarriageServiceImpl implements TrainCarriageService {

    private final static Logger LOG = LoggerFactory.getLogger(TrainCarriageService.class);

    @Resource
    TrainCarriageMapper trainCarriageMapper;

    @Override
    public void saveTrainCarriage(TrainCarriageSaveReq req) {
        TrainCarriage trainCarriage = BeanUtil.copyProperties(req, TrainCarriage.class);


        DateTime nowTime  = DateTime.now();

        if(ObjectUtil.isNull(trainCarriage.getId())){
            //        从 线程中获取数据
//          trainCarriage.setMemberId(LoginMemberContext.getId());
            trainCarriage.setId(SnowUtil.getSnowflakeNextId());
            trainCarriage.setCreateTime(nowTime);
            trainCarriage.setUpdateTime(nowTime);

            trainCarriageMapper.insert(trainCarriage);
        }else{
            trainCarriage.setUpdateTime(nowTime);
            trainCarriageMapper.updateByPrimaryKeySelective(trainCarriage);
        }



    }

    @Override
    public PageRes<TrainCarriageQueryRes> queryTrainCarriageList(TrainCarriageQueryReq req) {
        TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
        TrainCarriageExample.Criteria criteria = trainCarriageExample.createCriteria();
//        if(ObjectUtil.isNotNull(req.getMemberId())){
//            criteria.andMemberIdEqualTo(req.getMemberId());
//        }

        // 分页处理
        PageHelper.startPage(req.getPage(), req.getSize());
        List<TrainCarriage> trainCarriageList = trainCarriageMapper.selectByExample(trainCarriageExample);


        PageInfo<TrainCarriage> trainCarriagePageInfo = new PageInfo<>(trainCarriageList);

        LOG.info("总行数：{}", trainCarriagePageInfo.getTotal());
        LOG.info("总页数：{}", trainCarriagePageInfo.getPages());

//  转成Controller的传输类
        List<TrainCarriageQueryRes> trainCarriageQueryResList = BeanUtil.copyToList(trainCarriageList, TrainCarriageQueryRes.class);

        PageRes<TrainCarriageQueryRes> pageRes = new PageRes<>();
        pageRes.setList(trainCarriageQueryResList);
        pageRes.setTotal(trainCarriagePageInfo.getTotal());
        return pageRes;
    }


    @Override
    public void deleteById(Long id) {
        trainCarriageMapper.deleteByPrimaryKey(id);
    }
}
