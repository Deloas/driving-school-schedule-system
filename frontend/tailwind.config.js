/** @type {import('tailwindcss').Config} */

// Tailwind CSS 配置
// 使用 Anthropic frontend-design 风格策略：
//   "智能调度驾驶舱 + 清爽业务管理台"
// 配色：深青绿、墨蓝、道路绿、柔和橙

export default {
  content: [
    './index.html',
    './src/**/*.{vue,js,ts,jsx,tsx}',
  ],
  theme: {
    extend: {
      // 项目自定义配色方案
      colors: {
        // 主色调 - 深青绿系，传递"专业、可信赖"的驾校品牌感
        primary: {
          50: '#e6f7f5',
          100: '#ccefeb',
          200: '#99dfd7',
          300: '#66cfc3',
          400: '#33bfaf',
          500: '#00af9b',  // 主色 — 道路绿
          600: '#008c7c',
          700: '#00695d',
          800: '#00463e',
          900: '#00231f',
        },
        // 辅助色 - 墨蓝，用于导航/侧边栏
        ink: {
          50: '#e8eef4',
          100: '#d1dde9',
          200: '#a3bbd3',
          300: '#7599bd',
          400: '#4777a7',
          500: '#1a5591',  // 主墨蓝
          600: '#154474',
          700: '#103357',
          800: '#0a223a',
          900: '#05111d',
        },
        // 强调色 - 柔和橙，用于满员警告、调剂标识
        accent: {
          50: '#fff4ed',
          100: '#ffe8d5',
          200: '#ffd1ab',
          300: '#ffba81',
          400: '#ffa357',
          500: '#ff8c2d',  // 主橙色
          600: '#cc7024',
          700: '#99541b',
          800: '#663812',
          900: '#331c09',
        },
      },
      // 自定义字体（系统字体优先，避免加载外部字体影响性能）
      fontFamily: {
        sans: [
          '"Inter"',
          '"PingFang SC"',
          '"Microsoft YaHei"',
          '"Helvetica Neue"',
          'Arial',
          'sans-serif',
        ],
      },
    },
  },
  plugins: [],
}
