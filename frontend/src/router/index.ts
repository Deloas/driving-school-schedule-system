import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

/**
 * Vue Router — M3 阶段：增加管理员子页面（教练/学员/车辆管理）
 */

const routes: RouteRecordRaw[] = [
  { path: '/', redirect: '/login' },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/LoginView.vue'),
    meta: { title: '登录', requiresAuth: false },
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminHomeView.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
    children: [
      { path: '', name: 'AdminHome', component: () => import('@/views/admin/AdminDashboard.vue'), meta: { title: '管理员首页' } },
      { path: 'coaches', name: 'CoachManage', component: () => import('@/views/admin/CoachManageView.vue'), meta: { title: '教练管理' } },
      { path: 'students', name: 'StudentManage', component: () => import('@/views/admin/StudentManageView.vue'), meta: { title: '学员管理' } },
      { path: 'vehicles', name: 'VehicleManage', component: () => import('@/views/admin/VehicleManageView.vue'), meta: { title: '车辆管理' } },
      { path: 'schedules', name: 'ScheduleManage', component: () => import('@/views/admin/ScheduleManageView.vue'), meta: { title: '排班管理' } },
    ],
  },
  {
    path: '/coach',
    name: 'CoachHome',
    component: () => import('@/views/coach/CoachHomeView.vue'),
    meta: { title: '教练工作台', requiresAuth: true, role: 'COACH' },
  },
  {
    path: '/student',
    component: () => import('@/views/student/StudentHomeView.vue'),
    meta: { requiresAuth: true, role: 'STUDENT' },
    children: [
      { path: '', redirect: '/student/reservations' },
      { path: 'reservations', name: 'StudentReservations', component: () => import('@/views/student/ReservationView.vue'), meta: { title: '预约练车' } },
    ],
  },
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to, _from, next) => {
  if (to.meta.title) document.title = `${to.meta.title} - 驾校练车预约系统`

  const token = localStorage.getItem('token')
  const userRole = localStorage.getItem('role')

  // 已登录访问登录页 → 跳转角色首页
  if (to.path === '/login' && token && userRole) {
    const map: Record<string, string> = { ADMIN: '/admin', COACH: '/coach', STUDENT: '/student' }
    next(map[userRole] || '/login')
    return
  }

  if (to.meta.requiresAuth === false) { next(); return }

  if (!token) { next('/login'); return }

  // 角色不匹配 → 跳转到对应首页
  if (to.meta.role && to.meta.role !== userRole) {
    const map: Record<string, string> = { ADMIN: '/admin', COACH: '/coach', STUDENT: '/student' }
    next(map[userRole || ''] || '/login')
    return
  }

  next()
})

export default router
