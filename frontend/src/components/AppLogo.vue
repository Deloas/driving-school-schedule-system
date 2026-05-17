<script setup lang="ts">
/** 品牌 Logo — 支持 full(完整品牌)/compact(仅图标) + dark(深色背景)模式 */
defineProps<{ compact?: boolean; dark?: boolean; size?: 'sm' | 'md' | 'lg' }>()
</script>
<template>
  <div class="flex items-center gap-3 select-none">
    <div class="flex-shrink-0 flex items-center justify-center rounded-xl"
      :class="[
        dark ? 'bg-white/15' : 'bg-primary-500',
        size === 'lg' ? 'w-11 h-11' : size === 'sm' ? 'w-7 h-7 rounded-lg' : 'w-9 h-9'
      ]">
      <svg :class="['text-white', size === 'lg' ? 'w-6 h-6' : size === 'sm' ? 'w-4 h-4' : 'w-5 h-5']"
        fill="none" stroke="currentColor" stroke-width="1.8" viewBox="0 0 24 24">
        <circle cx="12" cy="12" r="3"/><path d="M12 2v3m0 14v3M2 12h3m14 0h3" stroke-linecap="round"/>
        <circle cx="12" cy="12" r="9" stroke-dasharray="4 4" opacity="0.4"/>
      </svg>
    </div>
    <div v-if="!compact">
      <div class="font-bold leading-tight" :class="[dark ? 'text-white' : 'text-gray-800', size === 'lg' ? 'text-base' : 'text-sm']">驾校调度系统</div>
      <div class="leading-tight" :class="[dark ? 'text-white/45' : 'text-gray-400', size === 'lg' ? 'text-[11px]' : 'text-[10px]']">Training Scheduling</div>
    </div>
  </div>
</template>
