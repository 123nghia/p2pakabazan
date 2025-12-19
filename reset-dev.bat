@echo off
REM Reset development environment completely
REM This will remove all data!

echo.
echo ========================================
echo  WARNING: This will delete all data!
echo ========================================
echo.
set /p confirm="Are you sure? Type 'yes' to continue: "

if not "%confirm%"=="yes" (
    echo Cancelled.
    pause
    exit /b 0
)

echo.
echo Stopping and removing all containers and volumes...
docker-compose down -v

echo.
echo Removing orphaned containers...
docker-compose down --remove-orphans

echo.
echo Done! Run start-dev.bat to start fresh.
echo.
pause
