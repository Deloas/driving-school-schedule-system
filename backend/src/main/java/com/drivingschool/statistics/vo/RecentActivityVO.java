package com.drivingschool.statistics.vo;

import lombok.Data;

/** 最近动态 */
@Data
public class RecentActivityVO {
    private String type;
    private String title;
    private String description;
    private String time;
}
