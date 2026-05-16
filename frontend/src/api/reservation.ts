import request from './request'
import type { ApiResult, PageResult } from '@/types/common'
import type { Reservation, ReservationOption, ReservationQuery } from '@/types/reservation'

/** 查询可预约方案 */
export function getReservationOptions(date: string, timeSlot: string) {
  return request.get<ApiResult<ReservationOption>>('/reservations/options', { params: { date, timeSlot } })
}

/** 创建预约（仅学员） — M7 支持调剂 */
export function createReservation(scheduleId: number, adjusted = false, adjustReason?: string) {
  return request.post<ApiResult<Reservation>>('/reservations', { scheduleId, adjusted, adjustReason })
}

/** 取消预约 */
export function cancelReservation(id: number, reason?: string) {
  return request.post<ApiResult<string>>(`/reservations/${id}/cancel`, { reason })
}

/** 查询我的预约 */
export function getMyReservations(page = 1, size = 10) {
  return request.get<ApiResult<PageResult<Reservation>>>('/reservations/my', { params: { page, size } })
}

/** 教练查询今日预约 */
export function getCoachTodayReservations(date?: string) {
  return request.get<ApiResult<PageResult<Reservation>>>('/reservations/coach/today', { params: { date } })
}

/** 管理员分页查询全部预约 */
export function getAllReservations(params: ReservationQuery) {
  return request.get<ApiResult<PageResult<Reservation>>>('/reservations', { params })
}
