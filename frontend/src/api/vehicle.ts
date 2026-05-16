import request from './request'
import type { ApiResult, PageResult } from '@/types/common'
import type { Vehicle, VehicleSimple, VehicleCreateDTO, VehicleUpdateDTO, VehicleQuery } from '@/types/vehicle'

/** 分页查询车辆 */
export function getVehicleList(params: VehicleQuery) {
  return request.get<ApiResult<PageResult<Vehicle>>>('/vehicles', { params })
}

/** 查询车辆简单列表 */
export function getVehicleSimpleList() {
  return request.get<ApiResult<VehicleSimple[]>>('/vehicles/simple')
}

/** 查询车辆详情 */
export function getVehicleDetail(id: number) {
  return request.get<ApiResult<Vehicle>>(`/vehicles/${id}`)
}

/** 新增车辆（仅管理员） */
export function createVehicle(data: VehicleCreateDTO) {
  return request.post<ApiResult<Vehicle>>('/vehicles', data)
}

/** 修改车辆（仅管理员） */
export function updateVehicle(id: number, data: VehicleUpdateDTO) {
  return request.put<ApiResult<Vehicle>>(`/vehicles/${id}`, data)
}

/** 修改车辆状态（仅管理员） */
export function updateVehicleStatus(id: number, status: string) {
  return request.patch<ApiResult<string>>(`/vehicles/${id}/status`, { status })
}
