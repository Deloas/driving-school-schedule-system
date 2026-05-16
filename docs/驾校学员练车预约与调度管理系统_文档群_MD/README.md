# 驾校学员练车预约与调度管理系统 文档群

版本：v1.0  
生成日期：2026-05-15  
项目定位：面向驾校日常练车场景的预约、排班、调剂与资源均衡管理系统。

## 文档目录

| 文件 | 用途 |
|---|---|
| 00_项目总览.md | 项目背景、范围、角色、目标与总体方案 |
| 01_需求规格说明书_SRS.md | 功能需求、非功能需求、业务边界 |
| 02_业务规则与流程设计.md | 预约、取消、调剂、签到、完成练车等核心流程 |
| 03_产品原型与前端设计规范.md | 页面清单、交互说明、Anthropic frontend-design 风格要求 |
| 04_系统架构设计.md | 前后端架构、模块划分、技术选型 |
| 05_数据库设计.md | 表结构、字段、索引、ER 图、建表 SQL |
| 06_API接口设计.md | REST API 规范、接口清单、请求响应样例 |
| 07_权限与安全设计.md | 角色权限、认证鉴权、安全策略 |
| 08_Redis缓存与并发预约设计.md | Redis 使用边界、分布式锁、防超额预约方案 |
| 09_测试计划与验收标准.md | 单元测试、接口测试、业务测试、验收标准 |
| 10_开发计划与AI协作任务拆分.md | 里程碑、迭代计划、AI 编程助手任务拆分 |
| 11_部署运行说明.md | 本地开发、Docker 部署、环境变量说明 |
| 12_答辩展示脚本.md | 答辩讲解结构、演示路径、亮点表达 |
| 13_AI编程助手总提示词.md | 可直接复制给 AI 编程助手的一体化开发提示词 |

## 项目核心原则

1. **严格聚焦练车管理**：不做考试预约、考试成绩、交管系统对接。
2. **预约调度是核心**：不是普通 CRUD 信息系统。
3. **主教练负责制 + 临时调剂机制**：符合老师录音要求。
4. **容量控制 + 资源均衡**：避免一天来太多人、一天没人。
5. **产品级体验**：前端使用 Anthropic frontend-design 思路，避免模板感。
6. **代码必须有中文注释**：关键类、方法、业务规则、复杂 SQL、Redis 锁均需中文说明。

## 推荐技术栈

- 前端：Vue 3 + Vite + TypeScript + Tailwind CSS + Pinia + Vue Router + Axios
- 设计：Anthropic frontend-design 风格策略
- 后端：Java 17/21 + Spring Boot 3.x + Spring Security + MyBatis-Plus
- 数据库：MySQL 8.x
- 缓存与并发：Redis 7.x + Redisson
- 文档：Markdown + Mermaid
- 部署：Docker Compose

## 参考资料

- Anthropic frontend-design Skill：https://github.com/anthropics/skills/blob/main/skills/frontend-design/SKILL.md
- Claude 前端美学提示指南：https://platform.claude.com/cookbook/coding-prompting-for-frontend-aesthetics
- 北京市机动车驾驶培训学时管理规则：https://jtw.beijing.gov.cn/xxgk/tzgg/202205/P020220524346002134738.pdf
- 北京市机动车驾驶培训服务合同示范文本：https://scjgj.beijing.gov.cn/bsfw/bmfw/shxfl/yfk/202406/t20240606_3705705.html
