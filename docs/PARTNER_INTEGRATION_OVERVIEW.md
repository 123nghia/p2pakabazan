# Hướng dẫn tích hợp Partner - P2P

Tài liệu ngắn gọn cho slide thuyết trình: mô tả ai cần gì, luồng làm việc, và yêu cầu kỹ thuật để đối tác tích hợp P2P.

## Thành phần & môi trường
- P2P backend: `http://<p2p-host>:9000` (Docker dev: service `p2p-app`, port 9000).
- Partner mock (tham chiếu demo): `http://<partner-host>:9001` (Docker dev: service `partner_mock`, container `p2p-partner-mock`, port 9001).
- Infra: Postgres 5432, Redis 6379, RabbitMQ 5672, WebSocket 9002.
- Env mapping (Docker): `APP_PARTNER_API_PARTNERS=SAN_A=http://p2p-partner-mock:9001` (dùng hostname không có `_` để tránh 400 từ Tomcat), `APP_SSO_PARTNERS=SAN_A:<sharedSecret>`.

## Đối tác cần chuẩn bị
- **UI/UX:** Nút/đường dẫn “P2P” trên web/app; khi bấm sẽ chuyển hướng sang P2P (xem luồng SSO bên dưới).
- **Backend SSO caller:** Endpoint server-side để gọi `POST /api/sso/issue` của P2P, ký HMAC với `partnerId` + `sharedSecret`, truyền `externalUserId`, email, username, kycStatus.
- **Funds API (server-to-server) phục vụ P2P:**
  - `GET /internal/p2p/users/{externalUserId}/balances?asset=USDT`
  - `POST /internal/p2p/funds/lock`
  - `POST /internal/p2p/funds/unlock`
  - `POST /internal/p2p/funds/transfer`
  - Yêu cầu: HMAC kiểm tra `X-P2P-Id/X-Timestamp/X-Nonce/X-Signature`, idempotency qua `requestId`, trả `available`/`locked` dạng chuỗi số thập phân, mã lỗi 400/401/409 phù hợp.
- **Quản lý bảo mật:** Lưu trữ `sharedSecret` an toàn, chống replay bằng kiểm tra timestamp + nonce, log audit cho các lệnh lock/transfer.
- **Thông tin cung cấp cho P2P:** Base URL nội bộ cho funds API, `partnerId`, `sharedSecret`, whitelist CORS (nếu cần), thông số timeout/rate limit.

## P2P cần chuẩn bị/cung cấp
- **Cấp thông tin cho đối tác:**
  - `partnerId` + `sharedSecret` (tạo qua `POST /api/integration/partners` với `X-Admin-Token`).
  - P2P client credential cho funds API: `clientId` (mặc định `P2P_APP`) + `clientSecret`.
  - P2P base URL và UI URL để partner redirect.
- **Cấu hình P2P:**
  - `.env`: `APP_SSO_PARTNERS=SAN_A:<sharedSecret>`, `APP_PARTNER_API_PARTNERS=SAN_A=http://p2p-partner-mock:9001`, `APP_PARTNER_API_CLIENT_ID`, `APP_PARTNER_API_CLIENT_SECRET`.
  - Redis cho nonce/code (`SPRING_REDIS_HOST/PORT`), RabbitMQ/Postgres như docker-compose.
- **Giám sát & vận hành:** Log HMAC lỗi, cảnh báo khi funds API trả 401/409, theo dõi latency/timeout.

## Luồng tích hợp (tổng quan)
1) **Onboard partner:** Admin P2P gọi `POST /api/integration/partners` để lấy `partnerId` + `sharedSecret`; cấu hình vào `.env` của cả hai phía.
2) **User click P2P:**
   - User đã đăng nhập trên sàn đối tác.
   - Partner backend ký HMAC gọi `POST /api/sso/issue` kèm thông tin user.
   - Nhận `{ code, expiresIn }`, redirect browser sang `https://p2p.../sso?code=<code>`.
   - P2P UI gọi `POST /api/sso/exchange` lấy JWT `{ token, userId }`.
3) **Hiển thị số dư:** P2P backend nhận token, thấy user thuộc partner -> gọi `GET /internal/p2p/users/{externalUserId}/balances` của đối tác, hiển thị available/locked.
4) **Đặt lệnh bán (ví dụ):**
   - P2P gửi `POST /internal/p2p/funds/lock` để khóa số dư cho order.
   - Nếu khóa thành công, order được tạo trong P2P.
5) **Thanh toán/hoàn tất trade:**
   - Nếu hủy: `POST /internal/p2p/funds/unlock`.
   - Nếu khớp thành công: `POST /internal/p2p/funds/transfer` (chuyển từ seller sang buyer).
6) **Truy vết & idempotency:** Mọi gọi funds API dùng `requestId` duy nhất; partner phải trả kết quả idempotent cho cùng `requestId`.

## Chi tiết endpoints (tóm tắt)
- **P2P nhận từ partner:**
  - `POST /api/sso/issue` (HMAC `X-Partner-Id/Timestamp/Nonce/Signature`, body user info).
  - `POST /api/sso/exchange` (browser -> P2P, dùng `code`).
  - `POST /api/integration/partners` (admin, tạo credential).
- **P2P gọi sang partner (funds API):**
  - `GET /internal/p2p/users/{externalUserId}/balances?asset=USDT`
  - `POST /internal/p2p/funds/lock` `{ requestId, externalUserId, asset, amount, refType, refId }`
  - `POST /internal/p2p/funds/unlock` `{ requestId, lockId, amount? }`
  - `POST /internal/p2p/funds/transfer` `{ requestId, lockId, fromExternalUserId, toExternalUserId, asset, amount, refType, refId }`

## HMAC (rút gọn)
- Headers: với SSO dùng `X-Partner-Id`, với funds API dùng `X-P2P-Id`; chung: `X-Timestamp`, `X-Nonce`, `X-Signature`.
- Chuỗi ký: `METHOD + "\n" + PATH + "\n" + timestamp + "\n" + nonce + "\n" + sha256Hex(body)`.
- Kiểm tra: lệch thời gian <= `APP_SSO_TIMESTAMP_SKEW_SECONDS`, nonce chưa dùng, signature hợp lệ.

## Dev/test nhanh (docker)
- `./docker-dev.sh` hoặc `docker compose up -d --build`.
- P2P API: `http://localhost:9000/api`, Partner mock: `http://localhost:9001`.
- Env mẫu: `.env` đã set `APP_PARTNER_API_PARTNERS=SAN_A=http://p2p-partner-mock:9001`; chỉ cần thay `APP_SSO_PARTNERS` và `APP_PARTNER_API_CLIENT_SECRET` theo credential thật.
