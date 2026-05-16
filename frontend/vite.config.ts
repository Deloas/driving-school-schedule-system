import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

// Vite 构建配置
// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],

  // 路径别名：@ 指向 src 目录
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },

  // 开发服务器配置
  server: {
    port: 25173,
    // 代理转发：将 /api 开头的请求转发到后端 28081 端口
    // 解决开发时前后端跨域问题
    proxy: {
      '/api': {
        target: 'http://localhost:28081',
        changeOrigin: true,
      },
    },
  },
})
