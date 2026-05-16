/** 车辆相关类型定义 */

export interface Vehicle {
  id: number
  plateNumber: string
  coachId: number
  coachName: string
  vehicleType: string
  status: 'NORMAL' | 'MAINTENANCE' | 'STOPPED'
  remark: string
  createTime: string
}

export interface VehicleSimple {
  id: number
  plateNumber: string
  status: string
}

export interface VehicleCreateDTO {
  plateNumber: string
  coachId?: number
  vehicleType?: string
  remark?: string
}

export interface VehicleUpdateDTO {
  plateNumber: string
  coachId?: number
  vehicleType?: string
  remark?: string
}

export interface VehicleQuery {
  page?: number
  size?: number
  keyword?: string
  coachId?: number
  status?: string
}
