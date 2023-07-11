package com.jktickets.mapper;

import com.jktickets.domain.TrainCarriage;
import com.jktickets.domain.TrainCarriageExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface TrainCarriageMapper {
    long countByExample(TrainCarriageExample example);

    int deleteByExample(TrainCarriageExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TrainCarriage record);

    int insertSelective(TrainCarriage record);

    List<TrainCarriage> selectByExample(TrainCarriageExample example);

    TrainCarriage selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TrainCarriage record, @Param("example") TrainCarriageExample example);

    int updateByExample(@Param("record") TrainCarriage record, @Param("example") TrainCarriageExample example);

    int updateByPrimaryKeySelective(TrainCarriage record);

    int updateByPrimaryKey(TrainCarriage record);
}