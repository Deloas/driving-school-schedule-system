package com.drivingschool.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drivingschool.training.entity.TrainingRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TrainingRecordMapper extends BaseMapper<TrainingRecord> {
}
