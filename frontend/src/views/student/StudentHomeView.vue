<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

/**
 * 学员首页 — M2 阶段：使用真实用户信息
 * <p>
 * 展示当前登录学员的账号，退出登录功能可用。
 * 预约功能将在 M5 阶段实现。
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
  <div class="min-h-screen bg-gray-50">
    <header class="bg-white border-b border-gray-100 sticky top-0 z-10">
      <div class="max-w-5xl mx-auto px-6 py-3 flex items-center justify-between">
        <div class="flex items-center gap-3">
          <div class="w-8 h-8 bg-primary-500/15 rounded-lg flex items-center justify-center">
            <svg class="w-5 h-5 text-primary-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                d="M9 17a2 2 0 11-4 0 2 2 0 014 0zM19 17a2 2 0 11-4 0 2 2 0 014 0z" />
            </svg>
          </div>
          <span class="text-sm font-semibold text-gray-700">练车预约</span>
        </div>

        <div class="flex items-center gap-4">
          <span class="text-sm text-gray-400">{{ authStore.username }}</span>
          <a class="text-sm text-gray-500 hover:text-primary-600 transition-colors cursor-pointer">
            我的预约
          </a>
          <a class="text-sm text-gray-500 hover:text-primary-600 transition-colors cursor-pointer">
            练车记录
          </a>
          <button
            @click="handleLogout"
            class="text-sm text-gray-400 hover:text-gray-600 transition-colors"
          >
            退出
          </button>
        </div>
      </div>
    </header>

    <main class="max-w-5xl mx-auto px-6 py-8">
      <div class="card p-8 text-center">
        <div class="w-16 h-16 bg-primary-50 rounded-2xl flex items-center justify-center mx-auto mb-4">
          <svg class="w-8 h-8 text-primary-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
          </svg>
        </div>
        <h3 class="text-lg font-semibold text-gray-700 mb-2">
          欢迎，{{ authStore.username }}
        </h3>
        <p class="text-sm text-gray-500 max-w-md mx-auto mb-6">
          学员预约页 — 预约功能将在 M5 阶段实现。
        </p>

        <div class="max-w-sm mx-auto grid grid-cols-2 gap-3">
          <div class="border border-gray-200 rounded-xl p-3 text-left">
            <div class="text-xs text-gray-400 mb-1">日期</div>
            <div class="text-sm font-medium text-gray-700">2026-05-16</div>
          </div>
          <div class="border border-gray-200 rounded-xl p-3 text-left">
            <div class="text-xs text-gray-400 mb-1">时间段</div>
            <div class="text-sm font-medium text-gray-700">上午</div>
          </div>
        </div>
        <p class="text-xs text-gray-400 mt-4">以上为静态占位，M5 阶段替换为真实交互组件</p>
      </div>
    </main>
  </div>
</template>
