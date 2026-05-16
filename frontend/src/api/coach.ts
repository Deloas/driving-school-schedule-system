import request from './request'
import type { ApiResult, PageResult } from '@/types/common'
import type { Coach, CoachSimple, CoachCreateDTO, CoachUpdateDTO, CoachQuery } from '@/types/coach'

/** 分页查询教练 */
export function getCoachList(params: CoachQuery) {
  return request.get<ApiResult<PageResult<Coach>>>('/coaches', { params })
}

/** 查询教练简单列表 */
export function getCoachSimpleList() {
  return request.get<ApiResult<CoachSimple[]>>('/coaches/simple')
}

/** 查询教练详情 */
export function getCoachDetail(id: number) {
  return request.get<ApiResult<Coach>>(`/coaches/${id}`)
}

/** 新增教练（仅管理员） */
export function createCoach(data: CoachCreateDTO) {
  return request.post<ApiResult<Coach>>('/coaches', data)
}

/** 修改教练（仅管理员） */
export function updateCoach(id: number, data: CoachUpdateDTO) {
  return request.put<ApiResult<Coach>>(`/coaches/${id}`, data)
}

/** 修改教练状态（仅管理员） */
export function updateCoachStatus(id: number, status: string) {
  return request.patch<ApiResult<string>>(`/coaches/${id}/status`, { status })
}
