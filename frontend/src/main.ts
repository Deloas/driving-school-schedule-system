import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './styles/global.css'

// 创建 Vue 应用实例
const app = createApp(App)

// 注册 Pinia 状态管理（全局共享状态）
app.use(createPinia())

// 注册 Vue Router（页面路由）
app.use(router)

// 挂载到 #app 根元素
app.mount('#app')
