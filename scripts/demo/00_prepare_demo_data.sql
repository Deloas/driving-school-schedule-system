-- ============================================================
-- 00_prepare_demo_data.sql
-- 用途：检查答辩演示基础数据，确保必要账号、教练、学员、车辆存在且状态正常
-- 前提：DataInitializer 已运行，基础数据已初始化
-- 影响：仅检查，不修改密码，不做破坏性操作
-- ============================================================

USE driving_school_schedule;

-- 1. 确保演示教练状态正常
UPDATE coach SET status = 'NORMAL' WHERE name IN ('唐国义', '张三');
SELECT id, name, status FROM coach WHERE name IN ('唐国义', '张三');

-- 2. 确保演示学员状态正常，主教练正确
-- 王小明 (student001) 主教练 = 唐国义
-- 李小红 (student002) 主教练 = 张三
UPDATE student SET status = 'NORMAL' WHERE name IN ('王小明', '李小红');
SELECT s.id, s.name, s.main_coach_id, c.name AS coach_name, s.status
FROM student s JOIN coach c ON s.main_coach_id = c.id
WHERE s.name IN ('王小明', '李小红');

-- 3. 确保演示车辆状态正常
UPDATE vehicle SET status = 'NORMAL' WHERE plate_number IN ('苏A10001', '苏A10002', '苏A10003');
SELECT id, plate_number, coach_id, status FROM vehicle WHERE plate_number IN ('苏A10001', '苏A10002', '苏A10003');

-- 4. 确保系统账号启用
UPDATE sys_user SET status = 'ENABLE' WHERE username IN ('admin', 'coach001', 'coach002', 'student001', 'student002');
SELECT id, username, role, related_id, status FROM sys_user WHERE username IN ('admin', 'coach001', 'coach002', 'student001', 'student002');

-- 5. 确保 student002 主教练为张三 (coach.id = 2)
SELECT s.id, s.name, s.main_coach_id, c.name AS coach_name
FROM student s JOIN coach c ON s.main_coach_id = c.id
WHERE s.name = '李小红';
