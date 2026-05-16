package com.drivingschool.coach.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 教练实体 — 对应 coach 表
 * <p>
 * 存储教练基本信息，包含默认每半天最大带练人数。
 * 排班时以 max_students_per_half_day 为默认上限，管理员可针对特定排班调整。
 * </p>
 */
@Data
@TableName("coach")
public class Coach {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 教练姓名 */
    private String name;

    /** 手机号 */
    private String phone;

    /** 教练证号 */
    private String licenseNo;

    /** 默认每半天最多带练人数 */
    private Integer maxStudentsPerHalfDay;

    /** 状态：NORMAL / LEAVE / STOPPED */
    private String status;

    /** 备注 */
    private String remark;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
