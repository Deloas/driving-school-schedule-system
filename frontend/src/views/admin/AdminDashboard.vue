<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAdminOverview, getCoachLoad, getReservationTrend, getScheduleUtilization, getStudentProgress, getRecentActivities } from '@/api/statistics'
import type { AdminOverview, CoachLoad, ReservationTrend, ScheduleUtilization, StudentProgress, RecentActivity } from '@/types/statistics'

const overview = ref<AdminOverview | null>(null)
const coachLoad = ref<CoachLoad[]>([])
const trend = ref<ReservationTrend[]>([])
const utilization = ref<ScheduleUtilization[]>([])
const progress = ref<StudentProgress[]>([])
const activities = ref<RecentActivity[]>([])

onMounted(async () => {
  try { overview.value = (await getAdminOverview()).data.data } catch {}
  try { coachLoad.value = (await getCoachLoad()).data.data || [] } catch {}
  try { trend.value = (await getReservationTrend()).data.data || [] } catch {}
  try { utilization.value = (await getScheduleUtilization()).data.data || [] } catch {}
  try { progress.value = (await getStudentProgress()).data.data || [] } catch {}
  try { activities.value = (await getRecentActivities()).data.data || [] } catch {}
})

function pct(v: number) { return v.toFixed(1) + '%' }
function maxVal(list: any[], key: string) { return Math.max(1, ...list.map((i: any) => i[key] || 0)) }
</script>

<template>
  <div class="space-y-6">
    <!-- 核心指标 -->
    <div class="grid grid-cols-2 md:grid-cols-4 gap-4" v-if="overview">
      <div class="card p-5"><div class="text-xs text-gray-400 mb-1">学员总数</div><div class="text-2xl font-bold text-primary-600">{{ overview.totalStudents }}</div></div>
      <div class="card p-5"><div class="text-xs text-gray-400 mb-1">教练总数</div><div class="text-2xl font-bold text-ink-600">{{ overview.totalCoaches }}</div></div>
      <div class="card p-5"><div class="text-xs text-gray-400 mb-1">车辆总数</div><div class="text-2xl font-bold text-accent-600">{{ overview.totalVehicles }}</div></div>
      <div class="card p-5"><div class="text-xs text-gray-400 mb-1">累计完成练车</div><div class="text-2xl font-bold text-emerald-600">{{ overview.completedTrainingCount }}</div></div>
    </div>

    <!-- 今日指标 -->
    <div class="grid grid-cols-2 md:grid-cols-4 gap-4" v-if="overview">
      <div class="card p-4"><div class="text-xs text-gray-400">今日预约</div><div class="text-xl font-bold text-gray-700 mt-1">{{ overview.todayReservations }}</div></div>
      <div class="card p-4"><div class="text-xs text-gray-400">今日完成</div><div class="text-xl font-bold text-green-600 mt-1">{{ overview.todayCompleted }}</div></div>
      <div class="card p-4"><div class="text-xs text-gray-400">今日缺席</div><div class="text-xl font-bold text-red-500 mt-1">{{ overview.todayAbsent }}</div></div>
      <div class="card p-4"><div class="text-xs text-gray-400">今日剩余名额</div><div class="text-xl font-bold text-primary-600 mt-1">{{ overview.todayAvailableSlots }}</div></div>
    </div>

    <!-- 教练负载 -->
    <div class="card p-6">
      <h3 class="text-sm font-semibold text-gray-700 mb-4">教练负载分布</h3>
      <div class="space-y-3" v-if="coachLoad.length">
        <div v-for="c in coachLoad" :key="c.coachId" class="flex items-center gap-4">
          <span class="text-sm text-gray-700 w-16">{{ c.coachName }}</span>
          <div class="flex-1 bg-gray-100 rounded-full h-2.5">
            <div class="h-2.5 rounded-full" :class="c.utilizationRate>80?'bg-red-400':c.utilizationRate>50?'bg-amber-400':'bg-primary-500'" :style="{width:Math.min(100,c.utilizationRate)+'%'}" />
          </div>
          <span class="text-xs text-gray-500 w-24 text-right">{{ c.usedCapacity }}/{{ c.capacity }} ({{ pct(c.utilizationRate) }})</span>
        </div>
      </div>
      <p class="text-xs text-gray-400 mt-3">调剂预约占比 {{ overview?.adjustmentRate || 0 }}%，体现教练资源弹性利用</p>
    </div>

    <!-- 近7天趋势 -->
    <div class="card p-6">
      <h3 class="text-sm font-semibold text-gray-700 mb-4">近 7 天预约趋势</h3>
      <div class="flex items-end gap-2 h-32" v-if="trend.length">
        <div v-for="t in trend" :key="t.date" class="flex-1 flex flex-col items-center gap-1">
          <div class="w-full flex flex-col justify-end" style="height:100px">
            <div class="bg-primary-400 rounded-t w-full" :style="{height:(t.reservationCount/maxVal(trend,'reservationCount')*100)+'%',minHeight:t.reservationCount>0?'4px':'0'}" />
          </div>
          <span class="text-xs text-gray-400">{{ (t.date||'').toString().slice(5) }}</span>
          <span class="text-xs font-medium text-gray-600">{{ t.reservationCount }}</span>
        </div>
      </div>
    </div>

    <!-- 容量利用率 -->
    <div class="card p-6">
      <h3 class="text-sm font-semibold text-gray-700 mb-4">排班容量利用率</h3>
      <div class="grid grid-cols-7 gap-2" v-if="utilization.length">
        <div v-for="u in utilization" :key="u.date" class="text-center">
          <div class="text-xs text-gray-400 mb-1">{{ (u.date||'').toString().slice(5) }}</div>
          <div class="text-sm font-semibold" :class="u.utilizationRate>80?'text-red-500':u.utilizationRate>50?'text-amber-500':'text-green-500'">{{ pct(u.utilizationRate) }}</div>
          <div class="text-xs text-gray-400">{{ u.usedCapacity }}/{{ u.totalCapacity }}</div>
        </div>
      </div>
    </div>

    <!-- 学员进度 + 动态 -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <div class="card p-6">
        <h3 class="text-sm font-semibold text-gray-700 mb-4">学员练车进度分布</h3>
        <div class="space-y-3" v-if="progress.length">
          <div v-for="p in progress" :key="p.rangeName" class="flex items-center gap-3">
            <span class="text-sm text-gray-600 w-20">{{ p.rangeName }}</span>
            <div class="flex-1 bg-gray-100 rounded-full h-2"><div class="bg-ink-500 h-2 rounded-full" :style="{width:(p.studentCount/Math.max(1,...progress.map(x=>x.studentCount))*100)+'%'}" /></div>
            <span class="text-sm font-medium text-gray-700">{{ p.studentCount }} 人</span>
          </div>
        </div>
      </div>
      <div class="card p-6">
        <h3 class="text-sm font-semibold text-gray-700 mb-4">最近动态</h3>
        <div class="space-y-2" v-if="activities.length">
          <div v-for="a in activities.slice(0,8)" :key="a.title" class="flex items-start gap-2 text-sm">
            <span :class="a.type==='COMPLETED'?'text-green-500':a.type==='ABSENT'?'text-red-500':a.type==='CANCELLED'?'text-gray-400':'text-primary-500'" class="mt-0.5">●</span>
            <div><span class="text-gray-700">{{ a.title }}</span><div v-if="a.description" class="text-xs text-gray-400">{{ a.description }}</div></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
