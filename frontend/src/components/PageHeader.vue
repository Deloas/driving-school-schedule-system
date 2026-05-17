<script setup lang="ts">
defineProps<{ title: string; subtitle?: string }>()
</script>

<template>
  <div class="mb-5">
    <h2 class="text-base font-bold text-gray-800">{{ title }}</h2>
    <p v-if="subtitle" class="text-xs text-gray-500 mt-1">{{ subtitle }}</p>
  </div>
</template>
