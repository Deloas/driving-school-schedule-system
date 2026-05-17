<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getReservationOptions, createReservation, getMyReservations, cancelReservation } from '@/api/reservation'
import request from '@/api/request'
import type { ApiResult, PageResult } from '@/types/common'
import type { ReservationOption, Reservation, AdjustOption } from '@/types/reservation'
import { showToast } from '@/utils/toast'
import { getStudentOverview } from '@/api/statistics'
import type { StudentOverview } from '@/types/statistics'

const date = ref('')
const timeSlot = ref<'MORNING' | 'AFTERNOON'>('MORNING')
const option = ref<ReservationOption | null>(null)
const loading = ref(false)
const booking = ref(false)
const stuOverview = ref<StudentOverview | null>(null)
const recentTraining = ref<any[]>([])
onMounted(async () => {
  try { stuOverview.value = (await getStudentOverview()).data.data } catch {}
  try { const r = await request.get<ApiResult<PageResult<any>>>('/training-records/my', { params: { page: 1, size: 1 } }); recentTraining.value = r.data.data?.records || [] } catch {}
})
const myList = ref<Reservation[]>([])
const myTotal = ref(0)
fetchMyReservations()

async function handleQuery() { if (!date.value) { showToast('请选择日期', 'error'); return }; loading.value = true; try { option.value = (await getReservationOptions(date.value, timeSlot.value)).data.data || null } catch (e: any) { showToast(e?.response?.data?.message || '查询失败', 'error') } finally { loading.value = false } }
async function handleBook(adjusted: boolean, scheduleId: number, reason?: string) { booking.value = true; try { await createReservation(scheduleId, adjusted, reason); showToast(adjusted ? '调剂预约成功' : '预约成功'); option.value = null; await fetchMyReservations(); handleQuery() } catch (e: any) { showToast(e?.response?.data?.message || '预约失败', 'error') } finally { booking.value = false } }
function handleAdjustBook(opt: AdjustOption) { if (!confirm('该预约将由其他教练临时带练，原主教练关系不变。确定继续吗？')) return; handleBook(true, opt.scheduleId, '主教练当前时间段已满，选择其他教练临时练车') }
async function handleCancel(r: Reservation) { if (!confirm('确定取消本次预约吗？')) return; try { await cancelReservation(r.id, '学员主动取消'); showToast('已取消'); await fetchMyReservations() } catch (e: any) { showToast(e?.response?.data?.message || '取消失败', 'error') } }
async function fetchMyReservations() { try { const r = await getMyReservations(1, 10); myList.value = r.data.data?.records || []; myTotal.value = r.data.data?.total || 0 } catch {} }

const slotLabel: Record<string, string> = { MORNING: '上午', AFTERNOON: '下午' }
const statusLabel: Record<string, string> = { SUCCESS: '已预约', CANCELLED: '已取消', COMPLETED: '已完成', ABSENT: '缺席' }
const statusColor: Record<string, string> = { SUCCESS: 'bg-blue-100 text-blue-700', CANCELLED: 'bg-gray-100 text-gray-500', COMPLETED: 'bg-green-100 text-green-700', ABSENT: 'bg-red-100 text-red-700' }
</script>

<template>
  <div class="max-w-6xl mx-auto space-y-6">
    <!-- Hero -->
    <div class="card p-6">
      <h2 class="text-lg font-bold text-gray-800">我的练车工作台</h2>
      <p class="text-sm text-gray-400 mt-1">查看练车进度、预约安排与训练记录</p>
    </div>

    <!-- 下一次预约 + 最近训练 (prominent) -->
    <div v-if="stuOverview" class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <div class="card p-5 border-l-4 border-primary-500">
        <h3 class="text-sm font-semibold text-gray-700 mb-2">下一次练车安排</h3>
        <div v-if="stuOverview.nextReservation" class="text-base font-bold text-ink-700">{{ stuOverview.nextReservation }}</div>
        <div v-else class="text-sm text-gray-400">暂无后续预约，可选择时间进行预约</div>
      </div>
      <div class="card p-5 border-l-4 border-emerald-500">
        <h3 class="text-sm font-semibold text-gray-700 mb-2">最近练车记录</h3>
        <div v-if="stuOverview.recentTrainingRecord" class="text-base font-bold text-ink-700">{{ stuOverview.recentTrainingRecord }}</div>
        <div v-else class="text-sm text-gray-400">完成练车后将自动生成训练记录</div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div v-if="stuOverview" class="grid grid-cols-3 md:grid-cols-6 gap-3">
      <div class="card p-3 text-center"><div class="text-[11px] text-gray-400">已完成</div><div class="text-lg font-bold text-primary-600">{{ stuOverview.completedTrainingCount }}</div></div>
      <div class="card p-3 text-center"><div class="text-[11px] text-gray-400">预约中</div><div class="text-lg font-bold text-green-600">{{ stuOverview.successReservationCount }}</div></div>
      <div class="card p-3 text-center"><div class="text-[11px] text-gray-400">已取消</div><div class="text-lg font-bold text-gray-500">{{ stuOverview.cancelledReservationCount }}</div></div>
      <div class="card p-3 text-center"><div class="text-[11px] text-gray-400">缺席</div><div class="text-lg font-bold text-red-500">{{ stuOverview.absentCount }}</div></div>
      <div class="card p-3 text-center"><div class="text-[11px] text-gray-400">调剂</div><div class="text-lg font-bold text-amber-600">{{ stuOverview.adjustmentCount }}</div></div>
      <div class="card p-3 text-center"><div class="text-[11px] text-gray-400">下次预约</div><div class="text-xs font-medium text-gray-700 mt-1">{{ stuOverview.nextReservation || '暂无' }}</div></div>
    </div>

    <!-- 预约练车区 (existing) -->
    <div class="card p-6">
      <h3 class="text-sm font-semibold text-gray-700 mb-4">预约练车</h3>
      <div class="flex flex-wrap items-end gap-4">
        <div><label class="block text-xs font-medium text-gray-500 mb-1">日期</label><input v-model="date" type="date" class="px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" /></div>
        <div><label class="block text-xs font-medium text-gray-500 mb-1">时间段</label><div class="flex gap-2"><button @click="timeSlot='MORNING'" :class="timeSlot==='MORNING'?'bg-primary-500 text-white':'bg-gray-100 text-gray-600'" class="px-4 py-2 text-sm rounded-lg transition-colors">上午</button><button @click="timeSlot='AFTERNOON'" :class="timeSlot==='AFTERNOON'?'bg-primary-500 text-white':'bg-gray-100 text-gray-600'" class="px-4 py-2 text-sm rounded-lg transition-colors">下午</button></div></div>
        <button @click="handleQuery" :disabled="loading" class="px-5 py-2 bg-ink-600 text-white text-sm font-medium rounded-lg hover:bg-ink-700 disabled:opacity-50 transition-colors">{{ loading?'查询中...':'查询' }}</button>
      </div>

      <div v-if="option" class="mt-4 pt-4 border-t border-gray-100">
        <div class="flex items-start justify-between">
          <div><div class="font-semibold text-gray-800">{{ option.mainCoachOption.coachName || '--' }}</div><div class="text-sm text-gray-500 mt-0.5">{{ option.mainCoachOption.scheduleDate }} {{ slotLabel[option.mainCoachOption.timeSlot] || '' }}</div><div v-if="option.mainCoachOption.plateNumber" class="text-xs text-gray-400 mt-0.5">车辆：{{ option.mainCoachOption.plateNumber }}</div></div>
          <div class="text-right"><div class="flex items-center gap-2"><div class="w-20 bg-gray-100 rounded-full h-1.5"><div class="h-1.5 rounded-full" :class="option.mainCoachOption.remainCount>0?'bg-primary-500':'bg-red-400'" :style="{width:(option.mainCoachOption.currentStudents||0)/(option.mainCoachOption.maxStudents||1)*100+'%'}"/></div><span class="text-sm font-medium" :class="option.mainCoachOption.remainCount>0?'text-gray-700':'text-red-500'">{{ option.mainCoachOption.currentStudents||0 }}/{{ option.mainCoachOption.maxStudents }}</span></div><div class="mt-1.5"><span v-if="option.mainCoachOption.available" class="text-xs text-green-600 font-medium">✓ 可预约</span><span v-else class="text-xs text-red-500">{{ option.mainCoachOption.message }}</span></div></div>
        </div>
        <button v-if="option.mainCoachOption.available" @click="handleBook(false, option.mainCoachOption.scheduleId)" :disabled="booking" class="mt-3 w-full py-2.5 bg-primary-600 hover:bg-primary-700 text-white text-sm font-medium rounded-lg disabled:opacity-50 transition-colors">{{ booking?'预约中...':'立即预约' }}</button>
      </div>

      <!-- 调剂推荐 -->
      <div v-if="option?.adjustOptions?.length" class="mt-4 pt-4 border-t border-amber-200">
        <h4 class="text-sm font-semibold text-gray-700 mb-2">可调剂教练</h4>
        <div class="space-y-2">
          <div v-for="ao in option.adjustOptions" :key="ao.scheduleId" class="flex items-center justify-between p-3 bg-amber-50 rounded-xl border border-amber-100">
            <div><div class="text-sm font-semibold text-gray-800">{{ ao.coachName }}</div><div class="text-xs text-gray-500">剩余 {{ ao.remainCount }} 名额<span v-if="ao.plateNumber"> · {{ ao.plateNumber }}</span></div></div>
            <button @click="handleAdjustBook(ao)" :disabled="booking" class="px-3 py-1.5 bg-amber-500 hover:bg-amber-600 text-white text-xs font-medium rounded-lg disabled:opacity-50 transition-colors">选择调剂</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 我的预约记录 -->
    <div class="card p-6">
      <h3 class="text-sm font-semibold text-gray-700 mb-4">我的预约记录（{{ myTotal }}）</h3>
      <div v-if="!myList.length" class="text-center py-8 text-sm text-gray-400">暂无预约记录</div>
      <div v-else class="space-y-2">
        <div v-for="r in myList" :key="r.id" class="flex items-center justify-between p-3 bg-gray-50 rounded-xl">
          <div class="min-w-0"><div class="flex items-center gap-2"><span class="text-sm font-medium text-gray-800">{{ r.reservationDate }} {{ slotLabel[r.timeSlot] }}</span><span v-if="r.isAdjusted" class="text-[10px] bg-amber-100 text-amber-700 px-1.5 py-0.5 rounded">调剂</span></div><div class="text-xs text-gray-500 mt-0.5">教练：{{ r.actualCoachName }}<span v-if="r.plateNumber"> · {{ r.plateNumber }}</span></div></div>
          <div class="flex items-center gap-3 flex-shrink-0"><span :class="['text-xs font-medium px-2 py-0.5 rounded-full', statusColor[r.status]||'bg-gray-100']">{{ statusLabel[r.status]||r.status }}</span><button v-if="r.status==='SUCCESS'" @click="handleCancel(r)" class="text-xs text-red-500 hover:text-red-700 font-medium">取消</button></div>
        </div>
      </div>
    </div>
  </div>
</template>
