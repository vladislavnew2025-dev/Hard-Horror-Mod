@echo off
setlocal EnableExtensions EnableDelayedExpansion

if not exist logs mkdir logs
if not exist out mkdir out

for /f %%i in ('powershell -NoProfile -Command "Get-Date -Format yyyyMMdd_HHmmss"') do set "ts=%%i"
if "%ts%"=="" set "ts=fallback_timestamp"
set "LOG_FILE=logs\build_%ts%.log"
set "SRC_LIST=logs\sources_%ts%.txt"

echo [HardHorror] Quick build started > "%LOG_FILE%"
echo [HardHorror] Timestamp: %DATE% %TIME% >> "%LOG_FILE%"

echo Checking javac availability... >> "%LOG_FILE%"
where javac >nul 2>&1
if errorlevel 1 (
  echo ERROR: javac was not found in PATH. >> "%LOG_FILE%"
  echo Build FAILED. See log: %LOG_FILE%
  type "%LOG_FILE%"
  pause
  exit /b 1
)

if exist "%SRC_LIST%" del "%SRC_LIST%"
for /r src\main\java %%f in (*.java) do echo %%f>>"%SRC_LIST%"

findstr /r /c:"." "%SRC_LIST%" >nul
if errorlevel 1 (
  echo ERROR: no Java sources were found. >> "%LOG_FILE%"
  echo Build FAILED. See log: %LOG_FILE%
  type "%LOG_FILE%"
  pause
  exit /b 1
)

echo Compiling Java sources...
echo javac -d out @%SRC_LIST% >> "%LOG_FILE%"
javac -d out @"%SRC_LIST%" >> "%LOG_FILE%" 2>&1
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
