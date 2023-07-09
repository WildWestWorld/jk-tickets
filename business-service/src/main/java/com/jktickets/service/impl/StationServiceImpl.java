package com.jktickets.service.impl;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.domain.Station;
import com.jktickets.domain.StationExample;
import com.jktickets.mapper.StationMapper;

import com.jktickets.req.station.StationQueryReq;
import com.jktickets.req.station.StationSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.station.StationQueryRes;
import com.jktickets.service.MemberService;
import com.jktickets.service.StationService;

import com.jktickets.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationServiceImpl implements StationService {

    private final static Logger LOG = LoggerFactory.getLogger(StationService.class);

    @Resource
    StationMapper stationMapper;

    @Override
    public void saveStation(StationSaveReq req) {
        Station station = BeanUtil.copyProperties(req, Station.class);


        DateTime nowTime  = DateTime.now();

        if(ObjectUtil.isNull(station.getId())){
            //        从 线程中获取数据
            station.setMemberId(LoginMemberContext.getId());
            station.setId(SnowUtil.getSnowflakeNextId());
            station.setCreateTime(nowTime);
            station.setUpdateTime(nowTime);

            stationMapper.insert(station);
        }else{
            station.setUpdateTime(nowTime);
            stationMapper.updateByPrimaryKeySelective(station);
        }



    }

    @Override
    public PageRes<StationQueryRes> queryStationList(StationQueryReq req) {
        StationExample stationExample = new StationExample();
        StationExample.Criteria criteria = stationExample.createCriteria();
//        if(ObjectUtil.isNotNull(req.getMemberId())){
//            criteria.andMemberIdEqualTo(req.getMemberId());
//        }

        // 分页处理
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Station> stationList = stationMapper.selectByExample(stationExample);


        PageInfo<Station> stationPageInfo = new PageInfo<>(stationList);

        LOG.info("总行数：{}", stationPageInfo.getTotal());
        LOG.info("总页数：{}", stationPageInfo.getPages());

//  转成Controller的传输类
        List<StationQueryRes> stationQueryResList = BeanUtil.copyToList(stationList, StationQueryRes.class);

        PageRes<StationQueryRes> pageRes = new PageRes<>();
        pageRes.setList(stationQueryResList);
        pageRes.setTotal(stationPageInfo.getTotal());
        return pageRes;
    }


    @Override
    public void deleteById(Long id) {
        stationMapper.deleteByPrimaryKey(id);
    }
}
