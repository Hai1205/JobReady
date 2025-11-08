# Script khởi động các services theo đúng thứ tự
# Author: JobReady Team
# Date: 2025-11-08

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  JobReady Microservices Startup" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$services = @(
    @{Name="Discovery Service"; Port=8761; Module="discovery-service"; Wait=30},
    @{Name="Gateway Service"; Port=8080; Module="gateway-service"; Wait=10},
    @{Name="User Service"; Port=8083; Module="user-service"; GrpcPort=9090; Wait=10},
    @{Name="Mail Service"; Port=8084; Module="mail-service"; Wait=5},
    @{Name="Auth Service"; Port=8081; Module="auth-service"; Wait=5},
    @{Name="CV Service"; Port=8082; Module="cv-service"; Wait=5},
    @{Name="AI Service"; Port=8085; Module="ai-service"; Wait=5}
)

# Kiểm tra port có đang được sử dụng không
function Test-Port {
    param([int]$Port)
    $connection = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue
    return $null -ne $connection
}

# Kill process trên port
function Stop-ProcessOnPort {
    param([int]$Port)
    $processId = (Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue).OwningProcess
    if ($processId) {
        Write-Host "  Killing process on port $Port..." -ForegroundColor Yellow
        Stop-Process -Id $processId -Force -ErrorAction SilentlyContinue
        Start-Sleep -Seconds 2
    }
}

Write-Host "Checking and cleaning ports..." -ForegroundColor Yellow
foreach ($service in $services) {
    if (Test-Port $service.Port) {
        Write-Host "  Port $($service.Port) is in use. Cleaning..." -ForegroundColor Red
        Stop-ProcessOnPort $service.Port
    }
    if ($service.GrpcPort -and (Test-Port $service.GrpcPort)) {
        Write-Host "  gRPC Port $($service.GrpcPort) is in use. Cleaning..." -ForegroundColor Red
        Stop-ProcessOnPort $service.GrpcPort
    }
}

Write-Host ""
Write-Host "Starting services..." -ForegroundColor Green
Write-Host ""

$processIds = @()

foreach ($service in $services) {
    Write-Host "[$($services.IndexOf($service)+1)/$($services.Count)] Starting $($service.Name)..." -ForegroundColor Cyan
    
    # Chạy service trong terminal mới
    $cmd = "cd '$PSScriptRoot'; mvn spring-boot:run -pl $($service.Module)"
    $process = Start-Process powershell -ArgumentList "-NoExit", "-Command", $cmd -PassThru
    $processIds += $process.Id
    
    Write-Host "  Process ID: $($process.Id)" -ForegroundColor Gray
    Write-Host "  Waiting $($service.Wait) seconds..." -ForegroundColor Gray
    Start-Sleep -Seconds $service.Wait
    
    # Kiểm tra xem service đã khởi động chưa
    if (Test-Port $service.Port) {
        Write-Host "  ✓ $($service.Name) is UP on port $($service.Port)" -ForegroundColor Green
    } else {
        Write-Host "  ✗ Warning: $($service.Name) might not be ready yet" -ForegroundColor Yellow
    }
    Write-Host ""
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "All services started!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Service URLs:" -ForegroundColor White
Write-Host "  Eureka Dashboard: http://localhost:8761" -ForegroundColor Gray
Write-Host "  Gateway:          http://localhost:8080" -ForegroundColor Gray
Write-Host "  Auth Service:     http://localhost:8081" -ForegroundColor Gray
Write-Host "  CV Service:       http://localhost:8082" -ForegroundColor Gray
Write-Host "  User Service:     http://localhost:8083" -ForegroundColor Gray
Write-Host "  Mail Service:     http://localhost:8084" -ForegroundColor Gray
Write-Host "  AI Service:       http://localhost:8085" -ForegroundColor Gray
Write-Host ""
Write-Host "Press Ctrl+C to stop all services or close all terminal windows manually" -ForegroundColor Yellow
Write-Host ""

# Giữ script chạy
try {
    while ($true) {
        Start-Sleep -Seconds 1
    }
} finally {
    Write-Host "Stopping all services..." -ForegroundColor Red
    foreach ($pid in $processIds) {
        Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
    }
}
