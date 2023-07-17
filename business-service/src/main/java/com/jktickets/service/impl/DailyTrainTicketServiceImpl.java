package com.jktickets.service.impl;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.domain.DailyTrain;
import com.jktickets.domain.DailyTrainTicket;
import com.jktickets.domain.DailyTrainTicketExample;
import com.jktickets.domain.TrainStation;
import com.jktickets.enums.SeatTypeEnum;
import com.jktickets.enums.TrainTypeEnum;
import com.jktickets.mapper.DailyTrainTicketMapper;

import com.jktickets.req.dailyTrainTicket.DailyTrainTicketQueryReq;
import com.jktickets.req.dailyTrainTicket.DailyTrainTicketSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.dailyTrainTicket.DailyTrainTicketQueryRes;

import com.jktickets.service.DailyTrainSeatService;
import com.jktickets.service.DailyTrainStationService;
import com.jktickets.service.DailyTrainTicketService;

import com.jktickets.service.TrainStationService;
import com.jktickets.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Service
public class DailyTrainTicketServiceImpl implements DailyTrainTicketService {

    private final static Logger LOG = LoggerFactory.getLogger(DailyTrainTicketService.class);

    @Resource
    DailyTrainTicketMapper dailyTrainTicketMapper;
    @Resource
    TrainStationService trainStationService;
    @Resource
    DailyTrainSeatService dailyTrainSeatService;

    @Override
    public void saveDailyTrainTicket(DailyTrainTicketSaveReq req) {


        DateTime nowTime  = DateTime.now();


        DailyTrainTicket dailyTrainTicket = BeanUtil.copyProperties(req, DailyTrainTicket.class);


        if(ObjectUtil.isNull(dailyTrainTicket.getId())){
            //        从 线程中获取数据
//          dailyTrainTicket.setMemberId(LoginMemberContext.getId());
            dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainTicket.setCreateTime(nowTime);
            dailyTrainTicket.setUpdateTime(nowTime);

            dailyTrainTicketMapper.insert(dailyTrainTicket);
        }else{
            dailyTrainTicket.setUpdateTime(nowTime);
            dailyTrainTicketMapper.updateByPrimaryKeySelective(dailyTrainTicket);
        }



    }

    @Override
    public PageRes<DailyTrainTicketQueryRes> queryDailyTrainTicketList(DailyTrainTicketQueryReq req) {
        DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();

        dailyTrainTicketExample.setOrderByClause("id desc");
        DailyTrainTicketExample.Criteria criteria = dailyTrainTicketExample.createCriteria();
//        if(ObjectUtil.isNotNull(req.getMemberId())){
//            criteria.andMemberIdEqualTo(req.getMemberId());
//        }


        if (ObjUtil.isNotNull(req.getDate())) {
            criteria.andDateEqualTo(req.getDate());
        }
        if (ObjUtil.isNotEmpty(req.getTrainCode())) {
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }
        if (ObjUtil.isNotEmpty(req.getStart())) {
            criteria.andStartEqualTo(req.getStart());
        }
        if (ObjUtil.isNotEmpty(req.getEnd())) {
            criteria.andEndEqualTo(req.getEnd());
        }

        // 分页处理
        PageHelper.startPage(req.getPage(), req.getSize());
        List<DailyTrainTicket> dailyTrainTicketList = dailyTrainTicketMapper.selectByExample(dailyTrainTicketExample);


        PageInfo<DailyTrainTicket> dailyTrainTicketPageInfo = new PageInfo<>(dailyTrainTicketList);

        LOG.info("总行数：{}", dailyTrainTicketPageInfo.getTotal());
        LOG.info("总页数：{}", dailyTrainTicketPageInfo.getPages());

//  转成Controller的传输类
        List<DailyTrainTicketQueryRes> dailyTrainTicketQueryResList = BeanUtil.copyToList(dailyTrainTicketList, DailyTrainTicketQueryRes.class);

        PageRes<DailyTrainTicketQueryRes> pageRes = new PageRes<>();
        pageRes.setList(dailyTrainTicketQueryResList);
        pageRes.setTotal(dailyTrainTicketPageInfo.getTotal());
        return pageRes;
    }


    @Override
    public void deleteById(Long id) {
        dailyTrainTicketMapper.deleteByPrimaryKey(id);
    }

    @Transactional
    @Override
    public void genDailyTrainTicket(DailyTrain dailyTrain, Date date, String trainCode) {
        LOG.info("生成日期【{}】车次【{}】的余票信息开始", DateUtil.formatDate(date), trainCode);

        // 删除某日某车次的余票信息
        DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
        dailyTrainTicketExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode);
        dailyTrainTicketMapper.deleteByExample(dailyTrainTicketExample);



        // 查出某车次的所有的车站信息
        List<TrainStation> stationList = trainStationService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(stationList)) {
            LOG.info("该车次没有车站基础数据，生成该车次的余票信息结束");
            return;
        }

        DateTime now = DateTime.now();
        for (int i = 0; i < stationList.size(); i++) {
            // 得到出发站
            TrainStation trainStationStart = stationList.get(i);
            BigDecimal sumKM = BigDecimal.ZERO;
            for (int j = (i + 1); j < stationList.size(); j++) {
            //得到终点站
                TrainStation trainStationEnd = stationList.get(j);
                sumKM = sumKM.add(trainStationEnd.getKm());

                DailyTrainTicket dailyTrainTicket = new DailyTrainTicket();
                dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
                dailyTrainTicket.setDate(date);
                dailyTrainTicket.setTrainCode(trainCode);
                dailyTrainTicket.setStart(trainStationStart.getName());
                dailyTrainTicket.setStartPinyin(trainStationStart.getNamePinyin());
                dailyTrainTicket.setStartTime(trainStationStart.getOutTime());
                dailyTrainTicket.setStartIndex(trainStationStart.getIndex());
                dailyTrainTicket.setEnd(trainStationEnd.getName());
                dailyTrainTicket.setEndPinyin(trainStationEnd.getNamePinyin());
                dailyTrainTicket.setEndTime(trainStationEnd.getInTime());
                dailyTrainTicket.setEndIndex(trainStationEnd.getIndex());
                int ydz = dailyTrainSeatService.countTrainSeat(date, trainCode, SeatTypeEnum.YDZ.getCode());
                int edz = dailyTrainSeatService.countTrainSeat(date, trainCode, SeatTypeEnum.EDZ.getCode());
                int rw = dailyTrainSeatService.countTrainSeat(date, trainCode, SeatTypeEnum.RW.getCode());
                int yw = dailyTrainSeatService.countTrainSeat(date, trainCode, SeatTypeEnum.YW.getCode());
                // 票价 = 里程之和 * 座位单价 * 车次类型系数
                String trainType = dailyTrain.getType();
                // 计算票价系数：TrainTypeEnum.priceRate
                //EnumUtil.getFieldBy(目标，条件1，条件2)=》条件1=条件2 找出目标
                BigDecimal priceRate = EnumUtil.getFieldBy(TrainTypeEnum::getPriceRate, TrainTypeEnum::getCode, trainType);

//                sumKM【公里数】 * SeatTypeEnum.YDZ.getPrice()【基础票价 N元/公里】 *priceRate【车次类型系数】
//                .setScale(2, RoundingMode.HALF_UP); 保留两位小数，四舍五入
                BigDecimal ydzPrice = sumKM.multiply(SeatTypeEnum.YDZ.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal edzPrice = sumKM.multiply(SeatTypeEnum.EDZ.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal rwPrice = sumKM.multiply(SeatTypeEnum.RW.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal ywPrice = sumKM.multiply(SeatTypeEnum.YW.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                dailyTrainTicket.setYdz(ydz);
                dailyTrainTicket.setYdzPrice(ydzPrice);
                dailyTrainTicket.setEdz(edz);
                dailyTrainTicket.setEdzPrice(edzPrice);
                dailyTrainTicket.setRw(rw);
                dailyTrainTicket.setRwPrice(rwPrice);
                dailyTrainTicket.setYw(yw);
                dailyTrainTicket.setYwPrice(ywPrice);
                dailyTrainTicket.setCreateTime(now);
                dailyTrainTicket.setUpdateTime(now);
                dailyTrainTicketMapper.insert(dailyTrainTicket);
            }
        }
        LOG.info("生成日期【{}】车次【{}】的余票信息结束", DateUtil.formatDate(date), trainCode);
    }

}
