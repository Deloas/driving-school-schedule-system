package com.drivingschool.coach.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drivingschool.coach.entity.Coach;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CoachMapper extends BaseMapper<Coach> {
}
