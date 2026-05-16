package com.drivingschool.training.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TrainingRecordVO {
    private Long id;
    private Long reservationId;
    private Long studentId;
    private String studentName;
    private Long coachId;
    private String coachName;
    private Long vehicleId;
    private String plateNumber;
    private LocalDate trainingDate;
    private String timeSlot;
    private String trainingContent;
    private String result;
    private String coachComment;
    private LocalDateTime createTime;
}
