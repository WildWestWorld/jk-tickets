package com.jktickets.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
    int countNum();
}