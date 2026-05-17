<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
import AppLogo from '@/components/AppLogo.vue'
import UserIdentity from '@/components/UserIdentity.vue'

const router = useRouter()
const route = useRoute()
const isActive = (path: string) => route.path.startsWith(path)
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <header class="bg-white border-b border-gray-100 sticky top-0 z-10">
      <div class="max-w-6xl mx-auto px-6 py-3 flex items-center justify-between">
        <AppLogo />
        <div class="flex items-center gap-6">
          <router-link to="/student/reservations" :class="isActive('/student/reservations')?'text-primary-600 font-semibold text-sm':'text-sm text-gray-500 hover:text-primary-600 transition-colors'">预约练车</router-link>
          <router-link to="/student/training-records" :class="isActive('/student/training-records')?'text-primary-600 font-semibold text-sm':'text-sm text-gray-500 hover:text-primary-600 transition-colors'">练车记录</router-link>
          <UserIdentity />
        </div>
      </div>
    </header>
    <main class="max-w-6xl mx-auto px-6 py-8"><router-view /></main>
  </div>
</template>
