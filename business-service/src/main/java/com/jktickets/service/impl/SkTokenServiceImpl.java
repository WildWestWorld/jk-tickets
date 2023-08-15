package com.jktickets.service.impl;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jktickets.domain.SkToken;
import com.jktickets.domain.SkTokenExample;
import com.jktickets.enums.RedisKeyPreEnum;
import com.jktickets.exception.BusinessException;
import com.jktickets.exception.BusinessExceptionEnum;
import com.jktickets.mapper.SkTokenMapper;

import com.jktickets.mapper.custom.SkTokenMapperCustom;
import com.jktickets.req.skToken.SkTokenQueryReq;
import com.jktickets.req.skToken.SkTokenSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.skToken.SkTokenQueryRes;

import com.jktickets.service.DailyTrainSeatService;
import com.jktickets.service.DailyTrainStationService;
import com.jktickets.service.SkTokenService;

import com.jktickets.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SkTokenServiceImpl implements SkTokenService {

    private final static Logger LOG = LoggerFactory.getLogger(SkTokenService.class);

    @Resource
    SkTokenMapper skTokenMapper;

    @Resource
    SkTokenMapperCustom skTokenMapperCustom;

    @Resource
    DailyTrainSeatService dailyTrainSeatService;

    @Resource
    DailyTrainStationService dailyTrainStationService;


    @Autowired
    RedissonClient redissonClient;

    @Autowired
    StringRedisTemplate redisTemplate;

    //    初始化
    @Override
    public void genDaily(Date date, String trainCode){
        LOG.info("删除日期[{}] 车次[{}] 的令牌记录", DateUtil.formatDate(date),trainCode);
        SkTokenExample skTokenExample = new SkTokenExample();
        skTokenExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        skTokenMapper.deleteByExample(skTokenExample);

        DateTime now = DateTime.now();
        SkToken skToken = new SkToken();
        skToken.setId(SnowUtil.getSnowflakeNextId());
        skToken.setDate(date);
        skToken.setTrainCode(trainCode);
        skToken.setCreateTime(now);
        skToken.setUpdateTime(now);

        int seatCount = dailyTrainSeatService.countTrainSeat(date, trainCode, null);
        LOG.info("车次[{}] 座位数:{}",trainCode,seatCount);

        long stationCount = dailyTrainStationService.countByTrainCode(date,trainCode);
        LOG.info("车次[{}] 到站数:{}",trainCode,seatCount);


//        3/4 需要根据实际买票比例来定，一趟火车最多可以卖(seatCount *stationCount)个座位
        int count = (int)(seatCount * stationCount );
        LOG.info("车次[{}] 初始生成令牌:{}",trainCode,count);
        skToken.setCount(count);

        skTokenMapper.insert(skToken);
    }



    @Override
    public void saveSkToken(SkTokenSaveReq req) {


        DateTime nowTime  = DateTime.now();


        SkToken skToken = BeanUtil.copyProperties(req, SkToken.class);


        if(ObjectUtil.isNull(skToken.getId())){
            //        从 线程中获取数据
//          skToken.setMemberId(LoginMemberContext.getId());
            skToken.setId(SnowUtil.getSnowflakeNextId());
            skToken.setCreateTime(nowTime);
            skToken.setUpdateTime(nowTime);

            skTokenMapper.insert(skToken);
        }else{
            skToken.setUpdateTime(nowTime);
            skTokenMapper.updateByPrimaryKeySelective(skToken);
        }



    }

    @Override
    public PageRes<SkTokenQueryRes> querySkTokenList(SkTokenQueryReq req) {
        SkTokenExample skTokenExample = new SkTokenExample();
        SkTokenExample.Criteria criteria = skTokenExample.createCriteria();
//        if(ObjectUtil.isNotNull(req.getMemberId())){
//            criteria.andMemberIdEqualTo(req.getMemberId());
//        }

        // 分页处理
        PageHelper.startPage(req.getPage(), req.getSize());
        List<SkToken> skTokenList = skTokenMapper.selectByExample(skTokenExample);


        PageInfo<SkToken> skTokenPageInfo = new PageInfo<>(skTokenList);

        LOG.info("总行数：{}", skTokenPageInfo.getTotal());
        LOG.info("总页数：{}", skTokenPageInfo.getPages());

//  转成Controller的传输类
        List<SkTokenQueryRes> skTokenQueryResList = BeanUtil.copyToList(skTokenList, SkTokenQueryRes.class);

        PageRes<SkTokenQueryRes> pageRes = new PageRes<>();
        pageRes.setList(skTokenQueryResList);
        pageRes.setTotal(skTokenPageInfo.getTotal());
        return pageRes;
    }


    @Override
    public void deleteById(Long id) {
        skTokenMapper.deleteByPrimaryKey(id);
    }


    @Override
    public boolean validSkToken(Date date, String trainCode, Long memberId) {
        LOG.info("会员[{}] 获取日期[{}] 车次[{}] 的令牌开始", memberId, DateUtil.formatDate(date),trainCode);
//        先获取令牌锁，再校验令牌余量
//        防止机器人抢票，localkey就是令牌 用来表示 谁能做什么的一个凭证
//        String lockKey =  RedisKeyPreEnum.SK_TOKEN + "-" +DateUtil.formatDate(date) + "-" + trainCode + "-" + memberId;
//        RLock lock = null;

//        try {
////        使用redisson 自带看门狗
//            lock = redissonClient.getLock(lockKey);
//
//            boolean tryLock = lock.tryLock(0, TimeUnit.SECONDS);
//            if (tryLock) {
//                LOG.info("恭喜，抢到令牌锁了");
//            测试锁
//                for(int i= 0;i<30;i++){
//                    Long expire = redisTemplate.opsForValue().getOperations().getExpire(lockKey);
//                    LOG.info("锁过期时间还有:{}",expire);
//                    Thread.sleep(1000);
//                }

//            } else {
//                LOG.info("很遗憾，没抢到令牌锁! localKey:{}",lockKey);
////                throw new BusinessException(BusinessExceptionEnum.CONFIRM_TOKEN_LOCK_FAIL);
//                return false;
//            }

            String skTokenCountKey =  RedisKeyPreEnum.SK_TOKEN_COUNT + "-" +DateUtil.formatDate(date) + "-" + trainCode + "-" + memberId;
            Object skTokenCount = redisTemplate.opsForValue().get(skTokenCountKey);

            if(skTokenCount != null){
                LOG.info("缓存中有车次令牌大闸的key:{}",skTokenCountKey);
                Long count = redisTemplate.opsForValue().decrement(skTokenCountKey,1);
                if(count <0L){
                    LOG.error("获取令牌失败:{}",skTokenCountKey);
                    return  false;
//                    throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_TOKEN_FAIL);
                }else{
                    LOG.info("获取令牌后，令牌余数:{}",count);
                    redisTemplate.expire(skTokenCountKey,60,TimeUnit.SECONDS);

//                    每获取5个令牌更新一次数据库
                    if(count%5 ==0){
                        skTokenMapperCustom.decrease(date,trainCode,5);
                    }
                    return true;
                }


            }else {
                LOG.info("缓存中没有该车次令牌大闸的key：{}", skTokenCountKey);
//                    查看是否还有令牌
                SkTokenExample skTokenExample = new SkTokenExample();
                skTokenExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
                List<SkToken> tokenCountList = skTokenMapper.selectByExample(skTokenExample);
                if (CollUtil.isEmpty(tokenCountList)) {
                    LOG.info("找不到日期[{}] 车次[{}]的令牌记录", DateUtil.formatDate(date), trainCode);
                    return false;

                }

                SkToken skToken = tokenCountList.get(0);
                if (skToken.getCount() <= 0) {
                    LOG.info("日期[{}]车次[{}]的令牌余量为0", DateUtil.formatDate(date), trainCode);
                    return false;
                }

//                令牌还有余量
                Integer count = skToken.getCount() - 1;
                skToken.setCount(count);
                LOG.info("将该车次令牌放入缓存中,key:{},count:{}",skTokenCountKey,count);
                redisTemplate.opsForValue().set(skTokenCountKey,String.valueOf(count),60,TimeUnit.SECONDS);
                return true;


            }


//            废弃我们加入了二级缓存
//            int updateCount = skTokenMapperCustom.decrease(date, trainCode,1);
//            if (updateCount > 0) {
//                return true;
//            } else {
//                return false;
//            }

//        } catch (InterruptedException e) {
//            LOG.error("获取令牌异常", e);
//        } finally {
//            //       购票成功，删除redis 分布式锁
//            LOG.info("获取令牌流程结束，释放锁");
////            redisTemplate.delete(lockKey);
////            isHeldByCurrentThread 判断是否是 当前线程
//            if (lock != null && lock.isHeldByCurrentThread()) {
//                lock.unlock();
//            }
//        }
//        return false;

    }
}
