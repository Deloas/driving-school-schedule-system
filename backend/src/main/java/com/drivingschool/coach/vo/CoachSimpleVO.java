package com.drivingschool.coach.vo;

import lombok.Data;

/**
 * 教练简单列表 VO — 用于下拉选择等场景
 */
@Data
public class CoachSimpleVO {
    private Long id;
    private String name;
    private String status;
}
