import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserRole, LoginResponse } from '@/types/auth'
import { loginApi, getCurrentUserApi } from '@/api/auth'

/**
 * 认证状态管理 — M2 阶段：真实登录 + Token 管理
 * <p>
 * 登录成功后 Token 存储到 localStorage，页面刷新后从 localStorage 恢复，
 * 并通过 /api/auth/me 验证 Token 有效性。
 * </p>
 */
export const useAuthStore = defineStore('auth', () => {
  // ==================== 状态 ====================

  /** JWT Token */
  const token = ref<string | null>(localStorage.getItem('token'))

  /** 当前用户角色 */
  const role = ref<UserRole | null>(
    (localStorage.getItem('role') as UserRole) || null
  )

  /** 用户 ID */
  const userId = ref<number | null>(
    localStorage.getItem('userId') ? Number(localStorage.getItem('userId')) : null
  )

  /** 用户名 */
  const username = ref<string | null>(localStorage.getItem('username'))

  /** 关联业务 ID（学员 ID 或教练 ID） */
  const relatedId = ref<number | null>(
    localStorage.getItem('relatedId') ? Number(localStorage.getItem('relatedId')) : null
  )

  // ==================== 计算属性 ====================

  /** 是否已登录（有 Token 即为已登录） */
  const isLoggedIn = computed(() => !!token.value)

  /** 是否为管理员 */
  const isAdmin = computed(() => role.value === 'ADMIN')

  /** 是否为教练 */
  const isCoach = computed(() => role.value === 'COACH')

  /** 是否为学员 */
  const isStudent = computed(() => role.value === 'STUDENT')

  // ==================== 方法 ====================

  /**
   * 用户登录
   * <p>
   * 调用后端登录接口，成功后将用户信息持久化到 localStorage，
   * 确保页面刷新后仍能保持登录状态。
   * </p>
   *
   * @param loginUsername 登录账号
   * @param password      密码
   * @returns 登录成功返回 true，失败抛出异常
   */
  async function login(loginUsername: string, password: string) {
    const res = await loginApi(loginUsername, password)
    const data: LoginResponse = res.data.data

    // 持久化到 localStorage，页面刷新后恢复
    token.value = data.token
    userId.value = data.userId
    username.value = data.username
    role.value = data.role
    relatedId.value = data.relatedId

    localStorage.setItem('token', data.token)
    localStorage.setItem('userId', String(data.userId))
    localStorage.setItem('username', data.username)
    localStorage.setItem('role', data.role)
    if (data.relatedId !== null) {
      localStorage.setItem('relatedId', String(data.relatedId))
    }
  }

  /**
   * 刷新用户信息
   * <p>
   * 页面刷新时调用 /api/auth/me 验证 Token 是否有效。
   * 如果 Token 已过期或无 Token，清除本地状态。
   * </p>
   */
  async function refreshUserInfo() {
    if (!token.value) return
    try {
      const res = await getCurrentUserApi()
      const data = res.data.data
      role.value = data.role
      username.value = data.username
      relatedId.value = data.relatedId
    } catch {
      // Token 无效或过期，清除登录状态
      logout()
    }
  }

  /**
   * 退出登录
   * <p>
   * 清除内存状态和 localStorage 中的所有认证信息。
   * </p>
   */
  function logout() {
    token.value = null
    userId.value = null
    username.value = null
    role.value = null
    relatedId.value = null

    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    localStorage.removeItem('role')
    localStorage.removeItem('relatedId')
  }

  return {
    token,
    role,
    userId,
    username,
    relatedId,
    isLoggedIn,
    isAdmin,
    isCoach,
    isStudent,
    login,
    logout,
    refreshUserInfo,
  }
})
