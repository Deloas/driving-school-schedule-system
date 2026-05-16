# 06 API 接口设计

## 1. 接口基础规范

基础路径：

```text
/api
```

统一响应：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

错误响应：

```json
{
  "code": 400,
  "message": "该时间段已满员",
  "data": null
}
```

## 2. 认证接口

### 2.1 登录

```http
POST /api/auth/login
```

请求：

```json
{
  "username": "student001",
  "password": "123456"
}
```

响应：

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "jwt-token",
    "role": "STUDENT",
    "userId": 1,
    "relatedId": 1001
  }
}
```

### 2.2 获取当前用户信息

```http
GET /api/auth/me
```

## 3. 学员接口

### 3.1 分页查询学员

```http
GET /api/students?page=1&size=10&keyword=张
```

### 3.2 新增学员

```http
POST /api/students
```

请求：

```json
{
  "name": "王小明",
  "phone": "13800000000",
  "gender": "男",
  "mainCoachId": 1,
  "subjectType": "SUBJECT_2",
  "requiredTrainingCount": 8
}
```

### 3.3 修改学员

```http
PUT /api/students/{id}
```

### 3.4 删除或禁用学员

```http
DELETE /api/students/{id}
```

## 4. 教练接口

### 4.1 查询教练列表

```http
GET /api/coaches
```

### 4.2 新增教练

```http
POST /api/coaches
```

请求：

```json
{
  "name": "唐国义",
  "phone": "13900000000",
  "licenseNo": "COACH-001",
  "maxStudentsPerHalfDay": 5
}
```

### 4.3 查询教练负责学员

```http
GET /api/coaches/{id}/students
```

### 4.4 查询教练工作量

```http
GET /api/coaches/{id}/workload?startDate=2026-05-01&endDate=2026-05-31
```

## 5. 车辆接口

### 5.1 查询车辆

```http
GET /api/vehicles
```

### 5.2 新增车辆

```http
POST /api/vehicles
```

请求：

```json
{
  "plateNumber": "苏A12345",
  "coachId": 1,
  "vehicleType": "C1",
  "status": "NORMAL"
}
```

### 5.3 修改车辆状态

```http
PATCH /api/vehicles/{id}/status
```

请求：

```json
{
  "status": "MAINTENANCE"
}
```

## 6. 排班接口

### 6.1 创建排班

```http
POST /api/schedules
```

请求：

```json
{
  "coachId": 1,
  "vehicleId": 2,
  "scheduleDate": "2026-05-16",
  "timeSlot": "MORNING",
  "maxStudents": 5
}
```

### 6.2 批量生成排班

```http
POST /api/schedules/batch
```

请求：

```json
{
  "coachIds": [1, 2, 3],
  "startDate": "2026-05-16",
  "endDate": "2026-05-22",
  "timeSlots": ["MORNING", "AFTERNOON"],
  "maxStudents": 5
}
```

### 6.3 查询可预约排班

```http
GET /api/schedules/available?date=2026-05-16&timeSlot=MORNING
```

响应：

```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "scheduleId": 10,
      "coachId": 1,
      "coachName": "唐国义",
      "vehicleId": 2,
      "plateNumber": "苏A12345",
      "maxStudents": 5,
      "currentStudents": 3,
      "remainCount": 2,
      "status": "OPEN"
    }
  ]
}
```

## 7. 预约接口

### 7.1 查询我的可预约方案

```http
GET /api/reservations/options?date=2026-05-16&timeSlot=MORNING
```

说明：

该接口用于学员端预约页，返回主教练状态和调剂推荐。

响应：

```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "mainCoachOption": {
      "scheduleId": 10,
      "coachId": 1,
      "coachName": "唐国义",
      "remainCount": 0,
      "available": false
    },
    "adjustmentOptions": [
      {
        "scheduleId": 12,
        "coachId": 2,
        "coachName": "张三",
        "remainCount": 3,
        "todayLoad": 2,
        "recommendReason": "该教练当前空余名额较多"
      }
    ]
  }
}
```

### 7.2 创建预约

```http
POST /api/reservations
```

请求：

```json
{
  "scheduleId": 12,
  "date": "2026-05-16",
  "timeSlot": "MORNING",
  "acceptAdjustment": true
}
```

响应：

```json
{
  "code": 200,
  "message": "预约成功",
  "data": {
    "reservationId": 10001,
    "isAdjusted": true,
    "actualCoachName": "张三"
  }
}
```

### 7.3 取消预约

```http
POST /api/reservations/{id}/cancel
```

请求：

```json
{
  "reason": "临时有事"
}
```

### 7.4 查询我的预约

```http
GET /api/reservations/my
```

### 7.5 教练查询当天预约名单

```http
GET /api/reservations/coach/today
```

## 8. 练车记录接口

### 8.1 确认到场

```http
POST /api/training/{reservationId}/arrive
```

### 8.2 完成练车

```http
POST /api/training/{reservationId}/complete
```

请求：

```json
{
  "trainingContent": "倒车入库、侧方停车",
  "coachComment": "基础较好，需加强方向盘控制"
}
```

### 8.3 查询学员练车记录

```http
GET /api/training/student/{studentId}
```

## 9. 统计接口

### 9.1 管理员首页统计

```http
GET /api/statistics/dashboard
```

响应：

```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "todayReservationCount": 20,
    "availableCoachCount": 5,
    "availableVehicleCount": 6,
    "adjustedReservationCount": 4
  }
}
```

### 9.2 教练工作量对比

```http
GET /api/statistics/coach-workload?startDate=2026-05-01&endDate=2026-05-31
```

### 9.3 每日预约趋势

```http
GET /api/statistics/reservation-trend?startDate=2026-05-01&endDate=2026-05-31
```
