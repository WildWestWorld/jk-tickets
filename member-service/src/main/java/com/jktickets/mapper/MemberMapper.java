package com.jktickets.mapper;

import com.jktickets.domain.Member;
import com.jktickets.domain.MemberExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface MemberMapper {

//    根据某个条件 查询 数量
    long countByExample(MemberExample example);
    //    根据某个条件 删除 数量
    int deleteByExample(MemberExample example);
    //    根据某个主键 删除 数量
    int deleteByPrimaryKey(Long id);
//        插入
    int insert(Member record);
//  插入有值的部分
     int insertSelective(Member record);
//    根据某个条件查找
    List<Member> selectByExample(MemberExample example);
//    根据某个主键查找

    Member selectByPrimaryKey(Long id);

    //    根据某个条件来更新  根据 record 的非空值来 更新
    int updateByExampleSelective(@Param("record") Member record, @Param("example") MemberExample example);
//    根据某个条件来更新
    int updateByExample(@Param("record") Member record, @Param("example") MemberExample example);
    //    根据某个主键来更新  根据 record 的非空值来 更新
    int updateByPrimaryKeySelective(Member record);
    //    根据某个主键来更新
    int updateByPrimaryKey(Member record);
}