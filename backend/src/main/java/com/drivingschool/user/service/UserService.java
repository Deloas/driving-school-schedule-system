package com.drivingschool.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drivingschool.user.dto.ResetPasswordDTO;
import com.drivingschool.user.dto.UserCreateDTO;
import com.drivingschool.user.dto.UserQueryDTO;
import com.drivingschool.user.vo.UserVO;

public interface UserService {
    Page<UserVO> queryPage(UserQueryDTO dto);
    UserVO create(UserCreateDTO dto);
    String resetPassword(Long userId, ResetPasswordDTO dto);
    void updateStatus(Long userId, String status);
}
