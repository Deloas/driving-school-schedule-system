<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getStudentList, createStudent, updateStudent, updateStudentStatus } from '@/api/student'
import { getCoachSimpleList } from '@/api/coach'
import type { Student, StudentCreateDTO, StudentUpdateDTO } from '@/types/student'
import type { CoachSimple } from '@/types/coach'
import PageModal from '@/components/PageModal.vue'
import { showToast } from '@/utils/toast'

const list = ref<Student[]>([])
const total = ref(0)
const loading = ref(false)
const coachOptions = ref<CoachSimple[]>([])
const query = ref({ page: 1, size: 10, keyword: '', coachId: undefined as number | undefined, status: '' })

const modalVisible = ref(false)
const modalTitle = ref('')
const editingStudent = ref<Student | null>(null)
const form = ref<StudentCreateDTO>({ name: '', phone: '', gender: '', mainCoachId: 0, subjectType: 'SUBJECT_2', requiredTrainingCount: 8, remark: '' })

onMounted(async () => {
  fetchList()
  try { const res = await getCoachSimpleList(); coachOptions.value = res.data.data || [] } catch { /* 降级 */ }
})

async function fetchList() {
  loading.value = true
  try {
    const res = await getStudentList(query.value)
    list.value = res.data.data?.records || []
    total.value = res.data.data?.total || 0
  } catch { /* 保持原有列表 */ }
  finally { loading.value = false }
}

function openCreate() {
  editingStudent.value = null
  modalTitle.value = '新增学员'
  form.value = { name: '', phone: '', gender: '', mainCoachId: coachOptions.value[0]?.id || 0, subjectType: 'SUBJECT_2', requiredTrainingCount: 8, remark: '' }
  modalVisible.value = true
}

function openEdit(s: Student) {
  editingStudent.value = s
  modalTitle.value = '编辑学员'
  form.value = { name: s.name, phone: s.phone, gender: s.gender || '', mainCoachId: s.mainCoachId, subjectType: s.subjectType, requiredTrainingCount: s.requiredTrainingCount, remark: s.remark || '' }
  modalVisible.value = true
}

async function handleConfirm() {
  try {
    if (editingStudent.value) {
      await updateStudent(editingStudent.value.id, form.value as StudentUpdateDTO)
      showToast('学员修改成功')
    } else {
      await createStudent(form.value)
      query.value.page = 1
      query.value.keyword = ''
      query.value.coachId = undefined
      query.value.status = ''
      showToast('学员新增成功')
    }
    modalVisible.value = false
    await fetchList()
  } catch (err: any) {
    const msg = err?.response?.data?.message || '操作失败，请稍后重试'
    showToast(msg, 'error')
  }
}

async function handleStatusChange(s: Student, status: string) {
  try {
    await updateStudentStatus(s.id, status)
    showToast('状态已更新')
    await fetchList()
  } catch (err: any) {
    showToast(err?.response?.data?.message || '状态修改失败', 'error')
  }
}

const statusMap: Record<string, string> = { NORMAL: '正常', STOPPED: '停用' }
</script>

<template>
  <div>
    <div class="flex flex-wrap items-center gap-3 mb-5">
      <input v-model="query.keyword" @keyup.enter="query.page=1;fetchList()" placeholder="搜索姓名/手机号"
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
        <option value="STOPPED">停用</option>
      </select>
      <div class="flex-1" />
      <button @click="openCreate" class="px-4 py-2 bg-ink-600 text-white text-sm font-medium rounded-lg hover:bg-ink-700 transition-colors">+ 新增学员</button>
    </div>

    <div class="card overflow-hidden">
      <table class="w-full text-sm">
        <thead class="bg-gray-50 text-gray-500">
          <tr>
            <th class="text-left px-4 py-3 font-medium">姓名</th>
            <th class="text-left px-4 py-3 font-medium">手机号</th>
            <th class="text-left px-4 py-3 font-medium">主教练</th>
            <th class="text-center px-4 py-3 font-medium">科目</th>
            <th class="text-center px-4 py-3 font-medium">练车进度</th>
            <th class="text-center px-4 py-3 font-medium">状态</th>
            <th class="text-right px-4 py-3 font-medium">操作</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-50">
          <tr v-if="loading"><td colspan="7" class="text-center py-10 text-gray-400">加载中...</td></tr>
          <tr v-else-if="!list.length"><td colspan="7" class="text-center py-10 text-gray-400">暂无数据</td></tr>
          <tr v-for="s in list" :key="s.id" class="hover:bg-gray-50/50">
            <td class="px-4 py-3 font-medium text-gray-800">{{ s.name }}</td>
            <td class="px-4 py-3 text-gray-600">{{ s.phone }}</td>
            <td class="px-4 py-3 text-gray-600">{{ s.mainCoachName }}</td>
            <td class="px-4 py-3 text-center">
              <span :class="s.subjectType==='SUBJECT_2'?'bg-blue-50 text-blue-600':'bg-purple-50 text-purple-600'" class="text-xs font-medium px-2 py-0.5 rounded-full">
                {{ s.subjectType === 'SUBJECT_2' ? '科目二' : '科目三' }}
              </span>
            </td>
            <td class="px-4 py-3 text-center">
              <div class="flex items-center gap-2 justify-center">
                <div class="w-20 bg-gray-100 rounded-full h-1.5">
                  <div class="bg-primary-500 h-1.5 rounded-full" :style="{ width: (s.completedTrainingCount / s.requiredTrainingCount * 100) + '%' }" />
                </div>
                <span class="text-xs text-gray-500">{{ s.completedTrainingCount }}/{{ s.requiredTrainingCount }}</span>
              </div>
            </td>
            <td class="px-4 py-3 text-center">
              <select :value="s.status" @change="handleStatusChange(s, ($event.target as HTMLSelectElement).value)"
                :class="['text-xs font-medium rounded-full px-2.5 py-1 outline-none border-0 cursor-pointer', s.status==='NORMAL'?'bg-green-100 text-green-700':'bg-red-100 text-red-700']">
                <option v-for="(l,v) in statusMap" :key="v" :value="v">{{ l }}</option>
              </select>
            </td>
            <td class="px-4 py-3 text-right">
              <button @click="openEdit(s)" class="text-xs text-primary-600 hover:text-primary-800 font-medium mr-3">编辑</button>
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
          <label class="block text-xs font-medium text-gray-500 mb-1">姓名 *</label>
          <input v-model="form.name" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
        </div>
        <div>
          <label class="block text-xs font-medium text-gray-500 mb-1">手机号 *</label>
          <input v-model="form.phone" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
        </div>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-xs font-medium text-gray-500 mb-1">性别</label>
            <select v-model="form.gender" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none">
              <option value="">未设置</option>
              <option value="男">男</option>
              <option value="女">女</option>
            </select>
          </div>
          <div>
            <label class="block text-xs font-medium text-gray-500 mb-1">科目类型</label>
            <select v-model="form.subjectType" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none">
              <option value="SUBJECT_2">科目二</option>
              <option value="SUBJECT_3">科目三</option>
            </select>
          </div>
        </div>
        <div>
          <label class="block text-xs font-medium text-gray-500 mb-1">主教练 *</label>
          <select v-model.number="form.mainCoachId" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none">
            <option v-for="co in coachOptions" :key="co.id" :value="co.id">{{ co.name }}</option>
          </select>
        </div>
        <div>
          <label class="block text-xs font-medium text-gray-500 mb-1">应完成练车次数</label>
          <input v-model.number="form.requiredTrainingCount" type="number" min="1" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
        </div>
        <div>
          <label class="block text-xs font-medium text-gray-500 mb-1">备注</label>
          <textarea v-model="form.remark" rows="2" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
        </div>
      </div>
    </PageModal>
  </div>
</template>
