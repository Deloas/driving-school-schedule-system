<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const router = useRouter()

const roleLabel: Record<string, string> = { ADMIN: '管理员', COACH: '教练', STUDENT: '学员' }
const roleAvatar: Record<string, string> = { ADMIN: '管', COACH: '教', STUDENT: '学' }

function handleLogout() { auth.logout(); router.push('/login') }
</script>

<template>
  <div class="flex items-center gap-3">
    <div v-if="auth.role" class="w-8 h-8 rounded-full bg-ink-100 flex items-center justify-center text-ink-600 text-xs font-medium flex-shrink-0">
      {{ roleAvatar[auth.role] || '?' }}
    </div>
    <div class="text-right sm:text-left">
      <div class="text-sm font-medium text-gray-800">{{ roleLabel[auth.role!] || '用户' }}<span class="text-gray-400 font-normal"> · {{ auth.username }}</span></div>
    </div>
    <button @click="handleLogout" class="ml-2 text-xs text-gray-400 hover:text-red-500 transition-colors">退出</button>
  </div>
</template>
