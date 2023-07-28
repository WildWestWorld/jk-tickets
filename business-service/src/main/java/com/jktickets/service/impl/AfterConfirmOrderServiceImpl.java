package com.jktickets.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.domain.*;
import com.jktickets.enums.ConfirmOrderStatusEnum;
import com.jktickets.enums.SeatColEnum;
import com.jktickets.enums.SeatTypeEnum;
import com.jktickets.exception.BusinessException;
import com.jktickets.exception.BusinessExceptionEnum;
import com.jktickets.mapper.ConfirmOrderMapper;
import com.jktickets.mapper.DailyTrainSeatMapper;
import com.jktickets.mapper.custom.DailyTrainTicketMapperCustom;
import com.jktickets.req.confirmOrder.ConfirmOrderDoReq;
import com.jktickets.req.confirmOrder.ConfirmOrderQueryReq;
import com.jktickets.req.confirmOrder.ConfirmOrderSaveReq;
import com.jktickets.req.confirmOrder.ConfirmOrderTicketReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.confirmOrder.ConfirmOrderQueryRes;
import com.jktickets.service.*;
import com.jktickets.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AfterConfirmOrderServiceImpl implements AfterConfirmOrderService {

    private final static Logger LOG = LoggerFactory.getLogger(AfterConfirmOrderService.class);

    @Resource
    DailyTrainSeatMapper dailyTrainSeatMapper;

    @Resource
    DailyTrainTicketMapperCustom dailyTrainTicketMapperCustom;


    //    为啥要另外开个类 去做后续的工作
//    因为用到了 Transactional ，如果在同一个类中 内部调用 带Transactional的方法是不生效的
//    所以得再写个类 使用Transactional
    @Override
    @Transactional
    public void afterDoConfirm(DailyTrainTicket dailyTrainTicket, List<DailyTrainSeat> finalSeatList) {
        for (DailyTrainSeat dailyTrainSeat : finalSeatList) {
            DailyTrainSeat seatForUpdate = new DailyTrainSeat();
            seatForUpdate.setId(dailyTrainSeat.getId());
            seatForUpdate.setSell(dailyTrainSeat.getSell());
            seatForUpdate.setUpdateTime(new Date());
//            updateByPrimaryKeySelective(根据主键修改（根据传入字段[需传入主键]）)
            dailyTrainSeatMapper.updateByPrimaryKeySelective(seatForUpdate);

//            影响库存:本次选座 之前 没卖过票的 和本次购买的区间 有交集的区间
            Integer startIndex = dailyTrainTicket.getStartIndex();
            Integer endIndex = dailyTrainTicket.getEndIndex();

            char[] chars = seatForUpdate.getSell().toCharArray();
//            最大的起始 索引值 应该为购票的终点站索引-1 因为你要是终点设置在起始位置 = 终点位置 就不用购票了
            Integer maxStartIndex = endIndex - 1;
//            最大的终点 索引值 应该为购票的起始站索引-1 因为你要是终点设置在起始位置 = 终点位置 就不用购票了
            Integer minEndIndex = startIndex - 1;

            Integer minStartIndex = 0;
//           从后往前找
//            从开始的索引 找1,1的索引-1 就是最小的可购票 起始站索引
            for (int i = startIndex - 1; i >= 0; i--) {
                char searchChar = chars[i];
                if (searchChar == '1') {
                    minStartIndex = i + 1;
                    break;
                }
            }
            LOG.info("影响出发站区间:{}-{}", minStartIndex, maxStartIndex);


            Integer maxEndIndex = seatForUpdate.getSell().length() ;
//            从开始的索引 找1,1的索引-1 就是最小的可购票 起始站索引
            for (int i = endIndex; i < seatForUpdate.getSell().length() - 1; i++) {
                char searchChar = chars[i];
                if (searchChar == '1') {
                    maxEndIndex = i;
                    break;
                }
            }
            LOG.info("影响到达站区间:{}-{}", minEndIndex, maxEndIndex);


//            更新余票数
            dailyTrainTicketMapperCustom.updateCountBySell(dailyTrainSeat.getDate(), dailyTrainSeat.getTrainCode(), dailyTrainSeat.getSeatType(), minStartIndex, maxStartIndex, minEndIndex, maxEndIndex);


        }

    }


}