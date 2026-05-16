<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

/**
 * 管理员首页布局 — M3 阶段：支持子路由
 * <p>
 * 侧边栏导航可切换到教练管理、学员管理、车辆管理等子页面。
 * 主内容区通过 router-view 渲染对应子路由组件。
 * </p>
 */

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

function handleLogout() {
  authStore.logout()
  router.push('/login')
}

/** 侧边栏菜单项 */
const menuItems = [
  { path: '/admin', icon: 'home', label: '首页概览' },
  { path: '/admin/coaches', icon: 'user', label: '教练管理' },
  { path: '/admin/students', icon: 'users', label: '学员管理' },
  { path: '/admin/vehicles', icon: 'truck', label: '车辆管理' },
]

/** 判断当前路由是否激活 */
function isActive(path: string) {
  if (path === '/admin') return route.path === '/admin'
  return route.path.startsWith(path)
}
</script>

<template>
  <div class="min-h-screen bg-gray-50 flex">
    <!-- 侧边栏 -->
    <aside class="w-60 bg-ink-800 text-white flex flex-col flex-shrink-0">
      <div class="px-5 py-5 border-b border-white/10">
        <div class="flex items-center gap-3">
          <div class="w-8 h-8 bg-primary-500/30 rounded-lg flex items-center justify-center">
            <svg class="w-5 h-5 text-primary-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 17a2 2 0 11-4 0 2 2 0 014 0zM19 17a2 2 0 11-4 0 2 2 0 014 0z" />
            </svg>
          </div>
          <span class="text-sm font-semibold text-white/90">驾校调度系统</span>
        </div>
      </div>

      <nav class="flex-1 px-3 py-4 space-y-1">
        <router-link
          v-for="item in menuItems" :key="item.path"
          :to="item.path"
          class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition-colors"
          :class="isActive(item.path) ? 'bg-white/10 text-white font-medium' : 'text-white/60 hover:bg-white/5 hover:text-white/80'"
        >
          {{ item.label }}
        </router-link>
      </nav>

      <div class="px-3 py-3 border-t border-white/10">
        <button @click="handleLogout" class="flex items-center gap-2 px-3 py-2 w-full rounded-lg text-white/50 hover:text-white/80 hover:bg-white/5 text-sm transition-colors">
          退出系统
        </button>
      </div>
    </aside>

    <!-- 主内容区 -->
    <div class="flex-1 flex flex-col min-w-0">
      <header class="bg-white border-b border-gray-100 px-6 py-4 flex items-center justify-between">
        <div>
          <h2 class="text-lg font-semibold text-gray-800">管理员工作台</h2>
          <p class="text-xs text-gray-500 mt-0.5">{{ route.meta.title || '驾校学员练车预约与调度管理系统' }}</p>
        </div>
        <div class="flex items-center gap-3">
          <span class="text-sm text-gray-500">{{ authStore.username }}</span>
          <div class="w-8 h-8 rounded-full bg-ink-100 flex items-center justify-center text-ink-600 text-xs font-medium">管</div>
        </div>
      </header>

      <main class="flex-1 p-6">
        <router-view />
      </main>
    </div>
  </div>
</template>
