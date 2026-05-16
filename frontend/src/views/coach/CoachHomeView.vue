<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

/**
 * 教练工作台 — M2 阶段：使用真实用户信息
 * <p>
 * 展示当前登录教练的账号，退出登录功能可用。
 * 排班和预约名单将在 M7 阶段实现。
 * </p>
 */

const router = useRouter()
const authStore = useAuthStore()

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>

<template>
  <div class="min-h-screen bg-gray-50 flex">
    <aside class="w-56 bg-ink-800 text-white flex flex-col flex-shrink-0">
      <div class="px-4 py-5 border-b border-white/10">
        <div class="flex items-center gap-3">
          <div class="w-8 h-8 bg-primary-500/30 rounded-lg flex items-center justify-center">
            <svg class="w-5 h-5 text-primary-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
            </svg>
          </div>
          <span class="text-sm font-semibold text-white/90">教练工作台</span>
        </div>
      </div>

      <nav class="flex-1 px-3 py-4 space-y-1">
        <a class="flex items-center gap-3 px-3 py-2.5 rounded-lg bg-white/10 text-white text-sm font-medium">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
          </svg>
          今日排班
        </a>
        <a class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-white/60 hover:bg-white/5 text-sm transition-colors">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
          </svg>
          预约名单
        </a>
        <a class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-white/60 hover:bg-white/5 text-sm transition-colors">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          完成练车
        </a>
        <a class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-white/60 hover:bg-white/5 text-sm transition-colors">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
          </svg>
          工作量统计
        </a>
      </nav>

      <div class="px-3 py-3 border-t border-white/10">
        <button @click="handleLogout" class="flex items-center gap-2 px-3 py-2 w-full rounded-lg text-white/50 hover:text-white/80 hover:bg-white/5 text-sm transition-colors">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
          </svg>
          退出系统
        </button>
      </div>
    </aside>

    <div class="flex-1 flex flex-col min-w-0">
      <header class="bg-white border-b border-gray-100 px-6 py-4 flex items-center justify-between">
        <div>
          <h2 class="text-lg font-semibold text-gray-800">教练工作台</h2>
          <p class="text-xs text-gray-500 mt-0.5">查看排班、管理学员预约和练车记录</p>
        </div>
        <div class="flex items-center gap-3">
          <span class="text-sm text-gray-500">{{ authStore.username }}</span>
          <div class="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center text-primary-600 text-xs font-medium">
            教
          </div>
        </div>
      </header>

      <main class="flex-1 p-6">
        <div class="card p-8 text-center">
          <div class="w-16 h-16 bg-accent-50 rounded-2xl flex items-center justify-center mx-auto mb-4">
            <svg class="w-8 h-8 text-accent-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
          </div>
          <h3 class="text-lg font-semibold text-gray-700 mb-2">
            欢迎，{{ authStore.username }} 教练
          </h3>
          <p class="text-sm text-gray-500 max-w-md mx-auto">
            教练工作台 — 今日排班和预约名单将在 M7 阶段实现。
          </p>
        </div>
      </main>
    </div>
  </div>
</template>
