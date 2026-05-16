/**
 * 认证相关类型定义
 * M1 阶段仅为类型占位，M2 阶段将在此处定义完整的登录请求/响应类型
 */

/** 系统角色 */
export type UserRole = 'ADMIN' | 'COACH' | 'STUDENT'

/** 当前登录用户信息 */
export interface UserInfo {
  /** 用户 ID（sys_user.id） */
  userId: number
  /** 登录账号 */
  username: string
  /** 角色 */
  role: UserRole
  /** 关联业务 ID（学员 ID 或教练 ID） */
  relatedId: number | null
}

/** 登录响应 */
export interface LoginResponse {
  /** JWT Token */
  token: string
  /** 用户 ID */
  userId: number
  /** 登录账号 */
  username: string
  /** 角色 */
  role: UserRole
  /** 关联业务 ID */
  relatedId: number | null
}
