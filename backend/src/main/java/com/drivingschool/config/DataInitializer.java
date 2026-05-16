package com.drivingschool.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drivingschool.coach.entity.Coach;
import com.drivingschool.coach.mapper.CoachMapper;
import com.drivingschool.entity.SysUser;
import com.drivingschool.mapper.SysUserMapper;
import com.drivingschool.student.entity.Student;
import com.drivingschool.student.mapper.StudentMapper;
import com.drivingschool.vehicle.entity.Vehicle;
import com.drivingschool.vehicle.mapper.VehicleMapper;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 测试数据初始化器 — 仅在 dev 环境下运行
 * <p>
 * M3 阶段：初始化教练、车辆、学员基础资料，并创建对应的登录账号。
 * 所有初始化操作均为幂等（存在则跳过），可安全重复执行。
 * </p>
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile("dev")
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper sysUserMapper;
    private final CoachMapper coachMapper;
    private final StudentMapper studentMapper;
    private final VehicleMapper vehicleMapper;
    private final com.drivingschool.schedule.mapper.CoachScheduleMapper scheduleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(String... args) {
        initCoaches();
        initVehicles();
        initStudents();
        initSchedules();
        log.info("M4 测试数据初始化完成");
    }

    // ==================== 教练初始化 ====================

    private void initCoaches() {
        String[][] coachData = {
                {"唐国义", "13900001111", "COACH-A001"},
                {"张三", "13900002222", "COACH-A002"},
                {"李四", "13900003333", "COACH-A003"},
                {"王强", "13900004444", "COACH-A004"},
                {"陈敏", "13900005555", "COACH-A005"},
        };

        for (int i = 0; i < coachData.length; i++) {
            String name = coachData[i][0];
            String phone = coachData[i][1];
            String licenseNo = coachData[i][2];

            Coach coach = getOrCreateCoach(name, phone, licenseNo);
            // coach001→教练[0], coach002→教练[1], ... 一一对应
            String username = "coach" + String.format("%03d", i + 1);
            updateOrCreateSysUser(username, "123456", "COACH", coach.getId());
            // admin 账号确保存在
            updateOrCreateSysUser("admin", "123456", "ADMIN", null);
        }
    }

    // ==================== 车辆初始化 ====================

    private void initVehicles() {
        String[] plates = {"苏A10001", "苏A10002", "苏A10003", "苏A10004", "苏A10005", "苏A10006"};

        // 查询所有正常教练，轮换分配车辆
        List<Coach> coaches = coachMapper.selectList(
                new LambdaQueryWrapper<Coach>().eq(Coach::getStatus, "NORMAL").orderByAsc(Coach::getId));

        for (int i = 0; i < plates.length; i++) {
            Long bindCoachId = coaches.isEmpty() ? null : coaches.get(i % coaches.size()).getId();
            createVehicleIfNotExists(plates[i], bindCoachId);
        }
    }

    // ==================== 学员初始化 ====================

    private void initStudents() {
        String[][] studentData = {
                {"王小明", "13800000001", "男", "SUBJECT_2"},
                {"李小红", "13800000002", "女", "SUBJECT_2"},
                {"赵一鸣", "13800000003", "男", "SUBJECT_2"},
                {"陈雨", "13800000004", "女", "SUBJECT_3"},
                {"刘洋", "13800000005", "男", "SUBJECT_3"},
                {"张丽", "13800000006", "女", "SUBJECT_2"},
                {"周杰", "13800000007", "男", "SUBJECT_2"},
                {"吴婷", "13800000008", "女", "SUBJECT_3"},
                {"郑浩", "13800000009", "男", "SUBJECT_2"},
                {"孙悦", "13800000010", "女", "SUBJECT_2"},
                {"钱进", "13800000011", "男", "SUBJECT_3"},
                {"马超", "13800000012", "男", "SUBJECT_2"},
                {"黄蕾", "13800000013", "女", "SUBJECT_2"},
                {"林峰", "13800000014", "男", "SUBJECT_3"},
                {"何雪", "13800000015", "女", "SUBJECT_2"},
                {"罗明", "13800000016", "男", "SUBJECT_2"},
                {"梁静", "13800000017", "女", "SUBJECT_3"},
                {"宋涛", "13800000018", "男", "SUBJECT_2"},
                {"许晴", "13800000019", "女", "SUBJECT_2"},
                {"邓磊", "13800000020", "男", "SUBJECT_3"},
        };

        // 查询所有正常教练，轮换分配学员
        List<Coach> coaches = coachMapper.selectList(
                new LambdaQueryWrapper<Coach>().eq(Coach::getStatus, "NORMAL").orderByAsc(Coach::getId));

        for (int i = 0; i < studentData.length; i++) {
            String name = studentData[i][0];
            String phone = studentData[i][1];
            String gender = studentData[i][2];
            String subject = studentData[i][3];
            Long coachId = coaches.isEmpty() ? null : coaches.get(i % coaches.size()).getId();

            Student student = createStudentIfNotExists(name, phone, gender, subject, coachId);

            // student001 对应第一个学员（王小明）
            if (i == 0) {
                updateOrCreateSysUser("student001", "123456", "STUDENT", student.getId());
            }
        }
    }

    // ==================== 幂等创建辅助方法 ====================

    /** 创建教练（如已存在同名教练则跳过） */
    private Coach getOrCreateCoach(String name, String phone, String licenseNo) {
        Coach existing = coachMapper.selectOne(
                new LambdaQueryWrapper<Coach>().eq(Coach::getName, name));
        if (existing != null) {
            // 更新手机号和教练证号（可能被手动修改过）
            existing.setPhone(phone);
            existing.setLicenseNo(licenseNo);
            coachMapper.updateById(existing);
            return existing;
        }
        Coach coach = new Coach();
        coach.setName(name);
        coach.setPhone(phone);
        coach.setLicenseNo(licenseNo);
        coach.setMaxStudentsPerHalfDay(5);
        coach.setStatus("NORMAL");
        coachMapper.insert(coach);
        log.info("已创建教练：{}", name);
        return coach;
    }

    /** 创建车辆（如车牌号已存在则跳过） */
    private void createVehicleIfNotExists(String plate, Long coachId) {
        Long count = vehicleMapper.selectCount(
                new LambdaQueryWrapper<Vehicle>().eq(Vehicle::getPlateNumber, plate));
        if (count > 0) return;

        Vehicle v = new Vehicle();
        v.setPlateNumber(plate);
        v.setCoachId(coachId);
        v.setVehicleType("C1");
        v.setStatus("NORMAL");
        vehicleMapper.insert(v);
        log.info("已创建车辆：{}", plate);
    }

    /** 创建学员（如手机号已存在则跳过） */
    private Student createStudentIfNotExists(String name, String phone, String gender, String subject, Long coachId) {
        Student existing = studentMapper.selectOne(
                new LambdaQueryWrapper<Student>().eq(Student::getPhone, phone));
        if (existing != null) return existing;

        Student s = new Student();
        s.setName(name);
        s.setPhone(phone);
        s.setGender(gender);
        s.setMainCoachId(coachId);
        s.setSubjectType(subject);
        s.setRequiredTrainingCount(8);
        s.setCompletedTrainingCount(0);
        s.setStatus("NORMAL");
        studentMapper.insert(s);
        log.info("已创建学员：{}", name);
        return s;
    }

    /** 创建或更新系统用户，并修正 relatedId 指向真实业务数据 */
    private void updateOrCreateSysUser(String username, String rawPwd, String role, Long relatedId) {
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user == null) {
            user = new SysUser();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(rawPwd));
            user.setRole(role);
            user.setRelatedId(relatedId);
            user.setStatus("ENABLE");
            sysUserMapper.insert(user);
            log.info("已创建系统用户：{} / 角色：{}", username, role);
        } else {
            // 更新 relatedId 指向真实业务数据（M2 中为占位值 1）
            if (relatedId != null && !relatedId.equals(user.getRelatedId())) {
                user.setRelatedId(relatedId);
                sysUserMapper.updateById(user);
                log.info("已更新 {} 的 relatedId: {}", username, relatedId);
            }
        }
    }

    // ==================== 排班初始化（M4） ====================

    private void initSchedules() {
        List<Coach> coaches = coachMapper.selectList(
                new LambdaQueryWrapper<Coach>().eq(Coach::getStatus, "NORMAL").orderByAsc(Coach::getId));
        List<Vehicle> vehicles = vehicleMapper.selectList(
                new LambdaQueryWrapper<Vehicle>().eq(Vehicle::getStatus, "NORMAL").orderByAsc(Vehicle::getId));
        if (coaches.isEmpty() || vehicles.isEmpty()) return;

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String[] slots = {"MORNING", "AFTERNOON"};

        for (String slot : slots) {
            for (int i = 0; i < coaches.size(); i++) {
                Coach coach = coaches.get(i);
                // 已有排班则跳过
                Long count = scheduleMapper.selectCount(new LambdaQueryWrapper<com.drivingschool.schedule.entity.CoachSchedule>()
                        .eq(com.drivingschool.schedule.entity.CoachSchedule::getCoachId, coach.getId())
                        .eq(com.drivingschool.schedule.entity.CoachSchedule::getScheduleDate, tomorrow)
                        .eq(com.drivingschool.schedule.entity.CoachSchedule::getTimeSlot, slot));
                if (count > 0) continue;

                com.drivingschool.schedule.entity.CoachSchedule s = new com.drivingschool.schedule.entity.CoachSchedule();
                s.setCoachId(coach.getId());
                s.setVehicleId(vehicles.get(i % vehicles.size()).getId());
                s.setScheduleDate(tomorrow);
                s.setTimeSlot(slot);
                s.setMaxStudents(5);
                s.setCurrentStudents(0);
                s.setStatus("OPEN");
                scheduleMapper.insert(s);
            }
        }
        log.info("已创建明天上/下午排班，共 {} 名教练", coaches.size());
    }
}
