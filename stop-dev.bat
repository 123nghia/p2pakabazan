@echo off
REM Stop all development services

echo.
echo Stopping all services...
echo.

docker-compose down

echo.
echo All services stopped.
echo.
pause
