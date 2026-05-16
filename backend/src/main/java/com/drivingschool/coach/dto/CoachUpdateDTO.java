package com.drivingschool.coach.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 修改教练请求 DTO
 */
@Data
public class CoachUpdateDTO {

    @NotBlank(message = "教练姓名不能为空")
    private String name;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    private String licenseNo;

    @Min(value = 1, message = "最大带练人数必须大于0")
    private Integer maxStudentsPerHalfDay;

    private String remark;
}
