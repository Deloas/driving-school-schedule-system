# 驾校学员练车预约与调度管理系统

> 基于教练资源均衡的驾校学员练车预约与调度管理系统

## 项目定位

本系统聚焦驾校内部**学员练车预约与调度**，核心解决传统电话/微信群预约方式导致的预约混乱、教练负载不均、车辆资源浪费等问题。

### 系统要做

- 学员管理、教练管理、车辆管理
- 教练排班（日期 + 上午/下午时间段 + 容量限制）
- 学员在线预约练车（优先主教练）
- 主教练满员后的临时调剂（推荐空闲教练）
- 教练容量控制（防超额预约）
- 车辆占用校验
- 教练确认到场与完成练车
- 学员练车次数累计
- 管理员统计看板

### 系统不做

- 考试预约（科目一/二/三/四）
- 考试成绩查询与管理
- 交管系统对接
- 复杂缴费与合同管理
- 大而全的驾校 ERP

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端框架 | Vue 3 + Vite + TypeScript |
| 前端状态 | Pinia |
| 路由 | Vue Router |
| UI 样式 | Tailwind CSS |
| HTTP 库 | Axios |
| 图表 | ECharts |
| 后端框架 | Spring Boot 3.x |
| 安全 | Spring Security + JWT |
| ORM | MyBatis-Plus |
| 数据库 | MySQL 8.x |
| 缓存与锁 | Redis 7.x + Redisson |
| 构建工具 | Maven |
| 容器化 | Docker Compose（仅用于本地启动 MySQL 和 Redis） |

## 项目结构

```
├── README.md                 # 项目说明
├── docker-compose.yml        # MySQL + Redis 容器
├── sql/init.sql              # 数据库初始化脚本
├── docs/                     # 全部设计文档
├── backend/                  # Spring Boot 后端（标准 Maven 项目）
└── frontend/                 # Vue 3 前端
```

## 环境要求

| 软件 | 版本建议 |
|------|---------|
| JDK | 17 |
| Node.js | 20+ |
| MySQL | 8.x |
| Redis | 7.x |
| Maven | 3.9+ |
| Docker | 可选（用于快速启动 MySQL 和 Redis） |

## 快速开始

### 1. 启动 MySQL 和 Redis

**方式 A：Docker Compose（推荐，最简单）**

```bash
# 在项目根目录执行
docker-compose up -d
```

这将启动：
- MySQL 8.0（宿主机端口 23306，root 密码 123456，数据库 driving_school_schedule）
- Redis 7（宿主机端口 26379，无密码）

**方式 B：本地安装**

自行安装并启动 MySQL 8.x 和 Redis 7.x，然后创建数据库：

```sql
CREATE DATABASE IF NOT EXISTS driving_school_schedule DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

### 2. 初始化数据库

使用任意 MySQL 客户端执行 `sql/init.sql`：

```bash
# 方式 A：命令行
mysql -u root -p < sql/init.sql

# 方式 B：使用 IDEA 数据库工具
# 在 IDEA 中连接 MySQL，然后打开 sql/init.sql 并执行
```

### 3. 启动后端

**方式 A：IntelliJ IDEA（推荐）**

1. 使用 IntelliJ IDEA 打开项目根目录（`D:/DrivingStSystem`）或 `backend/` 目录
2. IDEA 会自动识别为 Maven 项目，等待 Maven 自动导入依赖
3. 配置 JDK 17：`File → Project Structure → SDK → 选择 JDK 17`
4. 检查 `backend/src/main/resources/application-dev.yml` 中的数据库和 Redis 配置是否正确
5. 确保 MySQL 和 Redis 已启动，数据库已初始化
6. 找到 `DrivingSchoolApplication.java`，点击类左侧的绿色运行按钮启动
7. 或右键 `DrivingSchoolApplication.java` → `Run 'DrivingSchoolApplication'`

**方式 B：Maven 命令行**

```bash
cd backend
mvn spring-boot:run
```

后端默认端口：`28081`

启动成功后访问健康检查接口：`GET http://localhost:28081/api/health`

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端默认端口：`25173`

在浏览器中访问：`http://localhost:25173`

## 演示账号（后续阶段准备）

| 角色 | 账号 | 密码 |
|------|------|------|
| 管理员 | admin | 123456 |
| 教练 | coach001 | 123456 |
| 学员 | student001 | 123456 |

## 开发阶段

| 阶段 | 名称 | 状态 |
|------|------|------|
| M1 | 项目初始化 | 进行中 |
| M2 | 用户认证与权限 | 待开始 |
| M3 | 基础资料管理 | 待开始 |
| M4 | 排班管理 | 待开始 |
| M5 | 预约主流程 | 待开始 |
| M6 | 调剂机制 | 待开始 |
| M7 | 练车完成与统计 | 待开始 |
| M8 | 产品级优化与答辩准备 | 待开始 |

## 参考文档

全部设计文档位于 `docs/驾校学员练车预约与调度管理系统_文档群_MD/` 目录下，包含需求规格、业务规则、原型设计、架构设计、数据库设计、API 设计等 15 份完整文档。
