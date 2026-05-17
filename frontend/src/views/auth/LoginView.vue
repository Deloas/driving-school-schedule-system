<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import AppLogo from '@/components/AppLogo.vue'

const router = useRouter()
const authStore = useAuthStore()
const username = ref('')
const password = ref('')
const errorMessage = ref('')
const loading = ref(false)

async function handleLogin() {
  if (!username.value.trim()) { errorMessage.value = '请输入账号'; return }
  if (!password.value.trim()) { errorMessage.value = '请输入密码'; return }
  loading.value = true; errorMessage.value = ''
  try {
    await authStore.login(username.value.trim(), password.value)
    const map: Record<string, string> = { ADMIN: '/admin', COACH: '/coach', STUDENT: '/student' }
    router.push(map[authStore.role!] || '/login')
  } catch (err: any) { errorMessage.value = err?.response?.data?.message || '账号或密码错误' }
  finally { loading.value = false }
}
function fillDemo(u: string, p: string) { username.value = u; password.value = p; errorMessage.value = '' }

const demoAccounts = [
  { role: '管理员', user: 'admin', pass: '123456', color: 'bg-purple-50 text-purple-700 border-purple-200' },
  { role: '教练', user: 'coach002', pass: '123456', color: 'bg-blue-50 text-blue-700 border-blue-200' },
  { role: '学员', user: 'student002', pass: '123456', color: 'bg-green-50 text-green-700 border-green-200' },
]
</script>

<template>
  <div class="min-h-screen flex bg-gradient-to-br from-slate-100 via-blue-50/40 to-slate-100">
    <!-- 左侧品牌面板：深色，右侧圆角 -->
    <div class="hidden lg:flex lg:w-[44%] xl:w-[40%] relative">
      <div class="absolute inset-y-0 left-0 right-4 bg-gradient-to-br from-ink-800 via-ink-700 to-primary-800 rounded-r-[2.5rem] shadow-2xl shadow-ink-900/20 flex items-center overflow-hidden">
        <!-- 背景装饰：训练路线 -->
        <svg class="absolute inset-0 w-full h-full opacity-[0.06]" viewBox="0 0 500 700" fill="none">
          <path d="M60 650 Q100 550 200 500 T350 400 Q400 350 380 280 T300 180 Q250 130 200 80" stroke="white" stroke-width="2" stroke-dasharray="8 6" fill="none"/>
          <circle cx="200" cy="500" r="8" stroke="white" stroke-width="1.5" fill="none"/><circle cx="200" cy="500" r="3" fill="white"/>
          <circle cx="350" cy="400" r="8" stroke="white" stroke-width="1.5" fill="none"/><circle cx="350" cy="400" r="3" fill="white"/>
          <circle cx="300" cy="180" r="8" stroke="white" stroke-width="1.5" fill="none"/><circle cx="300" cy="180" r="3" fill="white"/>
          <circle cx="200" cy="80" r="8" stroke="white" stroke-width="1.5" fill="none"/><circle cx="200" cy="80" r="3" fill="white"/>
          <text x="155" y="515" fill="white" font-size="10" opacity="0.5">预约</text>
          <text x="313" y="415" fill="white" font-size="10" opacity="0.5">排班</text>
          <text x="260" y="195" fill="white" font-size="10" opacity="0.5">练车</text>
          <text x="155" y="95" fill="white" font-size="10" opacity="0.5">记录</text>
        </svg>
        <!-- 半透明装饰圆 -->
        <div class="absolute -top-32 -right-32 w-80 h-80 bg-primary-400/5 rounded-full" />
        <div class="absolute -bottom-16 -left-16 w-48 h-48 bg-white/3 rounded-full" />

        <!-- 内容 -->
        <div class="relative z-10 px-12 xl:px-16 2xl:px-20 w-full text-white">
          <div class="mb-8"><div class="w-11 h-11 bg-white/15 rounded-2xl flex items-center justify-center mb-5"><svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" stroke-width="1.8" viewBox="0 0 24 24"><circle cx="12" cy="12" r="3"/><path d="M12 2v3m0 14v3M2 12h3m14 0h3" stroke-linecap="round"/><circle cx="12" cy="12" r="9" stroke-dasharray="4 4" opacity="0.4"/></svg></div><div class="text-lg font-bold tracking-wide opacity-90">驾校调度系统</div></div>
          <h1 class="text-[26px] xl:text-[30px] font-extrabold tracking-tight leading-tight mb-3">驾校学员练车预约与调度管理系统</h1>
          <p class="text-white/65 text-sm font-light mb-12">基于教练资源均衡的智能练车预约平台</p>

          <!-- 能力卡片 -->
          <div class="space-y-3">
            <div class="flex items-center gap-3 bg-white/8 rounded-xl px-4 py-3 text-sm"><div class="w-2 h-2 rounded-full bg-primary-300 flex-shrink-0" /><span class="text-white/75">按日期和时段在线预约练车</span></div>
            <div class="flex items-center gap-3 bg-white/8 rounded-xl px-4 py-3 text-sm"><div class="w-2 h-2 rounded-full bg-primary-300 flex-shrink-0" /><span class="text-white/75">教练、车辆、容量统一资源调度</span></div>
            <div class="flex items-center gap-3 bg-white/8 rounded-xl px-4 py-3 text-sm"><div class="w-2 h-2 rounded-full bg-primary-300 flex-shrink-0" /><span class="text-white/75">主教练满员时推荐可调剂教练</span></div>
            <div class="flex items-center gap-3 bg-white/8 rounded-xl px-4 py-3 text-sm"><div class="w-2 h-2 rounded-full bg-primary-300 flex-shrink-0" /><span class="text-white/75">练车完成后自动生成训练记录</span></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧登录区 -->
    <div class="flex-1 flex items-center justify-center px-6 py-10">
      <div class="w-full max-w-[440px]">
        <div class="lg:hidden mb-8 text-center"><div class="flex justify-center mb-3"><AppLogo /></div><h1 class="text-xl font-bold text-ink-700">驾校练车预约系统</h1></div>

        <!-- 登录卡片 -->
        <div class="bg-white rounded-2xl shadow-xl shadow-gray-200/60 border border-gray-100/80 p-8">
          <div class="flex items-center gap-2.5 mb-5"><div class="w-8 h-8 bg-primary-500 rounded-lg flex items-center justify-center"><svg class="w-4.5 h-4.5 text-white" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><circle cx="12" cy="12" r="3"/><path d="M12 2v3m0 14v3M2 12h3m14 0h3" stroke-linecap="round"/></svg></div><span class="text-sm font-semibold text-gray-500">驾校调度系统</span></div>
          <h2 class="text-xl font-bold text-gray-800 mb-1">登录系统</h2>
          <p class="text-sm text-gray-400 mb-6">使用账号进入对应工作平台</p>

          <form @submit.prevent="handleLogin" class="space-y-4">
            <div>
              <label class="block text-xs font-medium text-gray-500 mb-1.5">账号</label>
              <input v-model="username" type="text" placeholder="请输入登录账号" class="w-full px-4 py-3 text-sm border border-gray-200 rounded-xl focus:ring-2 focus:ring-primary-500/15 focus:border-primary-400 outline-none bg-gray-50 transition-colors" />
            </div>
            <div>
              <label class="block text-xs font-medium text-gray-500 mb-1.5">密码</label>
              <input v-model="password" type="password" placeholder="请输入登录密码" class="w-full px-4 py-3 text-sm border border-gray-200 rounded-xl focus:ring-2 focus:ring-primary-500/15 focus:border-primary-400 outline-none bg-gray-50 transition-colors" />
            </div>
            <div v-if="errorMessage" class="text-sm text-red-600 bg-red-50 rounded-lg px-3 py-2.5 flex items-center gap-2">
              <svg class="w-4 h-4 flex-shrink-0" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
              <span>{{ errorMessage }}</span>
            </div>
            <button type="submit" :disabled="loading" class="w-full py-3.5 px-4 bg-ink-600 hover:bg-ink-700 active:bg-ink-800 text-white font-semibold rounded-xl disabled:opacity-50 transition-all text-sm shadow-sm shadow-ink-900/10">
              {{ loading ? '登录中...' : '登录系统' }}
            </button>
          </form>

          <!-- 演示账号 -->
          <div class="mt-6 pt-5 border-t border-gray-100">
            <p class="text-xs text-gray-400 font-medium mb-3">演示账号</p>
            <div class="grid grid-cols-3 gap-2">
              <div v-for="d in demoAccounts" :key="d.role" @click="fillDemo(d.user, d.pass)" :class="[d.color, 'border rounded-xl px-3 py-2.5 text-center cursor-pointer hover:shadow-sm transition-shadow']">
                <div class="text-xs font-semibold mb-0.5">{{ d.role }}</div>
                <div class="text-[11px] opacity-70">{{ d.user }}</div>
                <div class="text-[11px] opacity-50">{{ d.pass }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
