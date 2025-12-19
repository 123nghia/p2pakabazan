# 🚀 Quick Start - Development Environment

## Khởi động nhanh (Windows)

### Cách đơn giản nhất:
```bash
start-dev.bat
```

Chỉ cần **double-click** file `start-dev.bat` hoặc chạy lệnh trên. Script sẽ tự động:
- ✅ Tạo database `p2p_trading_dev` (nếu chưa có)
- ✅ Chạy tất cả Flyway migrations tự động
- ✅ Seed master data (currencies, payment methods, etc.)
- ✅ Khởi động application trên port **9000**
- ✅ Khởi động pgAdmin trên port **5050**
- ✅ Khởi động RabbitMQ

### Các lệnh khác:
```bash
stop-dev.bat    # Dừng tất cả services
reset-dev.bat   # Xóa tất cả data và reset lại từ đầu
```

---

## 📋 Thông tin truy cập

### 🔵 Application API
- **URL**: http://localhost:9000/api
- **Health Check**: http://localhost:9000/api/actuator/health

### 🟢 pgAdmin (Quản lý Database qua Web)
- **URL**: http://localhost:5050
- **Email**: `admin@p2p.local`
- **Password**: `admin123`
- **Server**: P2P Trading DB (Dev) - Đã cấu hình sẵn!

### 🟣 PostgreSQL Database
- **Host**: `localhost:5432`
- **Database**: `p2p_trading_dev`
- **Username**: `postgres`
- **Password**: `postgres123`

### 🟠 RabbitMQ Management
- **URL**: http://localhost:15672
- **Username**: `guest`
- **Password**: `guest`

---

## 🔄 Workflow

### Lần đầu tiên:
1. Chạy `start-dev.bat`
2. Đợi ~2 phút
3. Mở http://localhost:9000/api
4. Mở http://localhost:5050 để xem database

### Lần sau:
- Chỉ cần chạy `start-dev.bat` lại
- Data được giữ nguyên
- Migration mới sẽ tự động chạy

### Khi cần reset hoàn toàn:
```bash
reset-dev.bat
start-dev.bat
```

---

## 🗄️ Xem và quản lý Database

### Cách 1: pgAdmin (Đơn giản nhất - Giao diện Web)
1. Mở http://localhost:5050
2. Đăng nhập: `admin@p2p.local` / `admin123`
3. Click vào "P2P Trading DB (Dev)" ở sidebar trái
4. Xem tables, data, chạy queries...

### Cách 2: psql (Command line)
```bash
docker exec -it p2p-postgres psql -U postgres -d p2p_trading_dev
```

### Cách 3: DBeaver / DataGrip
Kết nối với thông tin:
- Host: localhost
- Port: 5432
- Database: p2p_trading_dev
- User: postgres
- Password: postgres123

---

## 📝 Lệnh hữu ích

### Xem logs:
```bash
# Application logs
docker logs -f p2p-app

# Database logs
docker logs -f p2p-postgres

# Tất cả logs
docker-compose logs -f
```

### Khởi động lại một service:
```bash
docker-compose restart app      # Chỉ restart app
docker-compose restart db       # Chỉ restart database
```

### Kiểm tra trạng thái:
```bash
docker-compose ps
docker ps
```

---

## 🐛 Troubleshooting

### Port bị chiếm:
Nếu port 9000, 5432, hoặc 5050 đã được dùng:
1. Mở `docker-compose.yml`
2. Sửa phần `ports:`
```yaml
ports:
  - "XXXX:9000"  # Thay XXXX bằng port khác
```

### Application không chạy:
```bash
# Xem logs để biết lỗi
docker logs p2p-app

# Restart
docker-compose restart app
```

### Database không kết nối được:
```bash
# Kiểm tra database có chạy không
docker ps | findstr postgres

# Restart database
docker-compose restart db

# Nếu vẫn không được, reset:
reset-dev.bat
start-dev.bat
```

### Migration bị lỗi:
```bash
# Xem chi tiết lỗi
docker logs p2p-app | findstr -i flyway

# Reset database và chạy lại
reset-dev.bat
start-dev.bat
```

---

## 📚 Chi tiết kỹ thuật

### Migration tự động:
- Flyway tự động phát hiện và chạy migrations trong `p2p_repository/src/main/resources/db/migration/postgres/`
- Migration chỉ chạy một lần, Flyway tracking trong table `flyway_schema_history`
- Thêm migration mới: Đặt file `VXXX__description.sql` vào thư mục trên

### Seed Data:
- Master data (currencies, payment methods) được seed tự động qua migrations
- File seed: `V8__create_currency_master_data.sql`, `V9__create_payment_methods.sql`

### Docker Volumes:
- `postgres_data`: Lưu database data (persistent)
- `pgadmin_data`: Lưu pgAdmin config (persistent)
- `rabbitmq_data`: Lưu RabbitMQ data (persistent)

### Healthchecks:
- PostgreSQL: Kiểm tra `pg_isready` mỗi 10s
- RabbitMQ: Kiểm tra `rabbitmq-diagnostics ping` mỗi 10s
- App chỉ start khi database và RabbitMQ đã ready

---

## 🎯 Tính năng chính

✅ **Một lệnh khởi động mọi thứ**
✅ **Database tự động init, migrate, seed**
✅ **Web UI để quản lý database (pgAdmin)**
✅ **Data persistent (không mất khi restart)**
✅ **Chạy lần đầu hay lần N đều giống nhau**
✅ **Port 9000 cho dev, dễ nhớ**
✅ **Logs đầy đủ để debug**

---

## 📖 Đọc thêm

- Chi tiết kỹ thuật: [DOCKER-DEV-README.md](DOCKER-DEV-README.md)
- API Documentation: [docs/api-spec.md](docs/api-spec.md)
- System Overview: [docs/system-overview.md](docs/system-overview.md)

---

## ✨ Happy Coding!

Nếu có vấn đề gì, xem logs hoặc reset lại:
```bash
docker logs -f p2p-app
reset-dev.bat && start-dev.bat
```
