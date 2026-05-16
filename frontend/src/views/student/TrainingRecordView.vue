<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/api/request'
import type { ApiResult, PageResult } from '@/types/common'

interface TrainingRecordVO { id:number; reservationId:number; studentName:string; coachName:string; plateNumber:string; trainingDate:string; timeSlot:string; trainingContent:string; result:string; coachComment:string; createTime:string }

const list = ref<TrainingRecordVO[]>([])
const total = ref(0)
onMounted(async () => { try { const r = await request.get<ApiResult<PageResult<TrainingRecordVO>>>('/training-records/my',{params:{page:1,size:20}}); list.value=r.data.data?.records||[]; total.value=r.data.data?.total||0 } catch {} })

const slotLabel: Record<string, string> = { MORNING: '上午', AFTERNOON: '下午' }
</script>
<template>
  <div>
    <h3 class="text-base font-semibold text-gray-700 mb-4">我的练车记录（{{ total }}）</h3>
    <div v-if="!list.length" class="text-center py-10 text-gray-400">暂无练车记录</div>
    <div v-else class="space-y-3">
      <div v-for="r in list" :key="r.id" class="card p-4">
        <div class="flex items-center justify-between mb-2">
          <span class="text-sm font-semibold text-gray-800">{{ r.trainingDate }} {{ slotLabel[r.timeSlot]||r.timeSlot }}</span>
          <span class="text-xs font-medium px-2 py-0.5 rounded-full bg-blue-100 text-blue-700">已完成</span>
        </div>
        <div class="text-xs text-gray-500">教练：{{ r.coachName }} · 车辆：{{ r.plateNumber||'-' }}</div>
        <div v-if="r.trainingContent" class="text-sm text-gray-700 mt-2 bg-gray-50 rounded-lg p-2">{{ r.trainingContent }}</div>
        <div v-if="r.coachComment" class="text-xs text-gray-500 mt-1">教练评语：{{ r.coachComment }}</div>
      </div>
    </div>
  </div>
</template>
