@echo off
chcp 65001 > nul

set "JDK_BIN=%USERPROFILE%\.jdks\openjdk-26.0.2.1\bin"
if not exist "%JDK_BIN%\javac.exe" (
    if defined JAVA_HOME (
        set "JDK_BIN=%JAVA_HOME%\bin"
    ) else (
        set "JDK_BIN="
    )
)

if defined JDK_BIN (
    set "JAVAC=%JDK_BIN%\javac.exe"
    set "JAVA=%JDK_BIN%\java.exe"
) else (
    set "JAVAC=javac"
    set "JAVA=java"
)

"%JAVAC%" --enable-preview --release 26 -d out/production/netodology src/*.java
if %errorlevel% neq 0 (
    echo [ERROR] Ошибка компиляции!
    pause
    exit /b %errorlevel%
)

"%JAVA%" --enable-preview -cp out/production/netodology Main
