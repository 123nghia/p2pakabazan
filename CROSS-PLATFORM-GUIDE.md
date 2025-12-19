# 🌍 Hướng dẫn sử dụng đa nền tảng

Script Docker development của project hỗ trợ đầy đủ trên:
- ✅ Windows 10/11 (PowerShell, CMD)
- ✅ Ubuntu Desktop/Server (20.04+, 22.04+)
- ✅ Debian (10+, 11+)
- ✅ macOS (Intel & Apple Silicon)

---

## 🚀 Khởi động nhanh

### Windows:

**Cách 1: Dùng Batch Script (đơn giản nhất)**
```cmd
start-dev.bat
```
Hoặc double-click file `start-dev.bat`

**Cách 2: Dùng PowerShell Script**
```powershell
.\docker-dev.ps1
```

**Cách 3: Dùng Docker Compose trực tiếp**
```cmd
docker-compose up -d --build
```

### Ubuntu/Debian:

**Bước 1: Cấp quyền thực thi (chỉ làm 1 lần)**
```bash
chmod +x docker-dev.sh
```

**Bước 2: Chạy script**
```bash
./docker-dev.sh
```

**Hoặc dùng Docker Compose trực tiếp:**
```bash
docker-compose up -d --build
```

### macOS:

Giống với Ubuntu/Debian:
```bash
chmod +x docker-dev.sh
./docker-dev.sh
```

---

## ⚙️ Cấu hình môi trường (.env)

### Lần đầu tiên:

**Windows:**
```cmd
copy .env.example .env
```

**Linux/Mac:**
```bash
cp .env.example .env
```

Script sẽ **tự động tạo** file `.env` nếu chưa có.

### Chỉnh sửa cấu hình:

Mở file `.env` và sửa các giá trị:

```bash
# Database
POSTGRES_DB=p2p_trading_dev
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres123

# Application
SERVER_PORT=9000
SPRING_PROFILES_ACTIVE=dev

# Ports
APP_PORT=9000
PGADMIN_PORT=5050
DB_PORT=5432
```

**Lưu ý**: 
- Script sẽ đọc cấu hình từ file `.env`
- Không cần restart Docker, chỉ cần restart containers:
  ```bash
  docker-compose down
  docker-compose up -d
  ```

---

## 📋 So sánh lệnh giữa các nền tảng

| Tác vụ | Windows (CMD) | Windows (PowerShell) | Linux/Mac |
|--------|---------------|---------------------|-----------|
| **Khởi động** | `start-dev.bat` | `.\docker-dev.ps1` | `./docker-dev.sh` |
| **Dừng** | `stop-dev.bat` | `docker-compose down` | `docker-compose down` |
| **Reset** | `reset-dev.bat` | `.\docker-dev.ps1 --clean` | `./docker-dev.sh --clean` |
| **Xem logs** | `docker logs -f p2p-app` | `docker logs -f p2p-app` | `docker logs -f p2p-app` |
| **Copy file** | `copy .env.example .env` | `Copy-Item .env.example .env` | `cp .env.example .env` |

---

## 🐛 Troubleshooting theo nền tảng

### Windows:

#### Docker không chạy:
```cmd
# Kiểm tra Docker Desktop đang chạy
docker info

# Nếu lỗi, khởi động Docker Desktop từ Start Menu
```

#### Port bị chiếm:
```cmd
# Kiểm tra port nào đang dùng
netstat -ano | findstr :9000

# Đổi port trong file .env
notepad .env
```

#### PowerShell Execution Policy:
Nếu không chạy được `.ps1`:
```powershell
# Chạy PowerShell as Administrator
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### Ubuntu/Debian:

#### Docker không chạy:
```bash
# Kiểm tra Docker service
sudo systemctl status docker

# Khởi động Docker
sudo systemctl start docker

# Cho phép user hiện tại dùng Docker (không cần sudo)
sudo usermod -aG docker $USER
# Sau đó logout và login lại
```

#### Permission denied khi chạy script:
```bash
# Cấp quyền thực thi
chmod +x docker-dev.sh

# Hoặc chạy với bash
bash docker-dev.sh
```

#### Port bị chiếm:
```bash
# Kiểm tra port đang dùng
sudo netstat -tulpn | grep :9000
# Hoặc
sudo lsof -i :9000

# Đổi port trong .env
nano .env
# hoặc
vim .env
```

### macOS:

#### Docker không chạy:
```bash
# Mở Docker Desktop từ Applications
open -a Docker

# Đợi Docker khởi động (biểu tượng Docker trên menu bar)
```

#### Permission issues:
```bash
# Cấp quyền thực thi
chmod +x docker-dev.sh

# Nếu gặp "command not found"
bash docker-dev.sh
```

---

## 🔧 Cài đặt Docker theo nền tảng

### Windows:

1. Tải Docker Desktop: https://www.docker.com/products/docker-desktop/
2. Cài đặt và khởi động lại máy
3. Mở Docker Desktop
4. Vào Settings → Resources → WSL Integration (nếu dùng WSL2)

**Yêu cầu**:
- Windows 10/11 Pro, Enterprise, hoặc Education
- Hoặc Windows 10/11 Home với WSL2

### Ubuntu/Debian:

```bash
# Update packages
sudo apt-get update

# Install Docker
sudo apt-get install -y docker.io docker-compose

# Start Docker service
sudo systemctl start docker
sudo systemctl enable docker

# Add user to docker group (không cần sudo)
sudo usermod -aG docker $USER

# Logout và login lại để áp dụng
```

**Hoặc cài Docker Engine mới nhất:**
```bash
# Xóa phiên bản cũ
sudo apt-get remove docker docker-engine docker.io containerd runc

# Cài dependencies
sudo apt-get install -y ca-certificates curl gnupg lsb-release

# Add Docker GPG key
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

# Add repository
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Install Docker
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

# Start Docker
sudo systemctl start docker
sudo systemctl enable docker
```

### macOS:

1. Tải Docker Desktop for Mac: https://www.docker.com/products/docker-desktop/
2. Cài đặt file .dmg
3. Mở Docker Desktop từ Applications
4. Đợi Docker khởi động

**Lưu ý cho Apple Silicon (M1/M2/M3)**:
- Tải bản "Apple Chip" (ARM64)
- Docker sẽ tự động handle emulation nếu cần

---

## 🎯 Kiểm tra môi trường

Chạy các lệnh sau để kiểm tra Docker đã cài đúng:

```bash
# Kiểm tra Docker version
docker --version
docker-compose --version

# Kiểm tra Docker đang chạy
docker info

# Test chạy container
docker run hello-world
```

Kết quả mong đợi:
```
Docker version 24.x.x
Docker Compose version v2.x.x
```

---

## 📝 Lưu ý về Line Endings

### Git Configuration:

**Windows:**
```cmd
git config --global core.autocrlf true
```

**Linux/Mac:**
```bash
git config --global core.autocrlf input
```

### Nếu script .sh báo lỗi trên Linux/Mac:

```bash
# Convert line endings from CRLF to LF
dos2unix docker-dev.sh
# Hoặc
sed -i 's/\r$//' docker-dev.sh
```

---

## 🌐 Networking

Tất cả các containers đều trong cùng network `p2p-network`:
- Containers giao tiếp với nhau qua tên service (db, app, rabbitmq, pgadmin)
- Truy cập từ host machine qua `localhost:PORT`

**Ví dụ:**
- Application connect database: `jdbc:postgresql://db:5432/p2p_trading_dev`
- Bạn connect database từ máy: `jdbc:postgresql://localhost:5432/p2p_trading_dev`

---

## 🔐 Bảo mật

### Development:
File `.env` chứa credentials và **KHÔNG ĐƯỢC commit** vào git.

### Production:
- Đổi tất cả passwords
- Dùng secrets management (Docker Secrets, Kubernetes Secrets, AWS Secrets Manager, etc.)
- Không dùng default passwords

---

## ✅ Checklist môi trường sẵn sàng

- [ ] Docker đã cài đặt và chạy
- [ ] Docker Compose có sẵn
- [ ] File `.env` đã được tạo (từ `.env.example`)
- [ ] Ports 5432, 5050, 5672, 9000, 15672 không bị chiếm
- [ ] Script có quyền thực thi (Linux/Mac: `chmod +x`)
- [ ] Git line endings được cấu hình đúng

---

## 🎊 Tóm tắt

| Nền tảng | Script khuyên dùng | Lệnh |
|----------|-------------------|------|
| **Windows** | `start-dev.bat` | Double-click hoặc `start-dev.bat` |
| **Ubuntu/Debian** | `docker-dev.sh` | `./docker-dev.sh` |
| **macOS** | `docker-dev.sh` | `./docker-dev.sh` |
| **Tất cả** | Docker Compose | `docker-compose up -d --build` |

**Tất cả đều đọc cấu hình từ file `.env`!**

---

Có vấn đề? Xem thêm:
- [DEV-QUICKSTART.md](DEV-QUICKSTART.md) - Hướng dẫn nhanh
- [DOCKER-DEV-README.md](DOCKER-DEV-README.md) - Chi tiết đầy đủ
- [SETUP-COMPLETE.md](SETUP-COMPLETE.md) - Tổng quan setup
