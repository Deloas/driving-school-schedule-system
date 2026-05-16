<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getCoachTodayReservations } from '@/api/reservation'
import type { Reservation } from '@/types/reservation'

/** 教练工作台 — M5 阶段：增加今日预约名单 */
const router = useRouter()
const authStore = useAuthStore()

const todayList = ref<Reservation[]>([])

onMounted(async () => {
  try {
    const res = await getCoachTodayReservations()
    todayList.value = res.data.data?.records || []
  } catch { /* 暂无预约 */ }
})

function handleLogout() { authStore.logout(); router.push('/login') }

const slotLabel: Record<string, string> = { MORNING: '上午', AFTERNOON: '下午' }
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
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" /></svg>
          今日排班
        </a>
      </nav>
      <div class="px-3 py-3 border-t border-white/10">
        <button @click="handleLogout" class="flex items-center gap-2 px-3 py-2 w-full rounded-lg text-white/50 hover:text-white/80 hover:bg-white/5 text-sm transition-colors">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" /></svg>
          退出系统
        </button>
      </div>
    </aside>

    <div class="flex-1 flex flex-col min-w-0">
      <header class="bg-white border-b border-gray-100 px-6 py-4 flex items-center justify-between">
        <div><h2 class="text-lg font-semibold text-gray-800">教练工作台</h2><p class="text-xs text-gray-500 mt-0.5">{{ authStore.username }}</p></div>
      </header>

      <main class="flex-1 p-6">
        <h3 class="text-base font-semibold text-gray-700 mb-4">今日预约名单</h3>
        <div class="card overflow-hidden">
          <table class="w-full text-sm">
            <thead class="bg-gray-50 text-gray-500">
              <tr>
                <th class="text-left px-4 py-3 font-medium">学员</th>
                <th class="text-left px-4 py-3 font-medium">手机号</th>
                <th class="text-center px-4 py-3 font-medium">时段</th>
                <th class="text-left px-4 py-3 font-medium">车辆</th>
                <th class="text-center px-4 py-3 font-medium">状态</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-50">
              <tr v-if="!todayList.length"><td colspan="5" class="text-center py-10 text-gray-400">今日暂无预约</td></tr>
              <tr v-for="r in todayList" :key="r.id" class="hover:bg-gray-50/50">
                <td class="px-4 py-3 font-medium text-gray-800">{{ r.studentName }}</td>
                <td class="px-4 py-3 text-gray-600">{{ r.studentPhone }}</td>
                <td class="px-4 py-3 text-center">
                  <span :class="r.timeSlot==='MORNING'?'bg-blue-50 text-blue-600':'bg-amber-50 text-amber-600'" class="text-xs font-medium px-2 py-0.5 rounded-full">{{ slotLabel[r.timeSlot] || r.timeSlot }}</span>
                </td>
                <td class="px-4 py-3 text-gray-600">{{ r.plateNumber || '-' }}</td>
                <td class="px-4 py-3 text-center">
                  <span class="text-xs font-medium px-2 py-0.5 rounded-full bg-green-100 text-green-700">已预约</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </main>
    </div>
  </div>
</template>
