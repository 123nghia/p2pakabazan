@echo off
REM Quick start script for Windows
REM Simply run: start-dev.bat
REM 
REM This script will:
REM - Start PostgreSQL database with auto-initialization
REM - Run Flyway migrations automatically (28 migrations)
REM - Start RabbitMQ message broker
REM - Start pgAdmin web UI for database management
REM - Build and start Spring Boot application

echo.
echo ========================================
echo  P2P Trading - Development Environment
echo ========================================
echo.
echo [INFO] Auto-setup includes:
echo   - PostgreSQL database creation
echo   - Flyway migrations (auto-run)
echo   - RabbitMQ broker
echo   - pgAdmin web UI
echo   - Spring Boot application
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
echo [INFO] Database migrations will run automatically...
timeout /t 20 /nobreak >nul

echo.
echo ========================================
echo  Development Environment is Ready!
echo ========================================
echo.
echo [SUCCESS] All services started successfully!
echo [SUCCESS] Database initialized and migrations completed (28 migrations)
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
echo   Application API:    http://localhost:%APP_PORT%/api
echo   Swagger UI:         http://localhost:%APP_PORT%/api/swagger-ui/index.html
echo   pgAdmin:            http://localhost:%PGADMIN_PORT% (admin@example.com / admin123)
echo   RabbitMQ Mgmt:      http://localhost:%RABBITMQ_MGMT_PORT% (guest / guest)
echo.
echo Database Info: (PostgreSQL with auto-migrations)
echo   Host:               localhost:%DB_PORT%
echo   Database:           p2p_trading_dev
echo   Migrations:         28 migrations auto-applied via Flyway
echo.
echo Network Access:
echo   From other devices: Replace 'localhost' with your IP address
echo   Example:            http://192.168.1.17:%APP_PORT%/api
echo.
echo Useful commands:
echo   View logs:    docker logs -f p2p-app
echo   Stop all:     docker-compose down
echo   Restart:      docker-compose restart
echo.

pause
