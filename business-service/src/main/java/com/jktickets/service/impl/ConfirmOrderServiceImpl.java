package com.jktickets.service.impl;

import cn.hutool.core.bean.BeanUtil;

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

import com.jktickets.req.confirmOrder.ConfirmOrderDoReq;
import com.jktickets.req.confirmOrder.ConfirmOrderQueryReq;
import com.jktickets.req.confirmOrder.ConfirmOrderSaveReq;
import com.jktickets.req.confirmOrder.ConfirmOrderTicketReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.confirmOrder.ConfirmOrderQueryRes;

import com.jktickets.service.ConfirmOrderService;

import com.jktickets.service.DailyTrainCarriageService;
import com.jktickets.service.DailyTrainSeatService;
import com.jktickets.service.DailyTrainTicketService;
import com.jktickets.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ConfirmOrderServiceImpl implements ConfirmOrderService {

    private final static Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);

    @Resource
    ConfirmOrderMapper confirmOrderMapper;

    @Resource
    DailyTrainTicketService dailyTrainTicketService;

    @Resource
    DailyTrainCarriageService dailyTrainCarriageService;

    @Resource
    DailyTrainSeatService dailyTrainSeatService;


    @Override
    public void saveConfirmOrder(ConfirmOrderSaveReq req) {


        DateTime nowTime = DateTime.now();


        ConfirmOrder confirmOrder = BeanUtil.copyProperties(req, ConfirmOrder.class);


        if (ObjectUtil.isNull(confirmOrder.getId())) {
            //        从 线程中获取数据
//          confirmOrder.setMemberId(LoginMemberContext.getId());
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(nowTime);
            confirmOrder.setUpdateTime(nowTime);

            confirmOrderMapper.insert(confirmOrder);
        } else {
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
        DateTime nowTime = DateTime.now();

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
        LOG.info("查出余票记录:{}", dailyTrainTicket);


        List<ConfirmOrderTicketReq> ticketReqList = req.getTickets();
        // 扣减余票数量，并判断余票是否足够
        reduceTickets(dailyTrainTicket, ticketReqList);

//        计算相对第一个座位的偏移量
//        例如:选择C1 D2 则偏移量是[0,5] (一排四个)
//        例如:选择A1 B1 C1 则偏移量是[0,1,2]

//        查询本次下单 是否为 选座（判定规则传入的ticketList 里面的item有seat就是选座 ）
        ConfirmOrderTicketReq confirmOrderTicketReq = ticketReqList.get(0);
        if (StrUtil.isNotBlank(confirmOrderTicketReq.getSeat())) {
            LOG.info("本次购票有选座");
//            查出本次选座的座位类型有哪些，用于计算所选座位的与第一个座位的偏移量
//            SeatColEnum.getColsByType(车箱的座位类型) = 根据车箱的座位类型，筛选出所有的列，比如车箱类型是一等座，则筛选出columnList={ACDF}
            List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(confirmOrderTicketReq.getSeatTypeCode());
            LOG.info("本次选座 座位类型包含的列:{}", colEnumList);

//          构造  用于参照的座位列表referSeatList ={A1,C1,D1,F1,A2,C2,D2,F2  }(以A、C、D、F循环)
            List<String> referSeatList = new ArrayList<>();
            for (int i = 1; i <= 2; i++) {
                for (SeatColEnum seatColEnum : colEnumList) {
//                    seatColEnum.getCode() = A/C/D/F
//                    seatColEnum.getCode()+1 = A1 C1 D1 F1
                    referSeatList.add(seatColEnum.getCode() + 1);
                }
            }

            LOG.info("用于作参照的两排座位,{}", referSeatList);

//           先找到绝对偏移值(数组的 两个位置的索引相减)
//            例如:C1 D2 =>绝对偏移值 [1,6]  减去第一位的值 => 相对偏移值[0,5]
//           创建存放索引位置的数组
            List<Integer> absoluteOffsetList = new ArrayList<>();
            for (ConfirmOrderTicketReq orderTicketReq : ticketReqList) {
//             获取购买单个票的索引
                int index = referSeatList.indexOf(orderTicketReq.getSeat());
                absoluteOffsetList.add(index);
            }
            LOG.info("计算得到所有座位的的绝对偏移量:{}", absoluteOffsetList);


//           创建存放相对位置 索引位置的数组
            List<Integer> relativeOffsetList = new ArrayList<>();
            for (Integer index : absoluteOffsetList) {
                Integer firstPositionIndex = absoluteOffsetList.get(0);
//                计算偏移量
                int offset = index - firstPositionIndex;
                relativeOffsetList.add(offset);
            }


            LOG.info("计算得到所有座位的 绝对偏移量:{}", relativeOffsetList);
//            confirmOrderTicketReq.getSeat().split("")[0]=》  A1 =>[A,1]=>A
            selectSeat(date, trainCode, confirmOrderTicketReq.getSeatTypeCode(), confirmOrderTicketReq.getSeat().split("")[0], relativeOffsetList, dailyTrainTicket.getStartIndex(), dailyTrainTicket.getEndIndex());
        } else {
            LOG.info("本次购票没有选座");


            for (ConfirmOrderTicketReq orderTicketReq : ticketReqList) {
                selectSeat(date, trainCode, orderTicketReq.getSeatTypeCode(), null, null, dailyTrainTicket.getStartIndex(), dailyTrainTicket.getEndIndex());

            }

        }


        // 选座

        // 一个车箱一个车箱的获取座位数据

        // 挑选符合条件的座位，如果这个车箱不满足，则进入下个车箱（多个选座应该在同一个车厢）

        // 选中座位后事务处理：

        // 座位表修改售卖情况sell；
        // 余票详情表修改余票；
        // 为会员增加购票记录
        // 更新确认订单为成功
    }


    //    挑选座位
    private void selectSeat(Date date, String trainCode, String seatType, String column, List<Integer> offsetList, Integer startIndex, Integer endIndex) {
        // 一个车箱一个车箱的获取座位数据
//        例如买的是一等座 就应该先找到一等座的车厢
        List<DailyTrainCarriage> dailyTrainCarriageList = dailyTrainCarriageService.selectBySeatType(date, trainCode, seatType);
        LOG.info("共查出{}个符合条件的车厢", dailyTrainCarriageList.size());


//            根据车厢找所有的座位
        for (DailyTrainCarriage dailyTrainCarriage : dailyTrainCarriageList) {
            LOG.info("从车厢{}选座", dailyTrainCarriage.getIndex());
//            根据日期 火车 火车车厢的Index，查询座位列表
            List<DailyTrainSeat> dailyTrainSeatList = dailyTrainSeatService.selectByCarriage(date, trainCode, dailyTrainCarriage.getIndex());
            LOG.info("车厢{}的座位数:{}", dailyTrainCarriage.getIndex(), dailyTrainSeatList.size());


            for (DailyTrainSeat dailyTrainSeat : dailyTrainSeatList) {

//                判断column 是否有值，如果有值就对比列号
                String col = dailyTrainSeat.getCol();
//                这里的column 是外部传入
                if(StrUtil.isBlank(column)){
                    LOG.info("无选座");
                }else{
//                    如果没有找到对应的列就跳出当前循环
                    if(!column.equals(col)){
                        continue;
                    }
                }


                boolean isChoose = checkSeatSellState(dailyTrainSeat, startIndex, endIndex);
//                如果已经选中好了，就跳出循环，没有就继续
                if (isChoose) {
                    LOG.info("选中座位");
                    return;
                } else {
//                    LOG.info("未选中座位");
                    ;
                }

            }

        }

    }


//    计算 某座位在区间内的是否可卖
//    sell = 10001 =》1-2站被买 5-6站被买=》 000=》2-5站可买
//    全为0 可卖，只要1 就不可卖

    //    例如已卖 1001  现买 0110  使用按位或=》 1111
    private boolean checkSeatSellState(DailyTrainSeat dailyTrainSeat, Integer startIndex, Integer endIndex) {
//         获得座位的售卖记录
//        00001
        String sell = dailyTrainSeat.getSell();
        String sellPart = sell.substring(startIndex, endIndex);

//        只要有1 就不可卖
        if (Integer.parseInt(sellPart) > 0) {
            LOG.info("座位{}在本次车站区间{}-{}已售过票,不可选中该座位", dailyTrainSeat.getCarriageIndex(), startIndex, endIndex);

            return false;
        } else {
//            可选的座位
            LOG.info("座位{}在本次车站区间{}-{}没有售过票,可选中该座位", dailyTrainSeat.getCarriageIndex(), startIndex, endIndex);

//            把准备购买的区间 转换为111
//            111
            String currentSell = sellPart.replace("0", "1");
//           0111 =>  StrUtil.fillBefore(原字符串，填充字符串，总字符串的长度)
//            补充左边的零
            currentSell = StrUtil.fillBefore(currentSell, '0', endIndex);
//            补充右边的零
//            01110
            currentSell = StrUtil.fillAfter(currentSell, '0', sell.length());
//         当前区间售票信息currentSell与库里面的售卖信息相或
//            15 相或之后 得到的数是15(01111)
            int newSellInt = NumberUtil.binaryToInt(currentSell) | NumberUtil.binaryToInt(sell);
//            15 转换为2进制 =>1111
            String newSell = NumberUtil.getBinaryStr(newSellInt);
//        重新把左边 缺少的0 补上，右边是不会缺0的
            currentSell = StrUtil.fillBefore(newSell, '0', sell.length());

            LOG.info("座位{}被选中，原售票信息:{},车站区间:{}-{},即:{},最终售票信息:{}", dailyTrainSeat.getCarriageIndex(), sell, startIndex, endIndex, currentSell, newSell);


            return true;
        }


    }


    private static void reduceTickets(DailyTrainTicket dailyTrainTicket, List<ConfirmOrderTicketReq> ticketReqList) {
        //  循环获取的票类型，
        for (ConfirmOrderTicketReq ticketReq : ticketReqList) {
//seatTypeCode =1(一等座)，2（二等座）
            String seatTypeCode = ticketReq.getSeatTypeCode();
//          SeatTypeEnum::getCode =   YDZ("1",), EDZ("2"）
//
//            EnumUtil.getBy( 查询的对象,查询的内容 )
            SeatTypeEnum seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getCode, seatTypeCode);
//查询的结果seatTypeCode =YDZ =》   seatTypeEnum ={YDZ("1")}

//            这样我们就获取到对应的 车票类型 这样我们就能取出库存 数量并减一
            switch (seatTypeEnum) {
                case YDZ -> {
                    int countRemain = dailyTrainTicket.getYdz() - 1;
                    if (countRemain < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countRemain);
                }
                case EDZ -> {
                    int countRemain = dailyTrainTicket.getEdz() - 1;
                    if (countRemain < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setEdz(countRemain);
                }
                case RW -> {
                    int countRemain = dailyTrainTicket.getRw() - 1;
                    if (countRemain < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setRw(countRemain);

                }
                case YW -> {
                    int countRemain = dailyTrainTicket.getYw() - 1;
                    if (countRemain < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYw(countRemain);
                }
            }
        }
    }
}
