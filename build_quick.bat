@echo off
setlocal EnableExtensions EnableDelayedExpansion

if not exist logs mkdir logs
if not exist out mkdir out

set ts=%DATE:~-4%%DATE:~4,2%%DATE:~7,2%_%TIME:~0,2%%TIME:~3,2%%TIME:~6,2%
set ts=%ts: =0%
set LOG_FILE=logs\build_%ts%.log

echo [HardHorror] Quick build started > "%LOG_FILE%"
echo [HardHorror] Timestamp: %DATE% %TIME% >> "%LOG_FILE%"

echo Compiling Java sources...
javac -d out src\main\java\com\hardhorror\*.java >> "%LOG_FILE%" 2>&1
if errorlevel 1 (
  echo Build FAILED. See log: %LOG_FILE%
  type "%LOG_FILE%"
  pause
  exit /b 1
)

echo Build OK. Log saved: %LOG_FILE%
echo --- Last 20 log lines ---
powershell -NoProfile -Command "Get-Content -Path '%LOG_FILE%' -Tail 20"
pause
exit /b 0
