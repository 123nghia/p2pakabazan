@echo off
REM Quick start script for Windows
REM Simply run: start-dev.bat

echo.
echo ========================================
echo  P2P Trading - Development Environment
echo ========================================
echo.

REM Check if Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker is not running! Please start Docker Desktop first.
    pause
    exit /b 1
)

echo [OK] Docker is running
echo.

REM Check and create .env file if not exists
if not exist .env (
    echo [WARNING] File .env chua ton tai. Dang tao tu .env.example...
    if exist .env.example (
        copy .env.example .env >nul
        echo [OK] Da tao file .env. Ban co the chinh sua neu can.
        echo.
    ) else (
        echo [ERROR] File .env.example khong ton tai!
        pause
        exit /b 1
    )
) else (
    echo [INFO] Su dung cau hinh tu file .env
    echo.
)

REM Start all services
echo [INFO] Starting all services...
docker-compose up -d --build

if errorlevel 1 (
    echo [ERROR] Failed to start services
    pause
    exit /b 1
)

echo.
echo [INFO] Waiting for services to be ready...
timeout /t 20 /nobreak >nul

echo.
echo ========================================
echo  Development Environment is Ready!
echo ========================================
echo.

REM Try to read ports from .env file (basic parsing)
set APP_PORT=9000
set PGADMIN_PORT=5050
set RABBITMQ_MGMT_PORT=15672
set DB_PORT=5432

for /f "tokens=1,2 delims==" %%a in (.env) do (
    if "%%a"=="APP_PORT" set APP_PORT=%%b
    if "%%a"=="PGADMIN_PORT" set PGADMIN_PORT=%%b
    if "%%a"=="RABBITMQ_MGMT_PORT" set RABBITMQ_MGMT_PORT=%%b
    if "%%a"=="DB_PORT" set DB_PORT=%%b
)

echo Access URLs:
echo   Application:  http://localhost:%APP_PORT%/api
echo   pgAdmin:      http://localhost:%PGADMIN_PORT%
echo   RabbitMQ:     http://localhost:%RABBITMQ_MGMT_PORT%
echo.
echo Database Info: (xem chi tiet trong file .env)
echo   Host:         localhost:%DB_PORT%
echo.
echo Useful commands:
echo   View logs:    docker logs -f p2p-app
echo   Stop all:     docker-compose down
echo   Restart:      docker-compose restart
echo.

pause
