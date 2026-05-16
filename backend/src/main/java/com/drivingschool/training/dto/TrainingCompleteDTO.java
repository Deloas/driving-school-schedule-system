package com.drivingschool.training.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** 教练完成练车请求 — 新增 @Valid 校验 */
@Data
public class TrainingCompleteDTO {

    @NotBlank(message = "练车内容不能为空")
    private String trainingContent;

    @NotNull(message = "练车时长不能为空")
    @Min(value = 1, message = "练车时长必须大于0")
    private Integer durationMinutes;

    /** 教练备注，可为空 */
    private String coachRemark;
}
