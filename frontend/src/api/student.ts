import request from './request'
import type { ApiResult, PageResult } from '@/types/common'
import type { Student, StudentSimple, StudentCreateDTO, StudentUpdateDTO, StudentQuery } from '@/types/student'

/** 分页查询学员 */
export function getStudentList(params: StudentQuery) {
  return request.get<ApiResult<PageResult<Student>>>('/students', { params })
}

/** 查询学员简单列表 */
export function getStudentSimpleList() {
  return request.get<ApiResult<StudentSimple[]>>('/students/simple')
}

/** 查询学员详情 */
export function getStudentDetail(id: number) {
  return request.get<ApiResult<Student>>(`/students/${id}`)
}

/** 新增学员（仅管理员） */
export function createStudent(data: StudentCreateDTO) {
  return request.post<ApiResult<Student>>('/students', data)
}

/** 修改学员（仅管理员） */
export function updateStudent(id: number, data: StudentUpdateDTO) {
  return request.put<ApiResult<Student>>(`/students/${id}`, data)
}

/** 修改学员状态（仅管理员） */
export function updateStudentStatus(id: number, status: string) {
  return request.patch<ApiResult<string>>(`/students/${id}/status`, { status })
}
