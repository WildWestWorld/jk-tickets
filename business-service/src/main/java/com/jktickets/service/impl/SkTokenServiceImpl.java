package com.jktickets.service.impl;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.domain.SkToken;
import com.jktickets.domain.SkTokenExample;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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

        long stationCount = dailyTrainStationService.countByTrainCode(trainCode);
        LOG.info("车次[{}] 到站数:{}",trainCode,seatCount);


//        3/4 需要根据实际买票比例来定，一趟火车最多可以卖(seatCount *stationCount)个座位
        int count = (int)(seatCount * stationCount * 3 / 4);
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
        LOG.info("会员[{}] 获取日期[{}] 车次[{}] 的令牌开始",memberId,DateUtil.formatDate(date));
        int updateCount = skTokenMapperCustom.decrease(date, trainCode);
        if(updateCount>0){
            return true;
        }else {
            return false;
        }


    }
}
