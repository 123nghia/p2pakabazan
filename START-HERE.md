# 🚀 Quick Start - Chọn hệ điều hành của bạn

## Windows 🪟

### Cách đơn giản nhất (Double-click):
```
start-dev.bat
```

### Hoặc dùng PowerShell:
```powershell
.\docker-dev.ps1
```

---

## Ubuntu / Debian 🐧

### Lần đầu (cấp quyền):
```bash
chmod +x docker-dev.sh
```

### Chạy:
```bash
./docker-dev.sh
```

---

## macOS 🍎

Giống Ubuntu:
```bash
chmod +x docker-dev.sh
./docker-dev.sh
```

---

## 📝 Script tự động làm gì?

✅ Tạo file `.env` nếu chưa có
✅ Tạo database `p2p_trading_dev`
✅ Chạy tất cả migrations
✅ Seed master data
✅ Khởi động 4 services:
  - PostgreSQL Database (port 5432)
  - pgAdmin Web UI (port 5050)
  - RabbitMQ (port 5672, 15672)
  - Application API (port 9000)

---

## 🌐 Truy cập

| Service | URL | Credentials |
|---------|-----|-------------|
| **API** | http://localhost:9000/api | - |
| **pgAdmin** | http://localhost:5050 | admin@p2p.local / admin123 |
| **RabbitMQ** | http://localhost:15672 | guest / guest |
| **Database** | localhost:5432 | postgres / postgres123 |

---

## ⚙️ Thay đổi cấu hình

### Windows:
```cmd
notepad .env
start-dev.bat
```

### Linux/Mac:
```bash
nano .env
./docker-dev.sh
```

Thay đổi port, password, database name... tất cả trong file `.env`!

---

## 🛑 Dừng & Reset

### Dừng:
```bash
docker-compose down
```

### Reset hoàn toàn (xóa data):
**Windows:**
```cmd
reset-dev.bat
```

**Linux/Mac:**
```bash
./docker-dev.sh --clean
```

---

## 📚 Tài liệu đầy đủ

- [DEV-QUICKSTART.md](DEV-QUICKSTART.md) - Hướng dẫn nhanh, workflow
- [CROSS-PLATFORM-GUIDE.md](CROSS-PLATFORM-GUIDE.md) - Chi tiết cho từng OS
- [DOCKER-DEV-README.md](DOCKER-DEV-README.md) - Tài liệu Docker đầy đủ
- [UPDATE-SUMMARY.md](UPDATE-SUMMARY.md) - Các tính năng mới

---

## 🐛 Gặp vấn đề?

```bash
# Xem logs
docker logs -f p2p-app

# Reset và chạy lại
docker-compose down -v
start-dev.bat  # hoặc ./docker-dev.sh
```

Xem thêm troubleshooting trong [CROSS-PLATFORM-GUIDE.md](CROSS-PLATFORM-GUIDE.md)

---

**Chỉ một lệnh, mọi thứ sẵn sàng!** 🎉
