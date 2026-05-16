import { reactive } from 'vue'

/**
 * 全局 Toast 消息状态
 * 页面调用 showToast(msg, type) 显示消息提示，3 秒后自动消失。
 */
const state = reactive({ visible: false, message: '', type: 'success' as 'success' | 'error' })
let timer: ReturnType<typeof setTimeout> | null = null

export function showToast(message: string, type: 'success' | 'error' = 'success') {
  state.message = message
  state.type = type
  state.visible = true
  if (timer) clearTimeout(timer)
  timer = setTimeout(() => { state.visible = false }, 2500)
}

/** 仅供 ToastMessage 组件内部使用 */
export function getToastState() { return state }
