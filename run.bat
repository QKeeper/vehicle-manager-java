@echo off
chcp 65001 > nul
cd /d "%~dp0"

set "JAVAC=javac"
set "JAVA=java"
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\javac.exe" (
    set "JAVAC=%JAVA_HOME%\bin\javac.exe"
    set "JAVA=%JAVA_HOME%\bin\java.exe"
)

if not exist "out\production\netodology" mkdir "out\production\netodology"
"%JAVAC%" --release 17 -encoding UTF-8 -d "out\production\netodology" src\*.java
if errorlevel 1 (
    echo [ERROR] Ошибка компиляции!
    exit /b 1
)

"%JAVA%" -cp "out\production\netodology" Main
exit /b %errorlevel%
