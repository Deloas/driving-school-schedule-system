package com.drivingschool.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drivingschool.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户 Mapper — MyBatis-Plus BaseMapper 提供通用 CRUD
 * <p>
 * 无需写 XML，BaseMapper 已提供 insert/update/delete/selectById/selectList 等方法。
 * 后续如需复杂查询，可在 Mapper XML 中扩展。
 * </p>
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
