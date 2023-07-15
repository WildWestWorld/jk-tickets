package com.jktickets.service.impl;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.domain.DailyTrainStation;
import com.jktickets.domain.DailyTrainStationExample;
import com.jktickets.mapper.DailyTrainStationMapper;

import com.jktickets.req.dailyTrainStation.DailyTrainStationQueryReq;
import com.jktickets.req.dailyTrainStation.DailyTrainStationSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.dailyTrainStation.DailyTrainStationQueryRes;

import com.jktickets.service.DailyTrainStationService;

import com.jktickets.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyTrainStationServiceImpl implements DailyTrainStationService {

    private final static Logger LOG = LoggerFactory.getLogger(DailyTrainStationService.class);

    @Resource
    DailyTrainStationMapper dailyTrainStationMapper;

    @Override
    public void saveDailyTrainStation(DailyTrainStationSaveReq req) {
        DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(req, DailyTrainStation.class);


        DateTime nowTime  = DateTime.now();

        if(ObjectUtil.isNull(dailyTrainStation.getId())){
            //        从 线程中获取数据
//          dailyTrainStation.setMemberId(LoginMemberContext.getId());
            dailyTrainStation.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainStation.setCreateTime(nowTime);
            dailyTrainStation.setUpdateTime(nowTime);

            dailyTrainStationMapper.insert(dailyTrainStation);
        }else{
            dailyTrainStation.setUpdateTime(nowTime);
            dailyTrainStationMapper.updateByPrimaryKeySelective(dailyTrainStation);
        }



    }

    @Override
    public PageRes<DailyTrainStationQueryRes> queryDailyTrainStationList(DailyTrainStationQueryReq req) {
        DailyTrainStationExample dailyTrainStationExample = new DailyTrainStationExample();
        dailyTrainStationExample.setOrderByClause("date desc, train_code asc, `index` asc");
        DailyTrainStationExample.Criteria criteria = dailyTrainStationExample.createCriteria();
        if (ObjUtil.isNotNull(req.getDate())) {
            criteria.andDateEqualTo(req.getDate());
        }
        if (ObjUtil.isNotEmpty(req.getTrainCode())) {
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }

        // 分页处理
        PageHelper.startPage(req.getPage(), req.getSize());
        List<DailyTrainStation> dailyTrainStationList = dailyTrainStationMapper.selectByExample(dailyTrainStationExample);


        PageInfo<DailyTrainStation> dailyTrainStationPageInfo = new PageInfo<>(dailyTrainStationList);

        LOG.info("总行数：{}", dailyTrainStationPageInfo.getTotal());
        LOG.info("总页数：{}", dailyTrainStationPageInfo.getPages());

//  转成Controller的传输类
        List<DailyTrainStationQueryRes> dailyTrainStationQueryResList = BeanUtil.copyToList(dailyTrainStationList, DailyTrainStationQueryRes.class);

        PageRes<DailyTrainStationQueryRes> pageRes = new PageRes<>();
        pageRes.setList(dailyTrainStationQueryResList);
        pageRes.setTotal(dailyTrainStationPageInfo.getTotal());
        return pageRes;
    }


    @Override
    public void deleteById(Long id) {
        dailyTrainStationMapper.deleteByPrimaryKey(id);
    }



}
