package com.jktickets.service.impl;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.domain.DailyTrain;
import com.jktickets.domain.DailyTrainExample;
import com.jktickets.mapper.DailyTrainMapper;

import com.jktickets.req.dailyTrain.DailyTrainQueryReq;
import com.jktickets.req.dailyTrain.DailyTrainSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.dailyTrain.DailyTrainQueryRes;

import com.jktickets.service.DailyTrainService;

import com.jktickets.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyTrainServiceImpl implements DailyTrainService {

    private final static Logger LOG = LoggerFactory.getLogger(DailyTrainService.class);

    @Resource
    DailyTrainMapper dailyTrainMapper;

    @Override
    public void saveDailyTrain(DailyTrainSaveReq req) {
        DailyTrain dailyTrain = BeanUtil.copyProperties(req, DailyTrain.class);


        DateTime nowTime  = DateTime.now();

        if(ObjectUtil.isNull(dailyTrain.getId())){
            //        从 线程中获取数据
//          dailyTrain.setMemberId(LoginMemberContext.getId());
            dailyTrain.setId(SnowUtil.getSnowflakeNextId());
            dailyTrain.setCreateTime(nowTime);
            dailyTrain.setUpdateTime(nowTime);

            dailyTrainMapper.insert(dailyTrain);
        }else{
            dailyTrain.setUpdateTime(nowTime);
            dailyTrainMapper.updateByPrimaryKeySelective(dailyTrain);
        }



    }

    @Override
    public PageRes<DailyTrainQueryRes> queryDailyTrainList(DailyTrainQueryReq req) {
        DailyTrainExample dailyTrainExample = new DailyTrainExample();
        dailyTrainExample.setOrderByClause("date desc,code asc");
        DailyTrainExample.Criteria criteria = dailyTrainExample.createCriteria();
        if(ObjectUtil.isNotNull(req.getDate())){
            criteria.andDateEqualTo(req.getDate());
        }
        if(ObjectUtil.isNotEmpty(req.getCode())){
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
}
