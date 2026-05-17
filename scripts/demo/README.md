# 答辩演示脚本说明

## 用途

本目录包含答辩现场演示所需的 SQL 数据准备脚本和并发验证脚本。

## 演示日期约定

| 日期 | 场景 |
|------|------|
| 2026-06-20 | 满员不可预约 |
| 2026-06-21 | 调剂推荐 |
| 2026-06-22 | Redis 并发预约安全 |
| 2026-06-23 | 练车完成 / 缺席闭环 |

## 执行顺序

1. `00_prepare_demo_data.sql` — 基础数据检查
2. `01_prepare_full_schedule_scene.sql` — 满员场景准备
3. `02_prepare_adjustment_scene.sql` — 调剂场景准备
4. `03_concurrency_test.ps1` — 并发测试演示
5. `04_verify_demo_data.sql` — 演示后数据验证
6. `05_reset_demo_scene.sql` — 重置演示数据（如需重新演示）

## MySQL 连接方式

在项目根目录执行：

```bash
# Docker MySQL
docker exec -i driving-mysql mysql -u root -p123456 driving_school_schedule < scripts/demo/00_prepare_demo_data.sql

# 或本地 MySQL 客户端
mysql -u root -p -h localhost -P 23306 driving_school_schedule < scripts/demo/00_prepare_demo_data.sql
```

## PowerShell 脚本运行

```powershell
powershell -ExecutionPolicy Bypass -File scripts/demo/03_concurrency_test.ps1
```

## 后端 / 前端地址

- 后端：http://localhost:28081
- 前端：http://localhost:25173

## 答辩现场推荐流程

1. 先执行 05_reset_demo_scene.sql 清理旧数据
2. 依次执行 00 → 01 → 02 准备演示数据
3. 在浏览器中按演示流程展示各场景
4. 运行 03_concurrency_test.ps1 展示并发安全
5. 执行 04_verify_demo_data.sql 验证数据库状态

## 失败兜底方案

| 问题 | 兜底 |
|------|------|
| SQL 执行失败 | 检查 MySQL 是否运行，端口 23306 |
| 脚本数据冲突 | 先执行 05_reset_demo_scene.sql |
| PowerShell 拒绝执行 | 使用 -ExecutionPolicy Bypass 参数 |
| 后端无法连接 | 检查 IDEA 是否已启动，端口 28081 |
