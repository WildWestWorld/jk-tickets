package com.jktickets.res.passenger;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;
@Data
public class PassengerQueryRes {

//   @JsonSerialize(using= ToStringSerializer.class) 发给前端时转成String类型 解决前端精度问题
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using= ToStringSerializer.class)
    private Long memberId;

    private String name;

    private String idCard;

    private String type;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;
}
