package com.drivingschool.vehicle.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drivingschool.common.result.Result;
import com.drivingschool.common.utils.SecurityContextUtils;
import com.drivingschool.vehicle.dto.VehicleCreateDTO;
import com.drivingschool.vehicle.dto.VehicleQueryDTO;
import com.drivingschool.vehicle.dto.VehicleUpdateDTO;
import com.drivingschool.vehicle.service.VehicleService;
import com.drivingschool.vehicle.vo.VehicleSimpleVO;
import com.drivingschool.vehicle.vo.VehicleVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 车辆管理控制器
 */
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    /** 分页查询车辆 — 教练只能看绑定到自己的 */
    @GetMapping
    public Result<Page<VehicleVO>> list(VehicleQueryDTO dto) {
        if ("COACH".equals(SecurityContextUtils.getCurrentUserRole())) {
            dto.setCoachId(SecurityContextUtils.getCurrentUser().getRelatedId());
        }
        return Result.success(vehicleService.queryPage(dto));
    }

    @GetMapping("/simple")
    public Result<List<VehicleSimpleVO>> simpleList() {
        return Result.success(vehicleService.simpleList());
    }

    @GetMapping("/{id}")
    public Result<VehicleVO> detail(@PathVariable Long id) {
        return Result.success(vehicleService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<VehicleVO> create(@Valid @RequestBody VehicleCreateDTO dto) {
        return Result.success("新增车辆成功", vehicleService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<VehicleVO> update(@PathVariable Long id, @Valid @RequestBody VehicleUpdateDTO dto) {
        return Result.success("修改车辆成功", vehicleService.update(id, dto));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        vehicleService.updateStatus(id, body.get("status"));
        return Result.success("状态修改成功");
    }
}
