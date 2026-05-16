<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCoachList } from '@/api/coach'
import { getStudentList } from '@/api/student'
import { getVehicleList } from '@/api/vehicle'

/**
 * 管理员控制台首页
 * <p>
 * 展示驾校基础资料概览卡片，作为教练/学员/车辆管理的入口。
 * 数据通过后端接口实时获取。
 * </p>
 */

const router = useRouter()
const coachCount = ref(0)
const studentCount = ref(0)
const vehicleCount = ref(0)

onMounted(async () => {
  try {
    const [cRes, sRes, vRes] = await Promise.all([
      getCoachList({ page: 1, size: 1 }),
      getStudentList({ page: 1, size: 1 }),
      getVehicleList({ page: 1, size: 1 }),
    ])
    coachCount.value = cRes.data.data?.total || 0
    studentCount.value = sRes.data.data?.total || 0
    vehicleCount.value = vRes.data.data?.total || 0
  } catch { /* 接口不可用时静默降级 */ }
})

const entries = [
  { title: '教练管理', desc: '管理驾校教练信息与排班容量', count: coachCount, route: '/admin/coaches', color: 'bg-primary-50 text-primary-600' },
  { title: '学员管理', desc: '管理学籍与主教练分配', count: studentCount, route: '/admin/students', color: 'bg-ink-50 text-ink-600' },
  { title: '车辆管理', desc: '管理教练车与状态', count: vehicleCount, route: '/admin/vehicles', color: 'bg-accent-50 text-accent-600' },
]
</script>

<template>
  <div>
    <h3 class="text-base font-semibold text-gray-700 mb-4">驾校概览</h3>
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <div
        v-for="item in entries" :key="item.title"
        @click="router.push(item.route)"
        class="card p-5 cursor-pointer hover:shadow-md hover:-translate-y-0.5 transition-all duration-200"
      >
        <div class="flex items-center justify-between mb-3">
          <span class="text-sm font-medium text-gray-600">{{ item.title }}</span>
          <span :class="['text-2xl font-bold', item.color.split(' ')[1]]">{{ item.count }}</span>
        </div>
        <p class="text-xs text-gray-400 mb-3">{{ item.desc }}</p>
        <div class="flex items-center gap-1 text-xs text-primary-600 font-medium">
          进入管理 <span class="text-lg leading-none">&rarr;</span>
        </div>
      </div>
    </div>
  </div>
</template>
