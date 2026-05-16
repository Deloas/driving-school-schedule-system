import request from './request'
import type { ApiResult } from '@/types/common'
import type { AdminOverview, CoachLoad, ReservationTrend, ScheduleUtilization, StudentProgress, RecentActivity, CoachOverview, StudentOverview } from '@/types/statistics'

export function getAdminOverview() { return request.get<ApiResult<AdminOverview>>('/statistics/admin/overview') }
export function getCoachLoad() { return request.get<ApiResult<CoachLoad[]>>('/statistics/admin/coach-load') }
export function getReservationTrend() { return request.get<ApiResult<ReservationTrend[]>>('/statistics/admin/reservation-trend') }
export function getScheduleUtilization() { return request.get<ApiResult<ScheduleUtilization[]>>('/statistics/admin/schedule-utilization') }
export function getStudentProgress() { return request.get<ApiResult<StudentProgress[]>>('/statistics/admin/student-progress') }
export function getRecentActivities() { return request.get<ApiResult<RecentActivity[]>>('/statistics/admin/recent-activities') }
export function getCoachOverview() { return request.get<ApiResult<CoachOverview>>('/statistics/coach/overview') }
export function getStudentOverview() { return request.get<ApiResult<StudentOverview>>('/statistics/student/overview') }
