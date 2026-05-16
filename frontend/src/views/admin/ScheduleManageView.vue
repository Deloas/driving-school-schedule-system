<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getScheduleList, createSchedule, updateSchedule, updateScheduleStatus, batchCreateSchedule } from '@/api/schedule'
import { getCoachSimpleList } from '@/api/coach'
import { getVehicleSimpleList } from '@/api/vehicle'
import type { Schedule, ScheduleCreateDTO, ScheduleUpdateDTO, ScheduleBatchDTO } from '@/types/schedule'
import type { CoachSimple } from '@/types/coach'
import type { VehicleSimple } from '@/types/vehicle'
import PageModal from '@/components/PageModal.vue'
import { showToast } from '@/utils/toast'

/** 排班管理页 */
const list = ref<Schedule[]>([])
const total = ref(0)
const loading = ref(false)
const coachOptions = ref<CoachSimple[]>([])
const vehicleOptions = ref<VehicleSimple[]>([])
const query = ref({ page: 1, size: 10, coachId: undefined as number | undefined, timeSlot: '', status: '', startDate: '' as string } as any)

// 新增/编辑弹窗
const modalVisible = ref(false)
const modalTitle = ref('')
const editingId = ref<number | null>(null)
const form = ref<ScheduleCreateDTO>({ coachId: 0, vehicleId: undefined, scheduleDate: '', timeSlot: 'MORNING', maxStudents: 5, remark: '' })

// 批量生成弹窗
const batchVisible = ref(false)
const batchForm = ref<ScheduleBatchDTO>({ coachIds: [], startDate: '', endDate: '', timeSlots: ['MORNING'], maxStudents: 5, autoBindVehicle: false, remark: '' })

onMounted(async () => {
  fetchList()
  const [c, v] = await Promise.all([getCoachSimpleList().catch(()=>({data:{data:[]}})), getVehicleSimpleList().catch(()=>({data:{data:[]}}))])
  coachOptions.value = c.data.data || []
  vehicleOptions.value = v.data.data || []
})

async function fetchList() {
  loading.value = true
  try {
    const res = await getScheduleList(query.value)
    list.value = res.data.data?.records || []
    total.value = res.data.data?.total || 0
  } finally { loading.value = false }
}

function openCreate() {
  editingId.value = null
  modalTitle.value = '新增排班'
  form.value = { coachId: coachOptions.value[0]?.id || 0, vehicleId: undefined, scheduleDate: '', timeSlot: 'MORNING', maxStudents: 5, remark: '' }
  modalVisible.value = true
}

function openEdit(s: Schedule) {
  editingId.value = s.id
  modalTitle.value = '编辑排班'
  form.value = { coachId: s.coachId, vehicleId: s.vehicleId, scheduleDate: s.scheduleDate, timeSlot: s.timeSlot, maxStudents: s.maxStudents, remark: s.remark || '' }
  modalVisible.value = true
}

async function handleConfirm() {
  try {
    if (editingId.value) {
      await updateSchedule(editingId.value, form.value as ScheduleUpdateDTO)
      showToast('排班修改成功')
    } else {
      await createSchedule(form.value)
      query.value.page = 1; query.value.coachId = undefined; query.value.timeSlot = ''; query.value.status = ''; query.value.startDate = ''
      showToast('排班新增成功')
    }
    modalVisible.value = false
    await fetchList()
  } catch (err: any) {
    showToast(err?.response?.data?.message || '操作失败', 'error')
  }
}

async function handleStatusChange(s: Schedule, status: string) {
  try {
    await updateScheduleStatus(s.id, status)
    showToast('状态已更新')
    await fetchList()
  } catch (err: any) {
    showToast(err?.response?.data?.message || '状态修改失败', 'error')
  }
}

async function handleBatchSubmit() {
  try {
    const res = await batchCreateSchedule(batchForm.value)
    const d = res.data.data
    showToast(`成功创建 ${d?.created || 0} 条，跳过 ${d?.skipped || 0} 条`)
    batchVisible.value = false
    await fetchList()
  } catch (err: any) {
    showToast(err?.response?.data?.message || '批量生成失败', 'error')
  }
}

const statusMap: Record<string, string> = { OPEN: '开放', CLOSED: '关闭', CANCELLED: '取消' }
const statusColor: Record<string, string> = { OPEN: 'bg-green-100 text-green-700', CLOSED: 'bg-gray-100 text-gray-600', CANCELLED: 'bg-red-100 text-red-700' }
const slotLabel: Record<string, string> = { MORNING: '上午', AFTERNOON: '下午' }
</script>

<template>
  <div>
    <!-- 搜索和操作栏 -->
    <div class="flex flex-wrap items-center gap-3 mb-5">
      <input v-model="query.startDate" type="date" @change="query.page=1;fetchList()"
        class="px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
      <select v-model="query.coachId" @change="query.page=1;fetchList()" class="px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none">
        <option :value="undefined">全部教练</option>
        <option v-for="c in coachOptions" :key="c.id" :value="c.id">{{ c.name }}</option>
      </select>
      <select v-model="query.timeSlot" @change="query.page=1;fetchList()" class="px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none">
        <option value="">全部时段</option>
        <option value="MORNING">上午</option>
        <option value="AFTERNOON">下午</option>
      </select>
      <select v-model="query.status" @change="query.page=1;fetchList()" class="px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none">
        <option value="">全部状态</option>
        <option value="OPEN">开放</option>
        <option value="CLOSED">关闭</option>
        <option value="CANCELLED">取消</option>
      </select>
      <div class="flex-1" />
      <button @click="batchVisible = true" class="px-4 py-2 border border-gray-200 text-gray-600 text-sm font-medium rounded-lg hover:bg-gray-100 transition-colors">批量生成</button>
      <button @click="openCreate" class="px-4 py-2 bg-ink-600 text-white text-sm font-medium rounded-lg hover:bg-ink-700 transition-colors">+ 新增排班</button>
    </div>

    <!-- 表格 -->
    <div class="card overflow-hidden">
      <table class="w-full text-sm">
        <thead class="bg-gray-50 text-gray-500">
          <tr>
            <th class="text-left px-4 py-3 font-medium">日期</th>
            <th class="text-left px-4 py-3 font-medium">教练</th>
            <th class="text-left px-4 py-3 font-medium">车辆</th>
            <th class="text-center px-4 py-3 font-medium">时段</th>
            <th class="text-center px-4 py-3 font-medium">容量</th>
            <th class="text-center px-4 py-3 font-medium">状态</th>
            <th class="text-right px-4 py-3 font-medium">操作</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-50">
          <tr v-if="loading"><td colspan="7" class="text-center py-10 text-gray-400">加载中...</td></tr>
          <tr v-else-if="!list.length"><td colspan="7" class="text-center py-10 text-gray-400">暂无排班数据</td></tr>
          <tr v-for="s in list" :key="s.id" class="hover:bg-gray-50/50">
            <td class="px-4 py-3 font-medium text-gray-800">{{ s.scheduleDate }}</td>
            <td class="px-4 py-3 text-gray-600">{{ s.coachName }}</td>
            <td class="px-4 py-3 text-gray-600">{{ s.plateNumber || '未绑定' }}</td>
            <td class="px-4 py-3 text-center">
              <span :class="s.timeSlot==='MORNING'?'bg-blue-50 text-blue-600':'bg-amber-50 text-amber-600'" class="text-xs font-medium px-2 py-0.5 rounded-full">{{ slotLabel[s.timeSlot] || s.timeSlot }}</span>
            </td>
            <td class="px-4 py-3 text-center">
              <div class="flex items-center gap-2 justify-center">
                <div class="w-16 bg-gray-100 rounded-full h-1.5">
                  <div class="h-1.5 rounded-full" :class="s.remainCount > 0 ? 'bg-primary-500' : 'bg-red-400'" :style="{ width: (s.currentStudents / s.maxStudents * 100) + '%' }" />
                </div>
                <span class="text-xs" :class="s.remainCount > 0 ? 'text-gray-500' : 'text-red-500 font-medium'">{{ s.currentStudents }}/{{ s.maxStudents }}</span>
              </div>
            </td>
            <td class="px-4 py-3 text-center">
              <select :value="s.status" @change="handleStatusChange(s, ($event.target as HTMLSelectElement).value)"
                :class="['text-xs font-medium rounded-full px-2.5 py-1 outline-none border-0 cursor-pointer', statusColor[s.status] || 'bg-gray-100']">
                <option v-for="(l,v) in statusMap" :key="v" :value="v">{{ l }}</option>
              </select>
            </td>
            <td class="px-4 py-3 text-right">
              <button @click="openEdit(s)" class="text-xs text-primary-600 hover:text-primary-800 font-medium">编辑</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="flex items-center justify-between mt-4 text-sm text-gray-500">
      <span>共 {{ total }} 条</span>
      <div class="flex gap-2">
        <button :disabled="query.page <= 1" @click="query.page--;fetchList()" class="px-3 py-1 border rounded-lg disabled:opacity-30">上一页</button>
        <button :disabled="query.page * query.size >= total" @click="query.page++;fetchList()" class="px-3 py-1 border rounded-lg disabled:opacity-30">下一页</button>
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <PageModal :visible="modalVisible" :title="modalTitle" @close="modalVisible = false" @confirm="handleConfirm">
      <div class="space-y-4">
        <div v-if="!editingId">
          <label class="block text-xs font-medium text-gray-500 mb-1">教练 *</label>
          <select v-model.number="form.coachId" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none">
            <option v-for="c in coachOptions" :key="c.id" :value="c.id">{{ c.name }}</option>
          </select>
        </div>
        <div v-if="!editingId">
          <label class="block text-xs font-medium text-gray-500 mb-1">日期 *</label>
          <input v-model="form.scheduleDate" type="date" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
        </div>
        <div v-if="!editingId">
          <label class="block text-xs font-medium text-gray-500 mb-1">时间段 *</label>
          <select v-model="form.timeSlot" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none">
            <option value="MORNING">上午</option>
            <option value="AFTERNOON">下午</option>
          </select>
        </div>
        <div>
          <label class="block text-xs font-medium text-gray-500 mb-1">绑定车辆</label>
          <select v-model.number="form.vehicleId" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none">
            <option :value="undefined">不绑定</option>
            <option v-for="v in vehicleOptions" :key="v.id" :value="v.id">{{ v.plateNumber }}</option>
          </select>
        </div>
        <div>
          <label class="block text-xs font-medium text-gray-500 mb-1">最大预约人数 *</label>
          <input v-model.number="form.maxStudents" type="number" min="1" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
        </div>
        <div>
          <label class="block text-xs font-medium text-gray-500 mb-1">备注</label>
          <textarea v-model="form.remark" rows="2" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
        </div>
      </div>
    </PageModal>

    <!-- 批量生成弹窗 -->
    <PageModal :visible="batchVisible" title="批量生成排班" @close="batchVisible = false" @confirm="handleBatchSubmit">
      <div class="space-y-4">
        <div>
          <label class="block text-xs font-medium text-gray-500 mb-1">选择教练 *（可多选）</label>
          <div class="max-h-32 overflow-y-auto border border-gray-200 rounded-lg p-2 space-y-1">
            <label v-for="c in coachOptions" :key="c.id" class="flex items-center gap-2 text-sm cursor-pointer hover:bg-gray-50 px-2 py-1 rounded">
              <input type="checkbox" :value="c.id" v-model="batchForm.coachIds" class="rounded" />
              {{ c.name }}
            </label>
          </div>
        </div>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-xs font-medium text-gray-500 mb-1">开始日期 *</label>
            <input v-model="batchForm.startDate" type="date" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
          </div>
          <div>
            <label class="block text-xs font-medium text-gray-500 mb-1">结束日期 *</label>
            <input v-model="batchForm.endDate" type="date" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
          </div>
        </div>
        <div>
          <label class="block text-xs font-medium text-gray-500 mb-1">时间段 *</label>
          <div class="flex gap-4">
            <label class="flex items-center gap-2 text-sm cursor-pointer">
              <input type="checkbox" value="MORNING" v-model="batchForm.timeSlots" /> 上午
            </label>
            <label class="flex items-center gap-2 text-sm cursor-pointer">
              <input type="checkbox" value="AFTERNOON" v-model="batchForm.timeSlots" /> 下午
            </label>
          </div>
        </div>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-xs font-medium text-gray-500 mb-1">最大人数 *</label>
            <input v-model.number="batchForm.maxStudents" type="number" min="1" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
          </div>
          <div class="flex items-end pb-2">
            <label class="flex items-center gap-2 text-sm cursor-pointer">
              <input type="checkbox" v-model="batchForm.autoBindVehicle" /> 自动绑定车辆
            </label>
          </div>
        </div>
      </div>
    </PageModal>
  </div>
</template>
