package com.drivingschool.training.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drivingschool.training.dto.TrainingRecordQueryDTO;
import com.drivingschool.training.vo.TrainingRecordVO;

public interface TrainingRecordService {
    /** 学员查询我的练车记录 */
    Page<TrainingRecordVO> myRecords(Long studentId, Integer page, Integer size);
    /** 教练查询练车记录 */
    Page<TrainingRecordVO> coachRecords(Long coachId, Integer page, Integer size);
    /** 管理员查询全部 */
    Page<TrainingRecordVO> adminQuery(TrainingRecordQueryDTO dto);
}
