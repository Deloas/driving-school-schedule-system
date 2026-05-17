-- ============================================================
-- 02_prepare_adjustment_scene.sql
-- 用途：准备"主教练满员 + 调剂推荐"演示场景
-- 日期：2026-06-21 上午 MORNING
-- 主教练 张三 (id=2)：满员 (max=1, current=1)
-- 调剂教练 唐国义 (id=1)：有空位 (max=5, current=0)
-- ============================================================

USE driving_school_schedule;

SET @demo_date = '2026-06-21';
SET @main_coach_id = 2; -- 张三
SET @adjust_coach_id = 1; -- 唐国义

-- 1. 张三排班：满员
INSERT INTO coach_schedule (coach_id, vehicle_id, schedule_date, time_slot, max_students, current_students, status)
VALUES (@main_coach_id, 2, @demo_date, 'MORNING', 1, 1, 'OPEN')
ON DUPLICATE KEY UPDATE max_students = 1, current_students = 1, status = 'OPEN';

-- 2. 唐国义排班：有空位
INSERT INTO coach_schedule (coach_id, vehicle_id, schedule_date, time_slot, max_students, current_students, status)
VALUES (@adjust_coach_id, 1, @demo_date, 'MORNING', 5, 0, 'OPEN')
ON DUPLICATE KEY UPDATE max_students = 5, current_students = 0, status = 'OPEN';

-- 3. 删除 student002 在此日期的 SUCCESS 预约，避免重复预约干扰
DELETE FROM reservation
WHERE student_id = 2 AND reservation_date = @demo_date AND time_slot = 'MORNING' AND status = 'SUCCESS';

-- 4. 如果张三排班需要占位预约，创建一条
INSERT IGNORE INTO reservation (student_id, main_coach_id, actual_coach_id, vehicle_id, schedule_id, reservation_date, time_slot, status, is_adjusted)
SELECT 2, @main_coach_id, @main_coach_id, 2, id, @demo_date, 'MORNING', 'SUCCESS', 0
FROM coach_schedule
WHERE coach_id = @main_coach_id AND schedule_date = @demo_date AND time_slot = 'MORNING';

-- 5. 验证：检查两条排班
SELECT cs.id, c.name AS coach_name, cs.schedule_date, cs.time_slot,
       cs.max_students, cs.current_students, cs.status
FROM coach_schedule cs JOIN coach c ON cs.coach_id = c.id
WHERE cs.schedule_date = @demo_date AND cs.time_slot = 'MORNING';
