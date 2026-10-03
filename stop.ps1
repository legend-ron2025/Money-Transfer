#!/usr/bin/env pwsh
# ============================================================
#  MoneyTracker — Stop all local services
#  Usage:  .\stop.ps1
# ============================================================

$root = $PSScriptRoot

Write-Host ""
Write-Host "  Stopping MoneyTracker services..." -ForegroundColor Yellow
Write-Host ""

function Stop-ByPidFile($label, $pidFile) {
    if (Test-Path $pidFile) {
        $storedPid = (Get-Content $pidFile).Trim()
        if ($storedPid -match "^\d+$") {
            Stop-Process -Id $storedPid -Force -EA SilentlyContinue
            Remove-Item $pidFile -EA SilentlyContinue
            Write-Host "  ✅  $label stopped (PID $storedPid)" -ForegroundColor Green
        }
    } else {
        Write-Host "  ⚠️   $label — no PID file found" -ForegroundColor DarkYellow
    }
}

# Stop Backend
Stop-ByPidFile "Backend  " "$root\logs\backend.pid"

# Stop Website
Stop-ByPidFile "Website  " "$root\logs\website.pid"

# Kill any tsx / node processes on port 3000/3001 as backup
$ports = @(3000, 3001)
foreach ($port in $ports) {
    $line = netstat -ano 2>$null | Select-String ":$port .*LISTENING" | Select-Object -First 1
    if ($line) {
        $procId = ($line.ToString() -split "\s+")[-1]
        if ($procId -match "^\d+$") {
            Stop-Process -Id $procId -Force -EA SilentlyContinue
        }
    }
}

# Stop Redis 8 on port 6380
$redisCli = "$env:LOCALAPPDATA\Microsoft\WinGet\Packages\taizod1024.redis-windows-fork_Microsoft.Winget.Source_8wekyb3d8bbwe\Redis-8.10.1-Windows-x64-msys2\redis-cli.exe"
$shutdown = & $redisCli -p 6380 shutdown nosave 2>$null
Write-Host "  ✅  Redis 6380 shutdown sent" -ForegroundColor Green

Write-Host ""
Write-Host "  All services stopped. Run .\start.ps1 to restart." -ForegroundColor Cyan
Write-Host ""
