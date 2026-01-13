# Tài liệu kiểm thử luồng tích hợp đối tác

Mục tiêu: giúp tester nắm nhanh luồng tích hợp partner (SSO + Funds API) và biết điểm chạm để quan sát/kỳ vọng trong từng bước.

## Thành phần & cấu hình
- API prefix: phụ thuộc `spring.mvc.servlet.path` (thường set `/api`). Trong tài liệu dùng `<api>` đại diện cho prefix này.
- P2P backend: cổng 9000 (Docker dev), Redis dùng cho SSO code/nonce. Partner mock tham chiếu: cổng 9001 (Docker) hoặc 8085 (local).
- Env bắt buộc: `APP_SSO_PARTNERS=PARTNER_ID:SECRET`, `APP_SSO_CODE_TTL_SECONDS` (mặc định 60s), `APP_SSO_NONCE_TTL_SECONDS` (300s), `APP_SSO_TIMESTAMP_SKEW_SECONDS` (300s), `APP_PARTNER_API_PARTNERS=PARTNER_ID=http://host:port`, `APP_PARTNER_API_CLIENT_ID`/`APP_PARTNER_API_CLIENT_SECRET`. Admin tạo partner: `APP_SSO_ADMIN_TOKEN`.
- Định nghĩa user partner: sau khi `/sso/issue`, user được tạo/cập nhật với `user.type=partnerId`, `user.relId=externalUserId`, `kycStatus` có thể được set từ payload.

## Luồng SSO (đăng nhập từ sàn đối tác)
1) Onboard partner (admin): `POST <api>/integration/partners` header `X-Admin-Token` -> nhận `{partnerId, sharedSecret}`; cấu hình vào cả P2P & partner.
2) Partner backend gọi `POST <api>/sso/issue` với HMAC (`X-Partner-Id/Timestamp/Nonce/Signature`) và body `{ externalUserId, email?, username?, kycStatus? }`.
3) P2P kiểm HMAC + timestamp ±`APP_SSO_TIMESTAMP_SKEW_SECONDS`, lưu code ở Redis TTL = `APP_SSO_CODE_TTL_SECONDS`, trả `{ code, expiresIn }`.
4) Browser được redirect tới P2P UI: `.../sso?code=<code>`; UI gọi `POST <api>/sso/exchange` body `{ code }`.
5) P2P trả `{ token, userId }`; các API sau dùng `Authorization: Bearer <token>`.

## Luồng lấy số dư
- API: `GET <api>/p2p/wallets`.
- Nếu user là partner (`type` hoặc `relId` không trống): P2P gọi `GET /internal/p2p/users/{externalUserId}/balances?asset=<token>` tới partner với HMAC `X-P2P-Id`/`X-Signature` (secret = `APP_PARTNER_API_CLIENT_SECRET`), sau đó map `available + locked`.
- Nếu user nội bộ: đọc từ bảng `wallet`.

## Luồng quỹ khi giao dịch (Funds API)
| Hành động trong P2P | Khi nào gọi | Endpoint đối tác | requestId & lockId | Ghi chú |
| --- | --- | --- | --- | --- |
| Khóa SELL order | Tạo lệnh SELL | `POST /internal/p2p/funds/lock` | `requestId=ORDER_LOCK:<orderId>`, `lockId` lưu `order.fundsLockId` | Lock toàn bộ `order.amount`. |
| Mở SELL order (hủy/expire) | Cancel order, auto-expire | `POST /internal/p2p/funds/unlock` | `requestId=ORDER_UNLOCK:<orderId>:<amount>`, dùng `order.fundsLockId` | `amount` = phần còn available. |
| Khóa trade trên BUY order (seller là actor) | Tạo trade vào lệnh BUY | `POST /internal/p2p/funds/lock` | `requestId=TRADE_LOCK:<tradeId>`, `lockId` lưu `trade.fundsLockId` | Lock đúng `trade.amount`. |
| Chuyển khi seller xác nhận đã nhận tiền | `confirmReceived` (trade status PAID -> COMPLETED) | `POST /internal/p2p/funds/transfer` | `requestId=TRADE_TRANSFER:<tradeId>`, `lockId` = `order.fundsLockId` (SELL order) hoặc `trade.fundsLockId` (BUY order) | Yêu cầu buyer & seller cùng `partnerId`, không hỗ trợ cross-partner. |
| Mở khóa qua dispute “seller thắng” | Dispute outcome SELLER_FAVORED | `POST /internal/p2p/funds/unlock` | `requestId=ORDER_UNLOCK...` hoặc `TRADE_UNLOCK:<tradeId>` tùy loại lệnh | Dùng `refundToSeller` trong `SellerFundsManager`. |
| Chuyển qua dispute “buyer thắng” | Dispute outcome BUYER_FAVORED | `POST /internal/p2p/funds/transfer` | `requestId=TRADE_TRANSFER:<tradeId>` | Dùng `releaseToBuyer`/`settleTrade`. |

Ghi chú bổ sung:
- Các request đều ký HMAC với chuỗi chuẩn: `METHOD + "\n" + PATH + "\n" + timestamp + "\n" + nonce + "\n" + sha256Hex(body)`. SSO dùng header `X-Partner-Id`, Funds API dùng `X-P2P-Id`.
- `requestId` phải idempotent phía partner; P2P hiện gửi theo mẫu ở bảng trên.
- `amount`/`available`/`locked` đều là chuỗi số thập phân.
- Hủy trade (PENDING -> CANCELLED) hiện chưa gọi Funds API cho user partner, nên chưa giải phóng lock phía đối tác trong luồng này.

## Checklist kiểm thử nhanh (gợi ý)
- Khởi động bằng `./docker-dev.sh` (hoặc `docker compose up -d --build`); Docker map sẵn `APP_PARTNER_API_PARTNERS=SAN_A=http://p2p-partner-mock:9001`.
- Onboard partner: `POST <api>/integration/partners` -> cập nhật `.env` với `APP_SSO_PARTNERS`, `APP_PARTNER_API_CLIENT_SECRET`.
- SSO: dùng partner_mock hoặc script HMAC gọi `<api>/sso/issue` -> redirect -> `<api>/sso/exchange` -> lưu JWT.
- Lấy số dư: `GET <api>/p2p/wallets` -> đối chiếu với số dư mock.
- Tạo SELL order -> kiểm log partner nhận `ORDER_LOCK`, hủy order -> `ORDER_UNLOCK`.
- Tạo trade trên BUY order (seller là partner) -> `TRADE_LOCK`; seller confirm nhận tiền -> `TRADE_TRANSFER`.
- Dispute: đặt trade vào dispute, resolve BUYER_FAVORED/SELLER_FAVORED để quan sát `transfer`/`unlock` tương ứng.
