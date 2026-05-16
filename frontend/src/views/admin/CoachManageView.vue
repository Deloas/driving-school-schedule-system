<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getCoachList, createCoach, updateCoach, updateCoachStatus } from '@/api/coach'
import type { Coach, CoachCreateDTO, CoachUpdateDTO } from '@/types/coach'
import PageModal from '@/components/PageModal.vue'
import { showToast } from '@/utils/toast'

const list = ref<Coach[]>([])
const total = ref(0)
const loading = ref(false)
const query = ref({ page: 1, size: 10, keyword: '', status: '' })

const modalVisible = ref(false)
const modalTitle = ref('')
const editingCoach = ref<Coach | null>(null)
const form = ref<CoachCreateDTO>({ name: '', phone: '', licenseNo: '', maxStudentsPerHalfDay: 5, remark: '' })

onMounted(() => fetchList())

async function fetchList() {
  loading.value = true
  try {
    const res = await getCoachList(query.value)
    list.value = res.data.data?.records || []
    total.value = res.data.data?.total || 0
  } catch { /* 请求失败保持原有列表 */ }
  finally { loading.value = false }
}

function openCreate() {
  editingCoach.value = null
  modalTitle.value = '新增教练'
  form.value = { name: '', phone: '', licenseNo: '', maxStudentsPerHalfDay: 5, remark: '' }
  modalVisible.value = true
}

function openEdit(coach: Coach) {
  editingCoach.value = coach
  modalTitle.value = '编辑教练'
  form.value = {
    name: coach.name, phone: coach.phone, licenseNo: coach.licenseNo || '',
    maxStudentsPerHalfDay: coach.maxStudentsPerHalfDay, remark: coach.remark || '',
  }
  modalVisible.value = true
}

async function handleConfirm() {
  try {
    if (editingCoach.value) {
      await updateCoach(editingCoach.value.id, form.value as CoachUpdateDTO)
      showToast('教练修改成功')
    } else {
      await createCoach(form.value)
      query.value.page = 1
      query.value.keyword = ''
      query.value.status = ''
      showToast('教练新增成功')
    }
    modalVisible.value = false
    await fetchList()
  } catch (err: any) {
    const msg = err?.response?.data?.message || '操作失败，请稍后重试'
    showToast(msg, 'error')
  }
}

async function handleStatusChange(coach: Coach, status: string) {
  try {
    await updateCoachStatus(coach.id, status)
    showToast('状态已更新')
    await fetchList()  // 必须 await
  } catch (err: any) {
    const msg = err?.response?.data?.message || '状态修改失败'
    showToast(msg, 'error')
  }
}

const statusMap: Record<string, string> = { NORMAL: '正常', LEAVE: '请假', STOPPED: '停用' }
const statusColorMap: Record<string, string> = { NORMAL: 'bg-green-100 text-green-700', LEAVE: 'bg-yellow-100 text-yellow-700', STOPPED: 'bg-red-100 text-red-700' }
</script>

<template>
  <div>
    <!-- 搜索和操作栏 -->
    <div class="flex flex-wrap items-center gap-3 mb-5">
      <input v-model="query.keyword" @keyup.enter="query.page=1;fetchList()" placeholder="搜索姓名/手机号"
        class="px-3 py-2 text-sm border border-gray-200 rounded-lg w-52 outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
      <select v-model="query.status" @change="query.page=1;fetchList()"
        class="px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none">
        <option value="">全部状态</option>
        <option value="NORMAL">正常</option>
        <option value="LEAVE">请假</option>
        <option value="STOPPED">停用</option>
      </select>
      <div class="flex-1" />
      <button @click="openCreate"
        class="px-4 py-2 bg-ink-600 text-white text-sm font-medium rounded-lg hover:bg-ink-700 transition-colors">
        + 新增教练
      </button>
    </div>

    <!-- 表格 -->
    <div class="card overflow-hidden">
      <table class="w-full text-sm">
        <thead class="bg-gray-50 text-gray-500">
          <tr>
            <th class="text-left px-4 py-3 font-medium">姓名</th>
            <th class="text-left px-4 py-3 font-medium">手机号</th>
            <th class="text-left px-4 py-3 font-medium">证号</th>
            <th class="text-center px-4 py-3 font-medium">半天最大人数</th>
            <th class="text-center px-4 py-3 font-medium">状态</th>
            <th class="text-right px-4 py-3 font-medium">操作</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-50">
          <tr v-if="loading"><td colspan="6" class="text-center py-10 text-gray-400">加载中...</td></tr>
          <tr v-else-if="!list.length"><td colspan="6" class="text-center py-10 text-gray-400">暂无数据</td></tr>
          <tr v-for="c in list" :key="c.id" class="hover:bg-gray-50/50">
            <td class="px-4 py-3 font-medium text-gray-800">{{ c.name }}</td>
            <td class="px-4 py-3 text-gray-600">{{ c.phone }}</td>
            <td class="px-4 py-3 text-gray-500">{{ c.licenseNo || '-' }}</td>
            <td class="px-4 py-3 text-center text-gray-700">{{ c.maxStudentsPerHalfDay }} 人</td>
            <td class="px-4 py-3 text-center">
              <select
                :value="c.status"
                @change="handleStatusChange(c, ($event.target as HTMLSelectElement).value)"
                :class="['text-xs font-medium rounded-full px-2.5 py-1 outline-none border-0 cursor-pointer', statusColorMap[c.status] || 'bg-gray-100']"
              >
                <option v-for="(label, val) in statusMap" :key="val" :value="val">{{ label }}</option>
              </select>
            </td>
            <td class="px-4 py-3 text-right">
              <button @click="openEdit(c)" class="text-xs text-primary-600 hover:text-primary-800 font-medium mr-3">编辑</button>
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

    <!-- 弹窗：@confirm 替代 @submit，避免原生事件冲突 -->
    <PageModal :visible="modalVisible" :title="modalTitle" @close="modalVisible = false" @confirm="handleConfirm">
      <div class="space-y-4">
        <div>
          <label class="block text-xs font-medium text-gray-500 mb-1">姓名 *</label>
          <input v-model="form.name" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
        </div>
        <div>
          <label class="block text-xs font-medium text-gray-500 mb-1">手机号 *</label>
          <input v-model="form.phone" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
        </div>
        <div>
          <label class="block text-xs font-medium text-gray-500 mb-1">教练证号</label>
          <input v-model="form.licenseNo" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
        </div>
        <div>
          <label class="block text-xs font-medium text-gray-500 mb-1">每半天最大带练人数 *</label>
          <input v-model.number="form.maxStudentsPerHalfDay" type="number" min="1" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
        </div>
        <div>
          <label class="block text-xs font-medium text-gray-500 mb-1">备注</label>
          <textarea v-model="form.remark" rows="2" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
        </div>
      </div>
    </PageModal>
  </div>
</template>
