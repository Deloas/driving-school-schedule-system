/** 教练相关类型定义 */

export interface Coach {
  id: number
  name: string
  phone: string
  licenseNo: string
  maxStudentsPerHalfDay: number
  status: 'NORMAL' | 'LEAVE' | 'STOPPED'
  remark: string
  createTime: string
  updateTime: string
}

export interface CoachSimple {
  id: number
  name: string
  status: string
}

export interface CoachCreateDTO {
  name: string
  phone: string
  licenseNo?: string
  maxStudentsPerHalfDay: number
  remark?: string
}

export interface CoachUpdateDTO {
  name: string
  phone: string
  licenseNo?: string
  maxStudentsPerHalfDay: number
  remark?: string
}

export interface CoachQuery {
  page?: number
  size?: number
  keyword?: string
  status?: string
}
