import request from './request'
import type { ApiResult, PageResult } from '@/types/common'
import type { Schedule, ScheduleAvailable, ScheduleQuery, ScheduleCreateDTO, ScheduleUpdateDTO, ScheduleBatchDTO } from '@/types/schedule'

/** 分页查询排班 */
export function getScheduleList(params: ScheduleQuery) {
  return request.get<ApiResult<PageResult<Schedule>>>('/schedules', { params })
}

/** 查询可预约排班 */
export function getAvailableSchedules(date: string, timeSlot: string) {
  return request.get<ApiResult<ScheduleAvailable[]>>('/schedules/available', { params: { date, timeSlot } })
}

/** 查询排班详情 */
export function getScheduleDetail(id: number) {
  return request.get<ApiResult<Schedule>>(`/schedules/${id}`)
}

/** 新增排班 */
export function createSchedule(data: ScheduleCreateDTO) {
  return request.post<ApiResult<Schedule>>('/schedules', data)
}

/** 修改排班 */
export function updateSchedule(id: number, data: ScheduleUpdateDTO) {
  return request.put<ApiResult<Schedule>>(`/schedules/${id}`, data)
}

/** 修改排班状态 */
export function updateScheduleStatus(id: number, status: string) {
  return request.patch<ApiResult<string>>(`/schedules/${id}/status`, { status })
}

/** 批量生成排班，返回 { created, skipped } */
export function batchCreateSchedule(data: ScheduleBatchDTO) {
  return request.post<ApiResult<{ created: number; skipped: number }>>('/schedules/batch', data)
}
