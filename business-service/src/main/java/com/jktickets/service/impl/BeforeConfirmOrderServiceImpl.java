package com.jktickets.service.impl;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.domain.ConfirmOrder;
import com.jktickets.enums.ConfirmOrderStatusEnum;
import com.jktickets.enums.RedisKeyPreEnum;
import com.jktickets.enums.RocketMQTopicEnum;
import com.jktickets.exception.BusinessException;
import com.jktickets.exception.BusinessExceptionEnum;
import com.jktickets.feign.MemberFeign;
import com.jktickets.mapper.ConfirmOrderMapper;
import com.jktickets.mapper.DailyTrainSeatMapper;
import com.jktickets.mapper.custom.DailyTrainTicketMapperCustom;

import com.alibaba.fastjson.JSON;

import com.jktickets.req.confirmOrder.ConfirmOrderDoReq;
import com.jktickets.service.BeforeConfirmOrderService;

import com.jktickets.service.SkTokenService;
import com.jktickets.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;


@Service
public class BeforeConfirmOrderServiceImpl implements BeforeConfirmOrderService {

    private final static Logger LOG = LoggerFactory.getLogger(BeforeConfirmOrderService.class);

    @Resource
    SkTokenService skTokenService;

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedissonClient redissonClient;

    @Resource
    ConfirmOrderMapper confirmOrderMapper;

    @Resource
//            SpringBoot3 可能不认rocketMQTemplate
//            rocketMQ里面 用了spring.factor 现在SpringBoot3移除了
//            解决方法:
    RocketMQTemplate rocketMQTemplate;

    @Override
    @SentinelResource(value = "beforeDoConfirm", blockHandler = "beforeDoConfirmBlock")
    public void beforeDoConfirm(ConfirmOrderDoReq req) {

//        解决MQ中拿不到 用户ID的问题
        req.setMemberId(LoginMemberContext.getId());


        //        拿令牌
        boolean validSkToken = skTokenService.validSkToken(req.getDate(), req.getTrainCode(), LoginMemberContext.getId());
        if (validSkToken) {
            LOG.info("令牌校验通过");
        } else {
            LOG.info("令牌校验不通过");
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_TOKEN_FAIL);
        }


//        车次时间+车次号来认定车次的票
//        String lockKey = RedisKeyPreEnum.CONFIRM_ORDER + "-" + DateUtil.formatDate(req.getDate()) + "-" + req.getTrainCode();

//        Redis分布式锁 现在改为看门狗锁
//        替换原因：可能会卡，卡了就导致 时间过长，锁就消失了


//        setIfAbsent 如果该值不存在 就往里面set
//        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 5, TimeUnit.SECONDS);


//        if(Boolean.TRUE.equals(setIfAbsent)){
//            LOG.info("恭喜，抢到锁了");
//        }else {
//            LOG.info("很遗憾，没抢到锁");
//            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
//
//        }

//        RLock lock = null;

        try {
//        使用redisson 自带看门狗
//            lock = redissonClient.getLock(lockKey);
//
//
////            红锁
////            红锁，Redis 主从服务器 半数以上拿到锁就是拿到锁了
////            RedissonRedLock redissonRedLock = new RedissonRedLock(lock, lock);
////            boolean tryLock1 = redissonRedLock.tryLock(0, TimeUnit.SECONDS);
//
////        不带看门狗
////        lock.tryLock(0,10,TimeUnit.SECONDS);
//
////        自带看门狗
////            waitTime最长阻塞时间
////            leaseTime 拿到锁时 锁的时长
//            //        lock.tryLock(waitTime,leaseTime,时间单位);
//
//            boolean tryLock = lock.tryLock(0, TimeUnit.SECONDS);
//            if (tryLock) {
//                LOG.info("恭喜，抢到锁了");
////            测试锁
////                for(int i= 0;i<30;i++){
////                    Long expire = redisTemplate.opsForValue().getOperations().getExpire(lockKey);
////                    LOG.info("锁过期时间还有:{}",expire);
////                    Thread.sleep(1000);
////                }
//
//            } else {
//                LOG.info("很遗憾，没抢到锁");
//                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
//
//            }



            // 保存确认订单表，状态初始
            DateTime nowTime = DateTime.now();

            ConfirmOrder confirmOrder = new ConfirmOrder();
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
//            confirmOrder.setMemberId(LoginMemberContext.getId());

            confirmOrder.setMemberId(req.getMemberId());


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


//            发送MQ排队购票
            String reqJson = JSON.toJSONString(req);
            LOG.info("排队购票,发送mq开始,消息:{}",reqJson);
            rocketMQTemplate.convertAndSend(RocketMQTopicEnum.CONFIRM_ORDER.getCode(),reqJson);
            LOG.info("排队购票，发送mq结束");


        }
//        catch (InterruptedException e) {
//            LOG.error("购票异常", e);
//        }

        finally {
            //       购票成功，删除redis 分布式锁
            LOG.info("购票流程结束，释放锁");
//            redisTemplate.delete(lockKey);
//            isHeldByCurrentThread 判断是否是 当前线程
//            if (lock != null && lock.isHeldByCurrentThread()) {
//                lock.unlock();
//            }
        }
    }



    //    sentinel 降级方法
    public void beforeDoConfirmBlock(ConfirmOrderDoReq req, BlockException e) {
        LOG.info("购票请求被限流:{}", req);
        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
    }
}