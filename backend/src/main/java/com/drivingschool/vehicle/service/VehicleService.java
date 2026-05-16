package com.drivingschool.vehicle.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drivingschool.vehicle.dto.VehicleCreateDTO;
import com.drivingschool.vehicle.dto.VehicleQueryDTO;
import com.drivingschool.vehicle.dto.VehicleUpdateDTO;
import com.drivingschool.vehicle.vo.VehicleSimpleVO;
import com.drivingschool.vehicle.vo.VehicleVO;

import java.util.List;

public interface VehicleService {
    Page<VehicleVO> queryPage(VehicleQueryDTO dto);
    VehicleVO getById(Long id);
    VehicleVO create(VehicleCreateDTO dto);
    VehicleVO update(Long id, VehicleUpdateDTO dto);
    void updateStatus(Long id, String status);
    List<VehicleSimpleVO> simpleList();
}
