import axios from 'axios'
import type { ApiResult } from '@/types/common'

/**
 * Axios 实例 — M2 阶段：添加 JWT Token 拦截
 * <p>
 * 请求拦截器：自动从 localStorage 读取 Token 并附加到 Authorization 头。
 * 响应拦截器：Token 过期或无效时自动跳转到登录页。
 * </p>
 */

const request = axios.create({
  // 后端 API 基础路径（开发环境通过 Vite proxy 转发到 localhost:28081）
  baseURL: '/api',
  // 请求超时时间
  timeout: 10000,
  // 跨域请求时携带 Cookie
  withCredentials: true,
})

/**
 * 请求拦截器 — 自动附加 JWT Token
 * <p>
 * 每次请求前从 localStorage 读取 Token，如果存在则设置 Authorization 头。
 * 后端 JwtAuthenticationFilter 从此头中提取并校验 Token。
 * </p>
 */
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

/**
 * 响应拦截器 — 统一处理认证错误
 * <p>
 * 401：Token 过期或无效 → 清除本地登录状态并跳转到登录页。
 * 403：无权限访问 → 提示用户。
 * 500：服务器内部错误 → 提示用户。
 * </p>
 */
request.interceptors.response.use(
  (response) => {
    // 后端统一返回 { code, message, data }，code !== 200 视为业务失败
    const body = response.data
    if (body && typeof body.code === 'number' && body.code !== 200) {
      const err = new Error(body.message || '请求失败')
      ;(err as any).response = response
      return Promise.reject(err)
    }
    return response
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response
      switch (status) {
        case 401:
          localStorage.clear()
          window.location.href = '/login'
          console.warn('登录已过期，请重新登录')
          break
        case 403:
          console.warn('无权限访问：' + (data?.message || ''))
          break
        case 500:
          console.error('服务器内部错误：' + (data?.message || ''))
          break
      }
    }
    return Promise.reject(error)
  }
)

export default request
