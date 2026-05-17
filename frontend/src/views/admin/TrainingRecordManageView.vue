<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/api/request'
import type { ApiResult, PageResult } from '@/types/common'
import { getCoachSimpleList } from '@/api/coach'
import type { CoachSimple } from '@/types/coach'
import PageHeader from '@/components/PageHeader.vue'

interface TrainingRecordVO { id:number; reservationId:number; studentName:string; coachName:string; plateNumber:string; trainingDate:string; timeSlot:string; trainingContent:string; result:string; coachComment:string; createTime:string }
const list = ref<TrainingRecordVO[]>([])
const total = ref(0)
const loading = ref(false)
const coachOptions = ref<CoachSimple[]>([])
const query = ref({ page:1, size:10, studentName:'', coachId:undefined as number|undefined, startDate:'' as string, endDate:'' as string })

onMounted(async () => { fetchList(); try { const c=await getCoachSimpleList(); coachOptions.value=c.data.data||[] } catch {} })

async function fetchList() {
  loading.value=true
  try { const r=await request.get<ApiResult<PageResult<TrainingRecordVO>>>('/training-records',{params:query.value}); list.value=r.data.data?.records||[]; total.value=r.data.data?.total||0 } finally { loading.value=false }
}
const slotLabel: Record<string, string> = { MORNING: '上午', AFTERNOON: '下午' }
</script>
<template>
  <div>
    <PageHeader title="练车记录" subtitle="查看教练完成的练车记录与学员训练沉淀" />
    <div class="flex flex-wrap items-center gap-3 mb-5">
      <input v-model="query.studentName" @keyup.enter="query.page=1;fetchList()" placeholder="搜索学员" class="px-3 py-2 text-sm border border-gray-200 rounded-lg w-36 outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500"/>
      <select v-model="query.coachId" @change="query.page=1;fetchList()" class="px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none"><option :value="undefined">全部教练</option><option v-for="c in coachOptions" :key="c.id" :value="c.id">{{ c.name }}</option></select>
      <input v-model="query.startDate" type="date" @change="query.page=1;fetchList()" class="px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none"/>
      <span class="text-gray-400">至</span>
      <input v-model="query.endDate" type="date" @change="query.page=1;fetchList()" class="px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none"/>
    </div>
    <div class="card overflow-hidden">
      <table class="w-full text-sm"><thead class="bg-gray-50 text-gray-500"><tr><th class="text-left px-4 py-3">学员</th><th class="text-left px-4 py-3">教练</th><th class="text-left px-4 py-3">日期</th><th class="text-center px-4 py-3">时段</th><th class="text-left px-4 py-3">内容</th></tr></thead>
        <tbody class="divide-y divide-gray-50"><tr v-if="loading"><td colspan="5" class="text-center py-10 text-gray-400">加载中...</td></tr><tr v-else-if="!list.length"><td colspan="5" class="text-center py-12 text-sm text-gray-400">暂无练车记录，教练完成练车后将自动生成</td></tr>
          <tr v-for="r in list" :key="r.id" class="hover:bg-gray-50/50"><td class="px-4 py-3 font-medium text-gray-800">{{ r.studentName }}</td><td class="px-4 py-3 text-gray-600">{{ r.coachName }}</td><td class="px-4 py-3 text-gray-600">{{ r.trainingDate }}</td><td class="px-4 py-3 text-center"><span :class="r.timeSlot==='MORNING'?'bg-blue-50 text-blue-600':'bg-amber-50 text-amber-600'" class="text-xs font-medium px-2 py-0.5 rounded-full">{{ slotLabel[r.timeSlot]||r.timeSlot }}</span></td><td class="px-4 py-3 text-gray-600 max-w-xs truncate">{{ r.trainingContent||r.coachComment||'-' }}</td></tr>
        </tbody>
      </table>
    </div>
    <div class="flex items-center justify-between mt-4 text-sm text-gray-500"><span>共 {{ total }} 条</span><div class="flex gap-2"><button :disabled="query.page<=1" @click="query.page--;fetchList()" class="px-3 py-1 border rounded-lg disabled:opacity-30">上一页</button><button :disabled="query.page*query.size>=total" @click="query.page++;fetchList()" class="px-3 py-1 border rounded-lg disabled:opacity-30">下一页</button></div></div>
  </div>
</template>
