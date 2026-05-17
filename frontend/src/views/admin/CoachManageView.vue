<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getCoachList, createCoach, updateCoach, updateCoachStatus } from '@/api/coach'
import type { Coach, CoachCreateDTO, CoachUpdateDTO } from '@/types/coach'
import PageModal from '@/components/PageModal.vue'
import PageHeader from '@/components/PageHeader.vue'
import StatusBadge from '@/components/StatusBadge.vue'
import EmptyState from '@/components/EmptyState.vue'
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

async function fetchList() { loading.value = true; try { const r = await getCoachList(query.value); list.value = r.data.data?.records || []; total.value = r.data.data?.total || 0 } finally { loading.value = false } }

function openCreate() { editingCoach.value = null; modalTitle.value = '新增教练'; form.value = { name: '', phone: '', licenseNo: '', maxStudentsPerHalfDay: 5, remark: '' }; modalVisible.value = true }

function openEdit(c: Coach) { editingCoach.value = c; modalTitle.value = '编辑教练'; form.value = { name: c.name, phone: c.phone, licenseNo: c.licenseNo || '', maxStudentsPerHalfDay: c.maxStudentsPerHalfDay, remark: c.remark || '' }; modalVisible.value = true }

async function handleConfirm() { try { if (editingCoach.value) { await updateCoach(editingCoach.value.id, form.value as CoachUpdateDTO); showToast('修改成功') } else { await createCoach(form.value); query.value.page = 1; query.value.keyword = ''; query.value.status = ''; showToast('新增成功') } modalVisible.value = false; await fetchList() } catch (e: any) { showToast(e?.response?.data?.message || '操作失败', 'error') } }

async function handleStatusChange(c: Coach, status: string) { try { await updateCoachStatus(c.id, status); showToast('状态已更新'); await fetchList() } catch (e: any) { showToast(e?.response?.data?.message || '状态修改失败', 'error') } }

const statusMap: Record<string, string> = { NORMAL: '正常', LEAVE: '请假', STOPPED: '停用' }
const statusColor: Record<string, string> = { NORMAL: 'bg-green-100 text-green-700', LEAVE: 'bg-yellow-100 text-yellow-700', STOPPED: 'bg-gray-100 text-gray-600' }
</script>

<template>
  <div>
    <PageHeader title="教练管理" subtitle="维护教练基础资料、带教容量与账号状态" />

    <!-- 筛选区 -->
    <div class="card p-4 mb-4 flex flex-wrap items-center gap-3">
      <input v-model="query.keyword" @keyup.enter="query.page=1;fetchList()" placeholder="搜索姓名/手机号" class="px-3 py-2 text-sm border border-gray-200 rounded-lg w-52 outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
      <select v-model="query.status" @change="query.page=1;fetchList()" class="px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none">
        <option value="">全部状态</option><option value="NORMAL">正常</option><option value="LEAVE">请假</option><option value="STOPPED">停用</option>
      </select>
      <div class="flex-1" />
      <button @click="openCreate" class="px-4 py-2 bg-ink-600 text-white text-sm font-medium rounded-lg hover:bg-ink-700 transition-colors">+ 新增教练</button>
    </div>

    <!-- 表格卡片 -->
    <div class="card overflow-hidden">
      <div class="px-4 py-3 border-b border-gray-100 text-xs text-gray-400">共 {{ total }} 条记录</div>
      <table class="w-full text-sm">
        <thead class="bg-gray-50 text-gray-500"><tr><th class="text-left px-4 py-3 font-medium">姓名</th><th class="text-left px-4 py-3 font-medium">手机号</th><th class="text-left px-4 py-3 font-medium">教练证号</th><th class="text-center px-4 py-3 font-medium">每半天容量</th><th class="text-center px-4 py-3 font-medium">状态</th><th class="text-right px-4 py-3 font-medium">操作</th></tr></thead>
        <tbody class="divide-y divide-gray-50">
          <tr v-if="loading"><td colspan="6"><EmptyState title="加载中..." /></td></tr>
          <tr v-else-if="!list.length"><td colspan="6"><EmptyState title="暂无教练数据" description="点击「新增教练」维护带教资源" /></td></tr>
          <tr v-for="c in list" :key="c.id" class="hover:bg-gray-50/50" v-else>
            <td class="px-4 py-3 font-medium text-gray-800">{{ c.name }}</td>
            <td class="px-4 py-3 text-gray-600 text-sm">{{ c.phone }}</td>
            <td class="px-4 py-3 text-gray-500 text-sm">{{ c.licenseNo || '-' }}</td>
            <td class="px-4 py-3 text-center text-gray-700 text-sm">每半天 {{ c.maxStudentsPerHalfDay }} 人</td>
            <td class="px-4 py-3 text-center">
              <select :value="c.status" @change="handleStatusChange(c, ($event.target as HTMLSelectElement).value)" :class="['text-xs font-medium rounded-full px-2.5 py-1 outline-none border-0 cursor-pointer', statusColor[c.status] || 'bg-gray-100']">
                <option v-for="(l,v) in statusMap" :key="v" :value="v">{{ l }}</option>
              </select>
            </td>
            <td class="px-4 py-3 text-right"><button @click="openEdit(c)" class="text-xs text-primary-600 hover:text-primary-800 font-medium">编辑</button></td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="flex items-center justify-between mt-4 text-sm text-gray-500">
      <span>第 {{ query.page }} 页</span>
      <div class="flex gap-2"><button :disabled="query.page<=1" @click="query.page--;fetchList()" class="px-3 py-1 border rounded-lg disabled:opacity-30">上一页</button><button :disabled="query.page*query.size>=total" @click="query.page++;fetchList()" class="px-3 py-1 border rounded-lg disabled:opacity-30">下一页</button></div>
    </div>

    <PageModal :visible="modalVisible" :title="modalTitle" @close="modalVisible = false" @confirm="handleConfirm">
      <div class="space-y-4">
        <div><label class="block text-xs font-medium text-gray-500 mb-1">姓名 *</label><input v-model="form.name" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" /></div>
        <div><label class="block text-xs font-medium text-gray-500 mb-1">手机号 *</label><input v-model="form.phone" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" /></div>
        <div><label class="block text-xs font-medium text-gray-500 mb-1">教练证号</label><input v-model="form.licenseNo" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" /></div>
        <div><label class="block text-xs font-medium text-gray-500 mb-1">每半天最大带练人数 *</label><input v-model.number="form.maxStudentsPerHalfDay" type="number" min="1" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" /></div>
        <div><label class="block text-xs font-medium text-gray-500 mb-1">备注</label><textarea v-model="form.remark" rows="2" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" /></div>
      </div>
    </PageModal>
  </div>
</template>
