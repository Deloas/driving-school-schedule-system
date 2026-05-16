package com.drivingschool.coach.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drivingschool.coach.dto.CoachCreateDTO;
import com.drivingschool.coach.dto.CoachQueryDTO;
import com.drivingschool.coach.dto.CoachUpdateDTO;
import com.drivingschool.coach.vo.CoachSimpleVO;
import com.drivingschool.coach.vo.CoachVO;

import java.util.List;

/**
 * 教练业务接口
 */
public interface CoachService {

    /** 分页查询教练 */
    Page<CoachVO> queryPage(CoachQueryDTO dto);

    /** 查询教练详情 */
    CoachVO getById(Long id);

    /** 新增教练 */
    CoachVO create(CoachCreateDTO dto);

    /** 修改教练 */
    CoachVO update(Long id, CoachUpdateDTO dto);

    /** 修改教练状态（NORMAL/LEAVE/STOPPED） */
    void updateStatus(Long id, String status);

    /** 查询可用教练简单列表（用于下拉选择） */
    List<CoachSimpleVO> simpleList();
}
