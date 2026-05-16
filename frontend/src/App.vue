<script setup lang="ts">
/**
 * 根组件 — 渲染当前路由页面 + 全局消息提示
 */
import ToastMessage from '@/components/ToastMessage.vue'
</script>

<template>
  <router-view />
  <!-- 全局消息提示：所有页面共享，通过 useToast().showToast() 调用 -->
  <ToastMessage />
</template>
