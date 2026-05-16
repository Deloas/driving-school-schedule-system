<script setup lang="ts">
import { ref, watch } from 'vue'

/**
 * 通用弹窗组件
 * 保存按钮使用明确的 handleSave 方法（不在模板内联 $emit），确保事件链路可追踪
 */
const props = defineProps<{ visible: boolean; title: string }>()
const emit = defineEmits<{ close: []; confirm: [] }>()

const submitting = ref(false)
watch(() => props.visible, (v) => { if (v) submitting.value = false })

function setSubmitting(v: boolean) { submitting.value = v }
defineExpose({ setSubmitting })

/** 保存 — 使用明确方法而非模板内联 $emit，确保事件链路可靠 */
function handleSave() {
  emit('confirm')
}

/** 取消 — 明确方法 */
function handleCancel() {
  emit('close')
}
</script>

<template>
  <Teleport to="body">
    <div v-if="visible" class="fixed inset-0 z-50 flex items-center justify-center">
      <div class="absolute inset-0 bg-black/30" @click="handleCancel" />
      <div class="relative bg-white rounded-2xl shadow-xl w-full max-w-md mx-4 p-6">
        <div class="flex items-center justify-between mb-5">
          <h3 class="text-base font-semibold text-gray-800">{{ title }}</h3>
          <button type="button" @click="handleCancel" class="text-gray-400 hover:text-gray-600 text-xl leading-none">&times;</button>
        </div>
        <slot />
        <div class="flex justify-end gap-3 mt-6">
          <button type="button" @click="handleCancel" class="px-4 py-2 text-sm text-gray-600 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors">取消</button>
          <button
            type="button"
            :disabled="submitting"
            @click="handleSave"
            class="px-4 py-2 text-sm text-white bg-ink-600 rounded-lg hover:bg-ink-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            {{ submitting ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>
