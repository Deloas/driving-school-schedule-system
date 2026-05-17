# ============================================================
# 03_concurrency_test.ps1
# 用途：演示 Redis 分布式锁 + 数据库条件 UPDATE 防并发超额
# 运行：powershell -ExecutionPolicy Bypass -File scripts/demo/03_concurrency_test.ps1
# 前提：先执行 00、01、02 SQL 脚本，后端运行在 localhost:28081
# ============================================================

$BaseUrl = "http://localhost:28081/api"
$DemoDate = "2026-06-22"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  驾校预约系统 - 并发预约安全演示" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# ---- 登录 ----
function Login($username, $password) {
    $body = @{ username = $username; password = $password } | ConvertTo-Json
    $resp = Invoke-RestMethod -Uri "$BaseUrl/auth/login" -Method POST -Body $body -ContentType "application/json"
    if ($resp.code -ne 200) { Write-Host "登录失败: $username" -ForegroundColor Red; return $null }
    return $resp.data.token
}

Write-Host "正在登录 student001..." -ForegroundColor Gray
$token1 = Login "student001" "123456"
if (-not $token1) { Write-Host "student001 登录失败，请检查账号状态" -ForegroundColor Red; exit 1 }

Write-Host "正在登录 student002..." -ForegroundColor Gray
$token2 = Login "student002" "123456"
if (-not $token2) { Write-Host "student002 登录失败，请检查账号状态" -ForegroundColor Red; exit 1 }

Write-Host "登录成功" -ForegroundColor Green
Write-Host ""

# ---- 找到并发测试排班 ----
$ScheduleId = $null
$scheduleUrl = "$BaseUrl/reservations/options?date=$DemoDate&timeSlot=MORNING"
try {
    $resp1 = Invoke-RestMethod -Uri $scheduleUrl -Headers @{ Authorization = "Bearer $token1" }
    if ($resp1.data.mainCoachOption.scheduleId) {
        $ScheduleId = $resp1.data.mainCoachOption.scheduleId
    }
} catch { }

if (-not $ScheduleId -or $ScheduleId -eq 0) {
    Write-Host "未找到 $DemoDate MORNING 的排班，请先执行 SQL 脚本准备数据" -ForegroundColor Yellow
    Write-Host "或手动设置 `$ScheduleId 变量后重新运行" -ForegroundColor Yellow
    exit 1
}

Write-Host "并发测试排班 ID: $ScheduleId" -ForegroundColor Gray
Write-Host ""

# ---- 并发预约 ----
function Book($token, $scheduleId, $label) {
    $body = @{ scheduleId = $scheduleId } | ConvertTo-Json
    try {
        $resp = Invoke-RestMethod -Uri "$BaseUrl/reservations" -Method POST -Body $body -ContentType "application/json" -Headers @{ Authorization = "Bearer $token" }
        return "[$label] code=$($resp.code) message=$($resp.message)"
    } catch {
        if ($_.Exception.Response) {
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $body = $reader.ReadToEnd()
            return "[$label] ERROR: $body"
        }
        return "[$label] ERROR: $_"
    }
}

Write-Host "发起并发预约请求..." -ForegroundColor Cyan

$job1 = Start-Job -ScriptBlock {
    param($token, $id, $label)
    $body = @{ scheduleId = $id } | ConvertTo-Json
    try { $r = Invoke-RestMethod -Uri "http://localhost:28081/api/reservations" -Method POST -Body $body -ContentType "application/json" -Headers @{ Authorization = "Bearer $token" }; "[$label] code=$($r.code) $($r.message)" }
    catch { "[$label] 请求失败" }
} -ArgumentList $token1, $ScheduleId, "student001"

$job2 = Start-Job -ScriptBlock {
    param($token, $id, $label)
    $body = @{ scheduleId = $id } | ConvertTo-Json
    try { $r = Invoke-RestMethod -Uri "http://localhost:28081/api/reservations" -Method POST -Body $body -ContentType "application/json" -Headers @{ Authorization = "Bearer $token" }; "[$label] code=$($r.code) $($r.message)" }
    catch { "[$label] 请求失败" }
} -ArgumentList $token2, $ScheduleId, "student002"

$result1 = $job1 | Wait-Job | Receive-Job
$result2 = $job2 | Wait-Job | Receive-Job
Remove-Job $job1, $job2

Write-Host ""
Write-Host "======== 并发结果 ========" -ForegroundColor Cyan
Write-Host $result1
Write-Host $result2

$successCount = 0
if ($result1 -match "code=200") { $successCount++ }
if ($result2 -match "code=200") { $successCount++ }

Write-Host ""
if ($successCount -eq 1) {
    Write-Host "结论: 仅 1 人预约成功，Redis 锁 + 条件 UPDATE 双重保障有效" -ForegroundColor Green
} elseif ($successCount -eq 0) {
    Write-Host "结论: 无人成功，可能排班已满或数据未就绪" -ForegroundColor Yellow
} else {
    Write-Host "结论: 超过 1 人成功，可能存在超额风险" -ForegroundColor Red
}

Write-Host ""
Write-Host "请执行 04_verify_demo_data.sql 验证数据库状态" -ForegroundColor Gray
