package com.drivingschool.vehicle.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VehicleUpdateDTO {

    @NotBlank(message = "车牌号不能为空")
    private String plateNumber;

    private Long coachId;
    private String vehicleType;
    private String remark;
}
