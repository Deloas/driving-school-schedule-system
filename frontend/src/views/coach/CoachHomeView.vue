<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getCoachTodayReservations } from '@/api/reservation'
import request from '@/api/request'
import type { ApiResult, PageResult } from '@/types/common'
import type { Reservation } from '@/types/reservation'
import type { Student } from '@/types/student'
import { showToast } from '@/utils/toast'
import { getCoachOverview } from '@/api/statistics'
import type { CoachOverview } from '@/types/statistics'
import AppLogo from '@/components/AppLogo.vue'
import UserIdentity from '@/components/UserIdentity.vue'

const router = useRouter()
const authStore = useAuthStore()
const todayList = ref<Reservation[]>([])
const overview = ref<CoachOverview | null>(null)
const myStudents = ref<Student[]>([])
const recentRecords = ref<any[]>([])
const selDate = ref(new Date().toISOString().slice(0, 10))
const compForm = ref({ id: 0, trainingContent: '', durationMinutes: 120, coachRemark: '' })
const compVisible = ref(false)
const absForm = ref({ id: 0, reason: '' })
const absVisible = ref(false)

onMounted(async () => {
  await Promise.all([fetchReservations(), fetchOverview(), fetchMyStudents(), fetchRecentRecords()])
})

async function fetchReservations() { try { const r = await getCoachTodayReservations(selDate.value); todayList.value = r.data.data?.records || [] } catch {} }
async function fetchOverview() { try { overview.value = (await getCoachOverview()).data.data } catch {} }
async function fetchMyStudents() { try { const r = await request.get<ApiResult<PageResult<Student>>>('/students/my', { params: { page: 1, size: 5 } }); myStudents.value = r.data.data?.records || [] } catch {} }
async function fetchRecentRecords() { try { const r = await request.get<ApiResult<PageResult<any>>>('/training-records/coach', { params: { page: 1, size: 3 } }); recentRecords.value = r.data.data?.records || [] } catch {} }

function goDate(days: number) { const d = new Date(); d.setDate(d.getDate() + days); selDate.value = d.toISOString().slice(0, 10); fetchReservations() }
function openComplete(r: Reservation) { compForm.value = { id: r.id, trainingContent: '', durationMinutes: 120, coachRemark: '' }; compVisible.value = true }
function openAbsent(r: Reservation) { absForm.value = { id: r.id, reason: '' }; absVisible.value = true }
async function handleComplete() { try { await request.post(`/reservations/${compForm.value.id}/complete`, compForm.value); showToast('练车完成'); compVisible.value = false; fetchReservations(); fetchOverview() } catch (e: any) { showToast(e?.response?.data?.message||'操作失败','error') } }
async function handleAbsent() { try { await request.post(`/reservations/${absForm.value.id}/absent`, absForm.value); showToast('已标记缺席'); absVisible.value = false; fetchReservations(); fetchOverview() } catch (e: any) { showToast(e?.response?.data?.message||'操作失败','error') } }
function handleLogout() { authStore.logout(); router.push('/login') }

const slotLabel: Record<string, string> = { MORNING: '上午', AFTERNOON: '下午' }
const isToday = () => selDate.value === new Date().toISOString().slice(0, 10)
const isTomorrow = () => { const d = new Date(); d.setDate(d.getDate() + 1); return selDate.value === d.toISOString().slice(0, 10) }
</script>

<template>
  <div class="min-h-screen bg-gray-50 flex">
    <aside class="w-56 bg-ink-800 text-white flex flex-col flex-shrink-0">
      <div class="px-5 py-5 border-b border-white/10">
        <AppLogo dark /><p class="text-white/30 text-[10px] mt-1.5 tracking-wide">智能练车调度平台</p>
      </div>
      <nav class="flex-1 px-3 py-4"><a class="flex items-center gap-3 px-3 py-2.5 rounded-lg bg-white/10 text-white text-sm font-medium">工作台</a></nav>
      <div class="px-3 py-3 border-t border-white/10"><button @click="handleLogout" class="text-white/45 hover:text-white/80 text-sm w-full text-left px-3 py-2">退出系统</button></div>
    </aside>
    <div class="flex-1 flex flex-col min-w-0">
      <header class="bg-white border-b border-gray-100 px-6 py-3 flex items-center justify-between">
        <div><span class="text-sm font-semibold text-gray-700">教练工作台</span><p class="text-[11px] text-gray-400 mt-0.5">查看预约安排、处理练车完成与缺席</p></div>
        <UserIdentity />
      </header>
      <main class="flex-1 p-6 space-y-6">
        <!-- 统计卡片 -->
        <div v-if="overview" class="grid grid-cols-2 md:grid-cols-6 gap-3">
          <div class="card p-4 text-center"><div class="text-[11px] text-gray-400 mb-0.5">今日预约</div><div class="text-xl font-bold text-primary-600">{{ overview.todayReservations }}</div></div>
          <div class="card p-4 text-center"><div class="text-[11px] text-gray-400 mb-0.5">今日完成</div><div class="text-xl font-bold text-green-600">{{ overview.todayCompleted }}</div></div>
          <div class="card p-4 text-center"><div class="text-[11px] text-gray-400 mb-0.5">今日缺席</div><div class="text-xl font-bold text-red-500">{{ overview.todayAbsent }}</div></div>
          <div class="card p-4 text-center"><div class="text-[11px] text-gray-400 mb-0.5">待处理</div><div class="text-xl font-bold text-amber-600">{{ overview.upcomingReservations }}</div></div>
          <div class="card p-4 text-center"><div class="text-[11px] text-gray-400 mb-0.5">累计完成</div><div class="text-xl font-bold text-ink-600">{{ overview.completedTrainingCount }}</div></div>
          <div class="card p-4 text-center"><div class="text-[11px] text-gray-400 mb-0.5">调剂接收</div><div class="text-xl font-bold text-purple-600">{{ overview.adjustmentReceivedCount }}</div></div>
        </div>

        <div class="grid grid-cols-1 xl:grid-cols-3 gap-6">
          <!-- 预约处理区 xl:2列 -->
          <div class="xl:col-span-2 card p-5">
            <h3 class="text-sm font-semibold text-gray-700 mb-1">预约处理</h3>
            <p class="text-xs text-gray-400 mb-4">按日期查看预约，提前掌握训练安排</p>
            <div class="flex items-center gap-3 mb-4">
              <button @click="goDate(0)" :class="isToday()?'bg-primary-500 text-white':'bg-gray-100 text-gray-600'" class="px-3 py-1.5 text-xs rounded-lg transition-colors">今天</button>
              <button @click="goDate(1)" :class="isTomorrow()?'bg-primary-500 text-white':'bg-gray-100 text-gray-600'" class="px-3 py-1.5 text-xs rounded-lg transition-colors">明天</button>
              <input v-model="selDate" type="date" @change="fetchReservations" class="px-3 py-1.5 text-xs border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
            </div>
            <div class="space-y-2">
              <div v-if="!todayList.length" class="text-center py-8 text-sm text-gray-400">该日期暂无预约安排</div>
              <div v-for="r in todayList" :key="r.id" class="flex items-center justify-between p-3 bg-gray-50 rounded-xl">
                <div class="min-w-0">
                  <div class="flex items-center gap-2"><span class="text-sm font-medium text-gray-800">{{ r.studentName }}</span><span v-if="r.isAdjusted" class="text-[10px] bg-amber-100 text-amber-700 px-1.5 py-0.5 rounded">调剂</span></div>
                  <div class="text-xs text-gray-400 mt-0.5">{{ r.studentPhone }} · {{ slotLabel[r.timeSlot] }} · {{ r.plateNumber || '未绑定' }}</div>
                </div>
                <div class="flex items-center gap-3 flex-shrink-0">
                  <span :class="r.status==='SUCCESS'?'bg-blue-100 text-blue-700':r.status==='COMPLETED'?'bg-green-100 text-green-700':r.status==='ABSENT'?'bg-red-100 text-red-700':'bg-gray-100 text-gray-500'" class="text-xs font-medium px-2 py-0.5 rounded-full">{{ r.status==='SUCCESS'?'待练车':r.status==='COMPLETED'?'已完成':r.status==='ABSENT'?'缺席':r.status }}</span>
                  <template v-if="r.status==='SUCCESS'">
                    <button @click="openComplete(r)" class="text-xs text-green-600 hover:text-green-800 font-medium">完成</button>
                    <button @click="openAbsent(r)" class="text-xs text-red-500 hover:text-red-700 font-medium">缺席</button>
                  </template>
                </div>
              </div>
            </div>
          </div>

          <!-- 右侧面板 -->
          <div class="space-y-6">
            <!-- 我的学员 -->
            <div class="card p-5">
              <h3 class="text-sm font-semibold text-gray-700 mb-3">我的学员</h3>
              <div v-if="!myStudents.length" class="text-center py-6 text-xs text-gray-400">暂无绑定学员</div>
              <div v-else class="space-y-2">
                <div v-for="s in myStudents" :key="s.id" class="flex items-center justify-between py-1.5 border-b border-gray-50 last:border-0">
                  <div><div class="text-sm font-medium text-gray-700">{{ s.name }}</div><div class="text-[11px] text-gray-400">{{ s.phone }}</div></div>
                  <div class="text-right"><div class="text-xs text-gray-500">{{ s.subjectType === 'SUBJECT_2' ? '科目二' : '科目三' }}</div><div class="text-[11px] text-primary-600 font-medium">已完成 {{ s.completedTrainingCount }} 次</div></div>
                </div>
              </div>
            </div>
            <!-- 最近练车 -->
            <div class="card p-5">
              <h3 class="text-sm font-semibold text-gray-700 mb-3">最近练车记录</h3>
              <div v-if="!recentRecords.length" class="text-center py-6 text-xs text-gray-400">暂无记录</div>
              <div v-else class="space-y-2">
                <div v-for="r in recentRecords" :key="r.id" class="text-sm py-1.5 border-b border-gray-50 last:border-0">
                  <div class="flex justify-between"><span class="font-medium text-gray-700">{{ r.studentName }}</span><span class="text-xs text-gray-400">{{ r.trainingDate }}</span></div>
                  <div class="text-xs text-gray-500 mt-0.5 truncate">{{ r.trainingContent || r.coachComment || '-' }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>

  <!-- 完成弹窗 (unchanged) -->
  <div v-if="compVisible" class="fixed inset-0 z-50 flex items-center justify-center"><div class="absolute inset-0 bg-black/30" @click="compVisible=false"/><div class="relative bg-white rounded-2xl shadow-xl w-full max-w-md mx-4 p-6"><h3 class="text-base font-semibold mb-4">完成练车</h3><div class="space-y-3"><div><label class="text-xs text-gray-500">练车内容</label><input v-model="compForm.trainingContent" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg mt-1" placeholder="例：倒车入库练习"/></div><div><label class="text-xs text-gray-500">练车时长（分钟）</label><input v-model.number="compForm.durationMinutes" type="number" min="1" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg mt-1" placeholder="120"/></div><div><label class="text-xs text-gray-500">备注</label><textarea v-model="compForm.coachRemark" rows="2" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg mt-1" placeholder="练车评语"/></div></div><div class="flex justify-end gap-3 mt-4"><button @click="compVisible=false" class="px-4 py-2 text-sm bg-gray-100 rounded-lg">取消</button><button @click="handleComplete" class="px-4 py-2 text-sm bg-green-600 text-white rounded-lg">确认完成</button></div></div></div>
  <div v-if="absVisible" class="fixed inset-0 z-50 flex items-center justify-center"><div class="absolute inset-0 bg-black/30" @click="absVisible=false"/><div class="relative bg-white rounded-2xl shadow-xl w-full max-w-md mx-4 p-6"><h3 class="text-base font-semibold mb-4">标记缺席</h3><div class="space-y-3"><div><label class="text-xs text-gray-500">原因</label><input v-model="absForm.reason" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg mt-1" placeholder="学员未按时到场"/></div></div><div class="flex justify-end gap-3 mt-4"><button @click="absVisible=false" class="px-4 py-2 text-sm bg-gray-100 rounded-lg">取消</button><button @click="handleAbsent" class="px-4 py-2 text-sm bg-red-600 text-white rounded-lg">确认缺席</button></div></div></div>
</template>
