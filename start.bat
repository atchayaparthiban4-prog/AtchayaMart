@echo off
title AtchayaMart Launcher
color 0A

echo ============================================================
echo   AtchayaMart - Online Shopping Application
echo   Starting frontend + backend server...
echo ============================================================
echo.

rem Start the Spring Boot app (serves both frontend and backend) in its own window
start "AtchayaMart Server" cmd /k "cd /d %~dp0 && call mvnw.cmd spring-boot:run"

echo Waiting for the server to start on http://localhost:8080 ...
echo.

rem Wait until port 8080 responds (up to ~120 seconds)
set /a tries=0
:check
set /a tries+=1
powershell -NoProfile -Command "$c = New-Object System.Net.Sockets.TcpClient; try { $c.Connect('localhost', 8080); 'UP' } catch { 'DOWN' }" > "%TEMP%\atchaya_port.txt"
set /p portStatus=<"%TEMP%\atchaya_port.txt"
if "%portStatus%"=="UP" goto up
if %tries% geq 60 (
    echo Server did not start in time. Check the server window for errors.
    goto done
)
timeout /t 2 /nobreak >nul
goto check

:up
echo.
echo ============================================================
echo   AtchayaMart is running!
echo.
echo   View the web app here:  http://localhost:8080
echo   H2 console:             http://localhost:8080/h2-console
echo ============================================================
echo.
start "" "http://localhost:8080"

:done
echo.
echo Close this window to stop monitoring (server keeps running in its own window).
pause
