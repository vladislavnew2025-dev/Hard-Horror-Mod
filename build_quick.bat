@echo off
setlocal EnableExtensions EnableDelayedExpansion
cd /d "%~dp0"

if not exist logs mkdir logs
if not exist out mkdir out
if not exist dist mkdir dist

for /f %%i in ('powershell -NoProfile -Command "Get-Date -Format yyyyMMdd_HHmmss"') do set "ts=%%i"
if "%ts%"=="" set "ts=fallback_timestamp"
set "LOG_FILE=logs\build_%ts%.log"
set "JAR_FILE=dist\psychological-horror-test-%ts%.jar"
set /a SRC_COUNT=0
set /a CLASS_COUNT=0
set "BUILD_FAILED=0"

echo [PsychologicalHorror] Quick build started > "%LOG_FILE%"
echo [PsychologicalHorror] Working directory: %CD% >> "%LOG_FILE%"
echo [PsychologicalHorror] Timestamp: %DATE% %TIME% >> "%LOG_FILE%"

echo Checking javac availability... >> "%LOG_FILE%"
where javac >nul 2>&1
if errorlevel 1 (
  echo ERROR: javac was not found in PATH. >> "%LOG_FILE%"
  echo [PsychologicalHorror] RESULT: FAILED >> "%LOG_FILE%"
  echo Build FAILED. See log: %LOG_FILE%
  type "%LOG_FILE%"
  set "BUILD_FAILED=1"
  goto :END
)

echo Checking jar availability... >> "%LOG_FILE%"
where jar >nul 2>&1
if errorlevel 1 (
  echo WARNING: jar tool was not found in PATH, will use PowerShell zip fallback. >> "%LOG_FILE%"
)

for %%f in (src\main\java\com\hardhorror\*.java) do set /a SRC_COUNT+=1
if %SRC_COUNT% LEQ 0 (
  echo ERROR: no Java sources were found. >> "%LOG_FILE%"
  echo [PsychologicalHorror] RESULT: FAILED >> "%LOG_FILE%"
  echo Build FAILED. See log: %LOG_FILE%
  type "%LOG_FILE%"
  set "BUILD_FAILED=1"
  goto :END
)

echo Compiling Java sources...
echo Source files: %SRC_COUNT% >> "%LOG_FILE%"
echo javac -d out src\main\java\com\hardhorror\*.java >> "%LOG_FILE%"
echo [PsychologicalHorror] Compilation started >> "%LOG_FILE%"
javac -d out src\main\java\com\hardhorror\*.java >> "%LOG_FILE%" 2>&1
if errorlevel 1 (
  echo [PsychologicalHorror] Compilation failed >> "%LOG_FILE%"
  echo [PsychologicalHorror] RESULT: FAILED >> "%LOG_FILE%"
  echo Build FAILED. See log: %LOG_FILE%
  type "%LOG_FILE%"
  set "BUILD_FAILED=1"
  goto :END
)
echo [PsychologicalHorror] Compilation finished successfully >> "%LOG_FILE%"

for /r out %%f in (*.class) do set /a CLASS_COUNT+=1
echo [PsychologicalHorror] Class files counted: %CLASS_COUNT% >> "%LOG_FILE%"
if %CLASS_COUNT% LEQ 0 (
  echo ERROR: javac returned success but produced no .class files. >> "%LOG_FILE%"
  echo [PsychologicalHorror] RESULT: FAILED >> "%LOG_FILE%"
  echo Build FAILED (no class output). See log: %LOG_FILE%
  type "%LOG_FILE%"
  set "BUILD_FAILED=1"
  goto :END
)

echo [PsychologicalHorror] Packaging jar to %JAR_FILE% >> "%LOG_FILE%"
if exist "%JAR_FILE%" del "%JAR_FILE%"

where jar >nul 2>&1
if errorlevel 1 goto :ZIP_FALLBACK

jar cf "%JAR_FILE%" -C out . >> "%LOG_FILE%" 2>&1
if errorlevel 1 goto :ZIP_FALLBACK
goto :JAR_DONE

:ZIP_FALLBACK
echo [PsychologicalHorror] jar tool unavailable/failed, using Compress-Archive fallback >> "%LOG_FILE%"
powershell -NoProfile -Command "Compress-Archive -Path 'out\*' -DestinationPath '%JAR_FILE%' -Force" >> "%LOG_FILE%" 2>&1
if errorlevel 1 (
  echo ERROR: jar packaging failed (jar + fallback). >> "%LOG_FILE%"
  echo [PsychologicalHorror] RESULT: FAILED >> "%LOG_FILE%"
  echo Build FAILED (jar packaging). See log: %LOG_FILE%
  type "%LOG_FILE%"
  set "BUILD_FAILED=1"
  goto :END
)

:JAR_DONE
if not exist "%JAR_FILE%" (
  echo ERROR: packaging finished but output file is missing. >> "%LOG_FILE%"
  echo [PsychologicalHorror] RESULT: FAILED >> "%LOG_FILE%"
  echo Build FAILED (missing jar output). See log: %LOG_FILE%
  type "%LOG_FILE%"
  set "BUILD_FAILED=1"
  goto :END
)

echo [PsychologicalHorror] RESULT: SUCCESS >> "%LOG_FILE%"
echo Build OK. Compiled %SRC_COUNT% files, generated %CLASS_COUNT% class files.
echo Jar created: %JAR_FILE%
echo Exit code: 0
echo Log saved: %LOG_FILE%
echo --- Last 20 log lines ---
powershell -NoProfile -Command "Get-Content -Path '%LOG_FILE%' -Tail 20"

:END
if /I "%~1"=="--no-pause" goto :EXIT_NOW
pause
:EXIT_NOW
if "%BUILD_FAILED%"=="1" exit /b 1
exit /b 0
