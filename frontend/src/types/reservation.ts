/** 预约相关类型定义 */

export interface Reservation {
  id: number
  studentId: number
  studentName: string
  studentPhone: string
  mainCoachId: number
  mainCoachName: string
  actualCoachId: number
  actualCoachName: string
  vehicleId: number
  plateNumber: string
  scheduleId: number
  reservationDate: string
  timeSlot: 'MORNING' | 'AFTERNOON'
  status: 'SUCCESS' | 'CANCELLED' | 'COMPLETED' | 'ABSENT'
  isAdjusted: number
  adjustReason: string
  cancelReason: string
  createTime: string
  cancelTime: string
}

export interface AdjustOption {
  scheduleId: number
  coachId: number
  coachName: string
  vehicleId: number
  plateNumber: string
  maxStudents: number
  currentStudents: number
  remainCount: number
  recommendReason: string
}

export interface ReservationOption {
  mainCoachOption: {
    scheduleId: number
    coachId: number
    coachName: string
    vehicleId: number
    plateNumber: string
    scheduleDate: string
    timeSlot: string
    maxStudents: number
    currentStudents: number
    remainCount: number
    available: boolean
    message: string
  }
  /** M7：可调剂教练列表 */
  adjustOptions: AdjustOption[] | null
}

export interface ReservationQuery {
  page?: number
  size?: number
  studentName?: string
  coachId?: number
  reservationDate?: string
  timeSlot?: string
  status?: string
}
