#!/usr/bin/env pwsh
# ============================================================
#  MoneyTracker — One-command local startup
#  Usage:  .\start.ps1
#  Stops:  .\stop.ps1
# ============================================================

$ErrorActionPreference = "Continue"
$root = $PSScriptRoot

Write-Host ""
Write-Host "  ╔══════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "  ║       MoneyTracker  •  Starting      ║" -ForegroundColor Cyan
Write-Host "  ╚══════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# ── Paths ────────────────────────────────────────────────────────────────────
$REDIS_CLI  = "$env:LOCALAPPDATA\Microsoft\WinGet\Packages\taizod1024.redis-windows-fork_Microsoft.Winget.Source_8wekyb3d8bbwe\Redis-8.10.1-Windows-x64-msys2\redis-cli.exe"
$REDIS_SRV  = "$env:LOCALAPPDATA\Microsoft\WinGet\Packages\taizod1024.redis-windows-fork_Microsoft.Winget.Source_8wekyb3d8bbwe\Redis-8.10.1-Windows-x64-msys2\redis-server.exe"
$REDIS_PORT = 6380
$BACKEND    = Join-Path $root "backend"
$WEBSITE    = Join-Path $root "website"

# ── 1. PostgreSQL ─────────────────────────────────────────────────────────────
Write-Host "  [1/4]  PostgreSQL..." -ForegroundColor Yellow
$pg = Get-Service postgresql-x64-* -ErrorAction SilentlyContinue | Select-Object -First 1
if ($pg -and $pg.Status -ne "Running") {
    Start-Service $pg.Name -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 2
}
$pgRunning = ($pg -and $pg.Status -eq "Running") -or (Get-Service postgresql-x64-* -EA SilentlyContinue | Where-Object { $_.Status -eq "Running" })
if ($pgRunning) {
    Write-Host "        ✅  PostgreSQL running on port 5432" -ForegroundColor Green
} else {
    Write-Host "        ❌  PostgreSQL not running — start it manually" -ForegroundColor Red
    Write-Host "            Install: https://www.postgresql.org/download/windows/" -ForegroundColor Gray
}

# ── 2. Redis ──────────────────────────────────────────────────────────────────
Write-Host "  [2/4]  Redis 8..." -ForegroundColor Yellow
$redisPing = & $REDIS_CLI -p $REDIS_PORT ping 2>$null
if ($redisPing -eq "PONG") {
    Write-Host "        ✅  Redis already running on port $REDIS_PORT" -ForegroundColor Green
} else {
    # Kill any stale process on that port
    $oldPid = (netstat -ano 2>$null | Select-String ":$REDIS_PORT .*LISTENING") -replace ".*LISTENING\s+",""
    if ($oldPid) { Stop-Process -Id $oldPid.Trim() -Force -EA SilentlyContinue }
    Start-Sleep -Seconds 1

    # Start Redis 8 in a new window
    Start-Process -FilePath $REDIS_SRV -ArgumentList "--port $REDIS_PORT --loglevel warning" `
        -WindowStyle Minimized -RedirectStandardOutput "$root\logs\redis.log" `
        -PassThru | Out-Null
    Start-Sleep -Seconds 2

    $redisPing = & $REDIS_CLI -p $REDIS_PORT ping 2>$null
    if ($redisPing -eq "PONG") {
        Write-Host "        ✅  Redis 8.10.1 started on port $REDIS_PORT" -ForegroundColor Green
    } else {
        Write-Host "        ⚠️   Redis may not have started — BullMQ workers will retry" -ForegroundColor DarkYellow
    }
}

# ── 3. Backend (Node.js + Express) ───────────────────────────────────────────
Write-Host "  [3/4]  Backend API..." -ForegroundColor Yellow
New-Item -ItemType Directory -Force -Path "$root\logs" | Out-Null

$backendProc = Start-Process -FilePath "cmd.exe" `
    -ArgumentList "/c", "node_modules\.bin\tsx.cmd src\index.ts > ..\logs\backend.log 2>&1" `
    -WorkingDirectory $BACKEND `
    -WindowStyle Hidden -PassThru
$backendProc.Id | Out-File "$root\logs\backend.pid" -Encoding ASCII

# Wait for backend to be ready (up to 20s)
$ready = $false
for ($i = 0; $i -lt 20; $i++) {
    Start-Sleep -Seconds 1
    try {
        $r = Invoke-WebRequest "http://localhost:3000/health" -UseBasicParsing -TimeoutSec 2 -EA Stop
        if ($r.StatusCode -eq 200) { $ready = $true; break }
    } catch {}
}

if ($ready) {
    Write-Host "        ✅  Backend API running on http://localhost:3000" -ForegroundColor Green
} else {
    Write-Host "        ❌  Backend failed to start — check logs\backend.log" -ForegroundColor Red
}

# ── 4. Website (Next.js) ──────────────────────────────────────────────────────
Write-Host "  [4/4]  Website..." -ForegroundColor Yellow

$websiteProc = Start-Process -FilePath "cmd.exe" `
    -ArgumentList "/c", "node node_modules\next\dist\bin\next dev -p 3001 > ..\logs\website.log 2>&1" `
    -WorkingDirectory $WEBSITE `
    -WindowStyle Hidden -PassThru
$websiteProc.Id | Out-File "$root\logs\website.pid" -Encoding ASCII

# Wait for website to be ready (up to 30s)
$ready = $false
for ($i = 0; $i -lt 30; $i++) {
    Start-Sleep -Seconds 1
    try {
        $r = Invoke-WebRequest "http://localhost:3001" -UseBasicParsing -TimeoutSec 2 -EA Stop
        if ($r.StatusCode -eq 200) { $ready = $true; break }
    } catch {}
}

if ($ready) {
    Write-Host "        ✅  Website running on http://localhost:3001" -ForegroundColor Green
} else {
    Write-Host "        ❌  Website failed to start — check logs\website.log" -ForegroundColor Red
}

# ── Summary ───────────────────────────────────────────────────────────────────
Write-Host ""
Write-Host "  ╔══════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "  ║            MoneyTracker is RUNNING               ║" -ForegroundColor Cyan
Write-Host "  ╠══════════════════════════════════════════════════╣" -ForegroundColor Cyan
Write-Host "  ║  🌐  Website      http://localhost:3001          ║" -ForegroundColor White
Write-Host "  ║  🚀  Backend API  http://localhost:3000          ║" -ForegroundColor White
Write-Host "  ║  📊  API Health   http://localhost:3000/health   ║" -ForegroundColor White
Write-Host "  ║  📋  Dashboard    http://localhost:3001/dashboard║" -ForegroundColor White
Write-Host "  ║  📥  Download     http://localhost:3001/download ║" -ForegroundColor White
Write-Host "  ╠══════════════════════════════════════════════════╣" -ForegroundColor Cyan
Write-Host "  ║  Logs  →  .\logs\backend.log                     ║" -ForegroundColor Gray
Write-Host "  ║          .\logs\website.log                      ║" -ForegroundColor Gray
Write-Host "  ║  Stop  →  .\stop.ps1                             ║" -ForegroundColor Gray
Write-Host "  ╚══════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""
