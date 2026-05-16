package com.drivingschool.vehicle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drivingschool.vehicle.entity.Vehicle;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VehicleMapper extends BaseMapper<Vehicle> {
}
