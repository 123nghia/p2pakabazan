# Partner SSO Redirect (One-time code)

## Goal

From the partner exchange (sàn), when the user clicks the P2P menu, redirect the user into the P2P subdomain and let P2P mint a P2P JWT access token for that user.

## Flow (step-by-step)

1) User is logged in on the exchange UI (partner domain).
2) User clicks **P2P**.
3) Browser calls partner backend endpoint (example): `POST https://san.example.com/api/p2p/start` (or `GET /go/p2p`)
4) Partner backend calls P2P (server-to-server) to request a short-lived one-time code:
   - `POST https://p2p.example.com/api/sso/issue`
   - Signed with HMAC shared secret (see below)
5) P2P verifies signature + timestamp + nonce (anti-replay), provisions/mapping the P2P user, stores the code in Redis (TTL), then returns:
   - `{ code, expiresIn }`
6) Partner backend redirects browser to P2P:
   - `302 Location: https://p2p.example.com/sso?code=<code>`
7) P2P UI exchanges the code for a P2P JWT token:
   - `POST https://p2p.example.com/api/sso/exchange`
8) P2P returns `{ token, userId }`. P2P UI stores the token and uses `Authorization: Bearer <token>` for `/api/**`.

## Endpoints (P2P)

- `POST /api/sso/issue` (partner backend -> P2P)
  - Auth: HMAC (server-to-server)
  - Body example:
    - `{ "externalUserId": "user_1001", "email": "user1001@example.com", "username": "user1001", "kycStatus": "VERIFIED" }`
  - Response: `{ code, expiresIn }`
- `POST /api/sso/exchange` (browser -> P2P)
  - Auth: one-time code
  - Response: `{ token, userId }`

## Endpoints (Partner internal funds API)

These endpoints are called by P2P backend (server-to-server) when the user is a partner user (user has `type`/`relId`).

- `GET /internal/p2p/users/{externalUserId}/balances?asset=USDT` (P2P -> partner)
  - Auth: HMAC (server-to-server, P2P client credential)
  - Response example:
    - `{ "externalUserId": "user_1001", "balances": [ { "asset": "USDT", "available": "1000", "locked": "0" } ] }`

- `POST /internal/p2p/funds/lock` (P2P -> partner)
  - Auth: HMAC (server-to-server, P2P client credential)
  - Body example:
    - `{ "requestId":"...","externalUserId":"user_1001","asset":"USDT","amount":"10","refType":"ORDER","refId":"<orderId>" }`
  - Response example:
    - `{ "requestId":"...","lockId":"...","status":"LOCKED","lockedAmount":"10","availableAfter":"990","lockedAfter":"10" }`

- `POST /internal/p2p/funds/unlock` (P2P -> partner)
  - Auth: HMAC (server-to-server, P2P client credential)
  - Body example:
    - `{ "requestId":"...","lockId":"...","amount":"10" }` (`amount` optional = unlock all remaining)
  - Response example:
    - `{ "requestId":"...","lockId":"...","status":"RELEASED","unlockedAmount":"10","lockRemaining":"0","availableAfter":"1000","lockedAfter":"0" }`

- `POST /internal/p2p/funds/transfer` (P2P -> partner)
  - Auth: HMAC (server-to-server, P2P client credential)
  - Body example:
    - `{ "requestId":"...","lockId":"...","fromExternalUserId":"seller_1","toExternalUserId":"buyer_2","asset":"USDT","amount":"10","refType":"TRADE","refId":"<tradeId>" }`
  - Response example:
    - `{ "requestId":"...","transferId":"...","status":"COMPLETED","lockRemaining":"0","toAvailableAfter":"1010" }`

Notes:

- `requestId` should be unique per business action to support idempotency on the partner side.
- Partner can return:
  - `401` for invalid signature/timestamp/nonce,
  - `400` for invalid parameters,
  - `409` for insufficient funds / conflict.

## HMAC authentication (for `/api/sso/issue`)

### Headers (required)

- `X-Partner-Id`: partner identifier (e.g. `SAN_A`)
- `X-Timestamp`: epoch seconds
- `X-Nonce`: per-request unique value (UUID recommended)
- `X-Signature`: Base64(HMAC_SHA256(sharedSecret, canonicalString))

### Canonical string

```
METHOD + "\n" + PATH + "\n" + timestamp + "\n" + nonce + "\n" + sha256Hex(body)
```

Notes:

- `PATH` must be the request URI (example: `/api/sso/issue`)
- `body` is the raw JSON bytes sent over the wire (UTF-8)

### Anti-replay

- Reject if `abs(now - X-Timestamp) > APP_SSO_TIMESTAMP_SKEW_SECONDS`
- Reject if `X-Nonce` already exists in Redis (nonce is stored with TTL = `APP_SSO_NONCE_TTL_SECONDS`)

## HMAC authentication (for partner internal funds API)

### Headers (required)

- `X-P2P-Id`: P2P client identifier (e.g. `P2P_APP`)
- `X-Timestamp`: epoch seconds
- `X-Nonce`: per-request unique value (UUID recommended)
- `X-Signature`: Base64(HMAC_SHA256(p2pClientSecret, canonicalString))

### Canonical string

```
METHOD + "\n" + PATH + "\n" + timestamp + "\n" + nonce + "\n" + sha256Hex(body)
```

Notes:

- `PATH` must be the request URI (example: `/internal/p2p/funds/lock`)
- `body` is the raw JSON bytes sent over the wire (UTF-8)

## Redis (one-time code storage)

- Key pattern for code: `sso:code:<code>` -> `<userId>` (TTL = `APP_SSO_CODE_TTL_SECONDS`)
- Key pattern for nonce: `sso:nonce:<partnerId>:<nonce>` -> `1` (TTL = `APP_SSO_NONCE_TTL_SECONDS`)

## Docker dev configuration

- Redis is added in `docker-compose.yml` as service `redis`.
- Configure env vars in `.env` (see `.env.example`):
  - `SPRING_REDIS_HOST=redis`
  - `SPRING_REDIS_PORT=6379`
  - `APP_SSO_PARTNERS=SAN_A:<sharedSecret>`
  - `APP_SSO_CODE_TTL_SECONDS=60`
  - `APP_SSO_NONCE_TTL_SECONDS=300`
  - `APP_SSO_TIMESTAMP_SKEW_SECONDS=300`

## Local test (partner_mock UI + backend)

This repo includes a tiny partner app to simulate the exchange UI/backend:

1) Start infra (Postgres + Redis + RabbitMQ):
   - `docker-compose up -d db redis rabbitmq`

2) Start P2P backend (host, profile `local`) and configure partner secret:
   - PowerShell:
     - `$env:APP_SSO_PARTNERS='SAN_A:change-me'`
     - `$env:SPRING_REDIS_HOST='localhost'`
     - `mvn -pl p2p_p2p spring-boot:run`
   - P2P Swagger: `http://localhost:9999/api/swagger-ui/index.html`

3) Start partner mock app:
   - PowerShell:
     - `$env:PARTNER_SHARED_SECRET='change-me'`
     - `$env:P2P_BASE_URL='http://localhost:9999'`
     - `$env:P2P_UI_URL='http://localhost:9898'`
     - `mvn -pl partner_mock spring-boot:run`
   - Partner UI: `http://localhost:8085/`

4) Click **P2P** on partner UI:
   - Partner UI calls `POST /api/p2p/start` on partner backend.
   - Partner backend calls `POST /api/sso/issue` with HMAC, then browser is redirected to:
     - `http://localhost:9898/sso?code=...`

5) Start P2P UI mock app (pure HTML):
   - PowerShell:
     - `$env:P2P_API_BASE_URL='http://localhost:9999'`
     - `mvn -pl p2p_ui_mock spring-boot:run`
   - P2P UI: `http://localhost:9898/`

6) P2P UI exchanges code → JWT and calls `GET /api/me` to verify (requires CORS in local profile).

## mTLS (future)

For stronger transport-level authentication, you can later add mTLS at the gateway/ingress for `/api/sso/*`, while keeping HMAC as message-level verification.
