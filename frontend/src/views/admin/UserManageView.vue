<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/api/request'
import type { ApiResult, PageResult } from '@/types/common'
import { showToast } from '@/utils/toast'
import { getCoachSimpleList } from '@/api/coach'
import { getStudentSimpleList } from '@/api/student'
import type { CoachSimple } from '@/types/coach'
import type { StudentSimple } from '@/types/student'
import PageModal from '@/components/PageModal.vue'

/**
 * 账号管理页 — 管理员为教练/学员开通登录账号
 */

interface UserVO { id: number; username: string; role: string; relatedId: number; relatedName: string; status: string; createTime: string }

const list = ref<UserVO[]>([])
const total = ref(0)
const loading = ref(false)
const query = ref({ page: 1, size: 10, keyword: '', role: '', status: '' })
const coachOptions = ref<CoachSimple[]>([])
const studentOptions = ref<StudentSimple[]>([])

// 创建弹窗
const modalVisible = ref(false)
const form = ref({ username: '', role: 'COACH' as string, relatedId: undefined as number | undefined })

// 重置密码弹窗
const pwdVisible = ref(false)
const pwdForm = ref({ userId: 0, username: '', newPassword: '' })

onMounted(async () => {
  fetchList()
  const [c, s] = await Promise.all([
    getCoachSimpleList().catch(() => ({ data: { data: [] } })),
    getStudentSimpleList().catch(() => ({ data: { data: [] } })),
  ])
  coachOptions.value = c.data.data || []
  studentOptions.value = s.data.data || []
})

async function fetchList() {
  loading.value = true
  try {
    const res = await request.get<ApiResult<PageResult<UserVO>>>('/users', { params: query.value })
    list.value = res.data.data?.records || []
    total.value = res.data.data?.total || 0
  } finally { loading.value = false }
}

function openCreate() {
  form.value = { username: '', role: 'COACH', relatedId: undefined }
  modalVisible.value = true
}

async function handleCreate() {
  try {
    const res = await request.post<ApiResult<UserVO>>('/users', form.value)
    showToast('账号创建成功，默认密码 123456')
    modalVisible.value = false
    fetchList()
  } catch (err: any) { showToast(err?.response?.data?.message || '创建失败', 'error') }
}

async function handleStatusChange(u: UserVO, status: string) {
  try {
    await request.patch(`/users/${u.id}/status`, { status })
    showToast('状态已更新')
    fetchList()
  } catch (err: any) { showToast(err?.response?.data?.message || '操作失败', 'error') }
}

function openResetPwd(u: UserVO) {
  pwdForm.value = { userId: u.id, username: u.username, newPassword: '' }
  pwdVisible.value = true
}

async function handleResetPwd() {
  try {
    const res = await request.patch<ApiResult<string>>(`/users/${pwdForm.value.userId}/password/reset`, { newPassword: pwdForm.value.newPassword || undefined })
    const msg = res.data.message || '密码已重置'
    showToast(msg)
    pwdVisible.value = false
  } catch (err: any) { showToast(err?.response?.data?.message || '重置失败', 'error') }
}

const roleLabel: Record<string, string> = { ADMIN: '管理员', COACH: '教练', STUDENT: '学员' }
</script>

<template>
  <div>
    <div class="flex flex-wrap items-center gap-3 mb-5">
      <input v-model="query.keyword" @keyup.enter="query.page=1;fetchList()" placeholder="搜索账号" class="px-3 py-2 text-sm border border-gray-200 rounded-lg w-40 outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
      <select v-model="query.role" @change="query.page=1;fetchList()" class="px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none">
        <option value="">全部角色</option><option value="ADMIN">管理员</option><option value="COACH">教练</option><option value="STUDENT">学员</option>
      </select>
      <select v-model="query.status" @change="query.page=1;fetchList()" class="px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none">
        <option value="">全部状态</option><option value="ENABLE">启用</option><option value="DISABLE">停用</option>
      </select>
      <div class="flex-1" />
      <button @click="openCreate" class="px-4 py-2 bg-ink-600 text-white text-sm font-medium rounded-lg hover:bg-ink-700 transition-colors">+ 创建账号</button>
    </div>

    <div class="card overflow-hidden">
      <table class="w-full text-sm">
        <thead class="bg-gray-50 text-gray-500">
          <tr>
            <th class="text-left px-4 py-3 font-medium">账号</th><th class="text-center px-4 py-3 font-medium">角色</th><th class="text-left px-4 py-3 font-medium">绑定对象</th><th class="text-center px-4 py-3 font-medium">状态</th><th class="text-right px-4 py-3 font-medium">操作</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-50">
          <tr v-if="loading"><td colspan="5" class="text-center py-10 text-gray-400">加载中...</td></tr>
          <tr v-else-if="!list.length"><td colspan="5" class="text-center py-10 text-gray-400">暂无数据</td></tr>
          <tr v-for="u in list" :key="u.id" class="hover:bg-gray-50/50">
            <td class="px-4 py-3 font-medium text-gray-800">{{ u.username }}</td>
            <td class="px-4 py-3 text-center">
              <span :class="u.role==='ADMIN'?'bg-purple-50 text-purple-600':u.role==='COACH'?'bg-blue-50 text-blue-600':'bg-green-50 text-green-600'" class="text-xs font-medium px-2 py-0.5 rounded-full">{{ roleLabel[u.role] || u.role }}</span>
            </td>
            <td class="px-4 py-3 text-gray-600">{{ u.relatedName || '-' }}</td>
            <td class="px-4 py-3 text-center">
              <button v-if="u.role!=='ADMIN'" @click="handleStatusChange(u, u.status==='ENABLE'?'DISABLE':'ENABLE')" :class="u.status==='ENABLE'?'bg-green-100 text-green-700':'bg-red-100 text-red-700'" class="text-xs font-medium px-2.5 py-1 rounded-full">{{ u.status==='ENABLE'?'启用':'停用' }}</button>
              <span v-else class="text-xs bg-green-100 text-green-700 font-medium px-2.5 py-1 rounded-full">启用</span>
            </td>
            <td class="px-4 py-3 text-right">
              <button @click="openResetPwd(u)" class="text-xs text-ink-600 hover:text-ink-800 font-medium">重置密码</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="flex items-center justify-between mt-4 text-sm text-gray-500">
      <span>共 {{ total }} 条</span>
      <div class="flex gap-2">
        <button :disabled="query.page<=1" @click="query.page--;fetchList()" class="px-3 py-1 border rounded-lg disabled:opacity-30">上一页</button>
        <button :disabled="query.page*query.size>=total" @click="query.page++;fetchList()" class="px-3 py-1 border rounded-lg disabled:opacity-30">下一页</button>
      </div>
    </div>

    <!-- 创建账号弹窗 -->
    <PageModal :visible="modalVisible" title="创建账号" @close="modalVisible=false" @confirm="handleCreate">
      <div class="space-y-4">
        <div><label class="block text-xs font-medium text-gray-500 mb-1">账号 *</label><input v-model="form.username" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" /></div>
        <div><label class="block text-xs font-medium text-gray-500 mb-1">角色 *</label>
          <select v-model="form.role" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none">
            <option value="COACH">教练</option><option value="STUDENT">学员</option>
          </select>
        </div>
        <div v-if="form.role==='COACH'"><label class="block text-xs font-medium text-gray-500 mb-1">绑定教练 *</label>
          <select v-model.number="form.relatedId" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none">
            <option :value="undefined">请选择教练</option>
            <option v-for="c in coachOptions" :key="c.id" :value="c.id">{{ c.name }}</option>
          </select>
        </div>
        <div v-else><label class="block text-xs font-medium text-gray-500 mb-1">绑定学员 *</label>
          <select v-model.number="form.relatedId" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none">
            <option :value="undefined">请选择学员</option>
            <option v-for="s in studentOptions" :key="s.id" :value="s.id">{{ s.name }}</option>
          </select>
        </div>
        <p class="text-xs text-gray-400">默认密码为 123456，创建后可重置</p>
      </div>
    </PageModal>

    <!-- 重置密码弹窗 -->
    <PageModal :visible="pwdVisible" title="重置密码" @close="pwdVisible=false" @confirm="handleResetPwd">
      <div class="space-y-4">
        <p class="text-sm text-gray-600">账号：<span class="font-medium">{{ pwdForm.username }}</span></p>
        <div><label class="block text-xs font-medium text-gray-500 mb-1">新密码（留空则重置为 123456）</label>
          <input v-model="pwdForm.newPassword" class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500" />
        </div>
      </div>
    </PageModal>
  </div>
</template>
