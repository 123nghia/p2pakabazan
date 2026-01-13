# Slide Deck: Tích hợp Partner ↔ P2P

> Bộ slide gợi ý (dạng markdown) cho buổi trình bày với đối tác kỹ thuật & sản phẩm.

## 1) Mở đầu
- Mục tiêu: cho phép user từ sàn đối tác dùng P2P mà không phải đăng nhập lại, duy trì số dư ví trên sàn đối tác.
- Thành phần: P2P backend (9000), P2P UI, Partner backend, Partner Funds API, Redis, RabbitMQ, Postgres, WebSocket (9002).

## 2) Giá trị & phạm vi
- One-click vào P2P, không đăng nhập lại (SSO code).
- Số dư vẫn ở sàn đối tác, P2P chỉ gọi funds API để khóa/mở/chuyển.
- Hỗ trợ lock/unlock/transfer theo order/trade, idempotent theo `requestId`.

## 3) Kiến trúc logic
- Browser: chuyển hướng sang P2P bằng one-time code.
- P2P backend: xác thực HMAC, map user, gọi Partner Funds API khi user là partner.
- Partner backend: cung cấp SSO issue và Funds API (balances, lock, unlock, transfer).
- Khoá dữ liệu: Redis (nonce, code), DB P2P (order/trade log), DB partner (số dư).

## 4) Chuẩn bị từ phía Partner
- UI: nút/link “P2P” trên web/app, redirect theo luồng SSO.
- Backend SSO caller: ký HMAC với `partnerId` + `sharedSecret` gọi `POST /api/sso/issue`.
- Funds API server-to-server (HMAC P2P client): balances, lock, unlock, transfer; trả về số thập phân dưới dạng chuỗi; idempotent theo `requestId`.
- Bảo mật: lưu secret an toàn, kiểm tra timestamp ±skew, chống reuse `nonce`, log/audit.

## 5) Chuẩn bị từ phía P2P
- Cấp thông tin: `partnerId` + `sharedSecret` (qua `POST /api/integration/partners`), `clientId/clientSecret` cho funds API, base URL P2P/UI.
- Cấu hình env: `APP_SSO_PARTNERS=SAN_A:<secret>`, `APP_PARTNER_API_PARTNERS=SAN_A=http://p2p-partner-mock:9001`, `APP_PARTNER_API_CLIENT_SECRET=<...>`, Redis/RabbitMQ/Postgres theo docker-compose.
- Giám sát: cảnh báo khi funds API trả 401/409/timeout; theo dõi latency, tỉ lệ lỗi.

## 6) Luồng SSO (browser + backend)
1. User đã đăng nhập ở sàn đối tác, bấm P2P.
2. Partner backend ký HMAC gọi `POST /api/sso/issue` với `externalUserId`, email, username, kycStatus.
3. Nhận `{ code, expiresIn }` → redirect browser: `302 Location: https://p2p.../sso?code=<code>`.
4. P2P UI gọi `POST /api/sso/exchange` → nhận JWT `{ token, userId }`.
5. Từ đây tất cả API P2P dùng `Authorization: Bearer <token>`.

## 7) Luồng ví & giao dịch
- Xem số dư: P2P gọi `GET /internal/p2p/users/{externalUserId}/balances?asset=USDT` tại partner.
- Đặt lệnh bán: P2P gửi `POST /internal/p2p/funds/lock` (lock amount).
- Hủy lệnh: `POST /internal/p2p/funds/unlock`.
- Khớp lệnh: `POST /internal/p2p/funds/transfer` (unlock/chuyển cho buyer), idempotent theo `requestId`.

## 8) HMAC tóm tắt
- Headers: `X-Partner-Id` (cho `/api/sso/issue`) hoặc `X-P2P-Id` (cho funds API), chung: `X-Timestamp`, `X-Nonce`, `X-Signature`.
- Chuỗi ký: `METHOD + "\n" + PATH + "\n" + timestamp + "\n" + nonce + "\n" + sha256Hex(body)`.
- Kiểm tra: lệch thời gian <= `APP_SSO_TIMESTAMP_SKEW_SECONDS`, `nonce` chưa dùng, chữ ký hợp lệ.

## 9) Endpoint tóm tắt
- P2P nhận: `POST /api/sso/issue`, `POST /api/sso/exchange`, (admin) `POST /api/integration/partners`.
- P2P gọi partner: `GET /internal/p2p/users/{externalUserId}/balances`, `POST /internal/p2p/funds/lock`, `POST /internal/p2p/funds/unlock`, `POST /internal/p2p/funds/transfer`.

## 10) Dev/Test nhanh (Docker)
- `./docker-dev.sh` (hoặc `docker compose up -d --build`).
- P2P API: `http://localhost:9000/api`; Partner mock: `http://localhost:9001`.
- Env mẫu đã set `APP_PARTNER_API_PARTNERS=SAN_A=http://p2p-partner-mock:9001`; chỉ cần thay `APP_SSO_PARTNERS` + `APP_PARTNER_API_CLIENT_SECRET` cho credential thật.

## 11) Rủi ro & lưu ý
- Sai HMAC/clock skew → 401; xử lý đồng bộ thời gian (NTP).
- Thiếu idempotency `requestId` → double lock/transfer.
- Timeouts funds API → cần retry có backoff và idempotency.
- CORS/UI: whitelists origin phù hợp khi chạy thật.

## 12) Next steps cho buổi thuyết trình
- Chuẩn bị demo live: click P2P → redirect → hiển thị số dư partner mock → tạo/hủy order để thấy lock/unlock.
- Q&A: chia sẻ mẫu code ký HMAC (Java trong `partner_mock`) và Postman collection nếu cần.
