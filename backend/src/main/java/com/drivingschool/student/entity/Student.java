package com.drivingschool.student.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学员实体 — 对应 student 表
 * <p>
 * 每个学员归属于一个主教练（main_coach_id），练车次数由预约完成后自动累加。
 * required_training_count 表示应完成的练车总次数，completed_training_count 表示已完成次数。
 * </p>
 */
@Data
@TableName("student")
public class Student {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 学员姓名 */
    private String name;

    /** 手机号 */
    private String phone;

    /** 性别 */
    private String gender;

    /** 主教练ID */
    private Long mainCoachId;

    /** 训练科目：SUBJECT_2 / SUBJECT_3 */
    private String subjectType;

    /** 应完成练车次数 */
    private Integer requiredTrainingCount;

    /** 已完成练车次数 */
    private Integer completedTrainingCount;

    /** 状态：NORMAL / STOPPED */
    private String status;

    /** 备注 */
    private String remark;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
