/** 学员相关类型定义 */

export interface Student {
  id: number
  name: string
  phone: string
  gender: string
  mainCoachId: number
  mainCoachName: string
  subjectType: 'SUBJECT_2' | 'SUBJECT_3'
  requiredTrainingCount: number
  completedTrainingCount: number
  status: 'NORMAL' | 'STOPPED'
  remark: string
  createTime: string
}

export interface StudentSimple {
  id: number
  name: string
  phone: string
  status: string
}

export interface StudentCreateDTO {
  name: string
  phone: string
  gender?: string
  mainCoachId: number
  subjectType?: string
  requiredTrainingCount?: number
  remark?: string
}

export interface StudentUpdateDTO {
  name: string
  phone: string
  gender?: string
  mainCoachId?: number
  subjectType?: string
  requiredTrainingCount?: number
  remark?: string
}

export interface StudentQuery {
  page?: number
  size?: number
  keyword?: string
  coachId?: number
  status?: string
}
