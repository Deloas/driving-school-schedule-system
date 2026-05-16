<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

/** 学员首页布局 — M5 阶段：包含预约练车和我的预约入口 */
const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

function handleLogout() { authStore.logout(); router.push('/login') }

const isActive = (path: string) => route.path === path
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <header class="bg-white border-b border-gray-100 sticky top-0 z-10">
      <div class="max-w-5xl mx-auto px-6 py-3 flex items-center justify-between">
        <div class="flex items-center gap-3">
          <div class="w-8 h-8 bg-primary-500/15 rounded-lg flex items-center justify-center">
            <svg class="w-5 h-5 text-primary-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 17a2 2 0 11-4 0 2 2 0 014 0zM19 17a2 2 0 11-4 0 2 2 0 014 0z" />
            </svg>
          </div>
          <span class="text-sm font-semibold text-gray-700">练车预约</span>
        </div>
        <div class="flex items-center gap-4">
          <span class="text-sm text-gray-400">{{ authStore.username }}</span>
          <router-link to="/student/reservations" :class="['text-sm transition-colors', isActive('/student/reservations') ? 'text-primary-600 font-medium' : 'text-gray-500 hover:text-primary-600']">
            预约练车
          </router-link>
          <button @click="handleLogout" class="text-sm text-gray-400 hover:text-gray-600 transition-colors">退出</button>
        </div>
      </div>
    </header>
    <main class="max-w-5xl mx-auto px-6 py-8">
      <router-view />
    </main>
  </div>
</template>
