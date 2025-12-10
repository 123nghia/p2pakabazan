# ✅ CẬP NHẬT HOÀN TẤT - Hỗ trợ đa nền tảng & Cấu hình động

## 🎉 Đã cập nhật:

### 1. ⚙️ Cấu hình động với file .env

**Đã tạo:**
- ✅ `.env` - File cấu hình thực tế (đã có sẵn, sử dụng ngay!)
- ✅ `.env.example` - Template mẫu (đã cập nhật đầy đủ)

**Cách hoạt động:**
- Tất cả thông số (DB, ports, credentials) đều trong file `.env`
- Script tự động đọc và áp dụng cấu hình
- Dễ dàng thay đổi port nếu bị conflict
- Phù hợp với `application-dev.properties`

**File .env chứa:**
```bash
# Database
POSTGRES_DB=p2p_trading_dev
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres123
DB_URL=jdbc:postgresql://db:5432/p2p_trading_dev

# Application
SERVER_PORT=9000
APP_PORT=9000

# pgAdmin
PGADMIN_PORT=5050
PGADMIN_EMAIL=admin@p2p.local

# RabbitMQ
RABBITMQ_PORT=5672
RABBITMQ_MGMT_PORT=15672
```

---

### 2. 🌍 Hỗ trợ đa nền tảng

**Scripts đã cập nhật:**

#### ✅ `docker-dev.sh` (Linux/Mac)
- Tự động tạo `.env` nếu chưa có
- Load và hiển thị cấu hình từ `.env`
- Màu sắc output cho dễ đọc
- Error handling tốt hơn
- Timeout cho database connection
- Hướng dẫn khởi động Docker cho từng OS

#### ✅ `docker-dev.ps1` (Windows PowerShell)
- Tự động tạo `.env` nếu chưa có
- Parse và load variables từ `.env`
- Hiển thị cấu hình động
- Error handling và validation
- Tương thích PowerShell 5.1+ và Core 7+

#### ✅ `start-dev.bat` (Windows CMD)
- Kiểm tra và tạo `.env` tự động
- Parse ports từ `.env`
- Hiển thị URLs với ports đúng
- Đơn giản và dễ dùng (double-click!)

---

### 3. 🐳 Docker Compose với biến môi trường

**Đã cập nhật `docker-compose.yml`:**
- Sử dụng `${VAR:-default}` cho tất cả giá trị
- Ports, credentials, database name đều động
- Dễ dàng scale và customize
- Environment variables đầy đủ cho application

**Ví dụ:**
```yaml
environment:
  POSTGRES_DB: ${POSTGRES_DB:-p2p_trading_dev}
  SERVER_PORT: ${SERVER_PORT:-9000}
ports:
  - "${APP_PORT:-9000}:${SERVER_PORT:-9000}"
```

---

### 4. 📚 Tài liệu mới

#### ✅ `CROSS-PLATFORM-GUIDE.md`
Hướng dẫn chi tiết:
- Cách chạy trên từng nền tảng (Windows/Ubuntu/macOS)
- So sánh lệnh giữa các OS
- Troubleshooting theo từng platform
- Hướng dẫn cài Docker cho từng OS
- Line endings và Git configuration
- Checklist môi trường sẵn sàng

---

### 5. 🔒 Bảo mật

**Đã cập nhật `.gitignore`:**
```gitignore
### Environment variables ###
.env
.env.local
.env.*.local
```

⚠️ **File `.env` sẽ KHÔNG được commit vào Git!**

---

## 🚀 Cách sử dụng

### Windows:

**Lần đầu tiên (file .env đã có sẵn):**
```cmd
start-dev.bat
```

**Hoặc dùng PowerShell:**
```powershell
.\docker-dev.ps1
```

**Nếu cần thay đổi cấu hình:**
1. Mở file `.env` bằng Notepad/VSCode
2. Sửa giá trị (ví dụ: `APP_PORT=8080`)
3. Lưu file
4. Chạy lại: `start-dev.bat`

### Ubuntu/Debian/macOS:

**Lần đầu tiên:**
```bash
chmod +x docker-dev.sh
./docker-dev.sh
```

**Script sẽ tự động:**
- ✅ Tạo `.env` nếu chưa có
- ✅ Load cấu hình
- ✅ Hiển thị thông số
- ✅ Khởi động tất cả services

**Thay đổi cấu hình:**
```bash
# Sửa file .env
nano .env
# hoặc
vim .env

# Khởi động lại
./docker-dev.sh
```

---

## 📋 Thông tin truy cập (theo .env)

Tất cả thông tin này **ĐỌC TỪ FILE .env**:

### Application:
```
http://localhost:${APP_PORT}/api
# Mặc định: http://localhost:9000/api
```

### pgAdmin:
```
http://localhost:${PGADMIN_PORT}
# Mặc định: http://localhost:5050
Email: admin@p2p.local
Password: admin123
```

### Database:
```
Host: localhost:${DB_PORT}
Database: p2p_trading_dev
User: postgres
Password: postgres123
# Mặc định port: 5432
```

### RabbitMQ:
```
Management UI: http://localhost:${RABBITMQ_MGMT_PORT}
# Mặc định: http://localhost:15672
User: guest
Password: guest
```

---

## 🎯 Các tính năng mới

✅ **Cấu hình tập trung**: Tất cả trong file `.env`
✅ **Tự động tạo .env**: Không cần làm thủ công
✅ **Hiển thị cấu hình**: Biết chính xác port/credentials đang dùng
✅ **Đa nền tảng**: Windows, Ubuntu, Debian, macOS
✅ **Error handling**: Thông báo lỗi rõ ràng
✅ **Auto-retry**: Tự động đợi services sẵn sàng
✅ **Màu sắc**: Output dễ đọc với colors
✅ **Bảo mật**: .env không bao giờ commit vào Git

---

## 🔄 So sánh với bản cũ

| Tính năng | Trước | Bây giờ |
|-----------|-------|---------|
| **Cấu hình** | Hard-coded | File .env |
| **Port conflict** | Phải sửa nhiều file | Chỉ sửa .env |
| **Cross-platform** | Chỉ Windows | Win/Linux/Mac |
| **Auto-setup** | Không | Tự tạo .env |
| **Show config** | Không | Hiển thị đầy đủ |
| **Error handling** | Cơ bản | Chi tiết |

---

## 🛠️ Ví dụ thực tế

### Đổi port vì conflict:

**Trước (phức tạp):**
1. Sửa `docker-compose.yml`
2. Sửa `start-dev.bat`
3. Sửa `docker-dev.sh`
4. Rebuild containers

**Bây giờ (đơn giản):**
1. Mở `.env`
2. Sửa: `APP_PORT=8080`
3. Chạy: `start-dev.bat` hoặc `./docker-dev.sh`
4. Xong!

### Thay đổi database credentials:

**File .env:**
```bash
POSTGRES_USER=myuser
POSTGRES_PASSWORD=mypassword123
DB_USERNAME=myuser
DB_PASSWORD=mypassword123
```

Chạy lại script → Tất cả containers dùng credentials mới!

---

## 📖 Đọc thêm

1. **Quick Start**: [DEV-QUICKSTART.md](DEV-QUICKSTART.md)
2. **Chi tiết Docker**: [DOCKER-DEV-README.md](DOCKER-DEV-README.md)
3. **Đa nền tảng**: [CROSS-PLATFORM-GUIDE.md](CROSS-PLATFORM-GUIDE.md)
4. **Setup overview**: [SETUP-COMPLETE.md](SETUP-COMPLETE.md)

---

## ✨ Tóm tắt

Giờ đây bạn có:
- ✅ Scripts hoạt động trên **Windows, Ubuntu, macOS**
- ✅ Cấu hình **tập trung** trong file `.env`
- ✅ Tự động **tạo .env** nếu chưa có
- ✅ Hiển thị **cấu hình đang dùng** khi chạy
- ✅ Dễ dàng **thay đổi ports** khi conflict
- ✅ **Error handling** và retry logic tốt hơn
- ✅ Tài liệu **đầy đủ** cho từng nền tảng

**Chỉ cần chạy một lệnh và mọi thứ sẵn sàng!** 🚀

---

### Windows:
```cmd
start-dev.bat
```

### Linux/Mac:
```bash
./docker-dev.sh
```

**Script sẽ tự động:**
1. Kiểm tra Docker
2. Tạo/load file .env
3. Hiển thị cấu hình
4. Build và start containers
5. Đợi services ready
6. Hiển thị URLs để truy cập

Happy Coding! 🎉
