package com.jktickets.service.impl;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.domain.Train;
import com.jktickets.domain.TrainExample;
import com.jktickets.exception.BusinessException;
import com.jktickets.exception.BusinessExceptionEnum;
import com.jktickets.mapper.TrainMapper;

import com.jktickets.req.train.TrainQueryReq;
import com.jktickets.req.train.TrainSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.train.TrainQueryRes;

import com.jktickets.service.TrainService;

import com.jktickets.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainServiceImpl implements TrainService {

    private final static Logger LOG = LoggerFactory.getLogger(TrainService.class);

    @Resource
    TrainMapper trainMapper;

    @Override
    public void saveTrain(TrainSaveReq req) {


        DateTime nowTime  = DateTime.now();
        Train train = BeanUtil.copyProperties(req, Train.class);

        if(ObjectUtil.isNull(train.getId())){
            // 保存之前，先校验唯一键是否存在
            Train trainDB = selectByUnique(req.getCode());
            if (ObjectUtil.isNotEmpty(trainDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_CODE_UNIQUE_ERROR);
            }

            train.setId(SnowUtil.getSnowflakeNextId());
            train.setCreateTime(nowTime);
            train.setUpdateTime(nowTime);

            trainMapper.insert(train);
        }else{
            train.setUpdateTime(nowTime);
            trainMapper.updateByPrimaryKeySelective(train);
        }



    }

    @Override
    public Train selectByUnique(String code) {
        TrainExample trainExample = new TrainExample();
        trainExample.createCriteria()
                .andCodeEqualTo(code);
        List<Train> list = trainMapper.selectByExample(trainExample);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public PageRes<TrainQueryRes> queryTrainList(TrainQueryReq req) {
        TrainExample trainExample = new TrainExample();
        trainExample.setOrderByClause("code asc");
        TrainExample.Criteria criteria = trainExample.createCriteria();
//        if(ObjectUtil.isNotNull(req.getMemberId())){
//            criteria.andMemberIdEqualTo(req.getMemberId());
//        }

        // 分页处理
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Train> trainList = trainMapper.selectByExample(trainExample);


        PageInfo<Train> trainPageInfo = new PageInfo<>(trainList);

        LOG.info("总行数：{}", trainPageInfo.getTotal());
        LOG.info("总页数：{}", trainPageInfo.getPages());

//  转成Controller的传输类
        List<TrainQueryRes> trainQueryResList = BeanUtil.copyToList(trainList, TrainQueryRes.class);

        PageRes<TrainQueryRes> pageRes = new PageRes<>();
        pageRes.setList(trainQueryResList);
        pageRes.setTotal(trainPageInfo.getTotal());
        return pageRes;
    }



    @Override
    public List<TrainQueryRes> queryAllTrainList() {
        List<Train> trainList = selectAllTrainList();

        List<TrainQueryRes> trainQueryRes = BeanUtil.copyToList(trainList, TrainQueryRes.class);
        return trainQueryRes;
    }

    public List<Train> selectAllTrainList() {
        TrainExample trainExample = new TrainExample();
//        code:车次编号
        trainExample.setOrderByClause("code desc");
        List<Train> trainList = trainMapper.selectByExample(trainExample);
        return trainList;
    }


    @Override
    public void deleteById(Long id) {
        trainMapper.deleteByPrimaryKey(id);
    }




}
