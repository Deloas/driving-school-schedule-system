<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

/**
 * 登录页 — M2 阶段：真实登录表单
 * <p>
 * 设计保持 Anthropic frontend-design 的高级感：左右分栏布局，
 * 左侧品牌展示区（墨蓝渐变），右侧登录表单（白底清爽卡片）。
 * 登录成功后根据角色自动跳转到对应首页。
 * </p>
 */

const router = useRouter()
const authStore = useAuthStore()

/** 登录表单数据 */
const username = ref('')
const password = ref('')

/** 错误提示信息 */
const errorMessage = ref('')

/** 是否正在请求中 */
const loading = ref(false)

/**
 * 处理登录
 * <p>
 * 调用 authStore.login() 发起真实登录请求。
 * 成功 → 根据角色跳转到对应首页。
 * 失败 → 显示后端返回的中文错误提示。
 * </p>
 */
async function handleLogin() {
  // 基础校验
  if (!username.value.trim()) {
    errorMessage.value = '请输入账号'
    return
  }
  if (!password.value.trim()) {
    errorMessage.value = '请输入密码'
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    // 调用真实登录接口
    await authStore.login(username.value.trim(), password.value)

    // 根据角色跳转到对应首页
    const roleHomeMap: Record<string, string> = {
      ADMIN: '/admin',
      COACH: '/coach',
      STUDENT: '/student',
    }
    router.push(roleHomeMap[authStore.role!] || '/login')
  } catch (err: any) {
    // 显示后端返回的中文错误提示（如"账号或密码错误"）
    const msg = err?.response?.data?.message || '登录失败，请稍后重试'
    errorMessage.value = msg
  } finally {
    loading.value = false
  }
}

/** 回车键快捷登录 */
function handleKeydown(e: KeyboardEvent) {
  if (e.key === 'Enter') {
    handleLogin()
  }
}
</script>

<template>
  <div class="min-h-screen flex">
    <!-- ==================== 左侧品牌区 ==================== -->
    <div class="hidden lg:flex lg:w-5/12 bg-gradient-to-br from-ink-700 via-ink-600 to-primary-700 relative overflow-hidden">
      <div class="absolute top-0 right-0 w-96 h-96 bg-white/5 rounded-full -translate-y-1/2 translate-x-1/2" />
      <div class="absolute bottom-0 left-0 w-64 h-64 bg-primary-400/10 rounded-full translate-y-1/2 -translate-x-1/2" />

      <div class="relative z-10 flex flex-col justify-center px-16 text-white">
        <div class="mb-8">
          <div class="w-12 h-12 bg-primary-400/30 rounded-xl flex items-center justify-center mb-6">
            <svg class="w-7 h-7 text-primary-200" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                d="M9 17a2 2 0 11-4 0 2 2 0 014 0zM19 17a2 2 0 11-4 0 2 2 0 014 0z" />
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                d="M13 16V6a1 1 0 00-1-1H4a1 1 0 00-1 1v10a1 1 0 001 1h1m8-1a1 1 0 01-1 1H9m4-1V8a1 1 0 011-1h2.586a1 1 0 01.707.293l3.414 3.414a1 1 0 01.293.707V16a1 1 0 01-1 1h-1m-6-1a1 1 0 001 1h1M5 17a2 2 0 104 0m-4 0a2 2 0 114 0m6 0a2 2 0 104 0m-4 0a2 2 0 114 0" />
            </svg>
          </div>
          <h1 class="text-3xl font-bold tracking-tight mb-3">
            驾校学员练车预约与调度管理系统
          </h1>
          <p class="text-primary-100 text-lg font-light">
            基于教练资源均衡的智能调度平台
          </p>
        </div>

        <p class="text-white/80 text-sm leading-relaxed max-w-md">
          让每一次练车预约都有秩序，让每一位教练负载更均衡。
        </p>

        <div class="mt-12 space-y-3">
          <div class="flex items-center gap-3 text-white/70 text-sm">
            <svg class="w-4 h-4 text-primary-300 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
            </svg>
            <span>学员在线预约练车，优先主教练</span>
          </div>
          <div class="flex items-center gap-3 text-white/70 text-sm">
            <svg class="w-4 h-4 text-primary-300 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
            </svg>
            <span>主教练满员自动推荐调剂方案</span>
          </div>
          <div class="flex items-center gap-3 text-white/70 text-sm">
            <svg class="w-4 h-4 text-primary-300 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
            </svg>
            <span>容量控制 + 车辆校验，资源均衡调度</span>
          </div>
        </div>
      </div>
    </div>

    <!-- ==================== 右侧登录表单区 ==================== -->
    <div class="flex-1 flex items-center justify-center px-8 bg-gray-50">
      <div class="w-full max-w-sm">
        <div class="lg:hidden mb-10 text-center">
          <h1 class="text-2xl font-bold text-ink-700">驾校练车预约系统</h1>
          <p class="text-gray-500 text-sm mt-1">智能调度平台</p>
        </div>

        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-8">
          <h2 class="text-lg font-semibold text-gray-800 mb-1">登录系统</h2>
          <p class="text-sm text-gray-500 mb-6">使用你的账号登录驾校练车预约系统</p>

          <!-- 登录表单 -->
          <form @submit.prevent="handleLogin" @keydown="handleKeydown" class="space-y-4">
            <!-- 账号输入框 -->
            <div>
              <label class="block text-xs font-medium text-gray-500 uppercase tracking-wider mb-1.5">
                账号
              </label>
              <input
                v-model="username"
                type="text"
                autocomplete="username"
                placeholder="请输入登录账号"
                class="w-full px-4 py-2.5 text-sm border border-gray-200 rounded-xl focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 outline-none transition-all duration-200 bg-gray-50/50 focus:bg-white"
              />
            </div>

            <!-- 密码输入框 -->
            <div>
              <label class="block text-xs font-medium text-gray-500 uppercase tracking-wider mb-1.5">
                密码
              </label>
              <input
                v-model="password"
                type="password"
                autocomplete="current-password"
                placeholder="请输入登录密码"
                class="w-full px-4 py-2.5 text-sm border border-gray-200 rounded-xl focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 outline-none transition-all duration-200 bg-gray-50/50 focus:bg-white"
              />
            </div>

            <!-- 错误提示 -->
            <div
              v-if="errorMessage"
              class="flex items-center gap-2 text-sm text-red-600 bg-red-50 rounded-lg px-3 py-2"
            >
              <svg class="w-4 h-4 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <span>{{ errorMessage }}</span>
            </div>

            <!-- 登录按钮 -->
            <button
              type="submit"
              :disabled="loading"
              class="w-full py-3 px-4 bg-ink-600 hover:bg-ink-700 active:bg-ink-800 text-white font-medium rounded-xl transition-all duration-200 disabled:opacity-60 disabled:cursor-not-allowed flex items-center justify-center gap-2 mt-2"
            >
              <svg
                v-if="loading"
                class="animate-spin w-4 h-4 text-white"
                fill="none"
                viewBox="0 0 24 24"
              >
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
              </svg>
              <span>{{ loading ? '正在登录...' : '登 录' }}</span>
            </button>
          </form>

          <!-- 测试账号提示 -->
          <div class="mt-6 p-3 bg-gray-50 rounded-lg">
            <p class="text-xs text-gray-400 mb-2">M2 测试账号（点击自动填充）</p>
            <div class="space-y-1.5">
              <button
                @click="username = 'admin'; password = '123456'"
                class="block w-full text-left text-xs text-gray-500 hover:text-primary-600 hover:bg-primary-50/50 rounded-md px-2 py-1 transition-colors"
              >
                <span class="font-medium text-gray-600">管理员：</span>admin / 123456
              </button>
              <button
                @click="username = 'coach001'; password = '123456'"
                class="block w-full text-left text-xs text-gray-500 hover:text-primary-600 hover:bg-primary-50/50 rounded-md px-2 py-1 transition-colors"
              >
                <span class="font-medium text-gray-600">教练：</span>coach001 / 123456
              </button>
              <button
                @click="username = 'student001'; password = '123456'"
                class="block w-full text-left text-xs text-gray-500 hover:text-primary-600 hover:bg-primary-50/50 rounded-md px-2 py-1 transition-colors"
              >
                <span class="font-medium text-gray-600">学员：</span>student001 / 123456
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
