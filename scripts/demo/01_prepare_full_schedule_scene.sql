-- ============================================================
-- 01_prepare_full_schedule_scene.sql
-- 用途：准备"满员不可预约"演示场景
-- 日期：2026-06-20 上午 MORNING
-- 主教练：张三 (coach.id = 2)
-- 目标：student002 查询时看到主教练满员，无法预约
-- ============================================================

USE driving_school_schedule;

SET @demo_date = '2026-06-20';
SET @coach_id = 2; -- 张三
SET @vehicle_id = 2; -- 苏A10002

-- 1. 创建或更新张三的排班：满员场景 (max=1, current=1)
INSERT INTO coach_schedule (coach_id, vehicle_id, schedule_date, time_slot, max_students, current_students, status)
VALUES (@coach_id, @vehicle_id, @demo_date, 'MORNING', 1, 1, 'OPEN')
ON DUPLICATE KEY UPDATE max_students = 1, current_students = 1, status = 'OPEN', vehicle_id = @vehicle_id;

-- 2. 如果 current_students 需要占位，创建一条 SUCCESS 预约记录
-- 仅当该排班还没有 student002 的 SUCCESS 预约时才插入
INSERT IGNORE INTO reservation (student_id, main_coach_id, actual_coach_id, vehicle_id, schedule_id, reservation_date, time_slot, status, is_adjusted)
SELECT 2, 2, 2, @vehicle_id, id, @demo_date, 'MORNING', 'SUCCESS', 0
FROM coach_schedule WHERE coach_id = @coach_id AND schedule_date = @demo_date AND time_slot = 'MORNING';

-- 3. 验证
SELECT cs.id, c.name AS coach_name, cs.schedule_date, cs.time_slot, cs.max_students, cs.current_students, cs.status
FROM coach_schedule cs JOIN coach c ON cs.coach_id = c.id
WHERE cs.schedule_date = @demo_date AND cs.time_slot = 'MORNING';
