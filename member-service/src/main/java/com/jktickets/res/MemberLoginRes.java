package com.jktickets.res;

import lombok.Data;

//该实体 是与表结构一一对应
@Data
public class MemberLoginRes {
    private Long id;

    private String mobile;
    private String token;



}