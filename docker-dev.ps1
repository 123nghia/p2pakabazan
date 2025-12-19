# PowerShell script để khởi động môi trường development Docker
# Tự động xử lý: database init, migration, seed data
# Tương thích: Windows PowerShell 5.1+ và PowerShell Core 7+

$ErrorActionPreference = "Stop"

Write-Host "======================================" -ForegroundColor Cyan
Write-Host "P2P Trading - Development Environment" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

function Write-Info {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor Blue
}

function Write-Success {
    param([string]$Message)
    Write-Host "[SUCCESS] $Message" -ForegroundColor Green
}

function Write-Warning {
    param([string]$Message)
    Write-Host "[WARNING] $Message" -ForegroundColor Yellow
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor Red
}

# Kiểm tra Docker đang chạy
try {
    docker info | Out-Null
    Write-Success "Docker đang chạy"
} catch {
    Write-Error "Docker không chạy. Vui lòng khởi động Docker Desktop."
    Write-Host ""
    Read-Host "Nhấn Enter để thoát"
    exit 1
}

# Kiểm tra và tạo file .env nếu chưa có
if (-not (Test-Path ".env")) {
    Write-Warning "File .env chưa tồn tại. Đang tạo từ .env.example..."
    if (Test-Path ".env.example") {
        Copy-Item ".env.example" ".env"
        Write-Success "Đã tạo file .env. Bạn có thể chỉnh sửa nếu cần."
    } else {
        Write-Error "File .env.example không tồn tại!"
        Read-Host "Nhấn Enter để thoát"
        exit 1
    }
} else {
    Write-Info "Sử dụng cấu hình từ file .env"
}

# Load environment variables from .env file
Get-Content .env | ForEach-Object {
    if ($_ -match '^\s*([^#][^=]*)\s*=\s*(.*)$') {
        $name = $matches[1].Trim()
        $value = $matches[2].Trim()
        [Environment]::SetEnvironmentVariable($name, $value, "Process")
    }
}

$POSTGRES_DB = $env:POSTGRES_DB
$POSTGRES_USER = $env:POSTGRES_USER
$POSTGRES_PASSWORD = $env:POSTGRES_PASSWORD
$APP_PORT = if ($env:APP_PORT) { $env:APP_PORT } else { "9000" }
$PGADMIN_PORT = if ($env:PGADMIN_PORT) { $env:PGADMIN_PORT } else { "5050" }
$DB_PORT = if ($env:DB_PORT) { $env:DB_PORT } else { "5432" }
$RABBITMQ_MGMT_PORT = if ($env:RABBITMQ_MGMT_PORT) { $env:RABBITMQ_MGMT_PORT } else { "15672" }

Write-Info "Cấu hình:"
Write-Host "  - Database:   $POSTGRES_DB"
Write-Host "  - DB User:    $POSTGRES_USER"
Write-Host "  - App Port:   $APP_PORT"
Write-Host "  - pgAdmin:    $PGADMIN_PORT"
Write-Host ""

# Dừng và xóa containers cũ nếu có (tùy chọn)
if ($args[0] -eq "--clean") {
    Write-Info "Dọn dẹp containers và volumes cũ..."
    docker-compose down -v
    Write-Success "Đã dọn dẹp xong"
}

# Build và khởi động các services
Write-Info "Đang build và khởi động các services..."
docker-compose up -d --build

if ($LASTEXITCODE -ne 0) {
    Write-Error "Không thể khởi động services"
    Read-Host "Nhấn Enter để thoát"
    exit 1
}

# Đợi database sẵn sàng
Write-Info "Đợi PostgreSQL khởi động..."
Start-Sleep -Seconds 10

# Kiểm tra database connection
Write-Info "Kiểm tra kết nối database..."
$retries = 0
$maxRetries = 30
while ($retries -lt $maxRetries) {
    try {
        docker exec p2p-postgres pg_isready -U $POSTGRES_USER -d $POSTGRES_DB 2>&1 | Out-Null
        if ($LASTEXITCODE -eq 0) {
            break
        }
    } catch {
        # Continue waiting
    }
    Write-Host "." -NoNewline
    Start-Sleep -Seconds 2
    $retries++
}
Write-Host ""

if ($retries -eq $maxRetries) {
    Write-Warning "Database mất nhiều thời gian để khởi động. Vui lòng kiểm tra logs."
    Write-Info "Xem logs: docker logs p2p-postgres"
} else {
    Write-Success "Database đã sẵn sàng"
}

# Đợi application khởi động và chạy Flyway migration
Write-Info "Đợi application khởi động và chạy migrations..."
Start-Sleep -Seconds 15

# Kiểm tra logs của app để đảm bảo migration đã chạy
Write-Info "Kiểm tra trạng thái migration..."
try {
    docker logs p2p-app 2>&1 | Select-String -Pattern "flyway" -CaseSensitive:$false | Select-Object -Last 5
} catch {
    Write-Warning "Chưa thấy logs Flyway, có thể app đang khởi động..."
}

Write-Host ""
Write-Success "======================================"
Write-Success "Môi trường development đã sẵn sàng!"
Write-Success "======================================"
Write-Host ""
Write-Host "📋 Thông tin truy cập:" -ForegroundColor Cyan
Write-Host ""
Write-Host "  🚀 Application API:    http://localhost:$APP_PORT/api" -ForegroundColor White
Write-Host "  🗄️  PostgreSQL:        localhost:$DB_PORT" -ForegroundColor White
Write-Host "     - Database:        $POSTGRES_DB"
Write-Host "     - Username:        $POSTGRES_USER"
Write-Host "     - Password:        $POSTGRES_PASSWORD"
Write-Host ""
Write-Host "  🔧 pgAdmin (Web UI):   http://localhost:$PGADMIN_PORT" -ForegroundColor White
Write-Host "     - Email:           $($env:PGADMIN_EMAIL)"
Write-Host "     - Password:        $($env:PGADMIN_PASSWORD)"
Write-Host ""
Write-Host "  🐰 RabbitMQ:          http://localhost:$RABBITMQ_MGMT_PORT" -ForegroundColor White
Write-Host "     - Username:        $($env:RABBITMQ_USER)"
Write-Host "     - Password:        $($env:RABBITMQ_PASSWORD)"
Write-Host ""
Write-Host "📝 Lệnh hữu ích:" -ForegroundColor Cyan
Write-Host "  - Xem logs app:       docker logs -f p2p-app"
Write-Host "  - Xem logs database:  docker logs -f p2p-postgres"
Write-Host "  - Dừng tất cả:        docker-compose down"
Write-Host "  - Khởi động lại:      docker-compose restart"
Write-Host "  - Reset hoàn toàn:    .\docker-dev.ps1 --clean"
Write-Host ""
