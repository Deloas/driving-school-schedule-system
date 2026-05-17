<script setup lang="ts">
/**
 * 统一状态标签组件
 * 在所有管理员管理页面中复用，保证状态颜色一致
 */
defineProps<{ label: string; type?: string }>()

const colorMap: Record<string, string> = {
  NORMAL: 'bg-green-100 text-green-700',
  ENABLE: 'bg-green-100 text-green-700',
  OPEN: 'bg-green-100 text-green-700',
  SUCCESS: 'bg-blue-100 text-blue-700',
  COMPLETED: 'bg-green-100 text-green-700',
  CLOSED: 'bg-gray-100 text-gray-600',
  DISABLE: 'bg-gray-100 text-gray-600',
  STOPPED: 'bg-gray-100 text-gray-600',
  CANCELLED: 'bg-gray-100 text-gray-500',
  LEAVE: 'bg-yellow-100 text-yellow-700',
  MAINTENANCE: 'bg-orange-100 text-orange-700',
  ABSENT: 'bg-red-100 text-red-700',
}
</script>

<template>
  <span :class="['text-xs font-medium px-2.5 py-1 rounded-full inline-block', colorMap[type || ''] || 'bg-gray-100 text-gray-600']">
    {{ label }}
  </span>
</template>
