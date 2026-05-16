<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getCoachTodayReservations } from '@/api/reservation'
import request from '@/api/request'
import type { ApiResult } from '@/types/common'
import type { Reservation } from '@/types/reservation'
import { showToast } from '@/utils/toast'

const router = useRouter()
const authStore = useAuthStore()
const todayList = ref<Reservation[]>([])
const compForm = ref({ id: 0, trainingContent: '', durationMinutes: 120, coachRemark: '' })
const compVisible = ref(false)
const absForm = ref({ id: 0, reason: '' })
const absVisible = ref(false)

onMounted(fetchToday)

async function fetchToday() {
  try {
    const res = await getCoachTodayReservations()
    todayList.value = res.data.data?.records || []
  } catch { /* 暂无 */ }
}

function openComplete(r: Reservation) { compForm.value = { id: r.id, trainingContent: '', durationMinutes: 120, coachRemark: '' }; compVisible.value = true }
function openAbsent(r: Reservation) { absForm.value = { id: r.id, reason: '' }; absVisible.value = true }

async function handleComplete() {
  try {
    await request.post<ApiResult<string>>(`/reservations/${compForm.value.id}/complete`, compForm.value)
    showToast('练车完成')
    compVisible.value = false
    fetchToday()
  } catch (err: any) { showToast(err?.response?.data?.message || '操作失败', 'error') }
}

async function handleAbsent() {
  try {
    await request.post<ApiResult<string>>(`/reservations/${absForm.value.id}/absent`, absForm.value)
    showToast('已标记缺席')
    absVisible.value = false
    fetchToday()
  } catch (err: any) { showToast(err?.response?.data?.message || '操作失败', 'error') }
}

function handleLogout() { authStore.logout(); router.push('/login') }
const slotLabel: Record<string, string> = { MORNING: '上午', AFTERNOON: '下午' }
</script>

<template>
  <div class="min-h-screen bg-gray-50 flex">
    <aside class="w-56 bg-ink-800 text-white flex flex-col flex-shrink-0">
      <div class="px-4 py-5 border-b border-white/10"><span class="text-sm font-semibold text-white/90">教练工作台</span></div>
      <nav class="flex-1 px-3 py-4"><a class="flex items-center gap-3 px-3 py-2.5 rounded-lg bg-white/10 text-white text-sm font-medium">今日预约</a></nav>
      <div class="px-3 py-3 border-t border-white/10"><button @click="handleLogout" class="text-white/50 hover:text-white/80 text-sm">退出系统</button></div>
    </aside>
    <div class="flex-1 flex flex-col min-w-0">
      <header class="bg-white border-b border-gray-100 px-6 py-4"><h2 class="text-lg font-semibold text-gray-800">教练工作台</h2><p class="text-xs text-gray-500">{{ authStore.username }}</p></header>
      <main class="flex-1 p-6">
        <h3 class="text-base font-semibold text-gray-700 mb-4">今日预约名单</h3>
        <div class="card overflow-hidden">
          <table class="w-full text-sm">
            <thead class="bg-gray-50 text-gray-500"><tr><th class="text-left px-4 py-3">学员</th><th class="text-center px-4 py-3">时段</th><th class="text-left px-4 py-3">车辆</th><th class="text-center px-4 py-3">状态</th><th class="text-right px-4 py-3">操作</th></tr></thead>
            <tbody class="divide-y divide-gray-50">
              <tr v-if="!todayList.length"><td colspan="5" class="text-center py-10 text-gray-400">今日暂无预约</td></tr>
              <tr v-for="r in todayList" :key="r.id" class="hover:bg-gray-50/50">
                <td class="px-4 py-3"><div class="font-medium text-gray-800">{{ r.studentName }}<span v-if="r.isAdjusted" class="text-xs text-amber-600 ml-1">调剂</span></div><div class="text-xs text-gray-400">{{ r.studentPhone }}</div></td>
                <td class="px-4 py-3 text-center"><span :class="r.timeSlot==='MORNING'?'bg-blue-50 text-blue-600':'bg-amber-50 text-amber-600'" class="text-xs font-medium px-2 py-0.5 rounded-full">{{ slotLabel[r.timeSlot] }}</span></td>
                <td class="px-4 py-3 text-gray-600">{{ r.plateNumber || '-' }}</td>
                <td class="px-4 py-3 text-center"><span :class="r.status==='SUCCESS'?'bg-green-100 text-green-700':r.status==='COMPLETED'?'bg-blue-100 text-blue-700':r.status==='ABSENT'?'bg-red-100 text-red-700':'bg-gray-100 text-gray-500'" class="text-xs font-medium px-2 py-0.5 rounded-full">{{ r.status==='SUCCESS'?'待练车':r.status==='COMPLETED'?'已完成':r.status==='ABSENT'?'缺席':r.status }}</span></td>
                <td class="px-4 py-3 text-right">
                  <template v-if="r.status==='SUCCESS'">
                    <button @click="openComplete(r)" class="text-xs text-green-600 hover:text-green-800 font-medium mr-2">完成</button>
                    <button @click="openAbsent(r)" class="text-xs text-red-500 hover:text-red-700 font-medium">缺席</button>
                  </template>
                  <span v-else class="text-xs text-gray-400">--</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </main>
    </div>
  </div>

  <!-- 完成弹窗 -->
  <div v-if="compVisible" class="fixed inset-0 z-50 flex items-center justify-center"><div class="absolute inset-0 bg-black/30" @click="compVisible=false"/><div class="relative bg-white rounded-2xl shadow-xl w-full max-w-md mx-4 p-6"><h3 class="text-base font-semibold mb-4">完成练车</h3><div class="space-y-3"><div><label class="text-xs text-gray-500">练车内容</label><input v-model="compForm.trainingContent" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg mt-1" placeholder="例：倒车入库练习"/></div><div><label class="text-xs text-gray-500">练车时长（分钟）</label><input v-model.number="compForm.durationMinutes" type="number" min="1" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg mt-1" placeholder="120"/></div><div><label class="text-xs text-gray-500">备注</label><textarea v-model="compForm.coachRemark" rows="2" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg mt-1" placeholder="练车评语"/></div></div><div class="flex justify-end gap-3 mt-4"><button @click="compVisible=false" class="px-4 py-2 text-sm bg-gray-100 rounded-lg">取消</button><button @click="handleComplete" class="px-4 py-2 text-sm bg-green-600 text-white rounded-lg">确认完成</button></div></div></div>

  <!-- 缺席弹窗 -->
  <div v-if="absVisible" class="fixed inset-0 z-50 flex items-center justify-center"><div class="absolute inset-0 bg-black/30" @click="absVisible=false"/><div class="relative bg-white rounded-2xl shadow-xl w-full max-w-md mx-4 p-6"><h3 class="text-base font-semibold mb-4">标记缺席</h3><div class="space-y-3"><div><label class="text-xs text-gray-500">原因</label><input v-model="absForm.reason" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg mt-1" placeholder="学员未按时到场"/></div></div><div class="flex justify-end gap-3 mt-4"><button @click="absVisible=false" class="px-4 py-2 text-sm bg-gray-100 rounded-lg">取消</button><button @click="handleAbsent" class="px-4 py-2 text-sm bg-red-600 text-white rounded-lg">确认缺席</button></div></div></div>
</template>
