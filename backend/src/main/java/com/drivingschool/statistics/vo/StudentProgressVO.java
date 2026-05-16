package com.drivingschool.statistics.vo;

import lombok.Data;

/** 学员练车进度分布 */
@Data
public class StudentProgressVO {
    private String rangeName;
    private long studentCount;
}
