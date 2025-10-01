# Đặc Tả API P2P Trading System

Tài liệu mô tả các endpoint REST hiện có trong hệ thống. Mọi URL đều được tiền tố bằng `spring.mvc.servlet.path=/api` ⇒ base path mặc định là `https://{host}:{port}/api`.

## Môi trường & Ngôn ngữ

- **Ngôn ngữ triển khai:** Java 17 (Spring Boot 3.1.x)
- **Build tool:** Maven 3.8+ (đa mô-đun)
- **Cơ sở dữ liệu mặc định:** PostgreSQL 14+ (`p2p_trading`)
- **Module khởi động:** `p2p_p2p`
- **Tài liệu:** tiếng Việt; mã nguồn: tiếng Anh + Việt (comment/DTO)

## Cách chạy nhanh

1. Đảm bảo PostgreSQL đang chạy với cấu hình trong `p2p_p2p/src/main/resources/application.properties`.
2. Tạo schema nếu chưa có: `createdb p2p_trading` (hoặc tương đương).
3. Cài đặt dependencies: `mvn clean install`.
4. Khởi chạy ứng dụng: `mvn -pl p2p_p2p -am spring-boot:run`.
5. Truy cập Swagger UI: `http://localhost:8080/api/swagger-ui/index.html`.


## Tổng quan

- **Phiên bản API:** v1 (ngầm định)
- **Định dạng:** JSON UTF-8
- **Auth:** JWT Bearer. Thêm header `Authorization: Bearer <token>` cho mọi endpoint (trừ `/auth/**` và `/market/**`).
- **Mã lỗi chung:**
  - `400` dữ liệu đầu vào không hợp lệ (validation / thiếu tham số)
  - `401` không có hoặc token không hợp lệ
  - `403` không đủ quyền truy cập
  - `404` tài nguyên không tồn tại
  - `500` lỗi server nội bộ hoặc lỗi tới Binance

## 1. Authentication

### Đăng nhập

| Thuộc tính | Giá trị |
|------------|---------|
| **Method** | `POST` |
| **Path**   | `/api/auth/login` |
| **Auth**   | Không |
| **Body**   | `application/json` – [`LoginRequest`](#loginrequest) |
| **200**    | Trả về JWT ở dạng chuỗi (`text/plain`) |

#### LoginRequest

```json
{
  "email": "user@example.com",
  "password": "secret"
}
```

## 2. Thị trường công khai (`/market`)

Các endpoint này mở, không cần token.

### Lấy giá P2P tổng hợp

`GET /api/market/price`

| Tham số query | Bắt buộc | Mô tả |
|---------------|----------|-------|
| `token`       | Có       | Mã token crypto (VD: `USDT`) |
| `fiat`        | Có       | Mã tiền pháp định (VD: `VND`) |
| `tradeType`   | Không    | `BUY` hoặc `SELL` (mặc định `SELL`) |
| `top`         | Không    | Số lượng quảng cáo tối đa để tính giá (mặc định `5`) |

**200** – `double` (giá tốt nhất). `500` nếu không lấy được giá.

### Danh sách lệnh mua/bán công khai

`GET /api/market/orders/buy`

`GET /api/market/orders/sell`

| Query        | Bắt buộc | Mô tả |
|--------------|----------|-------|
| `token`       | Không    | Lọc theo token |
| `paymentMethod` | Không | Lọc theo phương thức thanh toán |
| `sortByPrice` | Không  | `asc` / `desc` |

**200** – Mảng [`OrderResponse`](#orderresult).

## 3. P2P Orders & Trades (`/p2p/**`)

Các endpoint này yêu cầu JWT.

### Tạo quảng cáo (order)

`POST /api/p2p/orders`

- **Body:** [`OrderRequest`](#orderrequest)
- **201**/**200** – [`OrderResponse`](#orderresult)

### Danh sách order

`GET /api/p2p/orders`

| Query        | Bắt buộc | Mô tả |
|--------------|----------|-------|
| `type`        | Không    | `BUY` hoặc `SELL` |
| `token`       | Không    | Lọc token |
| `paymentMethod` | Không | Lọc phương thức thanh toán |
| `sortByPrice` | Không  | `asc` / `desc` |

**200** – Mảng [`OrderResponse`](#orderresult).

### Danh sách trade theo order

`GET /api/p2p/orders/{orderId}/trades`

- **Path:** `orderId` – ID order
- **200** – Mảng [`TradeResponse`](#traderesult)

### Hủy order (chỉ chủ sở hữu, khi chưa có trade)

`POST /api/p2p/{orderId}/cancel`

- **200** – Rỗng khi thành công

### Tạo trade

`POST /api/p2p/trades`

- **Body:** [`TradeRequest`](#traderequest)
- **200** – [`TradeResponse`](#traderesult)

### Xác nhận người mua đã thanh toán

`POST /api/p2p/trades/{tradeId}/confirm-payment`

- **200** – [`TradeResponse`](#traderesult)

### Xác nhận người bán đã nhận tiền & giải phóng escrow

`POST /api/p2p/trades/{tradeId}/confirm-received`

- **200** – [`TradeResponse`](#traderesult)

### Hủy trade

`POST /api/p2p/trades/{tradeId}/cancel`

- **200** – [`TradeResponse`](#traderesult)

## 4. Dispute (`/p2p/trades/{tradeId}`)

### Mở tranh chấp

`POST /api/p2p/trades/{tradeId}/dispute`

| Query | Bắt buộc | Mô tả |
|-------|----------|-------|
| `reason`   | Có | Lý do tranh chấp |
| `evidence` | Không | Bằng chứng bổ sung (URL/ghi chú) |

**200** – [`DisputeResponse`](#disputeresult)

### Lịch sử tranh chấp của trade

`GET /api/p2p/trades/{tradeId}/disputes`

**200** – Mảng [`DisputeResponse`](#disputeresult)

## 5. Trade chat (`/p2p/trades/{tradeId}/chat`)

### Gửi tin nhắn chat

`POST /api/p2p/trades/{tradeId}/chat`

- **Body:** chuỗi text (raw JSON string `"message"`)
- **200** – [`TradeChatResponse`](#tradechatresult)

### Lấy lịch sử chat

`GET /api/p2p/trades/{tradeId}/chat`

- **200** – Mảng [`TradeChatResponse`](#tradechatresult)

## 6. Wallet (`/p2p/wallets`)

`GET /api/p2p/wallets`

- **200** – Mảng [`WalletBalanceResponse`](#walletbalanceresult)`

## 7. Người dùng (`/users`)

### Hồ sơ người dùng hiện tại

`GET /api/users/me`

- **200** – [`UserResponse`](#userresponse)
- **401** – Nếu chưa đăng nhập

### Thống kê order & trade của tôi

`GET /api/users/my-activities`

- **200** – [`UserTradesOrdersResponse`](#usertradesordersresult)

## 8. Tích hợp hệ thống ngoài (`/integration`)

### Đồng bộ user + wallet và lấy token

`POST /api/integration/users/sync`

- **Auth:** mở (đề xuất bổ sung chữ ký bảo mật ở reverse proxy)
- **Body:**

```json
{
  "userId": "user@example.com",
  "wallet": {
    "token": "USDT",
    "address": "0xabc...",
    "balance": 1200.0,
    "availableBalance": 1100.0
  },
  "kycStatus": "VERIFIED"
}
```

- **200** –

```json
{
  "user": { /* UserResponse */ },
  "wallet": {
    "id": 5,
    "token": "USDT",
    "address": "0xabc...",
    "balance": 1200.0,
    "availableBalance": 1100.0
  },
  "token": "<jwt>"
}
```

**Hành vi:**
- Nếu email chưa tồn tại → tạo user mới (password ngẫu nhiên, trạng thái mặc định).
- Nếu đã có user → tái sử dụng user.
- Ví được bind theo `token` (tự tạo mới hoặc cập nhật số dư hiện có).
- Nếu `kycStatus` được cung cấp → cập nhật trạng thái KYC tương ứng.
- Mọi trường hợp đều trả về JWT để client dùng tiếp với các endpoint yêu cầu auth.

## Định nghĩa Result/Response

### OrderRequest

Xem chi tiết trong mã (`OrderRequest.java`). Các trường chính:

```json
{
  "type": "BUY",
  "token": "USDT",
  "amount": 1000.0,
  "price": 24000.0,
  "priceMode": "CUSTOM",
  "paymentMethod": "BANK",
  "bankName": "Vietcombank",
  "bankAccount": "0123456789",
  "accountHolder": "Nguyen Van A",
  "minLimit": 500000.0,
  "maxLimit": 2000000.0
}
```

### OrderResponse

Trả về từ các endpoint order.

```json
{
  "id": 42,
  "type": "SELL",
  "token": "USDT",
  "amount": 1500.0,
  "price": 24000.0,
  "minLimit": 500000.0,
  "maxLimit": 2000000.0,
  "status": "OPEN",
  "paymentMethod": "BANK",
  "priceMode": "CUSTOM",
  "availableAmount": 1500.0,
  "expireAt": "2025-10-01T08:00:00",
  "fiatAccountId": 3,
  "userId": 7,
  "bankName": "Vietcombank",
  "bankAccount": "0123456789",
  "accountHolder": "Nguyen Van A",
  "trades": [ /* danh sách TradeResponse */ ]
}
```

### TradeRequest

```json
{
  "orderId": 42,
  "amount": 100.0,
  "chatMessage": "Xin chào, tôi muốn mua ngay."
}
```

### TradeResponse

```json
{
  "id": 1001,
  "orderId": 42,
  "buyerId": 15,
  "sellerId": 7,
  "amount": 100.0,
  "status": "PENDING_PAYMENT",
  "escrow": true
}
```

### DisputeResponse

```json
{
  "id": 10,
  "tradeId": 1001,
  "reason": "Người bán không xác nhận",
  "evidence": "https://drive.google.com/...",
  "createdAt": "2025-09-30T12:34:56"
}
```

### TradeChatResponse

```json
{
  "id": 55,
  "tradeId": 1001,
  "senderId": 15,
  "message": "Đã chuyển khoản, kiểm tra giúp mình",
  "timestamp": "2025-09-30T12:35:00"
}
```

### WalletBalanceResponse

```json
{
  "token": "USDT",
  "balance": 5000.0,
  "availableBalance": 4500.0
}
```

### UserResponse

```json
{
  "id": 7,
  "email": "user@example.com",
  "phone": "+84901234567",
  "kycStatus": "VERIFIED",
  "wallets": [
    { "token": "USDT", "address": "0x...", "balance": 1500.0 }
  ],
  "loginHistory": [
    { "ip": "1.2.3.4", "device": "Chrome on macOS", "timestamp": "2025-09-29T21:00:00" }
  ]
}
```

### UserTradesOrdersResponse

```json
{
  "orders": [ /* OrderResponse */ ],
  "trades": [ /* TradeResponse */ ]
}
```

---

## Ghi chú thêm

- Tất cả các đường dẫn trên yêu cầu base URL `/api`. Ví dụ thực tế: `http://localhost:8080/api/p2p/orders`.
- `springdoc-openapi` đã được cấu hình ⇒ có thể truy cập tài liệu tương tác tại `/api/swagger-ui/index.html`.
- Khi mở rộng API, đảm bảo cập nhật file này cũng như `OpenApiConfig` để giữ đồng bộ tài liệu.
