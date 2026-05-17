<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getVehicleList, createVehicle, updateVehicle, updateVehicleStatus } from '@/api/vehicle'
import { getCoachSimpleList } from '@/api/coach'
import type { Vehicle, VehicleCreateDTO, VehicleUpdateDTO } from '@/types/vehicle'
import type { CoachSimple } from '@/types/coach'
import PageModal from '@/components/PageModal.vue'
import PageHeader from '@/components/PageHeader.vue'
import EmptyState from '@/components/EmptyState.vue'
import { showToast } from '@/utils/toast'

const list = ref<Vehicle[]>([])
const total = ref(0)
const loading = ref(false)
const coachOptions = ref<CoachSimple[]>([])
const query = ref({ page: 1, size: 10, keyword: '', coachId: undefined as number | undefined, status: '' })

const modalVisible = ref(false)
const modalTitle = ref('')
const editingVehicle = ref<Vehicle | null>(null)
const form = ref<VehicleCreateDTO>({ plateNumber: '', coachId: undefined, vehicleType: 'C1', remark: '' })

onMounted(async () => {
  fetchList()
  try { const res = await getCoachSimpleList(); coachOptions.value = res.data.data || [] } catch { /* 降级 */ }
})

async function fetchList() {
  loading.value = true
  try {
    const res = await getVehicleList(query.value)
    list.value = res.data.data?.records || []
    total.value = res.data.data?.total || 0
  } catch { /* 保持原有列表 */ }
  finally { loading.value = false }
}

function openCreate() {
  editingVehicle.value = null
  modalTitle.value = '新增车辆'
  form.value = { plateNumber: '', coachId: coachOptions.value[0]?.id, vehicleType: 'C1', remark: '' }
  modalVisible.value = true
}

function openEdit(v: Vehicle) {
  editingVehicle.value = v
  modalTitle.value = '编辑车辆'
  form.value = { plateNumber: v.plateNumber, coachId: v.coachId, vehicleType: v.vehicleType, remark: v.remark || '' }
  modalVisible.value = true
}

async function handleConfirm() {
  try {
    if (editingVehicle.value) {
      await updateVehicle(editingVehicle.value.id, form.value as VehicleUpdateDTO)
      showToast('车辆修改成功')
    } else {
      await createVehicle(form.value)
      query.value.page = 1
      query.value.keyword = ''
      query.value.coachId = undefined
      query.value.status = ''
      showToast('车辆新增成功')
    }
    modalVisible.value = false
    await fetchList()
  } catch (err: any) {
    const msg = err?.response?.data?.message || '操作失败，请稍后重试'
    showToast(msg, 'error')
  }
}

async function handleStatusChange(v: Vehicle, status: string) {
  try {
    await updateVehicleStatus(v.id, status)
    showToast('状态已更新')
    await fetchList()
  } catch (err: any) {
    showToast(err?.response?.data?.message || '状态修改失败', 'error')
  }
}

const statusMap: Record<string, string> = { NORMAL: '正常', MAINTENANCE: '维修中', STOPPED: '停用' }
const statusColor: Record<string, string> = { NORMAL: 'bg-green-100 text-green-700', MAINTENANCE: 'bg-yellow-100 text-yellow-700', STOPPED: 'bg-red-100 text-red-700' }
</script>

<template>
  <div>
    <PageHeader title="车辆管理" subtitle="维护训练车辆、车辆状态与教练绑定关系" />
    <div class="card p-4 mb-4 flex flex-wrap items-center gap-3">
      <input v-model="query.keyword" @keyup.enter="query.page=1;fetchList()" placeholder="搜索车牌号"
        class="px-3 py-2 text-sm border border-gray-200 rounded-lg w-52 outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
      <select v-model="query.coachId" @change="query.page=1;fetchList()"
        class="px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none">
        <option :value="undefined">全部教练</option>
        <option v-for="co in coachOptions" :key="co.id" :value="co.id">{{ co.name }}</option>
      </select>
      <select v-model="query.status" @change="query.page=1;fetchList()"
        class="px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none">
        <option value="">全部状态</option>
        <option value="NORMAL">正常</option>
        <option value="MAINTENANCE">维修中</option>
        <option value="STOPPED">停用</option>
      </select>
      <div class="flex-1" />
      <button @click="openCreate" class="px-4 py-2 bg-ink-600 text-white text-sm font-medium rounded-lg hover:bg-ink-700 transition-colors">+ 新增车辆</button>
    </div>

    <div class="card overflow-hidden">
      <div class="px-4 py-3 border-b border-gray-100 text-xs text-gray-400">共 {{ total }} 条记录</div>
      <table class="w-full text-sm">
        <thead class="bg-gray-50 text-gray-500">
          <tr>
            <th class="text-left px-4 py-3 font-medium">车牌号</th>
            <th class="text-left px-4 py-3 font-medium">绑定教练</th>
            <th class="text-center px-4 py-3 font-medium">车型</th>
            <th class="text-center px-4 py-3 font-medium">状态</th>
            <th class="text-right px-4 py-3 font-medium">操作</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-50">
          <tr v-if="loading"><td colspan="5" class="text-center py-10 text-gray-400">加载中...</td></tr>
          <tr v-else-if="!list.length"><td colspan="5"><EmptyState title="暂无车辆数据" description="可新增训练车辆并绑定教练" /></td></tr>
          <tr v-for="v in list" :key="v.id" class="hover:bg-gray-50/50">
            <td class="px-4 py-3 font-medium text-gray-800">{{ v.plateNumber }}</td>
            <td class="px-4 py-3 text-gray-600">{{ v.coachName || '未绑定' }}</td>
            <td class="px-4 py-3 text-center text-gray-600">{{ v.vehicleType }}</td>
            <td class="px-4 py-3 text-center">
              <select :value="v.status" @change="handleStatusChange(v, ($event.target as HTMLSelectElement).value)"
                :class="['text-xs font-medium rounded-full px-2.5 py-1 outline-none border-0 cursor-pointer', statusColor[v.status] || 'bg-gray-100']">
                <option v-for="(l,val) in statusMap" :key="val" :value="val">{{ l }}</option>
              </select>
            </td>
            <td class="px-4 py-3 text-right">
              <button @click="openEdit(v)" class="text-xs text-primary-600 hover:text-primary-800 font-medium mr-3">编辑</button>
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

    <PageModal :visible="modalVisible" :title="modalTitle" @close="modalVisible = false" @confirm="handleConfirm">
      <div class="space-y-4">
        <div>
          <label class="block text-xs font-medium text-gray-500 mb-1">车牌号 *</label>
          <input v-model="form.plateNumber" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
        </div>
        <div>
          <label class="block text-xs font-medium text-gray-500 mb-1">绑定教练</label>
          <select v-model.number="form.coachId" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none">
            <option :value="undefined">不绑定</option>
            <option v-for="co in coachOptions" :key="co.id" :value="co.id">{{ co.name }}</option>
          </select>
        </div>
        <div>
          <label class="block text-xs font-medium text-gray-500 mb-1">车型</label>
          <input v-model="form.vehicleType" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
        </div>
        <div>
          <label class="block text-xs font-medium text-gray-500 mb-1">备注</label>
          <textarea v-model="form.remark" rows="2" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
        </div>
      </div>
    </PageModal>
  </div>
</template>
