package com.drivingschool.reservation.vo;

import lombok.Data;

/**
 * 学员预约方案 VO — 返回主教练排班可预约状态
 */
@Data
public class ReservationOptionVO {
    /** 主教练排班方案 */
    private MainCoachOption mainCoachOption;

    @Data
    public static class MainCoachOption {
        private Long scheduleId;
        private Long coachId;
        private String coachName;
        private Long vehicleId;
        private String plateNumber;
        private String scheduleDate;
        private String timeSlot;
        private Integer maxStudents;
        private Integer currentStudents;
        private Integer remainCount;
        /** 是否可预约 */
        private boolean available;
        /** 提示信息 */
        private String message;
    }
}
