<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
import AppLogo from '@/components/AppLogo.vue'
import UserIdentity from '@/components/UserIdentity.vue'
import PageHeader from '@/components/PageHeader.vue'

/** 管理员首页布局 */
const router = useRouter()
const route = useRoute()

const pageTitle: Record<string, string> = {
  '/admin': '首页概览',
  '/admin/coaches': '教练管理',
  '/admin/students': '学员管理',
  '/admin/vehicles': '车辆管理',
  '/admin/schedules': '排班管理',
  '/admin/users': '账号管理',
  '/admin/training-records': '练车记录',
}
const menuItems = [
  { path: '/admin', label: '首页概览' },
  { path: '/admin/coaches', label: '教练管理' },
  { path: '/admin/students', label: '学员管理' },
  { path: '/admin/vehicles', label: '车辆管理' },
  { path: '/admin/schedules', label: '排班管理' },
  { path: '/admin/users', label: '账号管理' },
  { path: '/admin/training-records', label: '练车记录' },
]
function isActive(path: string) { return path === '/admin' ? route.path === '/admin' : route.path.startsWith(path) }
</script>

<template>
  <div class="min-h-screen bg-gray-50 flex">
    <!-- 侧边栏 -->
    <aside class="w-56 bg-ink-800 text-white flex flex-col flex-shrink-0">
      <div class="px-5 py-5 border-b border-white/10">
        <AppLogo dark />
        <p class="text-white/30 text-[10px] mt-1.5 tracking-wide">智能练车调度平台</p>
      </div>
      <nav class="flex-1 px-3 py-4 space-y-0.5">
        <router-link v-for="item in menuItems" :key="item.path" :to="item.path"
          class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition-colors"
          :class="isActive(item.path) ? 'bg-white/10 text-white font-medium' : 'text-white/55 hover:bg-white/5 hover:text-white/80'">
          {{ item.label }}
        </router-link>
      </nav>
      <div class="px-3 py-3 border-t border-white/10">
        <router-link to="/login" class="flex items-center gap-2 px-3 py-2 w-full rounded-lg text-white/45 hover:text-white/80 hover:bg-white/5 text-sm transition-colors">退出系统</router-link>
      </div>
    </aside>

    <!-- 主内容 -->
    <div class="flex-1 flex flex-col min-w-0">
      <header class="bg-white border-b border-gray-100 px-6 py-3 flex items-center justify-between">
        <div class="text-sm font-semibold text-gray-700">{{ pageTitle[route.path] || '' }}</div>
        <UserIdentity />
      </header>
      <main class="flex-1 p-6">
        <router-view />
      </main>
    </div>
  </div>
</template>
