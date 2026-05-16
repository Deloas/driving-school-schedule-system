package com.drivingschool.reservation.vo;

import lombok.Data;
import java.util.List;

/**
 * 学员预约方案 VO — 包含主教练和调剂推荐
 */
@Data
public class ReservationOptionVO {
    /** 主教练排班方案 */
    private MainCoachOption mainCoachOption;
    /** 可调剂教练列表（主教练满员时推荐） */
    private List<AdjustOption> adjustOptions;

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
        private boolean available;
        private String message;
    }

    @Data
    public static class AdjustOption {
        private Long scheduleId;
        private Long coachId;
        private String coachName;
        private Long vehicleId;
        private String plateNumber;
        private Integer maxStudents;
        private Integer currentStudents;
        private Integer remainCount;
        /** 推荐理由 */
        private String recommendReason;
    }
}
