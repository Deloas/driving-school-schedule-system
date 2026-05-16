/** 排班相关类型定义 */

export interface Schedule {
  id: number
  coachId: number
  coachName: string
  vehicleId: number
  plateNumber: string
  scheduleDate: string
  timeSlot: 'MORNING' | 'AFTERNOON'
  maxStudents: number
  currentStudents: number
  remainCount: number
  status: 'OPEN' | 'CLOSED' | 'CANCELLED'
  remark: string
  createTime: string
}

export interface ScheduleAvailable {
  scheduleId: number
  coachId: number
  coachName: string
  vehicleId: number
  plateNumber: string
  maxStudents: number
  currentStudents: number
  remainCount: number
  status: string
}

export interface ScheduleQuery {
  page?: number
  size?: number
  coachId?: number
  vehicleId?: number
  scheduleDate?: string
  startDate?: string
  endDate?: string
  timeSlot?: string
  status?: string
}

export interface ScheduleCreateDTO {
  coachId: number
  vehicleId?: number
  scheduleDate: string
  timeSlot: string
  maxStudents: number
  remark?: string
}

export interface ScheduleUpdateDTO {
  vehicleId?: number
  maxStudents?: number
  remark?: string
}

export interface ScheduleBatchDTO {
  coachIds: number[]
  startDate: string
  endDate: string
  timeSlots: string[]
  maxStudents: number
  autoBindVehicle?: boolean
  remark?: string
}
