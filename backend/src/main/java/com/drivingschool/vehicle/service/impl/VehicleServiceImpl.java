package com.drivingschool.vehicle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drivingschool.coach.entity.Coach;
import com.drivingschool.coach.mapper.CoachMapper;
import com.drivingschool.common.exception.BusinessException;
import com.drivingschool.vehicle.dto.VehicleCreateDTO;
import com.drivingschool.vehicle.dto.VehicleQueryDTO;
import com.drivingschool.vehicle.dto.VehicleUpdateDTO;
import com.drivingschool.vehicle.entity.Vehicle;
import com.drivingschool.vehicle.mapper.VehicleMapper;
import com.drivingschool.vehicle.service.VehicleService;
import com.drivingschool.vehicle.vo.VehicleSimpleVO;
import com.drivingschool.vehicle.vo.VehicleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 车辆业务实现
 */
@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleMapper vehicleMapper;
    private final CoachMapper coachMapper;

    @Override
    public Page<VehicleVO> queryPage(VehicleQueryDTO dto) {
        LambdaQueryWrapper<Vehicle> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.like(Vehicle::getPlateNumber, dto.getKeyword());
        }
        if (dto.getCoachId() != null) {
            wrapper.eq(Vehicle::getCoachId, dto.getCoachId());
        }
        if (StringUtils.hasText(dto.getStatus())) {
            wrapper.eq(Vehicle::getStatus, dto.getStatus());
        }
        wrapper.orderByDesc(Vehicle::getCreateTime);

        Page<Vehicle> page = vehicleMapper.selectPage(
                new Page<>(dto.getPage(), dto.getSize()), wrapper);

        Page<VehicleVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(this::toVO).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public VehicleVO getById(Long id) {
        return toVO(findVehicle(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VehicleVO create(VehicleCreateDTO dto) {
        // 校验车牌号是否重复
        checkPlateDuplicate(dto.getPlateNumber(), null);

        // 如果绑定了教练，校验教练是否存在且状态正常
        if (dto.getCoachId() != null) {
            validateCoach(dto.getCoachId());
        }

        Vehicle vehicle = new Vehicle();
        BeanUtils.copyProperties(dto, vehicle);
        vehicle.setStatus("NORMAL");
        vehicleMapper.insert(vehicle);

        return toVO(vehicle);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VehicleVO update(Long id, VehicleUpdateDTO dto) {
        Vehicle vehicle = findVehicle(id);

        // 校验车牌号是否与其他车辆重复
        checkPlateDuplicate(dto.getPlateNumber(), id);

        // 如果更换了绑定教练，校验新教练
        if (dto.getCoachId() != null && !dto.getCoachId().equals(vehicle.getCoachId())) {
            validateCoach(dto.getCoachId());
        }

        BeanUtils.copyProperties(dto, vehicle, "id", "status", "createTime", "updateTime");
        vehicleMapper.updateById(vehicle);

        return toVO(vehicle);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, String status) {
        if (!List.of("NORMAL", "MAINTENANCE", "STOPPED").contains(status)) {
            throw new BusinessException("无效的车辆状态：" + status);
        }
        Vehicle vehicle = findVehicle(id);
        vehicle.setStatus(status);
        vehicleMapper.updateById(vehicle);
    }

    @Override
    public List<VehicleSimpleVO> simpleList() {
        return vehicleMapper.selectList(
                        new LambdaQueryWrapper<Vehicle>()
                                .eq(Vehicle::getStatus, "NORMAL")
                                .orderByAsc(Vehicle::getPlateNumber))
                .stream()
                .map(v -> {
                    VehicleSimpleVO vo = new VehicleSimpleVO();
                    vo.setId(v.getId());
                    vo.setPlateNumber(v.getPlateNumber());
                    vo.setStatus(v.getStatus());
                    return vo;
                })
                .collect(Collectors.toList());
    }

    // ==================== 内部方法 ====================

    private Vehicle findVehicle(Long id) {
        Vehicle v = vehicleMapper.selectById(id);
        if (v == null) {
            throw new BusinessException("车辆不存在");
        }
        return v;
    }

    /** 校验教练是否存在且为正常状态 */
    private void validateCoach(Long coachId) {
        Coach coach = coachMapper.selectById(coachId);
        if (coach == null) {
            throw new BusinessException("绑定的教练不存在");
        }
        if (!"NORMAL".equals(coach.getStatus())) {
            throw new BusinessException("绑定的教练当前不在职，无法绑定车辆");
        }
    }

    private void checkPlateDuplicate(String plateNumber, Long excludeId) {
        LambdaQueryWrapper<Vehicle> wrapper = new LambdaQueryWrapper<Vehicle>()
                .eq(Vehicle::getPlateNumber, plateNumber);
        if (excludeId != null) {
            wrapper.ne(Vehicle::getId, excludeId);
        }
        Long count = vehicleMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("该车牌号已存在");
        }
    }

    private VehicleVO toVO(Vehicle v) {
        VehicleVO vo = new VehicleVO();
        BeanUtils.copyProperties(v, vo);
        if (v.getCoachId() != null) {
            Coach coach = coachMapper.selectById(v.getCoachId());
            vo.setCoachName(coach != null ? coach.getName() : "未绑定");
        }
        return vo;
    }
}
