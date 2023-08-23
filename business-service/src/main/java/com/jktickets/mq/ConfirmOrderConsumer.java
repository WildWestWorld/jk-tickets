package com.jktickets.mq;// package com.jiawa.train.business.mq;
import com.alibaba.fastjson.JSON;

import com.jktickets.dto.ConfirmOrderMQDto;
import com.jktickets.req.confirmOrder.ConfirmOrderDoReq;
import com.jktickets.service.ConfirmOrderService;
 import jakarta.annotation.Resource;
 import org.apache.rocketmq.common.message.MessageExt;
 import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
 import org.apache.rocketmq.spring.core.RocketMQListener;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.slf4j.MDC;
 import org.springframework.stereotype.Service;

 @Service
 @RocketMQMessageListener(consumerGroup = "default", topic = "CONFIRM_ORDER")
 public class ConfirmOrderConsumer implements RocketMQListener<MessageExt> {

     private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderConsumer.class);

     @Resource
     private ConfirmOrderService confirmOrderService;

     @Override
     public void onMessage(MessageExt messageExt) {
         byte[] body = messageExt.getBody();
//         从mq拿到消息后 消息是JSON字符串的类型，得把他转成正常格式
         ConfirmOrderDoReq confirmOrderDoReq = JSON.parseObject(new String(body), ConfirmOrderDoReq.class);

//        处理 拦截器不拦截没有ID  问题
         MDC.put("LOG_ID",confirmOrderDoReq.getLogId());

         LOG.info("ROCKETMQ收到消息：{}", new String(body));

//         在这里使用方法，拦截器是不会进行拦截的，因为拦截器拦截接口请求的方法

         ConfirmOrderMQDto confirmOrderMQDto = new ConfirmOrderMQDto();
         confirmOrderMQDto.setLogId(confirmOrderDoReq.getLogId());
         confirmOrderMQDto.setDate(confirmOrderDoReq.getDate());
         confirmOrderMQDto.setTrainCode(confirmOrderDoReq.getTrainCode());



         confirmOrderService.doConfirm(confirmOrderMQDto);
     }
 }
