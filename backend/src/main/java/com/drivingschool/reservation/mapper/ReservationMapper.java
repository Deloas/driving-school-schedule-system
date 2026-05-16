package com.drivingschool.reservation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drivingschool.reservation.entity.Reservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 预约 Mapper
 * <p>
 * 提供条件更新方法，用于并发安全地增减排班的 current_students。
 * </p>
 */
@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {

    /**
     * 条件增加 current_students — 只有排班状态为 OPEN 且未满员时才执行
     * @return 影响行数（0 表示满员或排班不可预约）
     */
    @Update("UPDATE coach_schedule SET current_students = current_students + 1 " +
            "WHERE id = #{scheduleId} AND status = 'OPEN' AND current_students < max_students")
    int increaseCurrentStudents(@Param("scheduleId") Long scheduleId);

    /**
     * 条件减少 current_students — 确保不会减到负数
     */
    @Update("UPDATE coach_schedule SET current_students = current_students - 1 " +
            "WHERE id = #{scheduleId} AND current_students > 0")
    int decreaseCurrentStudents(@Param("scheduleId") Long scheduleId);
}
