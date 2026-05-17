-- ============================================================
-- 05_reset_demo_scene.sql
-- 用途：重置演示日期数据，准备重新演示
-- 影响范围：仅 2026-06-20 ~ 2026-06-23 四个演示日期
-- 注意：只做 UPDATE 和 DELETE，不做 DROP / TRUNCATE / ALTER
-- ============================================================

USE driving_school_schedule;

-- 1. 删除演示日期的 training_record
DELETE FROM training_record WHERE training_date IN ('2026-06-20', '2026-06-21', '2026-06-22', '2026-06-23');

-- 2. 删除演示日期的 reservation
DELETE FROM reservation WHERE reservation_date IN ('2026-06-20', '2026-06-21', '2026-06-22', '2026-06-23');

-- 3. 将演示日期的排班重置为空闲状态
UPDATE coach_schedule
SET current_students = 0, status = 'OPEN'
WHERE schedule_date IN ('2026-06-20', '2026-06-21', '2026-06-22', '2026-06-23');

-- 4. 确认清理结果
SELECT '演示日期数据已重置' AS status;
SELECT schedule_date, COUNT(*) AS schedule_count FROM coach_schedule
WHERE schedule_date IN ('2026-06-20', '2026-06-21', '2026-06-22', '2026-06-23')
GROUP BY schedule_date;
