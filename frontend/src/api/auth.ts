import request from './request'
import type { ApiResult } from '@/types/common'
import type { LoginResponse, UserInfo } from '@/types/auth'

/**
 * 认证相关 API 封装
 * M2 阶段：登录 + 获取当前用户信息
 */

/**
 * 用户登录
 *
 * @param username 登录账号
 * @param password 登录密码
 * @returns LoginResponse（token + userId + username + role + relatedId）
 */
export function loginApi(username: string, password: string) {
  return request.post<ApiResult<LoginResponse>>('/auth/login', {
    username,
    password,
  })
}

/**
 * 获取当前登录用户信息
 * 用于页面刷新后验证 Token 是否有效和获取最新用户状态
 */
export function getCurrentUserApi() {
  return request.get<ApiResult<UserInfo>>('/auth/me')
}
