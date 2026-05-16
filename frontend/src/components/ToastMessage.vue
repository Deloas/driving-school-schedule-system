<script setup lang="ts">
/**
 * 轻量消息提示组件
 * 内部使用 getToastState() 读取全局 reactive 状态，由 showToast() 驱动。
 */
import { getToastState } from '@/utils/toast'
const state = getToastState()
</script>

<template>
  <Teleport to="body">
    <Transition name="toast">
      <div
        v-if="state.visible"
        :class="[
          'fixed top-6 left-1/2 -translate-x-1/2 z-[100] px-5 py-3 rounded-xl shadow-lg text-sm font-medium flex items-center gap-2',
          state.type === 'success' ? 'bg-green-600 text-white' : 'bg-red-600 text-white'
        ]"
      >
        <span>{{ state.type === 'success' ? '✓' : '✗' }}</span>
        <span>{{ state.message }}</span>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.toast-enter-active, .toast-leave-active { transition: all 0.3s ease; }
.toast-enter-from, .toast-leave-to { opacity: 0; transform: translateY(-12px); }
</style>
