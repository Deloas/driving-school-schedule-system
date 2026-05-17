-- ============================================================
-- 04_verify_demo_data.sql
-- 用途：演示后验证数据库状态（只读）
-- 检查满员场景、调剂场景、并发安全、练车闭环
-- ============================================================

USE driving_school_schedule;

-- 1. 满员不可约场景 (2026-06-20)
SELECT '=== 满员场景 (2026-06-20) ===' AS info;
SELECT cs.id, c.name AS coach, cs.schedule_date, cs.time_slot, cs.max_students, cs.current_students, cs.status
FROM coach_schedule cs JOIN coach c ON cs.coach_id = c.id
WHERE cs.schedule_date = '2026-06-20';

-- 2. 调剂场景 (2026-06-21)  主教练 vs 调剂教练对比
SELECT '=== 调剂场景 (2026-06-21) ===' AS info;
SELECT c.name AS coach, cs.max_students, cs.current_students,
       cs.max_students - cs.current_students AS remain, cs.status
FROM coach_schedule cs JOIN coach c ON cs.coach_id = c.id
WHERE cs.schedule_date = '2026-06-21' AND cs.time_slot = 'MORNING';

-- 3. 并发预约安全 (2026-06-22)
SELECT '=== 并发场景 (2026-06-22) ===' AS info;
SELECT cs.id, cs.max_students, cs.current_students, cs.status
FROM coach_schedule cs
WHERE cs.schedule_date = '2026-06-22' AND cs.time_slot = 'MORNING';

-- 验证是否只有 1 条 SUCCESS
SELECT COUNT(*) AS succ_count, '期望=1' AS expect
FROM reservation
WHERE reservation_date = '2026-06-22' AND time_slot = 'MORNING' AND status = 'SUCCESS';

-- 4. 调剂预约记录
SELECT '=== 调剂预约记录 ===' AS info;
SELECT r.id, stu.name AS student, mc.name AS main_coach, ac.name AS actual_coach,
       r.is_adjusted, r.adjust_reason, r.status, r.reservation_date, r.time_slot
FROM reservation r
JOIN student stu ON r.student_id = stu.id
JOIN coach mc ON r.main_coach_id = mc.id
JOIN coach ac ON r.actual_coach_id = ac.id
WHERE r.is_adjusted = 1
ORDER BY r.id DESC LIMIT 5;

-- 5. 练车闭环 (2026-06-23): reservation + training_record
SELECT '=== 练车闭环 (2026-06-23) ===' AS info;
SELECT r.id, stu.name AS student, ac.name AS coach, r.status, r.reservation_date, r.time_slot
FROM reservation r
JOIN student stu ON r.student_id = stu.id
JOIN coach ac ON r.actual_coach_id = ac.id
WHERE r.reservation_date = '2026-06-23';

SELECT tr.id, stu.name AS student, c.name AS coach, tr.training_content, tr.coach_comment, tr.result
FROM training_record tr
JOIN student stu ON tr.student_id = stu.id
JOIN coach c ON tr.coach_id = c.id
WHERE tr.training_date = '2026-06-23';

-- 6. 学员练车次数
SELECT '=== 学员练车次数 ===' AS info;
SELECT name, completed_training_count, required_training_count
FROM student
WHERE name IN ('王小明', '李小红');
