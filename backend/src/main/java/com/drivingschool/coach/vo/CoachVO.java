package com.drivingschool.coach.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 教练详情响应 VO
 */
@Data
public class CoachVO {
    private Long id;
    private String name;
    private String phone;
    private String licenseNo;
    private Integer maxStudentsPerHalfDay;
    private String status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
