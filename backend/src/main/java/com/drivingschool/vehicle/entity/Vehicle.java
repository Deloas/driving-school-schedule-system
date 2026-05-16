package com.drivingschool.vehicle.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 车辆实体 — 对应 vehicle 表
 * <p>
 * 车辆可绑定默认教练（coach_id），排班时默认使用该教练的车辆。
 * 维修中或停用的车辆不可被排班绑定。
 * </p>
 */
@Data
@TableName("vehicle")
public class Vehicle {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 车牌号，唯一 */
    private String plateNumber;

    /** 默认绑定教练ID */
    private Long coachId;

    /** 车型 */
    private String vehicleType;

    /** 状态：NORMAL / MAINTENANCE / STOPPED */
    private String status;

    /** 备注 */
    private String remark;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
