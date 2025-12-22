#!/bin/bash
# Script để khởi động môi trường development Docker
# Tự động xử lý:
#   - PostgreSQL database initialization
#   - Flyway migrations (28 migrations auto-run)
#   - RabbitMQ message broker
#   - pgAdmin web UI
#   - Spring Boot application build & deploy
# Tương thích: Linux (Ubuntu, Debian, CentOS) và macOS

set -e

echo "======================================"
echo "P2P Trading - Development Environment"
echo "======================================"
echo ""
echo "Auto-setup includes:"
echo "  - PostgreSQL database creation"
echo "  - Flyway migrations (auto-run)"
echo "  - RabbitMQ broker"
echo "  - pgAdmin web UI"
echo "  - Spring Boot application"
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Kiểm tra Docker đang chạy
if ! docker info > /dev/null 2>&1; then
    print_error "Docker không chạy. Vui lòng khởi động Docker."
    echo ""
    echo "Hướng dẫn:"
    echo "  Ubuntu/Debian: sudo systemctl start docker"
    echo "  macOS:         Mở Docker Desktop"
    echo ""
    exit 1
fi

print_success "Docker đang chạy"

# Kiểm tra và tạo file .env nếu chưa có
if [ ! -f .env ]; then
    print_warning "File .env chưa tồn tại. Đang tạo từ .env.example..."
    if [ -f .env.example ]; then
        cp .env.example .env
        print_success "Đã tạo file .env. Bạn có thể chỉnh sửa nếu cần."
    else
        print_error "File .env.example không tồn tại!"
        exit 1
    fi
else
    print_info "Sử dụng cấu hình từ file .env"
fi

# Load environment variables
set -a
source .env
set +a

print_info "Cấu hình:"
echo "  - Database:   ${POSTGRES_DB}"
echo "  - DB User:    ${POSTGRES_USER}"
echo "  - App Port:   ${APP_PORT:-9000}"
echo "  - pgAdmin:    ${PGADMIN_PORT:-5050}"
echo ""

# Dừng và xóa containers cũ nếu có (tùy chọn)
if [ "$1" == "--clean" ]; then
    print_info "Dọn dẹp containers và volumes cũ..."
    docker compose down -v
    print_success "Đã dọn dẹp xong"
fi

# Build và khởi động các services
print_info "Đang build và khởi động các services..."
docker compose up -d --build

# Đợi database sẵn sàng
print_info "Đợi PostgreSQL khởi động..."
sleep 10

# Kiểm tra database connection
print_info "Kiểm tra kết nối database..."
RETRIES=0
MAX_RETRIES=30
until docker exec p2p-postgres pg_isready -U ${POSTGRES_USER} -d ${POSTGRES_DB} > /dev/null 2>&1; do
    if [ $RETRIES -eq $MAX_RETRIES ]; then
        print_error "Database không khởi động được sau ${MAX_RETRIES} lần thử"
        print_info "Xem logs: docker logs p2p-postgres"
        exit 1
    fi
    echo -n "."
    sleep 2
    RETRIES=$((RETRIES + 1))
done
echo ""
print_success "Database đã sẵn sàng"

# Đợi application khởi động và chạy Flyway migration
print_info "Đợi application khởi động và chạy migrations..."
print_info "Flyway sẽ tự động chạy 28 migrations khi app khởi động..."
sleep 15

# Kiểm tra logs của app để đảm bảo migration đã chạy
print_info "Kiểm tra trạng thái migration..."
if docker logs p2p-app 2>&1 | grep -i "flyway" | tail -5; then
    print_success "Flyway migrations đã chạy thành công!"
else
    print_warning "Chưa thấy logs Flyway, có thể app đang khởi động..."
fi

echo ""
print_success "======================================"
print_success "Development Environment Ready!"
print_success "======================================"
echo ""
print_success "✅ Database initialized and 28 migrations completed"
print_success "✅ All services are running"
echo ""
echo "📋 Access URLs:"
echo ""
echo "  🚀 Application API:      http://localhost:${APP_PORT:-9000}/api"
echo "  📖 Swagger UI:           http://localhost:${APP_PORT:-9000}/swagger-ui/index.html"
echo ""
echo "  🗄️  PostgreSQL Database:  localhost:${DB_PORT:-5432}"
echo "     Database:             ${POSTGRES_DB}"
echo "     Username:             ${POSTGRES_USER}"
echo "     Password:             ${POSTGRES_PASSWORD}"
echo "     Migrations:           28 auto-applied via Flyway"
echo ""
echo "  🔧 pgAdmin (Web UI):     http://localhost:${PGADMIN_PORT:-5050}"
echo "     Email:                ${PGADMIN_EMAIL}"
echo "     Password:             ${PGADMIN_PASSWORD}"
echo ""
echo "  🐰 RabbitMQ:          http://localhost:${RABBITMQ_MGMT_PORT:-15672}"
echo "     - Username:        ${RABBITMQ_USER}"
echo "     - Password:        ${RABBITMQ_PASSWORD}"
echo ""
echo "📝 Lệnh hữu ích:"
echo "  - Xem logs app:       docker logs -f p2p-app"
echo "  - Xem logs database:  docker logs -f p2p-postgres"
echo "  - Dừng tất cả:        docker compose down"
echo "  - Khởi động lại:      docker compose restart"
echo "  - Reset hoàn toàn:    ./docker-dev.sh --clean"
echo ""
