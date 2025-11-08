# Script dừng tất cả các services
# Author: JobReady Team
# Date: 2025-11-08

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Stopping All JobReady Services" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$ports = @(8761, 8080, 8081, 8082, 8083, 8084, 8085, 9090, 9091)

function Stop-ProcessOnPort {
    param([int]$Port)
    
    $connections = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue
    
    if ($connections) {
        $processIds = $connections.OwningProcess | Select-Object -Unique
        
        foreach ($pid in $processIds) {
            try {
                $process = Get-Process -Id $pid -ErrorAction SilentlyContinue
                if ($process) {
                    Write-Host "Stopping process on port ${Port}: $($process.ProcessName) (PID: $pid)" -ForegroundColor Yellow
                    Stop-Process -Id $pid -Force
                    Write-Host "  ✓ Stopped" -ForegroundColor Green
                }
            } catch {
                Write-Host "  ✗ Failed to stop process on port $Port" -ForegroundColor Red
            }
        }
    } else {
        Write-Host "No process found on port $Port" -ForegroundColor Gray
    }
}

Write-Host "Checking and stopping services on ports..." -ForegroundColor Yellow
Write-Host ""

foreach ($port in $ports) {
    Stop-ProcessOnPort -Port $port
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "All services stopped!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "You can now restart services safely." -ForegroundColor White
