package com.jktickets.service.impl;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.domain.*;
import com.jktickets.dto.ConfirmOrderMQDto;
import com.jktickets.enums.ConfirmOrderStatusEnum;
import com.jktickets.enums.RedisKeyPreEnum;
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

import com.jktickets.service.*;

import com.jktickets.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    @Resource
    AfterConfirmOrderService afterConfirmOrderService;

    @Resource
    SkTokenService skTokenService;


    //    Redis
//    一定得用 Autowired 他是按类去找的
//    Resource 按后面的名字找的对象，会找到我们自己写的类
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedissonClient redissonClient;

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

        confirmOrderExample.setOrderByClause("date desc");

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


    //    synchronized 加入锁，以防高并发超卖
//synchronized 只能解决 单机锁的问题，多个节点一起卖的时候还是超卖
    @Override
//    Sentinenl 限流前得加资源注释
    @SentinelResource(value = "doConfirm", blockHandler = "doConfirmBlock")
//    public void doConfirm(ConfirmOrderDoReq req) {
    public void doConfirm(ConfirmOrderMQDto dto) {
////        拿令牌
//        boolean validSkToken = skTokenService.validSkToken(req.getDate(), req.getTrainCode(), LoginMemberContext.getId());
//        if (validSkToken) {
//            LOG.info("令牌校验通过");
//        } else {
//            LOG.info("令牌校验不通过");
//            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_TOKEN_FAIL);
//        }
//
//
////        车次时间+车次号来认定车次的票
        String lockKey = RedisKeyPreEnum.CONFIRM_ORDER + "-" + DateUtil.formatDate(dto.getDate()) + "-" + dto.getTrainCode();
//
//        Redis分布式锁 现在改为看门狗锁
//        替换原因：可能会卡，卡了就导致 时间过长，锁就消失了
//
//
//        setIfAbsent 如果该值不存在 就往里面set
//        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 5, TimeUnit.SECONDS);
//
//
//        if(Boolean.TRUE.equals(setIfAbsent)){
//            LOG.info("恭喜，抢到锁了");
//        }else {
//            LOG.info("很遗憾，没抢到锁");
//            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
//
//        }
//
        RLock lock = null;

        try {
//        使用redisson 自带看门狗
            lock = redissonClient.getLock(lockKey);


//            红锁
//            红锁，Redis 主从服务器 半数以上拿到锁就是拿到锁了
//            RedissonRedLock redissonRedLock = new RedissonRedLock(lock, lock);
//            boolean tryLock1 = redissonRedLock.tryLock(0, TimeUnit.SECONDS);

//        不带看门狗
//        lock.tryLock(0,10,TimeUnit.SECONDS);

//        自带看门狗
//            waitTime最长阻塞时间
//            leaseTime 拿到锁时 锁的时长
            //        lock.tryLock(waitTime,leaseTime,时间单位);

            boolean tryLock = lock.tryLock(0, TimeUnit.SECONDS);
            if (tryLock) {
                LOG.info("恭喜，抢到锁了");
////            测试锁
////                for(int i= 0;i<30;i++){
////                    Long expire = redisTemplate.opsForValue().getOperations().getExpire(lockKey);
////                    LOG.info("锁过期时间还有:{}",expire);
////                    Thread.sleep(1000);
            } else {
                LOG.info("很遗憾，没抢到锁,有其他消费线程正在出票，不作任何处理");
                return;
//                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);

            }


            // 省略业务数据校验，如：车次是否存在，余票是否存在，车次是否在有效期内，tickets条数>0，同乘客同车次是否已买过

            // 保存确认订单表，状态初始
//            DateTime nowTime = DateTime.now();
//
//            ConfirmOrder confirmOrder = new ConfirmOrder();
//            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
//            confirmOrder.setMemberId(LoginMemberContext.getId());

//            confirmOrder.setMemberId(req.getMemberId());

//            从数据查出订单 因为MQ 创建订单移出去了所以我们自己创建订单
            while (true) {
                ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
                confirmOrderExample.setOrderByClause("id asc");
                ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();

                criteria.andDateEqualTo(dto.getDate()).andTrainCodeEqualTo(dto.getTrainCode()).andStatusEqualTo(ConfirmOrderStatusEnum.INIT.getCode());
                PageHelper.startPage(1, 5);
                List<ConfirmOrder> confirmOrderList = confirmOrderMapper.selectByExampleWithBLOBs(confirmOrderExample);

                if (CollUtil.isEmpty(confirmOrderList)) {
                    LOG.info("没有需要处理的订单，结束循环");
                    break;
                } else {
                    LOG.info("本次处理{}条订单", confirmOrderList.size());
                }

//                一条一条的卖    forEach(this::sell)  给每个元素执行这个方法
//                confirmOrderList.forEach(this::sell);
                confirmOrderList.forEach(confirmOrder -> {
                    try {
                        sell(confirmOrder);
                    } catch (BusinessException e) {
                        BusinessExceptionEnum confirmOrderTicketCountError = BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR;
                        if (e.getBusinessExceptionEnum().equals(confirmOrderTicketCountError)) {
                            LOG.info("本订单余票不足，继续售卖下一个订单");
                            confirmOrder.setStatus(ConfirmOrderStatusEnum.EMPTY.getCode());
                            updateStatus(confirmOrder);
                        } else {
                            throw e;
                        }

                    }
                });
            }


//            从数据查出订单 因为MQ 创建订单移出去了所以我们自己创建订单
//            ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
//            confirmOrderExample.setOrderByClause("id asc");
//            ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();
//            criteria.andDateEqualTo(dto.getDate()).andTrainCodeEqualTo(dto.getTrainCode()).andMemberIdEqualTo(req.getMemberId()).andStatusEqualTo(ConfirmOrderStatusEnum.INIT.getCode());
//
////            selectByExampleWithBLOBs 查询大字段 该接口的大字段 =  TICKET JSON类型的大字段
//            List<ConfirmOrder> confirmOrderList = confirmOrderMapper.selectByExampleWithBLOBs(confirmOrderExample);
//
//            ConfirmOrder confirmOrder =null;
//            if (CollUtil.isEmpty(confirmOrderList)){
//                LOG.info("找不到原始订单,结束");
//                return;
//            }else {
//                LOG.info("本次处理{}条确认订单",confirmOrderList.size());
//                confirmOrder  =  confirmOrderList.get(0);
//
//            }


            // 选座

            // 一个车箱一个车箱的获取座位数据

            // 挑选符合条件的座位，如果这个车箱不满足，则进入下个车箱（多个选座应该在同一个车厢）

            // 选中座位后事务处理：

            // 座位表修改售卖情况sell；
            // 余票详情表修改余票；
            // 为会员增加购票记录
            // 更新确认订单为成功


        } catch (InterruptedException e) {
            LOG.error("购票异常", e);
        } finally {
//            //       购票成功，删除redis 分布式锁
            LOG.info("购票流程结束，释放锁");
////            redisTemplate.delete(lockKey);
////            isHeldByCurrentThread 判断是否是 当前线程
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }

        }


    }


    private void sell(ConfirmOrder confirmOrder) {
//        为了演示排队效果，每次出票 200毫秒延迟
        try {
            Thread.sleep(200);
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }



        ConfirmOrderDoReq req = new ConfirmOrderDoReq();
        req.setMemberId(confirmOrder.getMemberId());
        req.setDate(confirmOrder.getDate());
        req.setTrainCode(confirmOrder.getTrainCode());
        req.setStart(confirmOrder.getStart());
        req.setEnd(confirmOrder.getEnd());
        req.setDailyTrainTicketId(confirmOrder.getDailyTrainTicketId());
        req.setTickets(JSON.parseArray(confirmOrder.getTickets(), ConfirmOrderTicketReq.class));
        req.setImageCode("");
        req.setImageCodeToken("");
        req.setLogId("");

//        将订单设置为处理中 避免重复处理
        LOG.info("将确认订单更新成处理中，避免重复处理，confirm_order_id:{}", confirmOrder.getId());
        confirmOrder.setStatus(ConfirmOrderStatusEnum.PENDING.getCode());
        updateStatus(confirmOrder);


        Date date = req.getDate();
//            confirmOrder.setDate(date);

        String trainCode = req.getTrainCode();
//            confirmOrder.setTrainCode(trainCode);

        String start = req.getStart();
//            confirmOrder.setStart(start);

        String end = req.getEnd();
//            confirmOrder.setEnd(end);
        List<ConfirmOrderTicketReq> ticketReqList = req.getTickets();


//            confirmOrder.setDailyTrainTicketId(req.getDailyTrainTicketId());
//            confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
//            confirmOrder.setCreateTime(nowTime);
//            confirmOrder.setUpdateTime(nowTime);
//            confirmOrder.setTickets(JSON.toJSONString(confirmOrder.getTickets()));
//            confirmOrderMapper.insert(confirmOrder);

        // 查出余票记录，需要得到真实的库存
//        根据日期，火车编号，出发地，目的地
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
        LOG.info("查出余票记录:{}", dailyTrainTicket);


        req.getTickets();
        // 扣减余票数量，并判断余票是否足够
        reduceTickets(dailyTrainTicket, ticketReqList);


//       最终选座结果 (不保存 自动选座会一直选择一个位置)
        List<DailyTrainSeat> finalSeatList = new ArrayList<>();


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
                    referSeatList.add(seatColEnum.getCode() + i);
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
            selectSeat(finalSeatList, date, trainCode, confirmOrderTicketReq.getSeatTypeCode(), confirmOrderTicketReq.getSeat().split("")[0], relativeOffsetList, dailyTrainTicket.getStartIndex(), dailyTrainTicket.getEndIndex());
        } else {
            LOG.info("本次购票没有选座");


            for (ConfirmOrderTicketReq orderTicketReq : ticketReqList) {
                selectSeat(finalSeatList, date, trainCode, orderTicketReq.getSeatTypeCode(), null, null, dailyTrainTicket.getStartIndex(), dailyTrainTicket.getEndIndex());
            }

        }

        LOG.info("最终选座:{}", finalSeatList);


        try {
            afterConfirmOrderService.afterDoConfirm(dailyTrainTicket, finalSeatList, ticketReqList, confirmOrder);
        } catch (Exception e) {
            LOG.error("保存购票信息失败", e);
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_EXCEPTION);
        }
    }


    //    更新状态
    public void updateStatus(ConfirmOrder confirmOrder) {
        ConfirmOrder confirmOrderUpdate = new ConfirmOrder();
        confirmOrderUpdate.setId(confirmOrder.getId());
        confirmOrderUpdate.setUpdateTime(new Date());
        confirmOrderUpdate.setStatus(confirmOrder.getStatus());
        confirmOrderMapper.updateByPrimaryKeySelective(confirmOrderUpdate);
    }


    //    sentinel 降级方法
    public void doConfirmBlock(ConfirmOrderDoReq req, BlockException e) {
        LOG.info("购票请求被限流:{}", req);
        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
    }


    //    挑选座位
    private void selectSeat(List<DailyTrainSeat> finalSeatList, Date date, String trainCode, String seatType, String column, List<Integer> offsetList, Integer startIndex, Integer endIndex) {
//finalSeatListTemp 临时变量的变量
        List<DailyTrainSeat> finalSeatListTemp = new ArrayList<>();

        // 一个车箱一个车箱的获取座位数据
//        例如买的是一等座 就应该先找到一等座的车厢
        List<DailyTrainCarriage> dailyTrainCarriageList = dailyTrainCarriageService.selectBySeatType(date, trainCode, seatType);
        LOG.info("共查出{}个符合条件的车厢", dailyTrainCarriageList.size());


//            根据车厢找所有的座位
        for (DailyTrainCarriage dailyTrainCarriage : dailyTrainCarriageList) {
            LOG.info("从车厢{}选座", dailyTrainCarriage.getIndex());

            //若是出现换车厢的情况 也清空临时变量
            finalSeatListTemp = new ArrayList<>();


//            根据日期 火车 火车车厢的Index，查询座位列表
            List<DailyTrainSeat> dailyTrainSeatList = dailyTrainSeatService.selectByCarriage(date, trainCode, dailyTrainCarriage.getIndex());
            LOG.info("车厢{}的座位数:{}", dailyTrainCarriage.getIndex(), dailyTrainSeatList.size());


//        座位列表
            for (int i = 0; i < dailyTrainSeatList.size(); i++) {
                DailyTrainSeat dailyTrainSeat = dailyTrainSeatList.get(i);
                Integer carriageSeatIndex = dailyTrainSeat.getCarriageSeatIndex();
//                判断column 是否有值，如果有值就对比列号
                String col = dailyTrainSeat.getCol();


//                判断当前座位不能被选中过
                boolean alreadyChooseFlag = false;
                for (DailyTrainSeat trainSeat : finalSeatList) {
//                    finalSeatList中的item 若是已经被选中了
//                    dailyTrainSeat 是当前循环的座位
                    if (trainSeat.getId().equals(dailyTrainSeat.getId())) {
                        alreadyChooseFlag = true;
                        break;
                    }
                }

                if (alreadyChooseFlag) {
                    LOG.info("座位{}被选中过,不能重复选中,继续判断下一个座位", carriageSeatIndex);
                    continue;
                }


                //                这里的column 是外部传入
                if (StrUtil.isBlank(column)) {
                    LOG.info("无选座");
                } else {
//                    如果没有找到对应的列就跳出当前循环
                    if (!column.equals(col)) {
                        LOG.info("座位{}列值不对,继续判断下一个座位,当前列支:{},目标列值:{}", carriageSeatIndex, col, column);
                        continue;
                    }
                }


                boolean isChoose = checkSeatSellState(dailyTrainSeat, startIndex, endIndex);
//                如果已经选中好了，就跳出循环，没有就继续
                if (isChoose) {
                    LOG.info("选中座位");
//                    最终的座位列表 添加座位
                    finalSeatListTemp.add(dailyTrainSeat);

//                    return;
                } else {
//                    LOG.info("未选中座位");
                    continue;
                }


                boolean isGetAllOffsetSeat = true;
//                根据offset选剩下的座位
                if (CollUtil.isNotEmpty(offsetList)) {
                    LOG.info("有偏移值:{}，校验偏移的座位是否可选", offsetList);
//                   从索引1开始，索引0就是当前已选中的票 例如[0,2,4] =>索引0 就是已选中的票
                    for (int j = 1; j < offsetList.size(); j++) {
                        Integer offset = offsetList.get(j);
//                        在库里面的  座位的索引是 从1开始的
//                        int nextIndex = carriageSeatIndex + offset - 1;
//                        i = 当前列表的索引号
                        int nextIndex = offset + i;

//                        有选座时，一定是在同一个车厢，所以索引号不是 不能大于一节车厢的座位数组的长度
                        if (nextIndex >= dailyTrainSeatList.size()) {
                            LOG.info("座位{}不可选,偏移后的索引超出了这个车厢的座位数", nextIndex);
//                            有超过偏离量就不用继续查了，直接跳出for循环


                            isGetAllOffsetSeat = false;
                            break;
                        }


                        DailyTrainSeat nextDailyTrainSeat = dailyTrainSeatList.get(nextIndex);
//                        查询位置是否售卖
                        boolean isChooseNext = checkSeatSellState(nextDailyTrainSeat, startIndex, endIndex);
                        if (isChooseNext) {
                            LOG.info("座位{}被选中", nextDailyTrainSeat.getCarriageSeatIndex());

                            //                    最终的座位列表 添加座位
                            finalSeatListTemp.add(nextDailyTrainSeat);
                        } else {
                            LOG.info("座位{}不可选中", nextDailyTrainSeat.getCarriageSeatIndex());
//                          有一个座位已经被售卖了，不用继续了就直接跳出循环

                            isGetAllOffsetSeat = false;
                            break;

                        }
                    }

                }

//                 如果没有选中就继续这个循环
                if (!isGetAllOffsetSeat) {
//                    没有选中就清空列表
                    finalSeatListTemp = new ArrayList<>();

                    continue;
                }
//     保存选择好的座位
                finalSeatList.addAll(finalSeatListTemp);
                return;
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
            LOG.info("座位{}在本次车站区间{}-{}已售过票,不可选中该座位", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);

            return false;
        } else {
//            可选的座位
            LOG.info("座位{}在本次车站区间{}-{}没有售过票,可选中该座位", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);

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
            newSell = StrUtil.fillBefore(newSell, '0', sell.length());

            LOG.info("座位{}被选中，原售票信息:{},车站区间:{}-{},即:{},最终售票信息:{}", dailyTrainSeat.getCarriageSeatIndex(), sell, startIndex, endIndex, currentSell, newSell);
            dailyTrainSeat.setSell(newSell);

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




    /**
     * 查询前面有几个人在排队
     * @param id
     */
    @Override
    public Integer queryLineCount(Long id) {
        ConfirmOrder confirmOrder = confirmOrderMapper.selectByPrimaryKey(id);
        ConfirmOrderStatusEnum statusEnum = EnumUtil.getBy(ConfirmOrderStatusEnum::getCode, confirmOrder.getStatus());
        int result = switch (statusEnum) {
            case PENDING -> 0; // 排队0
            case SUCCESS -> -1; // 成功
            case FAILURE -> -2; // 失败
            case EMPTY -> -3; // 无票
            case CANCEL -> -4; // 取消
            case INIT -> 999; // 需要查表得到实际排队数量
        };

        if (result == 999) {
            // 排在第几位，下面的写法：where a=1 and (b=1 or c=1) 等价于 where (a=1 and b=1) or (a=1 and c=1)
            ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();

//            查询订单状态是 同一车次 创建时间比当前时间的 INIT和PENDING状态的订单

            confirmOrderExample.or().andDateEqualTo(confirmOrder.getDate())
                    .andTrainCodeEqualTo(confirmOrder.getTrainCode())
                    .andCreateTimeLessThan(confirmOrder.getCreateTime())
                    .andStatusEqualTo(ConfirmOrderStatusEnum.INIT.getCode());


            confirmOrderExample.or().andDateEqualTo(confirmOrder.getDate())
                    .andTrainCodeEqualTo(confirmOrder.getTrainCode())
                    .andCreateTimeLessThan(confirmOrder.getCreateTime())
                    .andStatusEqualTo(ConfirmOrderStatusEnum.PENDING.getCode());



            return Math.toIntExact(confirmOrderMapper.countByExample(confirmOrderExample));
        } else {
            return result;
        }
    }
}
