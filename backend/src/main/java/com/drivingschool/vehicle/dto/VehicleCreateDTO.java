package com.drivingschool.vehicle.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VehicleCreateDTO {

    @NotBlank(message = "车牌号不能为空")
    private String plateNumber;

    /** 默认绑定教练ID，可选 */
    private Long coachId;

    /** 车型，默认 C1 */
    private String vehicleType = "C1";

    private String remark;
}
