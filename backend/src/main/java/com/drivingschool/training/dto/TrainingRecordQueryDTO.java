package com.drivingschool.training.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TrainingRecordQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
    private String studentName;
    private Long coachId;
    private LocalDate startDate;
    private LocalDate endDate;
}
