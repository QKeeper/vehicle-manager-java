@echo off
cd /d "%~dp0"

where javac >nul 2>nul
if errorlevel 1 goto nojdk
where jar >nul 2>nul
if errorlevel 1 goto nojdk

echo Compiling...
if exist out rmdir /s /q out
mkdir out
javac --release 17 -encoding UTF-8 -d out src\*.java
if errorlevel 1 goto failed

echo Packing vehicles.jar...
jar cfe vehicles.jar Main -C out .
if errorlevel 1 goto failed

echo Starting the application...
java -jar vehicles.jar
goto end

:nojdk
echo Error: javac/jar not found. Install JDK 17+ and make sure it is in PATH.
pause
goto end

:failed
echo Build failed.
pause

:end
