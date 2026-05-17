<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/api/request'
import type { ApiResult, PageResult } from '@/types/common'

interface TrainingRecordVO { id:number; reservationId:number; studentName:string; coachName:string; plateNumber:string; trainingDate:string; timeSlot:string; trainingContent:string; result:string; coachComment:string; createTime:string }

const list = ref<TrainingRecordVO[]>([])
const total = ref(0)
const loading = ref(false)
const query = ref({ page: 1, size: 10 })

onMounted(fetchList)

async function fetchList() {
  loading.value = true
  try { const r = await request.get<ApiResult<PageResult<TrainingRecordVO>>>('/training-records/my', { params: query.value }); list.value = r.data.data?.records || []; total.value = r.data.data?.total || 0 } finally { loading.value = false }
}

const slotLabel: Record<string, string> = { MORNING: '上午', AFTERNOON: '下午' }
</script>

<template>
  <div class="max-w-4xl mx-auto space-y-6">
    <div class="card p-6">
      <h2 class="text-lg font-bold text-gray-800">我的训练档案</h2>
      <p class="text-sm text-gray-400 mt-1">每次完成练车后，系统会自动沉淀训练记录</p>
    </div>

    <div v-if="loading" class="text-center py-10 text-gray-400 text-sm">加载中...</div>
    <div v-else-if="!list.length" class="card p-10 text-center">
      <div class="text-sm text-gray-400 mb-2">暂无练车记录</div>
      <p class="text-xs text-gray-300">完成练车后将自动生成训练记录</p>
    </div>
    <div v-else class="space-y-3">
      <div v-for="r in list" :key="r.id" class="card p-5">
        <div class="flex items-start justify-between mb-3">
          <div>
            <div class="flex items-center gap-2"><span class="font-semibold text-gray-800">{{ r.trainingDate }}</span><span class="text-xs bg-blue-50 text-blue-600 px-2 py-0.5 rounded-full">{{ slotLabel[r.timeSlot] || r.timeSlot }}</span></div>
            <div class="text-sm text-gray-500 mt-0.5">{{ r.coachName }}<span v-if="r.plateNumber"> · {{ r.plateNumber }}</span></div>
          </div>
          <span class="text-xs font-medium px-2 py-0.5 rounded-full bg-green-100 text-green-700">已完成</span>
        </div>
        <div v-if="r.trainingContent" class="bg-gray-50 rounded-lg p-3 text-sm text-gray-700 mb-2">{{ r.trainingContent }}</div>
        <div v-if="r.coachComment" class="text-xs text-gray-500">教练评语：{{ r.coachComment }}</div>
      </div>
    </div>
    <div class="flex items-center justify-between text-sm text-gray-500" v-if="total > 10">
      <span>共 {{ total }} 条</span>
      <div class="flex gap-2"><button :disabled="query.page<=1" @click="query.page--;fetchList()" class="px-3 py-1 border rounded-lg disabled:opacity-30">上一页</button><button :disabled="query.page*query.size>=total" @click="query.page++;fetchList()" class="px-3 py-1 border rounded-lg disabled:opacity-30">下一页</button></div>
    </div>
  </div>
</template>
