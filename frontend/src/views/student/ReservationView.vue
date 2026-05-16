<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getReservationOptions, createReservation, getMyReservations, cancelReservation } from '@/api/reservation'
import type { ReservationOption, Reservation, AdjustOption } from '@/types/reservation'
import { showToast } from '@/utils/toast'
import { getStudentOverview } from '@/api/statistics'
import type { StudentOverview } from '@/types/statistics'

/** 学员预约练车页 — M7：主教练满员后调剂推荐 */
const date = ref('')
const timeSlot = ref<'MORNING' | 'AFTERNOON'>('MORNING')
const option = ref<ReservationOption | null>(null)
const loading = ref(false)
const booking = ref(false)
const stuOverview = ref<StudentOverview | null>(null)
onMounted(async () => { try { stuOverview.value = (await getStudentOverview()).data.data } catch {} })
const myList = ref<Reservation[]>([])
const myTotal = ref(0)
fetchMyReservations()

async function handleQuery() {
  if (!date.value) { showToast('请选择日期', 'error'); return }
  loading.value = true
  try {
    const res = await getReservationOptions(date.value, timeSlot.value)
    option.value = res.data.data || null
  } catch (err: any) { showToast(err?.response?.data?.message || '查询失败', 'error') }
  finally { loading.value = false }
}

async function handleBook(adjusted: boolean, scheduleId: number, reason?: string) {
  booking.value = true
  try {
    await createReservation(scheduleId, adjusted, reason)
    showToast(adjusted ? '调剂预约成功，本次将由其他教练临时带练' : '预约成功')
    option.value = null
    await fetchMyReservations()
    handleQuery()
  } catch (err: any) { showToast(err?.response?.data?.message || '预约失败', 'error') }
  finally { booking.value = false }
}

function handleAdjustBook(opt: AdjustOption) {
  if (!confirm('该预约将由其他教练临时带练，原主教练关系不变。确定继续吗？')) return
  handleBook(true, opt.scheduleId, '主教练当前时间段已满，选择其他教练临时练车')
}

async function handleCancel(r: Reservation) {
  if (!confirm('确定取消本次预约吗？')) return
  try {
    await cancelReservation(r.id, '学员主动取消')
    showToast('已取消')
    await fetchMyReservations()
  } catch (err: any) { showToast(err?.response?.data?.message || '取消失败', 'error') }
}

async function fetchMyReservations() {
  try {
    const res = await getMyReservations(1, 10)
    myList.value = res.data.data?.records || []
    myTotal.value = res.data.data?.total || 0
  } catch { /* 降级 */ }
}

const slotLabel: Record<string, string> = { MORNING: '上午', AFTERNOON: '下午' }
const statusLabel: Record<string, string> = { SUCCESS: '已预约', CANCELLED: '已取消', COMPLETED: '已完成', ABSENT: '爽约' }
const statusColor: Record<string, string> = { SUCCESS: 'bg-green-100 text-green-700', CANCELLED: 'bg-gray-100 text-gray-500', COMPLETED: 'bg-blue-100 text-blue-700', ABSENT: 'bg-red-100 text-red-700' }
</script>

<template>
  <div class="max-w-4xl mx-auto">
    <!-- 我的练车进度 -->
    <div class="grid grid-cols-3 md:grid-cols-6 gap-3 mb-6" v-if="stuOverview">
      <div class="card p-3 text-center"><div class="text-xs text-gray-400">已完成</div><div class="text-lg font-bold text-primary-600">{{ stuOverview.completedTrainingCount }}</div></div>
      <div class="card p-3 text-center"><div class="text-xs text-gray-400">预约中</div><div class="text-lg font-bold text-green-600">{{ stuOverview.successReservationCount }}</div></div>
      <div class="card p-3 text-center"><div class="text-xs text-gray-400">已取消</div><div class="text-lg font-bold text-gray-500">{{ stuOverview.cancelledReservationCount }}</div></div>
      <div class="card p-3 text-center"><div class="text-xs text-gray-400">缺席</div><div class="text-lg font-bold text-red-500">{{ stuOverview.absentCount }}</div></div>
      <div class="card p-3 text-center"><div class="text-xs text-gray-400">调剂</div><div class="text-lg font-bold text-amber-600">{{ stuOverview.adjustmentCount }}</div></div>
      <div class="card p-3 text-center"><div class="text-xs text-gray-400">下次预约</div><div class="text-xs font-medium text-gray-700 mt-1">{{ stuOverview.nextReservation || '暂无' }}</div></div>
    </div>
    <div class="card p-6 mb-6">
      <h3 class="text-base font-semibold text-gray-700 mb-4">预约练车</h3>
      <div class="flex flex-wrap items-end gap-4">
        <div><label class="block text-xs font-medium text-gray-500 mb-1">日期</label><input v-model="date" type="date" class="px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" /></div>
        <div>
          <label class="block text-xs font-medium text-gray-500 mb-1">时间段</label>
          <div class="flex gap-2">
            <button @click="timeSlot='MORNING'" :class="timeSlot==='MORNING'?'bg-primary-500 text-white':'bg-gray-100 text-gray-600'" class="px-4 py-2 text-sm rounded-lg transition-colors">上午</button>
            <button @click="timeSlot='AFTERNOON'" :class="timeSlot==='AFTERNOON'?'bg-primary-500 text-white':'bg-gray-100 text-gray-600'" class="px-4 py-2 text-sm rounded-lg transition-colors">下午</button>
          </div>
        </div>
        <button @click="handleQuery" :disabled="loading" class="px-5 py-2 bg-ink-600 text-white text-sm font-medium rounded-lg hover:bg-ink-700 disabled:opacity-50 transition-colors">{{ loading ? '查询中...' : '查询' }}</button>
      </div>
    </div>

    <!-- 主教练卡片 -->
    <div v-if="option" class="card p-6 mb-6">
      <h3 class="text-base font-semibold text-gray-700 mb-4">我的主教练</h3>
      <div class="flex items-start justify-between">
        <div>
          <div class="text-lg font-semibold text-gray-800">{{ option.mainCoachOption.coachName || '--' }}</div>
          <div class="text-sm text-gray-500 mt-1">{{ option.mainCoachOption.scheduleDate }} {{ slotLabel[option.mainCoachOption.timeSlot] || '' }}</div>
          <div v-if="option.mainCoachOption.plateNumber" class="text-xs text-gray-400 mt-1">车辆：{{ option.mainCoachOption.plateNumber }}</div>
        </div>
        <div class="text-right">
          <div class="flex items-center gap-3">
            <div class="w-24 bg-gray-100 rounded-full h-2"><div class="h-2 rounded-full" :class="option.mainCoachOption.remainCount>0?'bg-primary-500':'bg-red-400'" :style="{width:(option.mainCoachOption.currentStudents||0)/(option.mainCoachOption.maxStudents||1)*100+'%'}" /></div>
            <span class="text-sm font-medium" :class="option.mainCoachOption.remainCount>0?'text-gray-700':'text-red-500'">{{ option.mainCoachOption.currentStudents||0 }}/{{ option.mainCoachOption.maxStudents }}</span>
            <span v-if="option.mainCoachOption.remainCount>0" class="text-xs text-primary-600 font-medium">剩{{ option.mainCoachOption.remainCount }}个名额</span>
          </div>
          <div class="mt-3">
            <span v-if="option.mainCoachOption.available" class="text-sm text-green-600 font-medium">✓ {{ option.mainCoachOption.message }}</span>
            <span v-else class="text-sm text-red-500 font-medium">{{ option.mainCoachOption.message }}</span>
          </div>
        </div>
      </div>
      <button v-if="option.mainCoachOption.available" @click="handleBook(false, option.mainCoachOption.scheduleId)" :disabled="booking" class="mt-4 w-full py-3 bg-primary-600 hover:bg-primary-700 text-white font-medium rounded-xl disabled:opacity-50 transition-colors">{{ booking?'预约中...':'立即预约' }}</button>
    </div>

    <!-- M7：可调剂教练推荐 -->
    <div v-if="option?.adjustOptions?.length" class="card p-6 mb-6 border border-amber-200">
      <h3 class="text-base font-semibold text-gray-700 mb-2">可调剂教练推荐</h3>
      <p class="text-xs text-gray-500 mb-4">你的主教练当前时间段已满员，以下教练仍有空余名额。调剂练车不会改变你的主教练归属。</p>
      <div class="space-y-3">
        <div v-for="ao in option.adjustOptions" :key="ao.scheduleId" class="flex items-center justify-between p-4 bg-amber-50 rounded-xl border border-amber-100">
          <div>
            <div class="text-sm font-semibold text-gray-800">{{ ao.coachName }}</div>
            <div class="text-xs text-gray-500 mt-0.5">剩余 <span class="text-primary-600 font-medium">{{ ao.remainCount }}</span> 个名额<span v-if="ao.plateNumber"> · {{ ao.plateNumber }}</span></div>
            <div class="text-xs text-amber-600 mt-0.5">{{ ao.recommendReason }}</div>
          </div>
          <button @click="handleAdjustBook(ao)" :disabled="booking" class="px-4 py-2 bg-amber-500 hover:bg-amber-600 text-white text-sm font-medium rounded-lg disabled:opacity-50 transition-colors">选择调剂</button>
        </div>
      </div>
    </div>

    <!-- 我的预约 -->
    <div class="card p-6">
      <h3 class="text-base font-semibold text-gray-700 mb-4">我的预约记录（{{ myTotal }}）</h3>
      <div v-if="!myList.length" class="text-center py-8 text-sm text-gray-400">暂无预约记录</div>
      <div v-else class="space-y-3">
        <div v-for="r in myList" :key="r.id" class="flex items-center justify-between p-4 bg-gray-50 rounded-xl">
          <div>
            <div class="flex items-center gap-2">
              <span class="text-sm font-medium text-gray-800">{{ r.reservationDate }} {{ slotLabel[r.timeSlot] }}</span>
              <span v-if="r.isAdjusted" class="text-xs bg-amber-100 text-amber-700 font-medium px-1.5 py-0.5 rounded">调剂</span>
            </div>
            <div class="text-xs text-gray-500 mt-0.5">
              教练：{{ r.actualCoachName }}
              <span v-if="r.isAdjusted && r.mainCoachName !== r.actualCoachName" class="text-amber-600">（主教练：{{ r.mainCoachName }}）</span>
              <span v-if="r.plateNumber"> · {{ r.plateNumber }}</span>
            </div>
          </div>
          <div class="flex items-center gap-3">
            <span :class="['text-xs font-medium px-2.5 py-1 rounded-full', statusColor[r.status]||'bg-gray-100']">{{ statusLabel[r.status]||r.status }}</span>
            <button v-if="r.status==='SUCCESS'" @click="handleCancel(r)" class="text-xs text-red-500 hover:text-red-700 font-medium">取消</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
