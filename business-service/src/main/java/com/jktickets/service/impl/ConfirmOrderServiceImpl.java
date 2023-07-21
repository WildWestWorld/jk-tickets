package com.jktickets.service.impl;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.domain.ConfirmOrder;
import com.jktickets.domain.ConfirmOrderExample;
import com.jktickets.domain.DailyTrainTicket;
import com.jktickets.enums.ConfirmOrderStatusEnum;
import com.jktickets.mapper.ConfirmOrderMapper;

import com.jktickets.req.confirmOrder.ConfirmOrderDoReq;
import com.jktickets.req.confirmOrder.ConfirmOrderQueryReq;
import com.jktickets.req.confirmOrder.ConfirmOrderSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.confirmOrder.ConfirmOrderQueryRes;

import com.jktickets.service.ConfirmOrderService;

import com.jktickets.service.DailyTrainTicketService;
import com.jktickets.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ConfirmOrderServiceImpl implements ConfirmOrderService {

    private final static Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);

    @Resource
    ConfirmOrderMapper confirmOrderMapper;

    @Resource
    DailyTrainTicketService dailyTrainTicketService;


    @Override
    public void saveConfirmOrder(ConfirmOrderSaveReq req) {


        DateTime nowTime  = DateTime.now();


        ConfirmOrder confirmOrder = BeanUtil.copyProperties(req, ConfirmOrder.class);


        if(ObjectUtil.isNull(confirmOrder.getId())){
            //        从 线程中获取数据
//          confirmOrder.setMemberId(LoginMemberContext.getId());
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(nowTime);
            confirmOrder.setUpdateTime(nowTime);

            confirmOrderMapper.insert(confirmOrder);
        }else{
            confirmOrder.setUpdateTime(nowTime);
            confirmOrderMapper.updateByPrimaryKeySelective(confirmOrder);
        }



    }

    @Override
    public PageRes<ConfirmOrderQueryRes> queryConfirmOrderList(ConfirmOrderQueryReq req) {
        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
        ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();
//        if(ObjectUtil.isNotNull(req.getMemberId())){
//            criteria.andMemberIdEqualTo(req.getMemberId());
//        }

        // 分页处理
        PageHelper.startPage(req.getPage(), req.getSize());
        List<ConfirmOrder> confirmOrderList = confirmOrderMapper.selectByExample(confirmOrderExample);


        PageInfo<ConfirmOrder> confirmOrderPageInfo = new PageInfo<>(confirmOrderList);

        LOG.info("总行数：{}", confirmOrderPageInfo.getTotal());
        LOG.info("总页数：{}", confirmOrderPageInfo.getPages());

//  转成Controller的传输类
        List<ConfirmOrderQueryRes> confirmOrderQueryResList = BeanUtil.copyToList(confirmOrderList, ConfirmOrderQueryRes.class);

        PageRes<ConfirmOrderQueryRes> pageRes = new PageRes<>();
        pageRes.setList(confirmOrderQueryResList);
        pageRes.setTotal(confirmOrderPageInfo.getTotal());
        return pageRes;
    }


    @Override
    public void deleteById(Long id) {
        confirmOrderMapper.deleteByPrimaryKey(id);
    }


    @Override
    public void doConfirm(ConfirmOrderDoReq req) {
        // 省略业务数据校验，如：车次是否存在，余票是否存在，车次是否在有效期内，tickets条数>0，同乘客同车次是否已买过

        // 保存确认订单表，状态初始
        DateTime nowTime  = DateTime.now();

        ConfirmOrder confirmOrder = new ConfirmOrder();
        confirmOrder.setId(SnowUtil.getSnowflakeNextId());
        confirmOrder.setMemberId(LoginMemberContext.getId());

        Date date = req.getDate();
        confirmOrder.setDate(date);

        String trainCode = req.getTrainCode();
        confirmOrder.setTrainCode(trainCode);

        String start = req.getStart();
        confirmOrder.setStart(start);

        String end = req.getEnd();
        confirmOrder.setEnd(end);

        confirmOrder.setDailyTrainTicketId(req.getDailyTrainTicketId());
        confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
        confirmOrder.setCreateTime(nowTime);
        confirmOrder.setUpdateTime(nowTime);
        confirmOrder.setTickets(JSON.toJSONString(req.getTickets()));
        confirmOrderMapper.insert(confirmOrder);

        // 查出余票记录，需要得到真实的库存
//        根据日期，火车编号，出发地，目的地
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
        LOG.info("查出余票记录:{}",dailyTrainTicket);

        // 扣减余票数量，并判断余票是否足够

        // 选座

        // 一个车箱一个车箱的获取座位数据

        // 挑选符合条件的座位，如果这个车箱不满足，则进入下个车箱（多个选座应该在同一个车厢）

        // 选中座位后事务处理：

        // 座位表修改售卖情况sell；
        // 余票详情表修改余票；
        // 为会员增加购票记录
        // 更新确认订单为成功
    }
}
