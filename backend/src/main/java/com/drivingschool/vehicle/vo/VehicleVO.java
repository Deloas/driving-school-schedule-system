package com.drivingschool.vehicle.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VehicleVO {
    private Long id;
    private String plateNumber;
    private Long coachId;
    /** 绑定教练姓名 */
    private String coachName;
    private String vehicleType;
    private String status;
    private String remark;
    private LocalDateTime createTime;
}
