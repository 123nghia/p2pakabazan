# Môi trường Development với Docker

Script này tự động thiết lập toàn bộ môi trường development với một lệnh duy nhất.

## 🚀 Khởi động nhanh

### Windows (PowerShell):
```powershell
.\docker-dev.ps1
```

### Linux/Mac:
```bash
chmod +x docker-dev.sh
./docker-dev.sh
```

## 📦 Các Services

Khi chạy script, hệ thống sẽ tự động khởi động:

1. **PostgreSQL Database** (Port 5432)
   - Tự động tạo database `p2p_trading_dev` nếu chưa tồn tại
   - Chạy tất cả Flyway migrations tự động
   - Khởi tạo schema và seed data

2. **pgAdmin** (Port 5050)
   - Web interface để quản lý PostgreSQL
   - Đã cấu hình sẵn kết nối đến database

3. **RabbitMQ** (Port 5672, Management: 15672)
   - Message broker cho hệ thống
   - Web management console

4. **Application** (Port 9000)
   - P2P Trading API
   - Tự động kết nối đến database và RabbitMQ

## 🔑 Thông tin đăng nhập

### Application API
- URL: http://localhost:9000/api
- Health check: http://localhost:9000/api/actuator/health

### PostgreSQL Database
- Host: localhost
- Port: 5432
- Database: p2p_trading_dev
- Username: postgres
- Password: postgres123

### pgAdmin (Web UI)
- URL: http://localhost:5050
- Email: admin@p2p.local
- Password: admin123

### RabbitMQ Management
- URL: http://localhost:15672
- Username: guest
- Password: guest

## 📝 Lệnh hữu ích

### Xem logs
```bash
# Logs của application
docker logs -f p2p-app

# Logs của database
docker logs -f p2p-postgres

# Logs của tất cả services
docker-compose logs -f
```

### Quản lý containers
```bash
# Dừng tất cả services
docker-compose down

# Khởi động lại services
docker-compose restart

# Khởi động lại một service cụ thể
docker-compose restart app
```

### Reset hoàn toàn
```powershell
# Windows
.\docker-dev.ps1 --clean
```

```bash
# Linux/Mac
./docker-dev.sh --clean
```

Lệnh này sẽ:
- Xóa tất cả containers
- Xóa tất cả volumes (bao gồm database data)
- Build lại từ đầu

## 🔄 Workflow Development

### Lần chạy đầu tiên:
1. Chạy script `docker-dev.ps1` hoặc `docker-dev.sh`
2. Đợi khoảng 2-3 phút để build và khởi động
3. Database sẽ tự động được tạo
4. Migrations sẽ tự động chạy
5. Application sẽ sẵn sàng tại http://localhost:9000/api

### Lần chạy tiếp theo:
- Chỉ cần chạy lại script
- Nếu có thay đổi code, containers sẽ rebuild
- Database và data vẫn được giữ nguyên
- Migrations mới sẽ tự động chạy

### Khi thêm migration mới:
1. Thêm file migration vào `p2p_repository/src/main/resources/db/migration/postgres/`
2. Khởi động lại app: `docker-compose restart app`
3. Flyway sẽ tự động phát hiện và chạy migration mới

## 🗄️ Quản lý Database

### Sử dụng pgAdmin:
1. Mở trình duyệt: http://localhost:5050
2. Đăng nhập với email/password ở trên
3. Server "P2P Trading DB (Dev)" đã được cấu hình sẵn
4. Click vào server để xem database

### Sử dụng psql từ command line:
```bash
# Kết nối vào database container
docker exec -it p2p-postgres psql -U postgres -d p2p_trading_dev

# Hoặc từ máy local (nếu có psql installed)
psql -h localhost -p 5432 -U postgres -d p2p_trading_dev
```

### Backup database:
```bash
docker exec p2p-postgres pg_dump -U postgres p2p_trading_dev > backup.sql
```

### Restore database:
```bash
docker exec -i p2p-postgres psql -U postgres -d p2p_trading_dev < backup.sql
```

## 🐛 Troubleshooting

### Port đã được sử dụng:
Nếu port 5432, 9000, hoặc 5050 đã được sử dụng, sửa file `docker-compose.yml`:
```yaml
ports:
  - "XXXX:5432"  # Thay XXXX bằng port khác
```

### Application không kết nối được database:
```bash
# Kiểm tra database có chạy không
docker ps | grep p2p-postgres

# Xem logs database
docker logs p2p-postgres

# Khởi động lại
docker-compose restart db app
```

### Migration bị lỗi:
```bash
# Xem logs chi tiết
docker logs p2p-app | grep -i flyway

# Nếu cần reset database hoàn toàn
.\docker-dev.ps1 --clean
```

### Build lỗi:
```bash
# Clean build
docker-compose down
docker system prune -a
.\docker-dev.ps1
```

## 📚 Cấu trúc thư mục Docker

```
docker/
├── init-db/
│   └── 01-init.sql          # Script khởi tạo database
├── pgadmin/
│   └── servers.json         # Cấu hình pgAdmin
└── wait-for-it.sh           # Script đợi database ready
```

## 🔧 Tùy chỉnh

### Thay đổi database credentials:
Sửa file `docker-compose.yml`:
```yaml
environment:
  POSTGRES_PASSWORD: your_password
  DB_PASSWORD: your_password
```

### Thay đổi Java version:
Sửa file `Dockerfile`:
```dockerfile
ARG JDK_IMAGE=eclipse-temurin:21-jdk  # Thay 17 thành 21
```

### Thêm environment variables:
Sửa file `docker-compose.yml` trong section `app.environment`:
```yaml
environment:
  YOUR_VAR: your_value
```
