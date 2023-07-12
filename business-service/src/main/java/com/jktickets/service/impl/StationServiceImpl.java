package com.jktickets.service.impl;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.domain.Station;
import com.jktickets.domain.StationExample;
import com.jktickets.domain.Train;
import com.jktickets.domain.TrainExample;
import com.jktickets.exception.BusinessException;
import com.jktickets.exception.BusinessExceptionEnum;
import com.jktickets.mapper.StationMapper;

import com.jktickets.req.station.StationQueryReq;
import com.jktickets.req.station.StationSaveReq;
import com.jktickets.req.train.TrainQueryReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.station.StationQueryRes;

import com.jktickets.res.train.TrainQueryRes;
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

            Station stationDB = selectStationByUnique(req.getName());
            if (ObjectUtil.isNotEmpty(stationDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_STATION_NAME_UNIQUE_ERROR);
            }


            //        从 线程中获取数据
//          station.setMemberId(LoginMemberContext.getId());
            station.setId(SnowUtil.getSnowflakeNextId());
            station.setCreateTime(nowTime);
            station.setUpdateTime(nowTime);

            stationMapper.insert(station);
        }else{
            station.setUpdateTime(nowTime);
            stationMapper.updateByPrimaryKeySelective(station);
        }



    }

    private Station selectStationByUnique(String name) {
        StationExample stationExample = new StationExample();
        stationExample.createCriteria().andNameEqualTo(name);
        List<Station> list = stationMapper.selectByExample(stationExample);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
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



    @Override
    public List<StationQueryRes> queryAllStationList(TrainQueryReq req) {
        StationExample stationExample = new StationExample();
//        code:车次编号
        stationExample.setOrderByClause("code desc");


        List<Station> stationList = stationMapper.selectByExample(stationExample);


        List<StationQueryRes> stationQueryRes = BeanUtil.copyToList(stationList, StationQueryRes.class);
        return stationQueryRes;
    }

}
