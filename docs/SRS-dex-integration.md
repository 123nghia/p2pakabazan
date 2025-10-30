# SRS: Tich hop he thong P2P vao san dex.co

Version: 1.0 (Draft)
Owner: Backend Team (2–3 dev)

## 1. Muc dich
Xac dinh yeu cau chuc nang va phi chuc nang de tich hop backend P2P hien co vao he sinh thai dex.co, trong do dex.co quan ly user & vi. UI/UX do dex.co thuc hien. Muc tieu la co the chay duoc trong thoi gian ngan voi rui ro thap.

## 2. Pham vi
- Trong pham vi: Tich hop xac thuc/phan quyen voi dex.co; map danh tinh nguoi dung; tich hop vi (lock/unlock/transfer) qua API dex.co; giu nguyen luong order/trade/dispute; day thong bao su kien; quan tri qua admin module.
- Ngoai pham vi: Frontend/UI, quan ly nguoi dung & vi noi bo (dex.co chiu trach nhiem), cong thanh toan fiat, KYC/AML (dex.co phu trach).

## 3. Dinh nghia
- External User (DEX): Nguoi dung do dex.co quan ly (ID, vi, quyen).
- External Wallet: So du, giao dich tai dex.co. He thong P2P khong giu so du that, chi yeu cau tac vu tren vi extern.
- Lock: Dong bang mot phan so du de dam bao giao dich P2P.
- Release: Giai toa so du da lock.
- Transfer: Chuyen so du giua vi (VD seller -> buyer sau khi confirm received).

## 4. Tai lieu tham chieu
- README.md (huong dan run)
- docs/system-overview.md (kien truc tong quan)
- docs/api-spec.md (REST API hien co)

## 5. Ben lien quan
- DEX Platform Team (chu thong tin user/vi, UI/UX, webhook/API)
- Backend P2P Team (2–3 dev)
- DevOps (CI/CD, secrets, observability)

## 6. Tong quan giai phap
- Chay he thong P2P o che do "external-wallet": tat toan bo tac vu so du noi bo, chuyen thanh goi API sang dex.co.
- Tin cay JWT/SSO tu dex.co (issuer + JWKS). Map claims -> principal noi bo.
- Luu tru order/trade/dispute o DB noi bo; khong luu PII (chi `external_user_id`).
- Day su kien (trade status) qua RabbitMQ hoac webhook dex.co; nhan webhook tu dex.co khi can (VD payment confirmed).

## 7. Yeu cau chuc nang
### 7.1 Xac thuc/Phan quyen
- FR-Auth-1: He thong tiep nhan JWT tu dex.co (header Authorization: Bearer), xac thuc bang issuer + JWKS.
- FR-Auth-2: Mapping claims: `sub` -> `external_user_id`; role/scope -> authorities noi bo.
- FR-Auth-3: Ho tro S2S (client-credentials) cho cac endpoint noi bo (integration) neu can.

### 7.2 Dong bo danh tinh
- FR-User-1: Khi user lan dau goi API, he thong tao/mapping ban ghi user noi bo voi `external_user_id`.
- FR-User-2: Endpoints khong yeu cau thong tin PII (email/phone) neu dex.co khong cap.

### 7.3 Tich hop vi (External Wallet Bridge)
- FR-Wallet-1: Thay cac tac vu noi bo bang goi API dex.co:
  - Lock so du: `POST /wallet/locks` (body: userId, amount, currency, idempotencyKey)
  - Release lock: `POST /wallet/locks/{lockId}:release`
  - Transfer: `POST /wallet/transfers` (fromUserId, toUserId, amount, currency, reference)
- FR-Wallet-2: Idempotency bat buoc (header `Idempotency-Key`).
- FR-Wallet-3: Mapping loi tu dex.co (INSUFFICIENT_FUNDS, LOCK_NOT_FOUND, TIMEOUT) -> ErrorCode noi bo.
- FR-Wallet-4: Timeout/retry voi backoff, circuit breaker.

### 7.4 Quan ly order
- FR-Order-1: Tao/sua/xoa/active order theo API hien co, nguoi dung duoc xac thuc boi dex.co.
- FR-Order-2: Khi publish order ban, he thong co the pre-check so du qua dex.co (optional).

### 7.5 Vong doi trade
- FR-Trade-1: Tao trade -> Lock so du cua seller ben dex.co.
- FR-Trade-2: Buyer confirm payment -> ghi nhan su kien; co the nhan webhook tu dex.co neu co cong thanh toan tich hop.
- FR-Trade-3: Seller confirm received -> Transfer so du seller->buyer (dex.co) + release lock neu co phan du.
- FR-Trade-4: Cancel -> Release lock neu chua chuyen tien.

### 7.6 Tranh chap
- FR-Dispute-1: Mo tranh chap, dinh kem chung cu (link doi tac UI).
- FR-Dispute-2: Admin giai quyet -> thuc thi transfer/release tuong ung qua dex.co.

### 7.7 Thi truong & thong bao
- FR-Market-1: Lay gia Binance (khong thay doi).
- FR-Notif-1: Day su kien trade/notification sang dex.co qua webhook hoac event bus, hoac su dung NotificationService noi bo neu duoc yeu cau.

### 7.8 Admin
- FR-Admin-1: Admin co the truy van order/trade/dispute.
- FR-Admin-2: Thuc thi thao tac ky thuat (force release/transfer) voi audit log day du.

## 8. Yeu cau giao tiep/Interface
### 8.1 API noi bo (P2P)
- Khong thay doi base path: `/api/**`. Them/cap nhat cac endpoint:
  - `POST /api/integration/wallet/lock` (noi bo -> goi dex.co)
  - `POST /api/integration/wallet/release`
  - `POST /api/integration/wallet/transfer`
- Chap nhan JWT dex.co o tat ca endpoint yeu cau auth.

### 8.2 API ben ngoai (dex.co) – placeholder
- `POST {DEX_BASE}/wallet/locks`
- `POST {DEX_BASE}/wallet/locks/{id}:release`
- `POST {DEX_BASE}/wallet/transfers`
- OAuth2/JWT: issuer `{DEX_ISSUER}`, jwks `{DEX_JWKS_URI}`.

### 8.3 Webhook
- P2P -> dex.co: `/webhook/p2p/trades.status.changed`
- dex.co -> P2P: `/api/integration/dex/payment-confirmed` (optional)
- Chu ky ky so hoac HMAC header `X-Signature`.

### 8.4 Messaging
- RabbitMQ exchange: `trade.events.exchange`, routing-key: `trade.events.status` (co the forward sang dex.co).

## 9. Yeu cau du lieu
- Luu tru: order, trade, dispute, log, mapping user voi truong `external_user_id` (index).
- Khong luu PII khong can thiet. Anonymize data o non-prod.
- Audit log day du cho thao tac admin & vi (request/response hash, idempotency key).

## 10. Yeu cau phi chuc nang
- Hieu nang: 95p < 300ms cho API chinh; 50 TPS sustained.
- Do tin cay: 99.9% uptime trong gio lam viec; retry/circuit breaker cho call dex.co.
- Bao mat: Xac thuc JWT voi JWKS rotate, role-based access, input validation, rate limit per user/IP.
- Observability: Log JSON, trace id, metrics (DB, HTTP client, queue), health checks.
- Compliance: Log audit 180 ngay, xoa du lieu test non-prod.

## 11. Gioi han & rang buoc
- Thoi gian ngan, team 2–3 dev: uu tien MVP flow trade on-chain/vi.
- Phu thuoc do tin cay cua API dex.co.
- Khong thay doi UI (dex.co phu trach).

## 12. Cau hinh & feature flags
- `app.features.wallet.external=true`
- `dex.auth.issuer`, `dex.auth.jwks-uri`
- `dex.api.base-url`, `dex.api.timeout-ms`, `dex.api.api-key`
- `dex.webhook.hmac-secret`

## 13. Ke hoach trien khai (timeline goi y ~3–4 tuan)
- P0 (0.5T): Lam ro API dex.co, lay keys, mo truong, secret.
- P1 (1T): Tich hop JWT dex.co + mapping user; feature flag external wallet; skeleton wallet client.
- P2 (1–1.5T): Hoan thien lock/release/transfer + idempotency + error mapping + test tich hop.
- P3 (0.5T): Webhook/event, audit log, metric, hardening security.
- P4 (0.5T): Pilot UAT voi dex.co, fix bug, tai lieu, go-live.

## 14. Tieu chi chap nhan
- E2E: tao trade -> lock -> buyer confirm -> seller confirm -> transfer thanh cong (<= 5s) hoac cancel -> release dung.
- 100% cac call vi co idempotency, khong double-spend.
- Auth: token khong hop le bi chan; JWKS rotate khong downtime.
- Observability: metric/health co du lieu; audit day du.

## 15. R?i ro & giam thieu
- JWKS/Issuer sai -> Fail-closed, canary + alerting.
- Do tre API dex.co -> timeout + retry + circuit breaker.
- Double-spend -> idempotency key + state machine trade.
- Webhook spoof -> HMAC + replay protection (timestamp + nonce).

## 16. Van de mo
- Dac ta API vi dex.co cu the (schema, ma loi) chua co -> can xac nhan.
- Co nhan webhook payment hay chi buyer confirm? -> xac nhan de dong bo trang thai.
- Quy uoc currency/precision -> xac nhan.