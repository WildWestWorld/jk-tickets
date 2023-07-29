package com.jktickets.service.impl;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.domain.Ticket;
import com.jktickets.domain.TicketExample;
import com.jktickets.mapper.TicketMapper;

import com.jktickets.req.MemberTicketReq;
import com.jktickets.req.ticket.TicketQueryReq;
import com.jktickets.req.ticket.TicketSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.ticket.TicketQueryRes;

import com.jktickets.service.TicketService;

import com.jktickets.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketServiceImpl implements TicketService {

    private final static Logger LOG = LoggerFactory.getLogger(TicketService.class);

    @Resource
    TicketMapper ticketMapper;

    @Override
    public void saveTicket(MemberTicketReq req) {


        DateTime nowTime  = DateTime.now();


        Ticket ticket = BeanUtil.copyProperties(req, Ticket.class);


            //        从 线程中获取数据
//          ticket.setMemberId(LoginMemberContext.getId());
            ticket.setId(SnowUtil.getSnowflakeNextId());
            ticket.setCreateTime(nowTime);
            ticket.setUpdateTime(nowTime);

            ticketMapper.insert(ticket);




    }

    @Override
    public PageRes<TicketQueryRes> queryTicketList(TicketQueryReq req) {
        TicketExample ticketExample = new TicketExample();
        TicketExample.Criteria criteria = ticketExample.createCriteria();
        if(ObjectUtil.isNotNull(req.getMemberId())){
            criteria.andMemberIdEqualTo(req.getMemberId());
        }

        // 分页处理
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Ticket> ticketList = ticketMapper.selectByExample(ticketExample);


        PageInfo<Ticket> ticketPageInfo = new PageInfo<>(ticketList);

        LOG.info("总行数：{}", ticketPageInfo.getTotal());
        LOG.info("总页数：{}", ticketPageInfo.getPages());

//  转成Controller的传输类
        List<TicketQueryRes> ticketQueryResList = BeanUtil.copyToList(ticketList, TicketQueryRes.class);

        PageRes<TicketQueryRes> pageRes = new PageRes<>();
        pageRes.setList(ticketQueryResList);
        pageRes.setTotal(ticketPageInfo.getTotal());
        return pageRes;
    }


    @Override
    public void deleteById(Long id) {
        ticketMapper.deleteByPrimaryKey(id);
    }
}
