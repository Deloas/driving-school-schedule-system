package com.drivingschool.schedule.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drivingschool.schedule.dto.ScheduleBatchCreateDTO;
import com.drivingschool.schedule.dto.ScheduleCreateDTO;
import com.drivingschool.schedule.dto.ScheduleQueryDTO;
import com.drivingschool.schedule.dto.ScheduleUpdateDTO;
import com.drivingschool.schedule.vo.ScheduleAvailableVO;
import com.drivingschool.schedule.vo.ScheduleVO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CoachScheduleService {
    Page<ScheduleVO> queryPage(ScheduleQueryDTO dto);
    ScheduleVO getById(Long id);
    ScheduleVO create(ScheduleCreateDTO dto);
    ScheduleVO update(Long id, ScheduleUpdateDTO dto);
    void updateStatus(Long id, String status);
    /** 批量生成排班，返回 { created: 成功数, skipped: 跳过数 } */
    Map<String, Integer> batchCreate(ScheduleBatchCreateDTO dto);
    /** 查询可预约排班（M5 学员预约使用） */
    List<ScheduleAvailableVO> availableList(LocalDate date, String timeSlot);
}
