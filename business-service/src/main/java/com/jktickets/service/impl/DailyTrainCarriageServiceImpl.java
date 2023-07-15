package com.jktickets.service.impl;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.domain.DailyTrainCarriage;
import com.jktickets.domain.DailyTrainCarriageExample;
import com.jktickets.enums.SeatColEnum;
import com.jktickets.mapper.DailyTrainCarriageMapper;

import com.jktickets.req.dailyTrainCarriage.DailyTrainCarriageQueryReq;
import com.jktickets.req.dailyTrainCarriage.DailyTrainCarriageSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.dailyTrainCarriage.DailyTrainCarriageQueryRes;

import com.jktickets.service.DailyTrainCarriageService;

import com.jktickets.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyTrainCarriageServiceImpl implements DailyTrainCarriageService {

    private final static Logger LOG = LoggerFactory.getLogger(DailyTrainCarriageService.class);

    @Resource
    DailyTrainCarriageMapper dailyTrainCarriageMapper;

    @Override
    public void saveDailyTrainCarriage(DailyTrainCarriageSaveReq req) {
        DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(req, DailyTrainCarriage.class);


        DateTime nowTime  = DateTime.now();


        // 自动计算出列数和总座位数
        //获取 座位类型对应的枚举
        List<SeatColEnum> seatColEnums = SeatColEnum.getColsByType(req.getSeatType());
//      通过枚举类型的长度 来设置列数
        req.setColCount(seatColEnums.size());
//        计算出座位数量( 枚举类型的长度 * 行数)
        req.setSeatCount(req.getColCount() * req.getRowCount());

        if(ObjectUtil.isNull(dailyTrainCarriage.getId())){
            //        从 线程中获取数据
//          dailyTrainCarriage.setMemberId(LoginMemberContext.getId());
            dailyTrainCarriage.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainCarriage.setCreateTime(nowTime);
            dailyTrainCarriage.setUpdateTime(nowTime);

            dailyTrainCarriageMapper.insert(dailyTrainCarriage);
        }else{
            dailyTrainCarriage.setUpdateTime(nowTime);
            dailyTrainCarriageMapper.updateByPrimaryKeySelective(dailyTrainCarriage);
        }



    }

    @Override
    public PageRes<DailyTrainCarriageQueryRes> queryDailyTrainCarriageList(DailyTrainCarriageQueryReq req) {
        DailyTrainCarriageExample dailyTrainCarriageExample = new DailyTrainCarriageExample();
        dailyTrainCarriageExample.setOrderByClause("date desc, train_code asc, `index` asc");
        DailyTrainCarriageExample.Criteria criteria = dailyTrainCarriageExample.createCriteria();
        if (ObjUtil.isNotNull(req.getDate())) {
            criteria.andDateEqualTo(req.getDate());
        }
        if (ObjUtil.isNotEmpty(req.getTrainCode())) {
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }

        // 分页处理
        PageHelper.startPage(req.getPage(), req.getSize());
        List<DailyTrainCarriage> dailyTrainCarriageList = dailyTrainCarriageMapper.selectByExample(dailyTrainCarriageExample);


        PageInfo<DailyTrainCarriage> dailyTrainCarriagePageInfo = new PageInfo<>(dailyTrainCarriageList);

        LOG.info("总行数：{}", dailyTrainCarriagePageInfo.getTotal());
        LOG.info("总页数：{}", dailyTrainCarriagePageInfo.getPages());

//  转成Controller的传输类
        List<DailyTrainCarriageQueryRes> dailyTrainCarriageQueryResList = BeanUtil.copyToList(dailyTrainCarriageList, DailyTrainCarriageQueryRes.class);

        PageRes<DailyTrainCarriageQueryRes> pageRes = new PageRes<>();
        pageRes.setList(dailyTrainCarriageQueryResList);
        pageRes.setTotal(dailyTrainCarriagePageInfo.getTotal());
        return pageRes;
    }


    @Override
    public void deleteById(Long id) {
        dailyTrainCarriageMapper.deleteByPrimaryKey(id);
    }
}
